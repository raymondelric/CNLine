package client.calls;
import java.util.*; //useful stuff
import java.io.*; //read write files
import java.nio.charset.Charset; //for encoding problems

//this is when client gets a file from server, not download

public class DownloadFileCall extends UiCallObject{
	//request

	public String filename;//if needed

	public File file; //the file, may need to open for writing

	public DownloadFileCall(String _filename, File _f){
		super(DOWNLOAD_FILE, REQUEST);
		this.filename = _filename;
		this.file = _f;
	}

	public void response(String res){
		super.type = RESPOND;
		if(res.equals("success")){
			super.success = true;
		} else{
			success = false;
		}
	}

	public void print(){

		System.out.println("filename: "+this.filename);
		try{
		System.out.println("filepath: "+this.file.getCanonicalPath());
		}catch(IOException e){}
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
