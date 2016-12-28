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
	}
}
