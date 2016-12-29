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

		while(true){
			
			try{
				Thread.sleep(0); // without this, client behaves very very very wierd
			} catch(InterruptedException e){}
			
			if(fromUI.peek() != null) {
				if(fromUI.peek().type == UiCallObject.REQUEST){
					switch(fromUI.peek().whatCall){
						//handle the cases(usually push something into the toUI queue)
						case UiCallObject.REGISTER:
							System.out.println("Register");
							break;
						case UiCallObject.LOGIN:
							System.out.println("Login");
							break;
						case UiCallObject.LOGOUT:
							break;
						case UiCallObject.EXIT:
							break;
						case UiCallObject.CONNECT_TO_SERVER:
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
}
