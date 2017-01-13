package client.session;
import java.util.*;
import java.nio.charset.Charset;

public class Room{
	public String rid;
	public String[] uids;
	public Vector<Msg> msgs;
	public Room(String _rid, String[] _uids){
		this.rid = _rid;
		this.uids = _uids;
		this.msgs = new Vector<Msg>();
	}
	public Room(String _rid, String[] _uids, Msg[] _msgs){
		this.rid = _rid;
		this.uids = _uids;
		this.msgs = new Vector<Msg>();
		for(int i = 0; i < _msgs.length;i++)
			this.msgs.add(_msgs[i]);

		System.out.println(this.msgs.size());
	}
	public void print(){
		System.out.println(this.rid);
		for (Msg _msg : this.msgs){
			System.out.println(_msg.owner+" "+_msg.msg);
		}
	}
}
