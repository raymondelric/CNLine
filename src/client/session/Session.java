package client.session;
import java.util.*;
import java.nio.charset.Charset;

public class Session{
	private String uid;
	private Vector<Room> rooms;

	public void set(String _uid, Room... _rooms){	
		this.uid = _uid;
		this.rooms = new Vector<Room>();
		for (int i = 0;i < _rooms.length;i++){
			this.rooms.add(_rooms[i]);
		}
		System.out.println(this.rooms.size());
	}

	public void reset(){
		this.uid = "";
		this.rooms = new Vector<Room>();
	}

	public String getuid(){
		return this.uid;
	}

	public Vector<Room> getroom(){
		return this.rooms;
	}

	public String[] getrid(){
		String[] ret = new String[this.rooms.size()];
		int i = 0;
		for(Room room : this.rooms){
			ret[i++] = room.rid;
		}
		return ret;
	}

	public void print(){
		System.out.println("id: "+this.uid);
		for(Room room : this.rooms){
			System.out.println("room"+room.rid);
			for(Msg _msg : room.msgs){
				System.out.println(_msg.owner+" "+_msg.msg);
			}
			System.out.println("");
		}
	}
}
