package client.session;
import java.util.*;
import java.nio.charset.Charset;

public class Session{
	public String ID;
	public List<Room> rooms;

	public Session(String _ID, Room... _rooms){
		this.ID = _ID;
		this.rooms = Arrays.asList(_rooms);
		//this.rooms = new LinkedList<Room>(_rooms);
	}

	public void reset(){
		this.ID = "";
		//this.rooms = new LinkedList<Room>();
		this.rooms = new ArrayList<Room>();
	}
	public void print(){
		System.out.println(this.ID);
		for(Room room : this.rooms){
			System.out.println(room.rid);
			for(Msg _msg : room.msgs){
				System.out.println(_msg.owner+" "+_msg.msg);
			}
		}
	}
}
