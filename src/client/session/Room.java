package client.session;
import java.util.*;
import java.nio.charset.Charset;

public class Room{
	public String rid;
	public List<Msg> msgs;
	public Room(String _rid, Msg[] _msgs){
		rid = _rid;
		msgs = Arrays.asList(_msgs);
	}
}
