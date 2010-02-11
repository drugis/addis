/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

import org.apache.myfaces.trinidad.context.Version;

//import java.version.*;


//import java.org.apache.myfaces.trinidad.context.Version;

public class AppInfo {
	private static final String APPNAMEFALLBACK = "ADDIS";
	private static final String APPVERSIONFALLBACK = "UNKNOWN";

	public static String getAppVersion() {
		return getProperty("version", APPVERSIONFALLBACK);
	}

	public static String getAppName() {
		return getProperty("name", APPNAMEFALLBACK);
	}
	
	public static String getLatestVersion() {
		URL updateWebService;
		try {
			updateWebService = new URL("http://drugis.org/service/currentVersion");
			URLConnection conn = updateWebService.openConnection();
			String line =  (new BufferedReader(new InputStreamReader(conn.getInputStream()))).readLine();
			
			StringTokenizer st = new StringTokenizer(line);
			
			String latestversion = st.nextToken(); 
			
			boolean newversion = compareVersion(latestversion, getAppVersion());
			
			return !newversion ? null : latestversion;
		} catch (Exception e) {
			System.err.println("Warning: Couldn't check for new versions. Connection issue?");
		}
		
		return null;
	}

	private static boolean compareVersion(String latestversion, String appVersion) {
		if (appVersion.equals(APPVERSIONFALLBACK))
			return true;		
		
		Version latest = new Version(latestversion);
		Version app	= new Version(appVersion);
		
		return latest.compareTo(app) > 0 ? true : false;
	}

	private static String getProperty(String property, String fallback) {
		try {
			InputStream is = AppInfo.class.getResourceAsStream("/META-INF/maven/org.drugis/addis/pom.properties");
			Properties props = new Properties();
			props.load(is);
			return props.getProperty(property, fallback);
		} catch (Exception e) {
			
		}
		return fallback;
	}
}
