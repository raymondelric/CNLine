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
import javafx.scene.control.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.event.*;
import javafx.stage.FileChooser;
import javafx.scene.layout.*;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import client.calls.*;

public class DebugScreen extends Stage{
	private static Queue<client.calls.UiCallObject> fromMain; //read only
	public DebugScreen(Queue<client.calls.UiCallObject> q){
		fromMain = q;
		this.initModality(Modality.NONE);
        this.initStyle(StageStyle.DECORATED);
        this.setTitle("DebugScreen");
        VBox layout = new VBox(8);
        layout.setFillWidth(true);
     	layout.setMinWidth(400);
     	layout.setMinHeight(300);
     	layout.setBackground(new Background(CommonUi.bg));
        layout.setPadding(new Insets(10,10,10,10));
        layout.setAlignment(Pos.CENTER);

        Button sendConnectResult = new Button("sendConnectResult");
        sendConnectResult.setOnAction(event1->{
        	System.out.println("sendConnectResult");
        	ConnectCall call = new ConnectCall("192.168.0.1",5050);
        	call.type = call.RESPOND;
        	call.success = true;
        	fromMain.offer(call);
        });
        layout.getChildren().add(sendConnectResult);

        Button sendLoginResult = new Button("sendLoginResult");
        sendLoginResult.setOnAction(event1->{
        	System.out.println("sendLoginResult");
        	LoginCall call = new LoginCall("192.168.0.1","5050");
        	call.type = call.RESPOND;
        	call.success = true;
        	call.rids = new String[1];
        	call.rids[0] = "TestRoom";
        	fromMain.offer(call);
        });
        layout.getChildren().add(sendLoginResult);

        Button sendInRoomCallResult = new Button("sendInRoomCallResult");
        sendInRoomCallResult.setOnAction(event1->{
        	System.out.println("sendInRoomCallResult");
        	InRoomCall call = new InRoomCall("TestRoom");
        	call.type = call.RESPOND;
        	call.success = true;
        	call.ids = new String[3];
        	call.ids[0] = "User1";
        	call.ids[1] = "User2";
        	call.ids[2] = "User3";
        	fromMain.offer(call);
        });
        layout.getChildren().add(sendInRoomCallResult);

        Scene scene = new Scene(layout);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(UI.css);

        this.setScene(scene);
        this.show();
	}
}

