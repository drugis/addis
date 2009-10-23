package org.drugis.common.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.UIManager;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

public class GUIHelper {

	public static void initializeLookAndFeel() {
		try {
			String osName = System.getProperty("os.name");
			
			if (osName.startsWith("Windows")) {
				UIManager.setLookAndFeel(new WindowsLookAndFeel());
			} else  if (osName.startsWith("Mac")) {
				// do nothing, use the Mac Aqua L&f
			} else {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
				} catch (Exception e) {
					UIManager.setLookAndFeel(new PlasticLookAndFeel());
				}
			}
		} catch (Exception e) {
			// Likely the Looks library is not in the class path; ignore.
		}
	}

	public static void centerWindow(Window window) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension fsize = window.getSize();
		int xLoc = (int) ((screenSize.getWidth() / 2) - (fsize.getWidth() / 2));
		int yLoc = (int) ((screenSize.getHeight() / 2) - (fsize.getHeight() / 2));
		window.setLocation(new Point(xLoc, yLoc));
	}

}
