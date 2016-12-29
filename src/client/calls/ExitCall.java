package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class ExitCall extends UiCallObject{
	public ExitCall(){
		super(EXIT, REQUEST);
	}
	public void print(){
		System.out.println("wc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
