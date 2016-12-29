package client.ui;
import java.util.*; //useful stuff
import java.io.*; //read write files
import java.nio.charset.Charset; //for encoding problems
//UI stuff
/*
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
*/
import client.calls.*;

public class UI{
	private Queue<client.calls.UiCallObject> fromMain; //read only
	private Queue<client.calls.UiCallObject> toMain; //write only
	public UI(Queue<client.calls.UiCallObject> toUI, Queue<client.calls.UiCallObject> fromUI){
		fromMain = toUI;
		toMain = fromUI;
	}
	public void run(){
		int id = 1;
		int pswd = -1;
		while(true){
			
			if(id%2==1)
				toMain.offer(new RegisterCall(Integer.toString(id++),Integer.toString(pswd--)));
			else
				toMain.offer(new LoginCall(Integer.toString(id++),Integer.toString(pswd--)));

			try {
				Thread.sleep(1000);
			} catch(InterruptedException e){}

			if(fromMain.peek() != null){ //has stuff to do
				if(fromMain.peek().type == UiCallObject.REQUEST){
					switch(fromMain.peek().whatCall){
						//handle the cases (usually push something into the toMain queue)
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
					fromMain.poll(); //handled, pop the request from queue
				}else{ //response from Main (usually show something on screen)
					
					fromMain.poll(); //handled, pop the request from queue
				}
			}
		}//end of while loop
	}
}
