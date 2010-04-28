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

package org.drugis.addis.gui;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.swing.JOptionPane;

public class BrowserLaunch {
	static final String[] browsers = { "firefox", "opera", "konqueror", "epiphany",
		"seamonkey", "galeon", "kazehakase", "mozilla", "netscape", "chrome" };

	/**
	 * Opens the specified web page in a web browser
	 * @param url A web address (URL) of a web page (ex: "http://www.google.com/")
	 */
	public static void openURL(String url) {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] {String.class});
				openURL.invoke(null, new Object[] {url});
			}
			else if (osName.startsWith("Windows"))
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			else { //assume Unix or Linux
				boolean found = false;
				for (String browser : browsers)
					if (!found) {
						found = Runtime.getRuntime().exec(
								new String[] {"which", browser}).waitFor() == 0;
						if (found)
							Runtime.getRuntime().exec(new String[] {browser, url});
					}
				if (!found)
					throw new Exception(Arrays.toString(browsers));
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Error attempting to launch web browser\n" + e.toString());
		}
	}
}
