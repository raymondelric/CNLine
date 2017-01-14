package client.ui;
import java.util.*; //useful stuff
import java.util.regex.*;
import java.io.*; //read write files
import java.nio.charset.Charset; //for encoding problems
//UI stuff

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
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
import javafx.stage.FileChooser;
import javafx.scene.layout.*;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.beans.value.*;

import client.calls.*;

public class UI extends Application{
	public static String css = UI.class.getResource("/client/ui/style/style.css").toExternalForm();
	private static Queue<client.calls.UiCallObject> fromMain; //read only
	private static Queue<client.calls.UiCallObject> toMain; //write only
	private boolean test;
	public int i;
	public static Vector<RoomScreen> s;
	public SplashScreen splash;
	public DebugScreen debug;
	public static String id;
	private static Vector<PendingPair> checkingUid = new Vector<PendingPair>();
	//Testing code without GUI
	public void runTest(){
		int id = 1;
		int pswd = -1;	
	}

	public void setQueue(Queue<client.calls.UiCallObject> toUI, Queue<client.calls.UiCallObject> fromUI){
		fromMain = toUI;
		toMain = fromUI;
	}

	@Override
    public void start(Stage mainWindow) throws Exception{
    	System.out.println("starting UI...");
    	s = new Vector<RoomScreen>();
    	splash = new SplashScreen();
    	//debug purpose only
    	//debug = new DebugScreen(fromMain);
    	BufferedReader br;
    	try {
    		br = new BufferedReader(new FileReader("./server.in"));
	    
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        if (line != null) {
	        	String[] server = line.split(":");
	        	if(server.length >= 2){
		            UI.pushIn(new ConnectCall(server[0],Integer.parseInt(server[1])) );
		        }else{
		        	throw new IOException();
		        }
	        }else{
	        	throw new IOException();
	        }
	        br.close();
	    } catch(Exception e){
	    	alert(splash,"server.in file corrupted", "QAQ");
	    }
    	

    	//the main loop of handling stuff
    	Timeline looper = new Timeline(new KeyFrame(Duration.millis(300), new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	//UI.pushIn(new ConnectCall("140.112.30.52",6655));
		        //System.out.println("this is called every second on UI thread");
		        //s.msgG.addBack(0,"usr1","Message"+i+" back");
		        //splash.printMsg("I am message "+i+"...");
		        if(!fromMain.isEmpty()) {
				UiCallObject call = fromMain.peek();
				//System.out.println("[UI] recieve something...");
				if(call.type == UiCallObject.RESPOND){
					switch(call.whatCall){
						case UiCallObject.CONNECT_TO_SERVER:
							if(call.success){
								splash.toggleMode(1);
							}else{
								alert(splash, "Cannot connect to server.", "OK");
								for(RoomScreen rs : s){
									rs.close();
								}
								s.clear();
								splash.printMsg("Connection Fail",1);
								splash.toggleMode(0);
							}
							break;
						case UiCallObject.REGISTER:
							RegisterCall rc = (RegisterCall)call;
							if(call.success){
								alert(splash, "Hi, "+rc.id+" you are a new user!", "Yes");
								if(splash.roomsBox == null){
									splash.roomsBox = new RoomBox(rc.id);
								}
								//System.out.println("RegisterRoombox");
								id = rc.id;
								splash.toggleMode(2);
							}else{
								alert(splash, "Register Fail", "QWQ");
								for(RoomScreen rs : s){
									rs.close();
								}
								s.clear();
								splash.toggleMode(1);
							}
							break;
						case UiCallObject.LOGIN:
							LoginCall lc = (LoginCall)call;
							if(call.success){
								if(splash.roomsBox == null){
									splash.roomsBox = new RoomBox(lc.id);
								}
								//System.out.println("LoginRoombox");
								splash.toggleMode(2);
								id = lc.id;
								//call InRoomCalls
								for(String roomid : lc.rids){
									pushIn(new InRoomCall(roomid));
								}
							}else{
								alert(splash, "Login Fail", "QWQ");
								for(RoomScreen rs : s){
									rs.close();
								}
								s.clear();
								splash.toggleMode(1);
							}
							break;
						case UiCallObject.LOGOUT:
								for(RoomScreen rs : s){
									rs.close();
								}
								s.clear();
								splash.toggleMode(1);
								splash.roomsBox = null;
							break;
						case UiCallObject.CREATE_ROOM:
								alert(splash, "New Room Created!", "OK");
								RoomCall roomc = (RoomCall)call;
								s.add(new RoomScreen(roomc.rid, 0));
								splash.roomsBox.addRoom(roomc.rid, roomc.ids);
								splash.sizeToScene();
							break;
						case UiCallObject.CHECK_EXIST_ID:
								CheckIDCall cic = (CheckIDCall)call;
							for(PendingPair pp : checkingUid){
								if(pp.id.equals(cic.id)){
									if(call.success){
										pp.pendingFrame.exist();
									}else{
										pp.pendingFrame.noExist();
									}
									checkingUid.remove(pp);
									break;
								}
							}
							break;
						case UiCallObject.PEOPLE_IN_ROOM:
							InRoomCall irc = (InRoomCall)call;
							if(irc.success){
								s.add(new RoomScreen(irc.rid, irc.historySize));
								splash.roomsBox.addRoom(irc.rid, irc.ids);
								splash.sizeToScene();
							}
							break;
						case UiCallObject.SEND_MESSAGE:
							
							break;
						case UiCallObject.GET_MESSAGE:
							GetMessageCall gmc = (GetMessageCall)call;
							//System.out.println("[UI] a message!..."+gmc.rid+"?!");
							for(RoomScreen rs : s){
								if(gmc.rid.equals(rs.roomId)){
									if(gmc.msgid == 0){
										System.out.println("[UI] new message");
										rs.msgG.addBack(0, gmc.uid, gmc.message);
									}else{
										System.out.println("[UI] old message");
										rs.msgG.addFront(0, gmc.uid, gmc.message);
									}
								}
							}
							break;
						case UiCallObject.SEND_FILE:

							break;
						case UiCallObject.GET_FILE:
							GetFileCall gfc = (GetFileCall)call;
							for(RoomScreen rs : s){
								if(gfc.rid.equals(rs.roomId)){
									rs.msgG.addBack(2, gfc.uid, gfc.filename);
									break;
								}
							}
							break;
						case UiCallObject.DOWNLOAD_FILE:
							if(call.success){
								alert(splash, "File Downloaded", "\\OWO/");
							}else{
								alert(splash, "Download Fail", "QWQ");
							}
							break;

						case UiCallObject.SOMEONE_LOGIN_OUT:
							LogNotifyCall lic  = (LogNotifyCall)call;
							for(RoomScreen rs : s){
								rs.logEvent(lic.id, lic.login);
							}
						break;
						default:
							System.out.println("unidentified call number from Main to UI");
							break;
					}
					System.out.println("POLL");
					fromMain.poll();
				}else{
					System.out.println("UI get request?!!");
					fromMain.poll();
				}
			}


		    }
		}));
		looper.setCycleCount(Timeline.INDEFINITE);
		looper.play();
    }


	public void run(String[] args) {
        launch(args);
        System.out.println("UI has ended");
        toMain.offer(new ExitCall());
    }

    public static void alert(Stage parent, String msg, String btnMsg){		
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
        	//splash.toggleMode(-1);
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
    public static void pending(PendingPair p){
    	checkingUid.add(p);
    }
    
}
class SplashScreen extends Stage{
	private boolean connected;
	private FadeTransition ft;
	private Label welcomeMsg;
	private VBox loginBox;
	public RoomBox roomsBox;
	private VBox layout;
	private int mode;
	private LoadingIcon li;
	public SplashScreen(){
		roomsBox = null;
		connected = false;
		this.initModality(Modality.NONE);
        this.initStyle(StageStyle.TRANSPARENT);
        this.setTitle("CNLine");
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

        welcomeMsg = new Label("CN Line");
        welcomeMsg.setId("Welcome");
        layout.getChildren().add(welcomeMsg);

        loginBox = new VBox(8);
        HBox accountWrapper = new HBox(8);
        accountWrapper.setAlignment(Pos.CENTER);
        TextField accountTxt = new TextField();
        PasswordField passTxt = new PasswordField();
        accountWrapper.getChildren().addAll(new WhiteLabel("account "), accountTxt);
        HBox passwordWrapper = new HBox(8);
        passwordWrapper.setAlignment(Pos.CENTER);
        passwordWrapper.getChildren().addAll(new WhiteLabel("password "), passTxt);
        HBox buttonsWrapper = new HBox(8);
        buttonsWrapper.setAlignment(Pos.CENTER);
        Button loginBtn = new Button("Login"), registerBtn = new Button("Register");
        loginBtn.setOnAction(event0->{
        	System.out.println("Login: "+accountTxt.getText()  +" password: "+passTxt.getText());
        	if(CommonUi.validateID(accountTxt.getText()) && CommonUi.validateID(passTxt.getText())){
        		UI.pushIn(new LoginCall(accountTxt.getText(),passTxt.getText()));
        		printMsg("Trying to log in...",0);
        		toggleMode(0);
        	}else{
        		UI.alert(this, "Don't enter wierd ID or Password, please.", "Got it");
        	}
        	
        });
        registerBtn.setOnAction(event1->{
        	System.out.println("Register: "+accountTxt.getText()  +" password: "+passTxt.getText());
        	if(CommonUi.validateID(accountTxt.getText()) && CommonUi.validateID(passTxt.getText())){
        		UI.pushIn(new RegisterCall(accountTxt.getText(),passTxt.getText()));
        		toggleMode(0);
        		printMsg("Trying to register and log in...",0);
        	}else{
        		UI.alert(this, "Don't enter wierd ID or Password, please.", "Got it");
        	}
        });
        buttonsWrapper.getChildren().addAll(loginBtn, registerBtn);


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
        this.show();
        this.setAlwaysOnTop(true);
        toggleMode(0);
	}
	public void printMsg(String s, int button){
		if(s!=null){
			welcomeMsg.setText(s);
			if(button == 1){
				li.stop();
			}
		}
	}
	public void toggleMode(int _mode){

		boolean changed = true;
		changed = (mode!=_mode)? true:false;
		if(changed){
			Node rm, ad;
			switch(mode){
				case 0:
					rm = welcomeMsg;
				break;
				case 1:
					rm = loginBox;
				break;
				case 2:
					rm = roomsBox;
				break;
				default:
				rm = welcomeMsg;
				break;
			}
			switch(_mode){
				case 0:
					mode = 0;
				break;
				case 1:
					mode = 1;
				break;
				case 2:
					mode = 2;
				break;
				default:
					if(mode == 0){
						mode = 1;
					}else{
						mode = 0;
					}
				break;
			}
			switch(mode){
				case 0:
					ad = welcomeMsg;
					this.setAlwaysOnTop(true);
					li.start();
				break;
				case 1:
					ad = loginBox;
					this.setAlwaysOnTop(true);
					li.stop();
				break;
				case 2:
					ad = roomsBox;
					this.setAlwaysOnTop(false);
					li.stop();
				break;
				default:
					ad = welcomeMsg;
				break;
			}
			ScaleTransition sa;
			sa = new ScaleTransition(Duration.millis(200), rm);
			sa.setFromY(1.0);
			sa.setToY(0.0);
			sa.setCycleCount(1);
			sa.setInterpolator(Interpolator.EASE_OUT);
			sa.play();
			sa.setOnFinished(event -> {
				layout.getChildren().remove(rm);
				ScaleTransition saAdd = new ScaleTransition(Duration.millis(200), ad);
				saAdd.setFromY(0.0);
				saAdd.setToY(1.0);
				saAdd.setCycleCount(1);
				saAdd.setInterpolator(Interpolator.EASE_OUT);
				saAdd.play();
				layout.getChildren().add(ad);
				SplashScreen.this.sizeToScene();
			});
		}
	}
}
class RoomScreen extends Stage{
	public String[] usrInRoom;
	public MessageGroup msgG;
	public final String roomId;
	public RoomScreen(String _rid, int _historysize){
		roomId = _rid;
		usrInRoom = null;
		this.initModality(Modality.NONE);
        this.initStyle(StageStyle.TRANSPARENT);
        this.setTitle("Chatroom");

        VBox layout = new VBox(8);
        layout.setFillWidth(true);
     	layout.setMinWidth(600);
        layout.setPadding(new Insets(10,10,10,10));
        Rectangle ghost = new Rectangle();
		ghost.setWidth(10);
		ghost.setHeight(20);
		ghost.setFill(Color.web("rgba(0,0,0,0)"));
        //layout.getChildren().add(new Label("Press a button"));
        //layout.getChildren().add(new LoadingIcon());
        msgG = new MessageGroup(_historysize, this);
        HBox typeArea = new HBox(8);
        TextArea typing = new TextArea ();
        typing.setPrefWidth(CommonUi.WIDTH*0.80);
        Button sendBtn = new Button("Send\nMessage");
        sendBtn.setPrefWidth(CommonUi.WIDTH*0.18);
        sendBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        sendBtn.setOnAction(event->{
        	if(!typing.getText().equals("")){
	        	//msgG.addBack(1, UI.id, typing.getText());
	        	UI.pushIn(new MessageCall(roomId, typing.getText()));
	        	typing.clear();
	        }
        });
        Button fileBtn = new Button("Send some file(s)...");
        fileBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        fileBtn.setOnAction(event -> {
        	Stage opener = new Stage();
			opener.initModality(Modality.APPLICATION_MODAL);
        	opener.initStyle(StageStyle.UTILITY);
        	FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Send Files");
            List<File> list = fileChooser.showOpenMultipleDialog(opener);
                    if (list != null) {
                    	System.out.println("Send Files:");
                    	try{
	                        for (File file : list) {
	                            System.out.println(file.getCanonicalPath());
	                            //////////////////////////////////////////////////////////////////////////////////////////////
	                            UI.pushIn(new FileCall(roomId,UI.id,file)); //call to client
	                            //////////////////////////////////////////////////////////////////////////////////////////////
	                        }
	                    }catch(IOException e){}
                    }
        });
        typeArea.getChildren().addAll(typing, sendBtn);
        layout.getChildren().addAll(ghost, msgG,typeArea, fileBtn);
        layout.setBackground(new Background(CommonUi.bg));
        CommonUi.setDrag(layout, this);
        Scene scene = new Scene(layout,CommonUi.WIDTH+30,820);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(UI.css);
        this.setScene(scene);
        //this.show();
        int i = 0;
        /*while(i<5){
			msgG.addFront(0,"usr1","Message"+i+" front");
			i++;
        }*/
	}
	public void logEvent(String _uid, boolean _login){
		if(usrInRoom != null){
			for(String s : usrInRoom){
				if(s.equals(_uid)){
						msgG.addBack((_login)?3:4, _uid, null);
				}
			}
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
	private Rectangle ghost;
	private Button history;
	private ScrollPane sp;
	private VBox msgWrapper;
	private LinkedList<MessageBubble> msgBubs;
	public int historySize;
	public int shownMessage;
	private RoomScreen parent;
	public MessageGroup(int _historySize, RoomScreen _p){
		parent = _p;
		shownMessage = 0;
		historySize = _historySize;
		msgWrapper = new VBox(8);
		msgWrapper.setFillWidth(true);
     	msgWrapper.setPrefWidth(CommonUi.WIDTH);
     	history = new Button("View History Message");
     	history.setMaxWidth(Double.MAX_VALUE);
     	msgWrapper.getChildren().add(0,history);
     	history.setOnAction(event -> {
     		if(shownMessage < historySize){
     			System.out.println("[" + shownMessage+" of "+ historySize+ " messages shown]");
        		UI.pushIn(new GetMessageCall(parent.roomId, shownMessage));
        		if(shownMessage==0) historySize--; //hot fix
        	}else{
        		UI.alert(parent, "All your history has been shown.", "wow");
        	}
        });
		sp = new ScrollPane();
		sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		sp.setPrefHeight(600);
		sp.setFitToWidth(true);
 		sp.setContent(msgWrapper);
 		sp.setPannable(true);
 		this.getChildren().add(sp);
 		//System.out.println("msgWrapper:"+msgWrapper.getWidth());
	}
	public void addFront(int type, String userName, String msgContent){
		shownMessage++;
		if(userName.equals(UI.id)){
			msgWrapper.getChildren().add(1,new MyMessageBubble(userName, msgContent));
		}else{
			msgWrapper.getChildren().add(1,new MessageBubble(userName, msgContent, false));
		}
		System.out.println("ADD: "+msgContent);
	}
	public void addBack(int type, String userName, String msgContent){
		boolean scrolldown = (sp.getVvalue()>=0.5d)? true:false;
		if(ghost != null) msgWrapper.getChildren().remove(ghost);
		ghost = new Rectangle();
		ghost.setWidth(10);
		ghost.setHeight(100);
		ghost.setFill(Color.web("rgba(0,0,0,0)"));
		
		switch(type){
			case 0:
				shownMessage++;
				historySize++;
				if(userName.equals(UI.id)){
					msgWrapper.getChildren().add(new MyMessageBubble(userName, msgContent));
				}else{
					msgWrapper.getChildren().add(new MessageBubble(userName, msgContent, true));
				}
			break;
			case 1:
				shownMessage++;
				historySize++;
				if(userName.equals(UI.id)){
					msgWrapper.getChildren().add(new MyMessageBubble(userName, msgContent));
				}else{
					msgWrapper.getChildren().add(new MessageBubble(userName, msgContent, true));
				}
			break;
			case 2:
				msgWrapper.getChildren().add(new FileBubble(userName, msgContent));
			break;
			case 3:
				msgWrapper.getChildren().add(new LogBubble(userName, true));
			break;
			case 4:
				msgWrapper.getChildren().add(new LogBubble(userName, false));
			break;
			default:
			break;
		}
		System.out.println("ADD: "+msgContent);

		msgWrapper.getChildren().add(ghost);
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
class MyMessageBubble extends Group{
	private ScaleTransition sa;
	private TranslateTransition st;
	private FadeTransition ft;
	static private BackgroundFill bg = new BackgroundFill(Color.web("#E0E0E0"), new CornerRadii(10),new Insets(0));
	public MyMessageBubble(String usr, String s){
		Label u = new Label(usr);
		Label l = new Label(s);
		l.setWrapText(true);
		l.setPrefWidth(CommonUi.WIDTH*0.6);
		l.setFont(new Font("Arial", 16));
		l.setTextFill(Color.web("#000000"));
		l.setBackground(new Background(bg));
		l.setTranslateX(CommonUi.WIDTH*0.4-9);
		l.setTranslateY(16);
		l.setMinHeight(20);
		l.setPadding(new Insets(5,10,5,10));
		StackPane sp = new StackPane();
		sp.setPrefWidth(CommonUi.WIDTH);
		sp.setAlignment(Pos.CENTER_RIGHT);
		u.setWrapText(false);
		u.setTranslateX(-9);
		u.setFont(new Font("Arial", 12));
		u.setTextFill(Color.web("#000000"));
		sp.getChildren().add(u);
		Polygon polygon = new Polygon();
		polygon.getPoints().addAll(new Double[]{
		    0.0, 13.0,
		    -10.0, 10.0,
		    -10.0, 16.0 });
		polygon.setTranslateY(12);
		polygon.setTranslateX(CommonUi.WIDTH);
		polygon.setFill(bg.getFill());
		this.getChildren().add(polygon);
		this.getChildren().add(l);
		this.getChildren().add(sp);
		sa = new ScaleTransition(Duration.millis(200), l);
		sa.setFromX(0.0);
		sa.setToX(1.0);
		sa.setCycleCount(1);
		sa.setInterpolator(Interpolator.EASE_OUT);
		st = new TranslateTransition(Duration.millis(200), l);
		st.setFromX(l.getPrefWidth()/2+l.getTranslateX());
		st.setToX(l.getTranslateX());
		st.setCycleCount(1);
		st.setInterpolator(Interpolator.EASE_OUT);
		sa.play();
		st.play();
	}
}

class FileBubble extends Group{
	ScaleTransition sa;
	String fname;
	public FileBubble(String usr, String filename){
		VBox wrapper = new VBox(4);
		wrapper.setFillWidth(true);
		wrapper.setAlignment(Pos.CENTER);
		wrapper.setBackground(new Background(CommonUi.blackBg));
		wrapper.setPrefWidth(CommonUi.WIDTH);
		wrapper.setPadding(new Insets(2,2,2,2));
		fname = filename;
		Label u = new Label(usr + " has sent a file:" + filename);
		u.setWrapText(true);
		u.setFont(new Font("Arial", 14));
		u.setTextFill(Color.web("#FFFFFF"));
		u.setTextAlignment(TextAlignment.CENTER);
		u.setContentDisplay(ContentDisplay.CENTER);
		Button recv = new Button("Download it!");
		recv.setOnAction(event -> {
			Stage saver = new Stage();
			saver.initModality(Modality.APPLICATION_MODAL);
        	saver.initStyle(StageStyle.UTILITY);
        	FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            System.out.println("Save File");
            fileChooser.setInitialFileName(filename);
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File file = fileChooser.showSaveDialog(saver);
            if(file != null){
            	System.out.print("Save File: ");
            	try{
                    System.out.println(file.getCanonicalPath());
                    //////////////////////////////////////////////////////////////////////////////////////////////
                    UI.pushIn(new DownloadFileCall(fname, file)); //call to client
                    //////////////////////////////////////////////////////////////////////////////////////////////
                }catch(IOException e){}
            }
        });
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
		Label u = new Label(usr + ((login)? " is online":" has left"));
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
class RoomBox extends VBox{
	VBox roomField;
	String[] usrInRoom;
	public RoomBox(String usr){
		super(8);
		Button addRoom = new Button("New Room...");
		addRoom.setOnAction(event -> {
        		new AddRoom();
        });
		Button logout = new Button("Logout");
		logout.setOnAction(event -> {
			UI.pushIn(new LogoutCall());
        		//LOGOUT CALL
        });
		roomField = new VBox(8);
		

		getChildren().addAll(new WhiteLabel("Hi, "+usr+", open a room to start chatting!"), roomField, addRoom, logout);
	}
	public void addRoom(String _roomID, String[] usrInRoom){
		String str = "";
		int i = 0;
		if(usrInRoom!=null){
			for (i = 0; i < usrInRoom.length; i++){
				str += usrInRoom[i];
				if(usrInRoom[i].equals(UI.id)){
					str += "(you)";
				}
				str += " ";
			}
			for(RoomScreen rs : UI.s){
        		if(rs.roomId.equals(_roomID)){
        			rs.usrInRoom = usrInRoom;
        		}
        	}
		}
		Button btn = new Button(str);
		btn.setId("RoomBtn");
		roomField.getChildren().add(btn);
		//getChildren().add(btn);
		System.out.println("HI "+str);
		btn.setOnAction(event -> {
        	for(RoomScreen rs : UI.s){
        		if(rs.roomId.equals(_roomID)){
        			if(rs.isShowing()){
        				rs.hide();
        			}else{
        				rs.show();
        			}
        			return;
        		}
        	}
        });
	}
}
class AddRoom extends Stage{
	private Vector<RoomUser> usrVec;
	private VBox usrs;
	public AddRoom(){
		this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setTitle("Add Room...");

        VBox layout = new VBox(8);
        layout.setFillWidth(true);
     	layout.setFillWidth(true);
     	layout.setMinWidth(300);
        layout.setPadding(new Insets(10,10,10,10));
     	layout.setMinHeight(300);
     	layout.setBackground(new Background(CommonUi.bg));
        layout.setAlignment(Pos.CENTER);

        usrs = new VBox(8);
        usrVec = new Vector<RoomUser>();
		for(int i = 0; i < 5; i++){
			usrVec.add(new RoomUser(i));
			usrs.getChildren().add(usrVec.lastElement());
		}
 		

        Button moreUsr = new Button("more user...");
		moreUsr.setOnAction(event -> {
        	usrVec.add(new RoomUser(usrVec.size()));
        	usrs.getChildren().add(usrVec.lastElement());
        	AddRoom.this.sizeToScene();
        });

        Button createRoom = new Button("create the room!");
        createRoom.setOnAction(event -> {
        	int num = 0;

        	
        	for(RoomUser user : usrVec){
        		if(user.existID){
        			num++;
        		}
        	}
        	String[] ids = new String[num];
        	int itnum = 0;
        	for(RoomUser user : usrVec){
        		if(user.existID){
        			ids[itnum] = user.pendingStr;
        			itnum++;
        		}
        	}
        	UI.pushIn(new RoomCall(ids));
        	AddRoom.this.close();
        });

        layout.getChildren().addAll(usrs, moreUsr, createRoom);
        Scene scene = new Scene(layout);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(UI.css);

        ScaleTransition ft = new ScaleTransition(Duration.millis(1000), layout);
		ft.setByY(0.0);
	    ft.setToY(1.0);
	    ft.setCycleCount(1);
		ft.play();

        this.setScene(scene);
        this.show();
	}
}
class RoomUser extends HBox{
	static private Image good = new Image("/client/ui/sprites/yes.png");
	static private Image bad = new Image("/client/ui/sprites/no.png");
	static private Image blank = new Image("/client/ui/sprites/blank.png");
	public String pendingStr;
	public boolean existID;
	TextField uid;
	ImageView check;
	public RoomUser(int i){
		super(5);
		pendingStr = "";
		this.setAlignment(Pos.CENTER);
    	uid = new TextField();
   		check = new ImageView(blank);
   		check.setFitHeight(16);
   		check.setFitWidth(16);
   		uid.setFocusTraversable(true);
   		uid.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
		    @Override
		    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
		    {
		        if (!newPropertyValue)
		        {
		        	if(!uid.getText().equals("")){
		            	System.out.println("Check if "+uid.getText()+" exists");
		            	check.setImage(blank);
		            	if(!CommonUi.validateID(uid.getText())){
		            		existID = false;
		            		noExist();
		            	}else{
		            		pendingStr = uid.getText();
		            		UI.pending(new PendingPair(RoomUser.this, uid.getText()));
		            		UI.pushIn(new CheckIDCall(uid.getText()));
		            	}
		        	}
		        }
		    }
		});
    	this.getChildren().addAll(new WhiteLabel("member"+i), uid, check);
	}
	public void exist(){
		check.setImage(good);
		existID = true;
	}
	public void noExist(){
		check.setImage(bad);
		existID = false;
	}
}
class PendingPair{
    	RoomUser pendingFrame;
    	String id;
    	public PendingPair(RoomUser _pendingFrame, String _id){
    		pendingFrame = _pendingFrame;
    		id = _id;
    	}
    }
class CommonUi{
	public static int WIDTH = 440;
	public static BackgroundFill blackBg = new BackgroundFill(Color.web("rgba(0,0,0,0.5)"), new CornerRadii(0),new Insets(0));
	public static BackgroundImage bg = new BackgroundImage(new Image("/client/ui/sprites/bg.png"),
		BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
	public static boolean validateID(String _id){
		Pattern p = Pattern.compile("\\w++");
 		Matcher m = p.matcher(_id);
		if(m.matches())	return true;
		else return false;
	}
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

}
class WhiteLabel extends Label{
	public WhiteLabel(String s){
		super(s);
		this.setId("White");
		this.setMinWidth(60);
	}
}

