package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class LogoutCall extends UiCallObject{
	public LogoutCall(){
		super(LOGOUT, REQUEST);
	}
	public void response(String res){
		super.type = RESPOND;
		if(res.equals("success")){
			super.success = true;
		}
	}
	public void print(){
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
