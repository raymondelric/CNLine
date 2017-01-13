package client;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.nio.charset.Charset;
import client.calls.*;
import client.packet.*;
import client.session.*;
import client.ui.*;

public class Client{
	private static Queue<UiCallObject> fromUI; //read only
	private static Queue<UiCallObject> toUI; //write only
	private static Queue<String> fromServer; //write only
	private static UI ui;
	//private static UI ui;
	private static Socket socket;
	private static PrintWriter out;
	private static BufferedReader in;
	private static char[] buffer;
	private static int buffersize = 4096;

	private static boolean IsConnected, IsLoggedIn, EXIT;
	private static Session session;

	public static void main(String[] args) {
		initState();
		initQueue();
		initUI();
		createUiThread(args);
		run();
	}

	public static void initQueue() {
		toUI = new LinkedBlockingQueue<UiCallObject>();
		fromUI = new LinkedBlockingQueue<UiCallObject>();
		fromServer = new LinkedBlockingQueue<String>();
	}
	
	public static void initUI() {
		ui = new client.ui.UI();
		//ui = new client.ui.UI();
		ui.setQueue(toUI, fromUI);
	}

	public static void initState() {
		IsConnected = false;
		IsLoggedIn = false;
		EXIT = false;
	}

	public static void createUiThread(String[] args) {
		try{
			if(args[0].equals("UI")){
				new Thread(new Runnable(){ public void run(){
					ui.run(args);
				}
				}).start();
			}else{
				throw new ArrayIndexOutOfBoundsException();
			}
		}catch(ArrayIndexOutOfBoundsException e){
			new Thread(new Runnable(){ public void run(){
				ui.runTest();
			}
			}).start();
		}
	}

	public static void createReadSocketThread(){
		new Thread(new Runnable(){ public void run(){
			while(true){
				try{
					int size = in.read(buffer, 0, buffersize);
					String serverMsg = "";
					if(size>0){
						serverMsg = (new String(buffer)).substring(0, size);
						System.out.println("[RST] "+serverMsg);
						fromServer.offer(serverMsg);
					}
				} catch(Exception e){}
				try{
					Thread.sleep(10);
				} catch(InterruptedException e){}

			}
		}
		}).start();
	}

	public static void resetSession(){
		session.reset();
	}

	public static void setSession(String ID, String[] loginResponse){
		int len = loginResponse.length-1;
		Room[] Rooms = new Room[len];
		while(fromServer.isEmpty()){}
		String[] strs = fromServer.peek().split("/");

		if(strs[0].equals(Packet.RECORD)){
			for(int i = 0; i < len; i++){
				String rid = strs[3*i+1];
				String[] users = strs[3*i+2].split("\\|");
				String[] msgs = strs[3*i+3].split("\\|");//EVEN IF NO MESSAGE, SERVER SHOULD SEND SOMETHING
				int msgcount = msgs.length;
				Msg[] historyMsgs = new Msg[msgcount];
				for(int j = 0; j < msgcount; j++){
					String[] pair = msgs[j].split("\\$");
					historyMsgs[j] = new Msg(pair[0], pair[1]);
				}
				Rooms[i] = new Room(rid, users, historyMsgs);
			}
			fromServer.poll();
			session.set(ID, Rooms);
		}
	}

	public static void run() {
		while(!EXIT){
			if(IsLoggedIn)
				session.print();
			if(!fromUI.isEmpty()) {
				UiCallObject call = fromUI.peek();
				if(call.type == UiCallObject.REQUEST){
					switch(call.whatCall){
						case UiCallObject.CONNECT_TO_SERVER:
							connect(call);
							break;
						case UiCallObject.REGISTER:
							register(call);
							break;
						case UiCallObject.LOGIN:
							login(call);
							break;
						case UiCallObject.LOGOUT:
							logout(call);
							break;
						case UiCallObject.EXIT:
							exit(call);
							break;
						case UiCallObject.CHECK_EXIST_ID:
							checkID(call);
							break;
						case UiCallObject.CREATE_ROOM:
							createroom(call);
							break;
						case UiCallObject.PEOPLE_IN_ROOM:
							roommember(call);
							break;
						case UiCallObject.SEND_MESSAGE:
							sendmessage(call);
							break;
						case UiCallObject.GET_MESSAGE:
							getmessage(call);
							break;
						case UiCallObject.SEND_FILE:
							//sendfile(call);
							break;
						case UiCallObject.GET_FILE:
							//getfile(call);
							break;
						case UiCallObject.DOWNLOAD_FILE:

							break;
						default:
							System.out.println("unidentified call number from UI");
							break;
					}
					fromUI.poll();
				}else{
					fromUI.poll();
				}
			}else{
				try{
					Thread.sleep(100);
				} catch(InterruptedException e){}
			}
			if(!fromServer.isEmpty()) {
				String[] strs = fromServer.peek().split("/");
				if(strs[0].equals(Packet.NEWMSG)){
					for(Room room : session.getroom()){
						if(strs[1].equals(room.rid)){
							room.msgs.add(0, new Msg(strs[2], strs[3]));
							GetMessageCall call = new GetMessageCall(strs[1],0);
							call.fill(strs[2], strs[3]);
							call.response("success");
							toUI.offer(call);
							break;
						}
					}
					fromServer.poll();
				}
				else if(strs[0].equals(Packet.ROOM_OK)){
					String[] ids = new String[strs.length-2];
					System.arraycopy(strs, 2, ids, 0, ids.length);
					session.getroom().add(new Room(strs[1], ids)); //
					fromServer.poll();
				}
			}
		}
	}

