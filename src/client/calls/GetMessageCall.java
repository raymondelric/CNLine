package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class GetMessageCall extends UiCallObject{
	//request and respond
	public String rid;
	public int msgid;
	
	//fill respond
	public String uid;//user name
	public String message;//message content

	public GetMessageCall(String _rid, int _msgid){
		super(GET_MESSAGE, REQUEST);
		this.rid = _rid;
		this.msgid = _msgid;
	}
	public void print(){
		System.out.println("rid:   "+this.rid);
		System.out.println("msgid: "+this.msgid);
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
