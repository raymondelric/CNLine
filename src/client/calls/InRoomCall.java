package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class InRoomCall extends UiCallObject{
	public String rid;

	//respond
	public String[] ids;
	public int historySize;
	
	public InRoomCall(String _rid){
		super(PEOPLE_IN_ROOM, REQUEST);
		rid = _rid;
	}
	public void print(){
		System.out.print("ids: ");
		for(int i = 0; i < this.ids.length; i++){
			System.out.print(this.ids[i]+" ");
		}
		System.out.println("\nwc:   "+this.whatCall);
		System.out.println("type: "+this.type);
	}
}
