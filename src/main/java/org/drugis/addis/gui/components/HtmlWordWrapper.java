package org.drugis.addis.gui.components;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class HtmlWordWrapper {
	public static String wordWrap(String input) {
		return wordWrap(input, true);
	}
	
	public static String wordWrap(String input, boolean surround) {
		String[] arr = makeParts(input);
		String resStr = "";
		for (String s : arr) {
			if (s.length() < 1) {
				continue;
			}
			if(!resStr.equals("")) {
				resStr = resStr + "<br>";
			}
			resStr += s;
		}
		resStr = StringEscapeUtils.escapeHtml(resStr);
		
		if (surround) {
			return "<html>" + resStr + "</html>";
		}
		return resStr;
	}

	private static final Pattern wrapRE = Pattern.compile(".{0,79}(?:\\S(?:-| |$)|$)");

	private static String[] makeParts(String str) {
	    List<String> list = new LinkedList<String>();
	    Matcher m = wrapRE.matcher(str);
	    while (m.find()) list.add(m.group());
	    return (String[]) list.toArray(new String[list.size()]);
	}
}
