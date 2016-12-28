package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class RegisterCall extends UiCallObject{
	public String id;
	public String pswd;
	public RegisterCall(String _id, String _pswd){
		super(LOGIN, REQUEST);
		this.id = _id;
		this.pswd = _pswd;
	}
	public void print(){
		System.out.println("id:   "+this.id);
		System.out.println("pswd: "+this.pswd);
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
