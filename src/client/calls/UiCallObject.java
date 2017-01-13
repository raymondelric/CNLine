package client.calls;
import java.util.*; //useful stuff
import java.nio.charset.Charset; //for encoding problems

public abstract class UiCallObject{
	//define type
	public static final int REQUEST = 0;
	public static final int RESPOND = 1;

	//define call
	public static final int REGISTER = 0;
	public static final int LOGIN = 1;
	public static final int LOGOUT = 2;
	public static final int CONNECT_TO_SERVER = 3;
	public static final int EXIT = 4;
	public static final int CREATE_ROOM = 5;
	public static final int CHECK_EXIST_ID = 6;
	public static final int PEOPLE_IN_ROOM = 7;

	public static final int SEND_MESSAGE = 10;
	public static final int GET_MESSAGE = 11;

	public static final int SEND_FILE = 20;
	public static final int GET_FILE = 21;
	public static final int DOWNLOAD_FILE = 22;

	public static final int SOMEONE_LOGIN_OUT = 30;

	//common variables
	public int type;
	public final int whatCall;
	public boolean success; //set by response
	//public String message; //some words to display on console, not necessary

	public UiCallObject(int _whatCall, int _type) {
		whatCall = _whatCall;
		type = _type;
		success = false;
	}
	public abstract void response(String res);
	public abstract void print(); //debug
}
