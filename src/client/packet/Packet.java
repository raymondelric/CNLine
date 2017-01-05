package client.packet;
import java.util.*;
import java.nio.charset.Charset;

public class Packet{

	public static final String CONNECT	= "00";
	public static final String REGISTER	= "01";
	public static final String LOGIN	= "02";
	public static final String LOGOUT	= "03";

	public static final String OK		= "10";
	public static final String FAIL		= "11";

	public static String makeMsg(String... strs) {
		String returnStr = strs[0];
		for(int i = 1; i < strs.length; i++){
			returnStr += "/";
			returnStr += strs[i];
		}
		return returnStr;
	}
}
