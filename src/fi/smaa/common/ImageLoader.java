/*
	This file is part of JSMAA.
	(c) Tommi Tervonen, 2009	

    JSMAA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JSMAA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JSMAA.  If not, see <http://www.gnu.org/licenses/>.
*/

package fi.smaa.common;

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
