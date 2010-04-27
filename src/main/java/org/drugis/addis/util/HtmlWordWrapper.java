/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.util;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import org.apache.commons.lang.StringEscapeUtils;
import org.drugis.addis.gui.BrowserLaunch;

public class HtmlWordWrapper {
	public static String wordWrap(String input) {
		return wordWrap(input, true);
	}
	
	public static String wordWrap(String input, boolean surround) {
		String[] arr = makeParts(StringEscapeUtils.escapeHtml(input));
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
		
		if (surround) {
			return "<html>" + resStr + "</html>";
		}
		return resStr;
	}

	private static final Pattern wrapRE = Pattern.compile(".{0,79}(?:\\S(?:-| |$)|$)");

	private static String[] makeParts(String str) {
		if (str != null && str != "") {
		    List<String> list = new LinkedList<String>();
		    Matcher m = wrapRE.matcher(str);
		    while (m.find()) list.add(m.group());
		    return list.toArray(new String[]{});
		}
		return new String[] {};
	}

	public static JTextPane createHtmlPane(String paneText) {
		return createHtmlPane(paneText, new Color(255, 255, 180));
	}
	
	public static JTextPane createHtmlPane(String paneText, Color paneColor) {
		JTextPane generalPane = new JTextPane();
		generalPane.setContentType("text/html");
		generalPane.setEditable(false);
		generalPane.setText(paneText);
		generalPane.addHyperlinkListener(new HyperlinkListener() {
			
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == EventType.ACTIVATED)
					BrowserLaunch.openURL(e.getURL().toString());
			}
		});
	
		generalPane.setBackground(paneColor);
		generalPane.setBorder(BorderFactory.createEtchedBorder());
		return generalPane;
	}
}
