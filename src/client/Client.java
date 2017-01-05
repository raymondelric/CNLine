package client;
import java.util.*; //useful stuff
import java.util.concurrent.*;
import java.io.*; //read write files
import java.net.*; //socket
import java.nio.charset.Charset; //for encoding problems
import client.calls.*;
import client.packet.*;
import client.ui.*;

public class Client{
	private static Queue<UiCallObject> fromUI; //read only
	private static Queue<UiCallObject> toUI; //write only
	private static UI ui;
	private static Socket socket;
	private static PrintWriter out;
	private static BufferedReader in;
	private static char[] buffer;
	private static int buffersize = 1000;
	private static boolean IsConnected, IsLoggedIn, EXIT;
	private static String user;

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
	}
	
	public static void initUI() {
		ui = new client.ui.UI();
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

	public static void run() {
		while(!EXIT){
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
						case UiCallObject.SEND_MESSAGE:
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
					fromUI.poll(); //handled, pop the request from queue
				}else{ //response from UI(usually just ignore it)
					
					fromUI.poll(); //handled, pop the request from queue
				}
			}else{ //UI queue is empty
				try{
					Thread.sleep(100); // Some idle time to prevent over using CPU
				} catch(InterruptedException e){}
			}
			//maybe read something from server and push it into the toUI queue if needed here
		}//end of while loop
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

				String msg = Packet.makeMsg(Packet.CONNECT, "Happy New Year!");
				String ret = "";
				out.println(msg);
				int size = in.read(buffer, 0, buffersize);
				if(size>0){
					ret = (new String(buffer)).substring(0, size);
				}
				if(ret.equals(Packet.OK)){
					IsConnected = true;
					System.out.println("Connect OK");
				} else{
					System.out.println("Connect fail");
				}
			} catch(Exception e){
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
				int size = in.read(buffer, 0, buffersize);
				if(size>0){
					ret = (new String(buffer)).substring(0, size);
				}
				if(ret.equals(Packet.OK)){
					System.out.println("Register OK");
					UiCallObject logincall = new LoginCall(registerCall.id, registerCall.pswd);
					login(logincall);
				} else{
					IsConnected = false;
					System.out.println("Register fail");
				}

			} catch(Exception e){
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
			String ret = "";
			try{
				out.println(msg);
				int size = in.read(buffer, 0, buffersize);
				if(size>0){
					ret = (new String(buffer)).substring(0, size);
				}
				if(ret.equals(Packet.OK)){
					IsLoggedIn = true;
					user = loginCall.id;
					System.out.println("Login OK, user = "+user);
				} else if(ret.equals(Packet.FAIL)){
					System.out.println("Login fail");
				} else{
					IsConnected = false;
					System.out.println("Login fail");
				}

			} catch(Exception e){
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
					int size = in.read(buffer, 0, buffersize);
					if(size>0){
						ret = (new String(buffer)).substring(0, size);
					}
					if(ret.equals(Packet.OK)){
						IsLoggedIn = false;
						System.out.println("Logout OK, user = "+user);
					} else{
						System.out.println("Logout fail");
					}

				} catch(Exception e){
					System.out.println("Logout fail");
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
