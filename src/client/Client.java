package client;
import java.util.*; //useful stuff
import java.util.concurrent.*;
import java.io.*; //read write files
import java.net.*; //socket
import java.nio.charset.Charset; //for encoding problems
import client.calls.*;
import client.packet.*;
import client.session.*;
import client.ui.*;

public class Client{
	private static Queue<UiCallObject> fromUI; //read only
	private static Queue<UiCallObject> toUI; //write only
	private static Queue<String> fromServer; //write only
	private static UI_test ui;
	//private static UI ui;
	private static Socket socket;
	private static PrintWriter out;
	private static BufferedReader in;
	private static char[] buffer;
	private static int buffersize = 1000;

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
		ui = new client.ui.UI_test();
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
					//ui.run(args);
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
						String[] strs = serverMsg.split("/");
						if(strs[0].equals(Packet.NEWMSG)){
							for(Room room : session.rooms){
								if(strs[1].equals(room.rid)){
									room.msgs.add(0, new Msg(strs[2], strs[3]));
									break;
								}
							}
						} else{
							fromServer.offer(serverMsg);
						}
					}
				} catch(Exception e){
				}
			}
		}
		}).start();
	}

	public static void createSession(String ID, String[] loginResponse){
		Room[] Rooms = new Room[loginResponse.length-1];
		for(int i = 1; i < loginResponse.length; i++){
			int count = Integer.parseInt(loginResponse[i]);
			Msg[] historyMsgs = new Msg[count];
			String rid = "";
			for(int j = 0; j < count; j++){
				
				while(fromServer.isEmpty()){}

				String[] strs = fromServer.peek().split("/");
				if(strs[0].equals(Packet.RECORD)){
					rid = strs[1];
					fromServer.poll();
					historyMsgs[j] = new Msg(strs[2], strs[3]);
				} else{
					j--;
					System.out.println(strs[0]);
				}
			}
			Rooms[i-1] = new Room(rid, historyMsgs);
		}
		session = new Session( ID, Rooms);
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
							logout();
							break;
						case UiCallObject.EXIT:
							exit();
							break;
						case UiCallObject.CREATE_ROOM:
							createroom(call);
							break;
						case UiCallObject.SEND_MESSAGE:
							sendmessage(call);
							break;
						case UiCallObject.GET_MESSAGE:
							break;
						case UiCallObject.SEND_FILE:
							break;
						case UiCallObject.GET_FILE:
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
					Thread.sleep(10);
				} catch(InterruptedException e){}
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
					toUI.offer(new Result(UiCallObject.RESULT_CONNECT, UiCallObject.RESULT_CONNECT_OK));
					System.out.println("Connect OK");
				} else{
					toUI.offer(new Result(UiCallObject.RESULT_CONNECT, UiCallObject.RESULT_CONNECT_FAIL));
					System.out.println("Connect fail");
				}

			} catch(Exception e){
				toUI.offer(new Result(UiCallObject.RESULT_CONNECT, UiCallObject.RESULT_CONNECT_FAIL));
				System.out.println("Connect fail");
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
					toUI.offer(new Result(UiCallObject.RESULT_REGISTER, UiCallObject.RESULT_REGISTER_OK));
					System.out.println("Register OK");
					UiCallObject logincall = new LoginCall(registerCall.id, registerCall.pswd);
					login(logincall);
				} else if(ret.equals(Packet.REGISTER_DUP)){
					toUI.offer(new Result(UiCallObject.RESULT_REGISTER, UiCallObject.RESULT_REGISTER_DUP));
					System.out.println("Register fail, duplicate");
				} else if(ret.equals(Packet.REGISTER_ILE)){
					toUI.offer(new Result(UiCallObject.RESULT_REGISTER, UiCallObject.RESULT_REGISTER_ILE));
					System.out.println("Register fail, illegal");
				} else{
					IsConnected = false;
					toUI.offer(new Result(UiCallObject.RESULT_REGISTER, UiCallObject.RESULT_REGISTER_FAIL));
					System.out.println("Register fail, disconnected");
				}
			} catch(Exception e){
				toUI.offer(new Result(UiCallObject.RESULT_REGISTER, UiCallObject.RESULT_REGISTER_FAIL));
				System.out.println("Register fail");
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
				if(ret.equals(Packet.LOGIN_OK)){
					toUI.offer(new Result(UiCallObject.RESULT_LOGIN, UiCallObject.RESULT_LOGIN_OK));
					createSession(loginCall.id, rets);
					IsLoggedIn = true;
					System.out.println("Login OK, user = "+session.ID);
				} else if(ret.equals(Packet.LOGIN_IDNF)){
					toUI.offer(new Result(UiCallObject.RESULT_LOGIN, UiCallObject.RESULT_LOGIN_IDNF));
					System.out.println("Login fail, id "+loginCall.id+" not found");
				} else if(ret.equals(Packet.LOGIN_WRPS)){
					toUI.offer(new Result(UiCallObject.RESULT_LOGIN, UiCallObject.RESULT_LOGIN_WRPS));
					System.out.println("Login fail, wrong password");
				} else if(ret.equals(Packet.LOGIN_ALRD)){
					toUI.offer(new Result(UiCallObject.RESULT_LOGIN, UiCallObject.RESULT_LOGIN_ALRD));
					System.out.println("Login fail, already logged in");
				} else{
					IsConnected = false;
					toUI.offer(new Result(UiCallObject.RESULT_LOGIN, UiCallObject.RESULT_LOGIN_FAIL));
					System.out.println("Login fail");
				}
			} catch(Exception e){
				toUI.offer(new Result(UiCallObject.RESULT_LOGIN, UiCallObject.RESULT_LOGIN_FAIL));
				System.out.println("Login fail");
			}
		} else{
			System.out.println("Not Yet Connected");
		}
	}
	
	public static void logout() {
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
						toUI.offer(new Result(UiCallObject.RESULT_LOGOUT, UiCallObject.RESULT_LOGOUT_OK));
						System.out.println("Logout OK, user = "+session.ID);
					} else{
						toUI.offer(new Result(UiCallObject.RESULT_LOGOUT, UiCallObject.RESULT_LOGOUT_FAIL));
						System.out.println("Logout fail");
					}

				} catch(Exception e){
					toUI.offer(new Result(UiCallObject.RESULT_LOGOUT, UiCallObject.RESULT_LOGOUT_FAIL));
					System.out.println("Logout fail");
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
					toUI.offer(new Result(UiCallObject.RESULT_MESSAGE, UiCallObject.RESULT_MESSAGE_FAIL));
					System.out.println("Send message fail");
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
					fromServer.poll();
					String[] strs = rets.split("/");
					String ret = strs[0];
					String roomid = strs[1];
					if(ret.equals(Packet.ROOM_OK)){
						toUI.offer(new Result(UiCallObject.RESULT_ROOM, UiCallObject.RESULT_ROOM_OK));
						System.out.println("Room OK, id = "+roomid);
						Room r = new Room(roomid);
						r.print();
						session.rooms.add(r);
					} else{
						toUI.offer(new Result(UiCallObject.RESULT_ROOM, UiCallObject.RESULT_ROOM_FAIL));
						System.out.println("Room fail");
					}

				} catch(Exception e){
					toUI.offer(new Result(UiCallObject.RESULT_ROOM, UiCallObject.RESULT_ROOM_FAIL));
					System.out.println("Room fail");
				}				
			} else{
				System.out.println("Not Yet Logged In");
			}
		} else{
			System.out.println("Not Yet Connected");
		}
	}

	public static void exit() {
		if(IsLoggedIn){
			logout();
		}
		IsConnected = false;
		EXIT = true;
	}
}
