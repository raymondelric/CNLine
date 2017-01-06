package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public class RoomCall extends UiCallObject{
	public String[] ids;
	public RoomCall(String[] _ids){
		super(CREATE_ROOM, REQUEST);
		this.ids = Arrays.copyOf(_ids, _ids.length);
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
