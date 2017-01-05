package client.packet;
import java.util.*;
import java.nio.charset.Charset;

public class Packet{

	public static final String CONNECT = "00";
	public static final String REGISTER = "01";
	public static final String LOGIN = "02";
	public static final String LOGOUT = "03";

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

	public static String makeMsg(String... strs) {
		String returnStr = strs[0];
		for(int i = 1; i < strs.length; i++){
			returnStr += "/";
			returnStr += strs[i];
		}
		return returnStr;
	}
}
