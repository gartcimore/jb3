package im.bci.jb3.wro;

import java.util.ArrayList;
import java.util.List;

public class WroMV {

	private List<String> css = new ArrayList<String>();
	private List<String> js = new ArrayList<String>();

	public void addCss(String c) {
		css.add(c);
	}

	public List<String> getCss() {
		return css;
	}

	public void addJs(String j) {
		js.add(j);
	}

	public List<String> getJs() {
		return js;
	}
}
