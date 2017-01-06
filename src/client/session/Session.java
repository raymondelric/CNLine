package client.session;
import java.util.*;
import java.nio.charset.Charset;

public class Session{
	public String ID;
	public List<Room> rooms;

	public Session(String _ID, Room... _rooms){
		this.ID = _ID;
		this.rooms = Arrays.asList(_rooms);
	}

	public static void main(String[] args){

		Msg[] msg1 = new Msg[3];
		msg1[0] = new Msg("u1","m1");
		msg1[1] = new Msg("u2","m2");
		msg1[2] = new Msg("u3","m3");

		Msg[] msg2 = new Msg[3];
		msg2[0] = new Msg("uu1","mm1");
		msg2[1] = new Msg("uu2","mm2");
		msg2[2] = new Msg("uu3","mm3");

		Msg[] msg3 = new Msg[3];
		msg3[0] = new Msg("uuu1","mmm1");
		msg3[1] = new Msg("uuu2","mmm2");
		msg3[2] = new Msg("uuu3","mmm3");

		Room[] r = new Room[3];
		
		r[0] = new Room("rid", msg1);
		r[1] = new Room("rrid", msg2);
		r[2] = new Room("rrrid", msg3);

		Session sess = new Session("uid", r);
		sess.print();
	}

	public void print(){
		System.out.println("id = " + this.ID);
		for(Room room : this.rooms){
			System.out.println(room.rid);
			for(Msg m : room.msgs){
				System.out.println(m.owner +" "+ m.msg);
			}
		}

	}
}