	public static void connect(UiCallObject _call) {
		if(IsConnected){
			System.out.println("Already Connected");
		} else{
			ConnectCall connectCall = (ConnectCall)_call;
			try{
				socket = new Socket(connectCall.ip, connectCall.port);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				buffer = new char[buffersize];
				createReadSocketThread();

				String msg = Packet.makeMsg(Packet.CONNECT, "Happy New Year!");
				String ret = "";
				out.println(msg);
				while(fromServer.isEmpty()){}
				ret = fromServer.peek();
				fromServer.poll();
				if(ret.equals(Packet.CONNECT_OK)){
					IsConnected = true;
					session = new Session();
					connectCall.response("success");
					toUI.offer(connectCall);
					System.out.println("[Connect] Success");
				} else{
					connectCall.response("fail");
					toUI.offer(connectCall);
					System.out.println("[Connect] Fail");
				}

			} catch(Exception e){
				connectCall.response("fail");
				toUI.offer(connectCall);
				System.out.println("[Connect] Fail");
			}
		}
	}
	
	public static void register(UiCallObject _call) {
		if(IsConnected){
			RegisterCall registerCall = (RegisterCall)_call;
			String msg = Packet.makeMsg(Packet.REGISTER, registerCall.id, registerCall.pswd);
			String ret = "";
			try{
				out.println(msg);
				while(fromServer.isEmpty()){}
				ret = fromServer.peek();
				fromServer.poll();
				if(ret.equals(Packet.REGISTER_OK)){
					registerCall.response("success");
					toUI.offer(registerCall);
					System.out.println("[Register] Success");
					UiCallObject logincall = new LoginCall(registerCall.id, registerCall.pswd);
					login(logincall);
				} else if(ret.equals(Packet.REGISTER_DUP)){
					registerCall.response("duplicate");
					toUI.offer(registerCall);
					System.out.println("[Register] ID duplicate");
				} else{
					IsConnected = false;
					ConnectCall disconnect = new ConnectCall("", 0);
					disconnect.response("fail");
					toUI.offer(disconnect);
					System.out.println("[Register] Fail, disconnected");
				}
			} catch(Exception e){
				IsConnected = false;
				ConnectCall disconnect = new ConnectCall("", 0);
				disconnect.response("fail");
				toUI.offer(disconnect);
				System.out.println("[Register] Fail, disconnected");
			}
		} else{
			System.out.println("Not Yet Connected");
		}
	}
	
	public static void login(UiCallObject _call) {
		if(IsConnected){
			LoginCall loginCall = (LoginCall)_call;
			String msg = Packet.makeMsg(Packet.LOGIN, loginCall.id, loginCall.pswd);
			String[] rets;
			try{
				out.println(msg);
				while(fromServer.isEmpty()){}
				rets = fromServer.peek().split("/");
				String ret = rets[0];
				System.out.print(ret);
				fromServer.poll();
				System.out.println("ret "+ret);
				if(ret.equals(Packet.LOGIN_OK)){
					setSession(loginCall.id, rets);
					IsLoggedIn = true;
					loginCall.roomlist(session.getrid());
					loginCall.response("success");
					toUI.offer(loginCall);
					System.out.println("[Login] Success, ID = "+session.getuid());
				} else if(ret.equals(Packet.LOGIN_FAIL)){
					loginCall.response("fail");
					toUI.offer(loginCall);
					System.out.println("[Login] Fail, id or password wrong");
				} else{
					IsConnected = false;
					ConnectCall disconnect = new ConnectCall("", 0);
					disconnect.response("fail");
					toUI.offer(disconnect);
					System.out.println("[Login] Fail, disconnected0");
				}
			} catch(Exception e){
				e.printStackTrace();
				IsConnected = false;
				ConnectCall disconnect = new ConnectCall("", 0);
				disconnect.response("fail");
				toUI.offer(disconnect);
				System.out.println("[Login] Fail, disconnected");
			}
		} else{
			System.out.println("Not Yet Connected");
		}
	}
	
