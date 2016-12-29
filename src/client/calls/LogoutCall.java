package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class LogoutCall extends UiCallObject{
	public LogoutCall(){
		super(LOGOUT, REQUEST);
	}
	public void print(){
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
