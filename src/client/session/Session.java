package client.session;
import java.util.*;
import java.nio.charset.Charset;

public class Session{
	public String ID;
	public Vector<Room> rooms;

	public Session(String _ID, Room... _rooms){
		this.ID = _ID;
		//this.rooms = Arrays.asList(_rooms);
		//this.rooms = new LinkedList<Room>(_rooms);
		this.rooms = new Vector<Room>();
		for (int i = 0;i < _rooms.length;i++){
			this.rooms.add(_rooms[i]);
		}
		System.out.println(this.rooms.size());
	}

	public void reset(){
		this.ID = "";
		//this.rooms = new LinkedList<Room>();
		this.rooms = new Vector<Room>();
		//this.rooms = new ArrayList<Room>();
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
