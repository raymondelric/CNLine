package client;
import java.util.*; //useful stuff
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
	private static boolean leave, connected;

	public static void main(String[] args) {
		//init queues
		toUI = new LinkedList<UiCallObject>();
		fromUI = new LinkedList<UiCallObject>();

		//starts UI
		ui = new client.ui.UI(toUI, fromUI);
		new Thread(new Runnable(){ public void run(){
			ui.run();
		}
		}).start();

		run();
	}
	
	public static void run() {
		connected = false;
		leave = false;
		while(!leave){
			
			try{
				Thread.sleep(0); // without this, client behaves very very very wierd
			} catch(InterruptedException e){}
			
			if(fromUI.peek() != null) {
				UiCallObject req = fromUI.peek();
				if(req.type == UiCallObject.REQUEST){
					switch(req.whatCall){
						//handle the cases(usually push something into the toUI queue)
						case UiCallObject.REGISTER:
							System.out.println("Register");
							break;
						case UiCallObject.LOGIN:
							System.out.println("Login");
							break;
						case UiCallObject.LOGOUT:
							System.out.println("Logout");
							break;
						case UiCallObject.EXIT:
							System.out.println("Exit");
							leave = true;
							break;
						case UiCallObject.CONNECT_TO_SERVER:
							System.out.println("Connect to server");
							if(connect(req)){
								connected=true;
								toUI.offer(new ConnectResult(true));
							} else{
								toUI.offer(new ConnectResult(false));
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
			}
			//maybe read something from server and push it into the toUI queue if needed here
		}//end of while loop
	}

	public static boolean connect(UiCallObject _req) {
		ConnectCall connectReq = (ConnectCall)_req;
		try{
			sock = new Socket(connectReq.ip, connectReq.port);
			sockwriter = new PrintStream(sock.getOutputStream());
			sockwriter.println("Happy New Year!");
		} catch(Exception e){
			System.out.println("Connect Fail");
			return false;
		}
		System.out.println("Connect Success");
		return true;
	}
}