	public static void logout(UiCallObject _call) {
		LogoutCall logoutCall = (LogoutCall)_call;
		if(IsConnected){
			if(IsLoggedIn){
				String msg = Packet.makeMsg(Packet.LOGOUT);
				String ret = "";
				try{
					out.println(msg);
					while(fromServer.isEmpty()){}
					ret = fromServer.peek();
					fromServer.poll();
					if(ret.equals(Packet.LOGOUT_OK)){
						IsLoggedIn = false;
						resetSession();
						logoutCall.response("success");
						toUI.offer(logoutCall);
						System.out.println("[Logout] Success, user = "+session.getuid());
					} else{
						logoutCall.response("success"); // logout fails?
						toUI.offer(logoutCall);
						System.out.println("[Logout] Success, user = "+session.getuid());
					}
				} catch(Exception e){
					IsConnected = false;
					ConnectCall disconnect = new ConnectCall("", 0);
					disconnect.response("fail");
					toUI.offer(disconnect);
					System.out.println("[Logout] Fail, disconnected");
				}
			} else{
				System.out.println("Not Yet Logged In");
			}
		} else{
			System.out.println("Not Yet Connected");
		}
	}
	
	public static void sendmessage(UiCallObject _call) {
		if(IsConnected){
			if(IsLoggedIn){
				MessageCall messageCall = (MessageCall)_call;
				String msg = Packet.makeMsg(Packet.MESSAGE, messageCall.rid, messageCall.data);
				try{
					out.println(msg);
				} catch(Exception e){
					IsConnected = false;
					ConnectCall disconnect = new ConnectCall("", 0);
					disconnect.response("fail");
					toUI.offer(disconnect);
					System.out.println("[Send Message] Fail, disconnected");
				}
			} else{
				System.out.println("Not Yet Logged In");
			}
		} else{
			System.out.println("Not Yet Connected");
		}
	}

	public static void getmessage(UiCallObject _call){
		boolean flag = true;
		if(IsConnected){
			if(IsLoggedIn){
				GetMessageCall getCall = (GetMessageCall)_call;
				String rid = "";
				int idx = getCall.msgid;
				for(Room room : session.getroom()){
					if(getCall.rid.equals(room.rid)){
						flag = false;
						getCall.fill(room.msgs.get(idx).owner, room.msgs.get(idx).msg);
						getCall.response("success");
						toUI.offer(getCall);
						System.out.println("[Get Message] Success");
						break;
					}
				}
				if(flag){
					getCall.response("fail");
					toUI.offer(getCall);
					System.out.println("[Get Message] Fail");
				}
			} else{
				System.out.println("Not Yet Logged In");
			}
		} else{
			System.out.println("Not Yet Connected");
		}	
	}

	public static void checkID(UiCallObject _call) {
		if(IsConnected){
			if(IsLoggedIn){
				CheckIDCall checkidCall = (CheckIDCall)_call;
				String cid = checkidCall.id;
				String msg = Packet.makeMsg(Packet.ID, checkidCall.id+"/");
				String ret = "";
				try{
					out.println(msg);
					while(fromServer.isEmpty()){}
					ret = fromServer.peek();
					fromServer.poll();
					if(ret.equals(Packet.ID_OK)){
						checkidCall.response("success");
						toUI.offer(checkidCall);
						System.out.println("[ID chk] Exist, user = "+cid);
					} else{
						checkidCall.response("not exist");
						toUI.offer(checkidCall);
						System.out.println("[ID chk] Not Exist, user = "+cid);
					}

				} catch(Exception e){
					IsConnected = false;
					ConnectCall disconnect = new ConnectCall("", 0);
					disconnect.response("fail");
					toUI.offer(disconnect);
					System.out.println("[Check ID] Fail, disconnected");
				}		
			} else{
				System.out.println("Not Yet Logged In");
			}
		} else{
			System.out.println("Not Yet Connected");
		}
	}

