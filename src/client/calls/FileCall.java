package client.calls;
import java.util.*; //useful stuff
import java.io.*; //read write files
import java.nio.charset.Charset; //for encoding problems
//send file
public class FileCall extends UiCallObject{
	//request
	public String rid;
	public String uid;
	public File file; //the file, may need to open for reading

	public FileCall(String _rid, String _uid, File _f){
		super(SEND_FILE, REQUEST);
		this.rid = _rid;
		this.uid = _uid;
		this.file = _f;
	}

	public void response(String str){
		super.type = RESPOND;
		super.success = true;
	}

	public void print(){
		System.out.println("rid:   "+this.rid);
		System.out.println("uid: "+this.uid);
		try{
		System.out.println("filepath: "+this.file.getCanonicalPath());
		}catch(IOException e){}
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
