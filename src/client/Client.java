package client;
import java.util.*; //useful stuff
import java.io.*; //read write files
import java.net.*; //socket
import java.nio.charset.Charset; //for encoding problems
import client.calls.*;
import client.ui.*;

public class Client{
	private static Queue<UiCallObject> fromUI; //read only
	private static  Queue<UiCallObject> toUI; //write only
	private static  client.ui.UI ui;

	public static void main(String[] args) {

		//init queues
		toUI = new LinkedList<UiCallObject>();
		fromUI = new LinkedList<UiCallObject>();
		//starts UI
		ui = new client.ui.UI(toUI, fromUI);

		while(true){
			if(fromUI.peek() != null){//has stuff to do
				if(fromUI.peek().type == UiCallObject.REQUEST){
					switch(fromUI.peek().whatCall){
						//handle the cases(usually push something into the toUI queue)
						case UiCallObject.REGISTER:
						break;
						case UiCallObject.LOGIN:
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