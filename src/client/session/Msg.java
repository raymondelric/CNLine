package client.session;
import java.util.*;
import java.nio.charset.Charset;

public class Msg{
	public String owner;
	public String msg;
	public Msg(String _owner, String _msg){
		owner = _owner;
		msg = _msg;
	}
}
