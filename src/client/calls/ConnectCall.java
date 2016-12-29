package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class ConnectCall extends UiCallObject{
	public String ip;
	public int port;
	public ConnectCall(String _ip, int _port){
		super(CONNECT_TO_SERVER, REQUEST);
		this.ip = _ip;
		this.port = _port;
	}
	public void print(){
		System.out.println("ip:   "+this.ip);
		System.out.println("port: "+Integer.toString(this.port));
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
