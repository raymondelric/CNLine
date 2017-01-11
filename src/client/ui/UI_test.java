package client.ui;
import java.util.*; //useful stuff
import java.io.*; //read write files
import java.nio.charset.Charset; //for encoding problems
import client.calls.*;

public class UI_test{
	private static Queue<client.calls.UiCallObject> fromMain; //read only
	private static Queue<client.calls.UiCallObject> toMain; //write only
	public int i;
	//Testing code without GUI
	public void runTest(){
		int id = 1;
		int pswd = -1;
		//boolean connected = false, loggedin = false;
		while(true){
			id++;
			pswd--;
			
			if(id==100)
				toMain.offer(new ExitCall());
			else if(id==2)
				toMain.offer(new ConnectCall("127.0.0.1",9000));
			else if(id==3)
				toMain.offer(new LoginCall(Integer.toString(5),Integer.toString(5)));
			else if(id==4){
				String rid = "1";
				String data = "1111";
				toMain.offer(new MessageCall(rid, data));
			}
			else if(id==5){
				String[] ids = new String[1];
				ids[0] = "5";
				toMain.offer(new RoomCall(ids));
			}
			else if(id==6){
				String[] ids = new String[1];
				ids[0] = "5";
				toMain.offer(new RoomCall(ids));
			}
			else if(id==7){
				String[] ids = new String[1];
				ids[0] = "5";
				toMain.offer(new RoomCall(ids));
			}


			else if(id==8){
				String rid = "3";
				String data = "3333";
				toMain.offer(new MessageCall(rid, data));
			}

			try {
				Thread.sleep(1000);
			} catch(InterruptedException e){}

			if(fromMain.peek() != null){ //has stuff to do
				UiCallObject req = fromMain.peek();
				if(req.type == UiCallObject.REQUEST){
					switch(req.whatCall){
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
					switch(req.whatCall){
						case UiCallObject.RESULT_CONNECT:
							Result connectRes = (Result)req;
							int connected = connectRes.result;
							System.out.println(connected);
							break;
						case UiCallObject.RESULT_REGISTER:
							Result registerRes = (Result)req;
							int registered = registerRes.result;
							System.out.println(registered);
							break;
						case UiCallObject.RESULT_LOGIN:
							Result loginRes = (Result)req;
							int loggedin = loginRes.result;
							System.out.println(loggedin);
							break;
						case UiCallObject.RESULT_LOGOUT:
							Result logoutRes = (Result)req;
							int loggedout = logoutRes.result;
							System.out.println(loggedout);
							break;
					}
					fromMain.poll(); //handled, pop the request from queue
				}
			}
		}//end of while loop
	}

	public void setQueue(Queue<client.calls.UiCallObject> toUI, Queue<client.calls.UiCallObject> fromUI){
		fromMain = toUI;
		toMain = fromUI;
	}

}

