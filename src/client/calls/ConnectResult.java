package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class ConnectResult extends UiCallObject{
	public boolean result;
	public ConnectResult(boolean _result){
		super(CONNECT_RESULT, RESPOND);
		this.result = _result;
	}
	public void print(){
		System.out.println("res:  "+this.result);
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
