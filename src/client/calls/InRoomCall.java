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
		this.rid = _rid;
	}
	public void response(String res){
		super.type = RESPOND;
		if(res.equals("success")){
			super.success = true;
		}
	}
	public void fill(String[] _ids, int _historySize){
		this.ids = _ids;
		this.historySize = _historySize;
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
