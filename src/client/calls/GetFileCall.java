package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

//this is when client gets a file from server, not download

public class GetFileCall extends UiCallObject{
	//request
	public String rid;
	public String uid;
	public String filename;

	public GetFileCall(String _rid, String _uid, String _filename){
		super(GET_FILE, RESPOND);
		this.rid = _rid;
		this.uid = _uid;
		this.filename = _filename;

	}
	public  void response(String res){}
	public void print(){
		System.out.println("rid:   "+this.rid);
		System.out.println("uid: "+this.uid);
		System.out.println("filename: "+this.filename);
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
