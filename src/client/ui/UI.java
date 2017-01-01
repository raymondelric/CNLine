package client.ui;
import java.util.*; //useful stuff
import java.io.*; //read write files
import java.nio.charset.Charset; //for encoding problems
//UI stuff

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
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.geometry.Point3D;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import client.calls.*;
import javafx.scene.layout.*;


public class UI extends Application{
	private Queue<client.calls.UiCallObject> fromMain; //read only
	private Queue<client.calls.UiCallObject> toMain; //write only
	//Testing code without GUI
	public void runTest(){
		int id = 1;
		int pswd = -1;
		boolean connected = false, loggedin = false;
		while(true){
			id++;
			pswd--;
			if(id%5==0)
				toMain.offer(new RegisterCall(Integer.toString(id),Integer.toString(pswd)));
			else if(id%5==1)
				toMain.offer(new LoginCall(Integer.toString(id),Integer.toString(pswd)));
			else if(id%5==2)
				toMain.offer(new LogoutCall());
			else if(id%5==3)
				toMain.offer(new ConnectCall("140.112.30.52",6655)); // oasis2
			else
				toMain.offer(new ConnectCall("140.112.30.52",5566)); // oasis2

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
						case UiCallObject.CONNECT_RESULT:
							Result connectRes = (Result)req;
							connected = connectRes.result;
							System.out.println(connected);
							break;
						case UiCallObject.REGISTER_RESULT:
							Result registerRes = (Result)req;
							boolean registered = registerRes.result;
							System.out.println(registered);
							break;
						case UiCallObject.LOGIN_RESULT:
							Result loginRes = (Result)req;
							loggedin = loginRes.result;
							System.out.println(loggedin);
							break;
						case UiCallObject.LOGOUT_RESULT:
							Result logoutRes = (Result)req;
							boolean loggedout = logoutRes.result;
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
	@Override
    public void start(Stage mainWindow) throws Exception{
    	Stage s = new DebugMaster();
    }


	public void run(String[] args) {
        launch(args);
    }
}

class DebugMaster extends Stage{
	MessageGroup msgG;
	public DebugMaster(){
		this.initModality(Modality.NONE);
        this.initStyle(StageStyle.UTILITY);
        this.setTitle("Debug Master");

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10,10,10,10));
        layout.setVgap(8);
        layout.setHgap(10);
        layout.add(new Label("Press a button"), 0, 0);
        layout.add(new LoadingIcon(), 0, 1);
        msgG = new MessageGroup();
        layout.add(msgG, 0, 2);
        Scene scene = new Scene(layout);
        this.setScene(scene);
        this.show();
        int i = 0;
        while(i<5){
			msgG.addBack(0,"usr1","Message"+i+" back");
			msgG.addFront(0,"usr1","Message"+i+" front");
			i++;
        }
	}
}

class LoadingIcon extends Group{
	RotateTransition animation;
	ImageView mailIconView;
	static Image mailIcon = new Image("/client/ui/sprites/mailIcon.png");
	public LoadingIcon(){
		mailIconView = new ImageView(mailIcon);
		mailIconView.setTranslateX(-mailIconView.getFitWidth()/2);
		mailIconView.setTranslateY(-mailIconView.getFitHeight()/2);
		this.getChildren().add(mailIconView);
		animation = new RotateTransition(Duration.millis(1500), this);
        animation.setAxis(new Point3D( 0.0, 1.0, 0.0));
        animation.setCycleCount(Animation.INDEFINITE);
        animation.setFromAngle(0);
        animation.setToAngle(360);
        animation.setInterpolator(Interpolator.LINEAR);
        animation.play();
	}
}

class MessageGroup extends Group{
	ScrollPane sp;
	VBox msgWrapper;
	LinkedList<MessageBubble> msgBubs;
	public MessageGroup(){
		msgWrapper = new VBox(8);
     	msgWrapper.setPrefWidth(300);
		sp = new ScrollPane();
		sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		sp.setPrefSize(300, 500);
 		sp.setContent(msgWrapper);
 		this.getChildren().add(sp);
	}
	public void addFront(int type, String userName, String msgContent){
		msgWrapper.getChildren().add(0,new MessageBubble(userName, msgContent,false));
		System.out.println("ADD: "+msgContent);
	}
	public void addBack(int type, String userName, String msgContent){
		msgWrapper.getChildren().add(new MessageBubble(userName, msgContent,true));
		System.out.println("ADD: "+msgContent);
	}
}
class MessageBubble extends Group{
	ScaleTransition sa;
	TranslateTransition st;
	FadeTransition ft;
	static Image newMsgImage = new Image("/client/ui/sprites/new.png");
	static BackgroundFill bg = new BackgroundFill(Color.web("#ff6688"), new CornerRadii(10),new Insets(0));
	public MessageBubble(String usr, String s, boolean newMsg){
		Label u = new Label(usr);
		Label l = new Label(s);
		l.setWrapText(true);
		l.setPrefWidth(150);
		l.setFont(new Font("Arial", 16));
		l.setTextFill(Color.web("#FFFFFF"));
		l.setBackground(new Background(bg));
		l.setTranslateX(9);
		l.setTranslateY(16);
		l.setMinHeight(20);
		l.setPadding(new Insets(5,10,5,10));
		u.setWrapText(false);
		u.setPrefWidth(150);
		u.setTranslateX(9);
		u.setFont(new Font("Arial", 12));
		u.setTextFill(Color.web("#000000"));
		Polygon polygon = new Polygon();
		polygon.getPoints().addAll(new Double[]{
		    0.0, 13.0,
		    10.0, 10.0,
		    10.0, 16.0 });
		polygon.setTranslateY(12);
		polygon.setFill(Color.web("#ff6688"));
		this.getChildren().add(polygon);
		this.getChildren().add(l);
		this.getChildren().add(u);
		sa = new ScaleTransition(Duration.millis(200), l);
		sa.setFromX(0.0);
		sa.setToX(1.0);
		sa.setCycleCount(1);
		sa.setInterpolator(Interpolator.EASE_OUT);
		st = new TranslateTransition(Duration.millis(200), l);
		st.setFromX(-l.getPrefWidth()/2+l.getTranslateX());
		st.setToX(l.getTranslateX());
		st.setCycleCount(1);
		st.setInterpolator(Interpolator.EASE_OUT);
		sa.play();
		st.play();
		if(newMsg){
			ImageView newMsgImg = new ImageView(newMsgImage);
			newMsgImg.setTranslateX(144);
			newMsgImg.setTranslateY(8);
			this.getChildren().add(newMsgImg);
			ft = new FadeTransition(Duration.millis(1000), newMsgImg);
			ft.setFromValue(1.0);
		    ft.setToValue(0.0);
		    ft.setCycleCount(1);
		    ft.setDelay(Duration.millis(2000));
			ft.play();	
		}
		
	}
}