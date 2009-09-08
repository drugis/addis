package org.drugis.common;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ImageLoader {
	
	private Map<String, Icon> icons;
	private String imagePath;
	
	public ImageLoader(String imagePath) {
		this.imagePath = imagePath;
		icons = new HashMap<String, Icon>();
	}
	
	public Icon getIcon(String name) throws FileNotFoundException {
		if (icons.containsKey(name)) {
			return icons.get(name);
		} else {
		    java.net.URL imgURL = getClass().getResource(deriveGfxPath(name));
		    if (imgURL == null) {
		    	throw new FileNotFoundException("File not found for icon " + deriveGfxPath(name));
		    }
		    ImageIcon icon = new ImageIcon(imgURL);
	        icons.put(name, icon);
	        return icon;
		}
	}

	private String deriveGfxPath(String name) {
		return imagePath + name;
	}
}
