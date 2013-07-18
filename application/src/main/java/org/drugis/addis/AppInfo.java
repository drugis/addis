/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.util.Version;

//import java.version.*;


//import java.org.apache.myfaces.trinidad.context.Version;

public class AppInfo {
	private static final String APPNAMEFALLBACK = "ADDIS";
	public static final String APPVERSIONFALLBACK = "UNKNOWN";

	public final static ValueHolder<String> s_newVersion = new ModifiableHolder<String>(null);

	static {
		Future<String> version = Executors.newFixedThreadPool(1).submit(new CheckVersion());
		try {
			s_newVersion.setValue(version.get());
		} catch (Exception e) {
			System.err.println("Warning: Couldn't check for new versions.");
			e.printStackTrace();
		}
	}

	public static String getAppVersion() {
		return getProperty("version", APPVERSIONFALLBACK);
	}

	public static String getAppName() {
		return getProperty("name", APPNAMEFALLBACK);
	}


	private static class CheckVersion implements Callable<String> {
	    public String call() {
			URL updateWebService;
			try {
				updateWebService = new URL("http://drugis.org/service/currentVersion");
				URLConnection conn = updateWebService.openConnection();
				String line =  new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine();
				return new StringTokenizer(line).nextToken();
			} catch (Exception e) {
				System.err.println("Warning: Couldn't check for new versions. Connection issue?");
			}
		    return null;
		}
	}

	public static ValueHolder<String> getLatestVersion() {
		return s_newVersion;
	}

	public static boolean compareVersion(String latestversion, String appVersion) {
		if (latestversion == null) return false;
		if (appVersion.equals(APPVERSIONFALLBACK)) return false;

		Version latest = new Version(latestversion);
		Version app	= new Version(appVersion);

		return latest.compareTo(app) > 0 ? true : false;
	}

	private static String getProperty(String property, String fallback) {
		try {
			InputStream is = AppInfo.class.getResourceAsStream("/META-INF/maven/org.drugis.addis/application/pom.properties");
			Properties props = new Properties();
			props.load(is);
			return props.getProperty(property, fallback);
		} catch (Exception e) {
			return fallback;
		}
	}
}
