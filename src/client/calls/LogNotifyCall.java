package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class LogNotifyCall extends UiCallObject{
	public String id;
	public boolean login;

	public LogNotifyCall(String _id, boolean _login){
		super(SOMEONE_LOGIN_OUT, RESPOND);
		this.id = _id;
		this.login = _login;
	}
	public void response(String res){
		super.type = RESPOND;
		if(res.equals("success")){
			super.success = true;
		}else{
			success = false;
		}
	}
	public void print(){
		System.out.println("id:   "+this.id);
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
