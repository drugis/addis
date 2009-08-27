package org.drugis.addis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class AppInfo {
	private static final String APPNAMEFALLBACK = "ADDIS";
	private static final String APPVERSIONFALLBACK = "UNKNOWN";

	public static String getAppVersion() {
		return getProperty("application.version", APPVERSIONFALLBACK);
	}

	public static String getAppName() {
		return getProperty("application.name", APPNAMEFALLBACK);
	}

	private static String getProperty(String property, String fallback) {
		try {
			InputStream is = AppInfo.class.getResourceAsStream("application.properties");
			Properties props = new Properties();
			props.load(is);
			return props.getProperty(property, fallback);
		} catch (IOException e) {
			
		}
		return fallback;
	}
}
