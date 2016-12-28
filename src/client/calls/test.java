package client.calls;
import java.util.*;
import java.nio.charset.Charset;

public class test{
	public static void main(String[] args) {
		RegisterCall rc = new RegisterCall("rc","rcpswd");
		UiCallObject ui = new RegisterCall("ui","uipswd");
		rc.print();
		ui.print();
		RegisterCall RC = (RegisterCall)ui;
		UiCallObject UI = (UiCallObject)rc;
		RC.print();
		UI.print();
		LoginCall lc = new LoginCall("lc","lcpswd");
		UiCallObject ui2 = new LoginCall("ui2","ui2pswd");
		lc.print();
		ui2.print();
		LoginCall LC = (LoginCall)ui2;
		UiCallObject UI2 = (UiCallObject)lc;
		LC.print();
		UI2.print();
	}
}
