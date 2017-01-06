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
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.geometry.Point3D;
import javafx.scene.control.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.event.*;
import javafx.scene.layout.*;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
*/
import client.calls.*;

//public class UI extends Application{
public class UI{
	//public static String css = UI.class.getResource("/client/ui/style/style.css").toExternalForm();
	private static Queue<client.calls.UiCallObject> fromMain; //read only
	private static Queue<client.calls.UiCallObject> toMain; //write only
	public int i;
	//public MainScreen s;
	//public SplashScreen splash;
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
				toMain.offer(new RegisterCall(Integer.toString(id),Integer.toString(pswd)));
			else if(id==4)
				toMain.offer(new LogoutCall());
			else if(id==5)
				toMain.offer(new RegisterCall(Integer.toString(id),Integer.toString(pswd)));
			else if(id==6){
				String[] ids = new String[2];
				ids[0] = "3";
				ids[1] = "5";
				toMain.offer(new RoomCall(ids));
			}
			else if(id==7){
				String rid = "4";
				String data = "messsssssssssssage";
				toMain.offer(new MessageCall(rid, data));
			}

			try {
				Thread.sleep(200);
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
/*
	@Override
    public void start(Stage mainWindow) throws Exception{
    	System.out.println("starting UI...");
    	splash = new SplashScreen();
    	s = new MainScreen();
    	i = 0;
    	alert(splash, "Test Alert", "Got it!");
    	//the main loop of handling stuff
    	Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	i++;
		        System.out.println("this is called every second on UI thread");
		        s.msgG.addBack(0,"usr1","Message"+i+" back");
		        splash.printMsg("I am message "+i+"...");
		    }
		}));
		fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
		fiveSecondsWonder.play();
    }


	public void run(String[] args) {
        launch(args);
        System.out.println("UI has ended");
        toMain.offer(new ExitCall());
    }

    private void alert(Stage parent, String msg, String btnMsg){
		
    	Stage alertBox = new Stage();
    	alertBox.initModality(Modality.APPLICATION_MODAL);
        alertBox.initStyle(StageStyle.TRANSPARENT);
        alertBox.setTitle("Uh-Oh");
        alertBox.initOwner(parent);
        VBox layout = new VBox(16);
        layout.setFillWidth(true);
     	layout.setId("AlertBg");
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20,50,20,50));
        Label message = new Label(msg);
        message.setId("AlertText");
        message.setMinWidth(Region.USE_PREF_SIZE);
        layout.getChildren().add(message);
        Button btn = new Button(btnMsg);
        btn.setOnAction(event -> {
        	splash.toggleMode(-1);
        	ScaleTransition sa = new ScaleTransition(Duration.millis(200), layout);
			sa.setFromX(1.0);
			sa.setToX(0.0);
			sa.setFromY(1.0);
			sa.setToY(0.0);
			sa.setCycleCount(1);
			sa.setInterpolator(Interpolator.EASE_OUT);
			sa.play();
            sa.setOnFinished(saEvent -> {alertBox.close();});
        });
        layout.getChildren().add(btn);
        Scene scene = new Scene(layout);
        ScaleTransition sa = new ScaleTransition(Duration.millis(200), layout);
		sa.setFromX(0.0);
		sa.setToX(1.0);
		sa.setFromY(0.0);
		sa.setToY(1.0);
		sa.setCycleCount(1);
		sa.setInterpolator(Interpolator.EASE_OUT);
		sa.play();
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(UI.css);
        CommonUi.setDrag(layout, alertBox);
        alertBox.setScene(scene);
        alertBox.show();
    }
    public static void pushIn(client.calls.UiCallObject call){
    	toMain.offer(call);
    }
}
class SplashScreen extends Stage{
	private FadeTransition ft;
	private Label welcomeMsg;
	private VBox loginBox;
	private VBox layout;
	private int mode;
	private LoadingIcon li;
	public SplashScreen(){
		this.initModality(Modality.NONE);
        this.initStyle(StageStyle.TRANSPARENT);
        this.setTitle("Welcome");
        mode = 0;
        li = new LoadingIcon();
        layout = new VBox(8);
        layout.setFillWidth(true);
     	layout.setMinWidth(400);
     	layout.setMinHeight(300);
     	layout.setBackground(new Background(CommonUi.bg));
        layout.setPadding(new Insets(10,10,10,10));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(li);

        welcomeMsg = new Label("Welcome");
        welcomeMsg.setId("Welcome");
        layout.getChildren().add(welcomeMsg);

        loginBox = new VBox(8);
        HBox accountWrapper = new HBox(8);
        accountWrapper.getChildren().addAll(new Label("account "), new TextField());
        HBox passwordWrapper = new HBox(8);
        passwordWrapper.getChildren().addAll(new Label("password "), new PasswordField());
        HBox buttonsWrapper = new HBox(8);
        buttonsWrapper.getChildren().addAll(new Button("Login"), new Button("Register"));
        loginBox.getChildren().addAll(accountWrapper, passwordWrapper, buttonsWrapper);

        Scene scene = new Scene(layout);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(UI.css);

        ft = new FadeTransition(Duration.millis(1000), layout);
		ft.setFromValue(0.0);
	    ft.setToValue(1.0);
	    ft.setCycleCount(1);
		ft.play();
		CommonUi.setDrag(layout, this);
        this.setScene(scene);
        this.centerOnScreen();
        this.setAlwaysOnTop(true);
        this.show();
	}
	public void printMsg(String s){
		if(s!=null){
			welcomeMsg.setText(s);
		}
	}
	public void toggleMode(int _mode){
		boolean changed = true;
		changed = (mode!=_mode)? true:false;
		if(changed){
			switch(_mode){
				case 0:
					mode = 0;
				break;
				case 1:
					mode = 1;
				break;
				default:
					if(mode == 0){
						mode = 1;
					}else{
						mode = 0;
					}
				break;
			}
			if(mode == 0){
				//do something
				layout.getChildren().remove(loginBox);
				layout.getChildren().add(welcomeMsg);
				li.start();
			}else{
				//do something
				layout.getChildren().remove(welcomeMsg);
				layout.getChildren().add(loginBox);
				li.stop();
			}
		}
	}
}
class MainScreen extends Stage{
	public MessageGroup msgG;
	
	public MainScreen(){
		this.initModality(Modality.NONE);
        this.initStyle(StageStyle.UTILITY);
        this.setTitle("Debug Master");

        VBox layout = new VBox(8);
        layout.setFillWidth(true);
     	layout.setMinWidth(600);
        layout.setPadding(new Insets(10,10,10,10));
        //layout.getChildren().add(new Label("Press a button"));
        //layout.getChildren().add(new LoadingIcon());
        msgG = new MessageGroup();
        HBox typeArea = new HBox(8);
        TextArea typing = new TextArea ();
        typing.setPrefWidth(CommonUi.WIDTH*0.80);
        Button sendBtn = new Button("Send\nMessage");
        sendBtn.setPrefWidth(CommonUi.WIDTH*0.18);
        sendBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        Button fileBtn = new Button("Send some file(s)...");
        fileBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        typeArea.getChildren().addAll(typing, sendBtn);
        layout.getChildren().addAll(msgG,typeArea, fileBtn);
        layout.setBackground(new Background(CommonUi.bg));
        Scene scene = new Scene(layout,CommonUi.WIDTH+30,820);
        scene.getStylesheets().add(UI.css);
        this.setScene(scene);
        this.show();
        int i = 0;
        while(i<5){
			msgG.addFront(0,"usr1","Message"+i+" front");
			i++;
        }
        
	}
}

class LoadingIcon extends Group{
	private RotateTransition stop_animation;
	private RotateTransition animation;
	private ImageView mailIconView;
	static private Image mailIcon = new Image("/client/ui/sprites/mailIcon.png");
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
	public void stop(){
		animation.stop();
		animation.setCycleCount(1);
		animation.playFrom(animation.getCurrentTime());
	}
	public void start(){
		animation.setCycleCount(Animation.INDEFINITE);
		animation.play();
	}
}

class MessageGroup extends Group{
	private ScrollPane sp;
	private VBox msgWrapper;
	private LinkedList<MessageBubble> msgBubs;
	public MessageGroup(){
		msgWrapper = new VBox(8);
		msgWrapper.setFillWidth(true);
     	msgWrapper.setPrefWidth(450);
     	
		sp = new ScrollPane();
		sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		sp.setPrefHeight(600);
		sp.setFitToWidth(true);
 		sp.setContent(msgWrapper);
 		sp.setPannable(true);
 		this.getChildren().add(sp);
 		System.out.println("msgWrapper:"+msgWrapper.getWidth());
	}
	public void addFront(int type, String userName, String msgContent){
		msgWrapper.getChildren().add(0,new FileBubble(userName, "陳菊.avi"));
		msgWrapper.getChildren().add(0,new MessageBubble(userName, msgContent,false));
		System.out.println("ADD: "+msgContent);
	}
	public void addBack(int type, String userName, String msgContent){
		boolean scrolldown = (sp.getVvalue()>=0.5d)? true:false;
		msgWrapper.getChildren().add(new MessageBubble(userName, msgContent,true));
		msgWrapper.getChildren().add(new LogBubble(userName, true));
		msgWrapper.getChildren().add(new LogBubble(userName, false));
		System.out.println("ADD: "+msgContent);
		if(scrolldown){
			this.layout();
			sp.setVvalue(1.0d);
		}
	}
}
class MessageBubble extends Group{
	private ScaleTransition sa;
	private TranslateTransition st;
	private FadeTransition ft;
	static private Image newMsgImage = new Image("/client/ui/sprites/new.png");
	static private BackgroundFill bg = new BackgroundFill(Color.web("#ff6688"), new CornerRadii(10),new Insets(0));
	public MessageBubble(String usr, String s, boolean newMsg){
		Label u = new Label(usr);
		Label l = new Label(s);
		l.setWrapText(true);
		l.setPrefWidth(CommonUi.WIDTH*0.6);
		l.setFont(new Font("Arial", 16));
		l.setTextFill(Color.web("#FFFFFF"));
		l.setBackground(new Background(bg));
		l.setTranslateX(9);
		l.setTranslateY(16);
		l.setMinHeight(20);
		l.setPadding(new Insets(5,10,5,10));
		u.setWrapText(false);
		u.setPrefWidth(CommonUi.WIDTH*0.6);
		u.setTranslateX(9);
		u.setFont(new Font("Arial", 12));
		u.setTextFill(Color.web("#000000"));
		Polygon polygon = new Polygon();
		polygon.getPoints().addAll(new Double[]{
		    0.0, 13.0,
		    10.0, 10.0,
		    10.0, 16.0 });
		polygon.setTranslateY(12);
		polygon.setFill(bg.getFill());
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
			newMsgImg.setTranslateX(CommonUi.WIDTH*0.6-6);
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
class FileBubble extends Group{
	ScaleTransition sa;
	public FileBubble(String usr, String filename){
		VBox wrapper = new VBox(4);
		wrapper.setFillWidth(true);
		wrapper.setAlignment(Pos.CENTER);
		wrapper.setBackground(new Background(CommonUi.blackBg));
		wrapper.setPrefWidth(CommonUi.WIDTH);
		wrapper.setPadding(new Insets(2,2,2,2));
		Label u = new Label(usr + " has sent a file:" + filename);
		u.setWrapText(true);
		u.setFont(new Font("Arial", 14));
		u.setTextFill(Color.web("#FFFFFF"));
		u.setTextAlignment(TextAlignment.CENTER);
		u.setContentDisplay(ContentDisplay.CENTER);
		Button recv = new Button("Download it!");
		//recv.setStyle();
		wrapper.getChildren().addAll(u, recv);
		this.getChildren().add(wrapper);
		sa = new ScaleTransition(Duration.millis(200), wrapper);
		sa.setFromY(0.0);
		sa.setToY(1.0);
		sa.setCycleCount(1);
		sa.setInterpolator(Interpolator.EASE_OUT);
		sa.play();
		System.out.println("FileBubbleWrapper:"+wrapper.getWidth());
	}
}
class LogBubble extends Group{
	ScaleTransition sa;
	public LogBubble(String usr, boolean login){
		VBox wrapper = new VBox(4);
		wrapper.setFillWidth(true);
		wrapper.setAlignment(Pos.CENTER);
		wrapper.setPrefWidth(CommonUi.WIDTH);
		wrapper.setBackground(new Background(CommonUi.blackBg));
		wrapper.setPadding(new Insets(2,2,2,2));
		Label u = new Label(usr + " has " + ((login)? "joined the chatroom":"left"));
		u.setWrapText(true);
		u.setFont(new Font("Arial", 14));
		u.setTextFill(Color.web("#FFFFFF"));
		u.setTextAlignment(TextAlignment.CENTER);
		u.setContentDisplay(ContentDisplay.CENTER);
		
		wrapper.getChildren().add(u);
		this.getChildren().add(wrapper);
		sa = new ScaleTransition(Duration.millis(200), wrapper);
		sa.setFromY(0.0);
		sa.setToY(1.0);
		sa.setCycleCount(1);
		sa.setInterpolator(Interpolator.EASE_OUT);
		sa.play();
	}
}
class CommonUi{
	public static int WIDTH = 450;
	public static BackgroundFill blackBg = new BackgroundFill(Color.web("rgba(0,0,0,0.5)"), new CornerRadii(0),new Insets(0));
	public static BackgroundImage bg = new BackgroundImage(new Image("/client/ui/sprites/bg.png"),
		BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
	public static void setDrag(Node drag, Stage target){
		Delta dragDelta = new Delta();
		drag.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = target.getX() - mouseEvent.getScreenX();
                dragDelta.y = target.getY() - mouseEvent.getScreenY();
            }
        });
        drag.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                target.setX(mouseEvent.getScreenX() + dragDelta.x);
                target.setY(mouseEvent.getScreenY() + dragDelta.y);
            }
        });
	}
	private static class Delta { double x, y; }
*/
}
