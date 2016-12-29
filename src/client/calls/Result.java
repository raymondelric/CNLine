package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class Result extends UiCallObject{
	public boolean result;
	public Result(int _type, boolean _result){
		super(_type, RESPOND);
		this.result = _result;
	}
	public void print(){
		System.out.println("res:  "+this.result);
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