	public static void createroom(UiCallObject _call) {
		if(IsConnected){
			if(IsLoggedIn){
				RoomCall roomCall = (RoomCall)_call;
				String[] param = new String[roomCall.ids.length+1];
				param[0] = Packet.ROOM;
				System.arraycopy(roomCall.ids, 0, param, 1, roomCall.ids.length);
				String msg = Packet.makeMsg(param); // include user himself
				String rets = "";
				try{
					out.println(msg);
					while(fromServer.isEmpty()){}
					rets = fromServer.peek();
					System.out.println(rets);
					fromServer.poll();
					String[] strs = rets.split("/");
					if(strs.length>2){
						String ret = strs[0];
						String roomid = strs[1];
						if(ret.equals(Packet.ROOM_OK)){
							roomCall.setrid(roomid);
							roomCall.response("success");
							toUI.offer(roomCall);
							System.out.println("[Room] OK, id = "+roomid);
							Room r = new Room(roomid, roomCall.ids);//
							r.print();//
							session.getroom().add(r);//
						} else{
							roomCall.response("fail");
							toUI.offer(roomCall);
							System.out.println("[Room] Fail");
						}
					} else{
						roomCall.response("fail");
						toUI.offer(roomCall);
						System.out.println("[Room] Fail");
					}
				} catch(Exception e){
					e.printStackTrace(System.out);
					IsConnected = false;
					ConnectCall disconnect = new ConnectCall("", 0);
					disconnect.response("fail");
					toUI.offer(disconnect);
					System.out.println("[Room] Fail, disconnected");
				}				
			} else{
				System.out.println("Not Yet Logged In");
			}
		} else{
			System.out.println("Not Yet Connected");
		}
	}

/*	public static void createroom(UiCallObject _call) {
		if(IsConnected){
			if(IsLoggedIn){
				RoomCall roomCall = (RoomCall)_call;
				String[] param = new String[roomCall.ids.length+1];
				param[0] = Packet.ROOM;
				System.arraycopy(roomCall.ids, 0, param, 1, roomCall.ids.length);
				String msg = Packet.makeMsg(param); // include user himself
				String rets = "";
				try{
					out.println(msg);
					while(fromServer.isEmpty()){}
					rets = fromServer.peek();
					System.out.println(rets);
					fromServer.poll();
					String[] strs = rets.split("/");
					if(strs.length==2){
						String ret = strs[0];
						String roomid = strs[1];
						if(ret.equals(Packet.ROOM_OK)){
							roomCall.setrid(roomid);
							roomCall.response("success");
							toUI.offer(roomCall);
							System.out.println("[Room] OK, id = "+roomid);
							Room r = new Room(roomid);//
							r.print();//
							session.getroom().add(r);//
						} else{
							roomCall.response("fail");
							toUI.offer(roomCall);
							System.out.println("[Room] Fail");
						}
					} else{
						roomCall.response("fail");
						toUI.offer(roomCall);
						System.out.println("[Room] Fail");
					}

				} catch(Exception e){
					e.printStackTrace(System.out);
					IsConnected = false;
					ConnectCall disconnect = new ConnectCall("", 0);
					disconnect.response("fail");
					toUI.offer(disconnect);
					System.out.println("[Room] Fail, disconnected");
				}				
			} else{
				System.out.println("Not Yet Logged In");
			}
		} else{
			System.out.println("Not Yet Connected");
		}
	}*/

	public static void roommember(UiCallObject _call) {
		boolean flag = true;
		if(IsConnected){
			if(IsLoggedIn){
				InRoomCall inroomCall = (InRoomCall)_call;
				for(Room room : session.getroom()){
					if(inroomCall.rid.equals(room.rid)){
						flag = false;
						inroomCall.fill(room.uids, room.msgs.size());
						inroomCall.response("success");
						toUI.offer(inroomCall);
						System.out.println("[In Room] Success");
						break;
					}
				}
				if(flag){
					inroomCall.response("fail");
					toUI.offer(inroomCall);
					System.out.println("[In Room] Fail");
				}
			} else{
				System.out.println("Not Yet Logged In");
			}
		} else{
			System.out.println("Not Yet Connected");
		}	
	}

	public static void exit(UiCallObject _call) {
		if(IsLoggedIn){
			logout(_call);
		}
		IsConnected = false;
		EXIT = true;
	}
}
