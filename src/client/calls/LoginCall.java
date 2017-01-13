package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class LoginCall extends UiCallObject{
	public String id;
	public String pswd;

	//respond
	public String[] rids; //the rooms that this user currently has

	public LoginCall(String _id, String _pswd){
		super(LOGIN, REQUEST);
		this.id = _id;
		this.pswd = _pswd;
	}
	public void response(String res){
		super.type = RESPOND;
		if(res.equals("success")){
			super.success = true;
		}else{
			success = false;
		}
	}
	public void roomlist(String[] _rids){
		rids = _rids;
	}
	public void print(){
		System.out.println("id:   "+this.id);
		System.out.println("pswd: "+this.pswd);
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
