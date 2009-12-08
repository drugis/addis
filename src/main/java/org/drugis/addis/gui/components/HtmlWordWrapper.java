package org.drugis.addis.gui.components;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlWordWrapper {
	
	public static String wordWrap(String input) {
		String[] arr = makeParts(input);
		String resStr = "";
		for (String s : arr) {
			s.isEmpty();
			if (s.length() < 1) {
				continue;
			}
			if(!resStr.equals("")) {
				resStr = resStr + "<br>";
			}
			resStr += s;
		}
		return "<html>"+resStr+"</html>";
	}

	private static final Pattern wrapRE = Pattern.compile(".{0,79}(?:\\S(?:-| |$)|$)");

	private static String[] makeParts(String str) {
	    List<String> list = new LinkedList<String>();
	    Matcher m = wrapRE.matcher(str);
	    while (m.find()) list.add(m.group());
	    return (String[]) list.toArray(new String[list.size()]);
	}
}
