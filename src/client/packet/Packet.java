package client.packet;
import java.util.*;
import java.nio.charset.Charset;

public class Packet{

	public static final String CONNECT = "00";
	public static final String REGISTER = "01";
	public static final String LOGIN = "02";
	public static final String LOGOUT = "03";
	public static final String MESSAGE = "04";
	public static final String ROOM = "05";
	public static final String NEWMSG = "06";
	public static final String RECORD = "07";
	public static final String ID = "08";

	public static final String CONNECT_OK = "10";
	public static final String CONNECT_FAIL = "11";

	public static final String REGISTER_OK = "20";
	public static final String REGISTER_DUP = "21";
	public static final String REGISTER_ILE = "22";
	public static final String REGISTER_FAIL = "23";

	public static final String LOGIN_OK = "30";
	public static final String LOGIN_IDNF = "31";
	public static final String LOGIN_WRPS = "32";
	public static final String LOGIN_ALRD = "33";
	public static final String LOGIN_FAIL = "34";

	public static final String LOGOUT_OK = "40";
	public static final String LOGOUT_FAIL = "41";

	public static final String ROOM_OK = "50";
	public static final String ROOM_FAIL = "51";

	public static final String ID_OK = "80";
	public static final String ID_NEX = "81";

	public static String makeMsg(String... strs) {
		String returnStr = strs[0];
		for(int i = 1; i < strs.length; i++){
			returnStr += "/";
			returnStr += strs[i];
		}
		return returnStr;
	}
}
