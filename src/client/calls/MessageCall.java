package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class MessageCall extends UiCallObject{
	//request
	public String rid;
	public String data;
	
	public MessageCall(String _rid, String _data){
		super(SEND_MESSAGE, REQUEST);
		this.rid = _rid;
		this.data = _data;
	}
	public void response(String res){}
	public void print(){
		System.out.println("rid:   "+this.rid);
		System.out.println("data: "+this.data);
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
