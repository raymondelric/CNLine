package client;
import java.util.*; //useful stuff
import java.util.concurrent.*;
import java.io.*; //read write files
import java.net.*; //socket
import java.nio.charset.Charset; //for encoding problems
import client.calls.*;
import client.ui.*;

public class Client{
	private static Queue<UiCallObject> fromUI; //read only
	private static Queue<UiCallObject> toUI; //write only
	private static UI ui;
	private static Socket sock;
	private static PrintStream sockwriter;
	private static boolean leave, connected, loggedin;
	private static String user;

	public static void main(String[] args) {
		//init queues
		toUI = new LinkedBlockingQueue<UiCallObject>();
		fromUI = new LinkedBlockingQueue<UiCallObject>();

		//starts UI
		ui = new client.ui.UI();
		ui.setQueue(toUI, fromUI);
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

		run();
	}
	
	public static void run() {
		connected = false;
		leave = false;
		loggedin = false;
		while(!leave){
			
			
			
			if(!fromUI.isEmpty()) {
				UiCallObject req = fromUI.peek();
				if(req.type == UiCallObject.REQUEST){
					switch(req.whatCall){
						case UiCallObject.REGISTER:
							if(connected){
								System.out.print("Register...");
								if(register(req)){
									toUI.offer(new Result(UiCallObject.REGISTER_RESULT, true));
								} else{
									toUI.offer(new Result(UiCallObject.REGISTER_RESULT, false));
								}
							} else{
								System.out.println("Not connected yet");
							}
							break;
						case UiCallObject.LOGIN:
							if(connected){
								System.out.print("Login...");
								if(login(req)){
									loggedin = true;
									toUI.offer(new Result(UiCallObject.LOGIN_RESULT, true));
								} else{
									toUI.offer(new Result(UiCallObject.LOGIN_RESULT, false));
								}
							} else{
								System.out.println("Not connected yet");
							}
							break;
						case UiCallObject.LOGOUT:
							if(connected){
								if(loggedin){
									if(logout()){
										System.out.println("Logout");
										loggedin = false;
										toUI.offer(new Result(UiCallObject.LOGOUT_RESULT, true));
									} else{
										toUI.offer(new Result(UiCallObject.LOGOUT_RESULT, false));
									}
								} else{
									System.out.println("Not logged in yet");
								}
							} else{
								System.out.println("Not connected yet");
							}
							break;
						case UiCallObject.EXIT:
							System.out.println("Exit");
							if(connected){
								logout();
								connected=false;
							}
							// exit() / sock.close();
							leave = true;
							//toUI.offer();
							break;
						case UiCallObject.CONNECT_TO_SERVER:
							if(connected){
								System.out.println("Already connected");
							} else{
								System.out.print("Connect to server...");
								if(connect(req)){
									connected=true;
									toUI.offer(new Result(UiCallObject.CONNECT_RESULT, true));
								} else{
									toUI.offer(new Result(UiCallObject.CONNECT_RESULT, false));
								}
							}
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

	public static boolean connect(UiCallObject _req) {
		ConnectCall connectReq = (ConnectCall)_req;
		try{
			sock = new Socket(connectReq.ip, connectReq.port);
			sockwriter = new PrintStream(sock.getOutputStream());
			sockwriter.print("Happy New Year!");
		} catch(Exception e){
			System.out.println("fail");
			return false;
		}
		System.out.println("success");
		return true;
	}
	
	public static boolean register(UiCallObject _req) {
		RegisterCall registerReq = (RegisterCall)_req;
		try{
			sockwriter.print("register("+registerReq.id+", "+registerReq.pswd+")");
		} catch(Exception e){
			System.out.println("fail");
			return false;
		}
		System.out.println("success");
		return true;
	}
	
	public static boolean login(UiCallObject _req) {
		LoginCall loginReq = (LoginCall)_req;
		try{
			sockwriter.print("login("+loginReq.id+", "+loginReq.pswd+")");
		} catch(Exception e){
			System.out.println("fail");
			return false;
		}
		user = loginReq.id;
		System.out.println("success, user = "+user);
		return true;
	}
	public static boolean logout() {		
		try{
			sockwriter.print("user "+user+" log out");
		} catch(Exception e){
			System.out.println("fail");
			return false;
		}
		System.out.println("success");
		return true;
	}
}
