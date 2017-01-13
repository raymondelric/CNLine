package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class CheckIDCall extends UiCallObject{
	public String id;
	public CheckIDCall(String _id){
		super(CHECK_EXIST_ID, REQUEST);
		this.id = _id;
	}
	public void response(String res){
		super.type = RESPOND;
		if(res.equals("success")){
			super.success = true;
		}
	}
	public void print(){
		System.out.println("id:   "+this.id);
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
