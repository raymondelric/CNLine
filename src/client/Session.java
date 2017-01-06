package client;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class Session{
	public String ID;
	public String[] Rooms;

	public Session(int _type, int _result){
		super(_type, RESPOND);
		this.result = _result;
	}
	public void print(){
		System.out.println("res:  "+this.result);
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
