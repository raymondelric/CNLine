package client.session;
import java.util.*;
import java.nio.charset.Charset;

public class Room{
	public String rid;
	public List<Msg> msgs;
	public Room(String _rid){
		this.rid = _rid;
		System.out.println("in constructor");
		//this.msgs = new LinkedList<Msg>();
		this.msgs = new ArrayList<Msg>();
		System.out.println("in constructor");
	}
	public Room(String _rid, Msg[] _msgs){
		this.rid = _rid;
		this.msgs = Arrays.asList(_msgs);
		//this.msgs = new LinkedList<Msg>(_msgs);
	}
	public void print(){
		System.out.println(this.rid);
		for (Msg _msg : this.msgs){
			System.out.println(_msg.owner+" "+_msg.msg);
		}
	}
}
