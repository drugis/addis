package org.drugis.common;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ImageLoader {
	
	static private Map<String, Icon> icons;
	static private String imagePath;
	
	public static void setImagePath(String _imagePath) {
		imagePath = _imagePath;
		icons = new HashMap<String, Icon>();
	}
	
	public static Icon getIcon(String name) {
		if (icons.containsKey(name)) {
			return icons.get(name);
		} else {
		    java.net.URL imgURL = ImageLoader.class.getResource(deriveGfxPath(name));
		    if (imgURL == null) {
		    	return null;
		    }
		    ImageIcon icon = new ImageIcon(imgURL);
	        icons.put(name, icon);
	        return icon;
		}
	}

	private static String deriveGfxPath(String name) {
		return imagePath + name;
	}
}
