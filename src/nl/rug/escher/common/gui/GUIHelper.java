package nl.rug.escher.common.gui;

import javax.swing.UIManager;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;
import com.sun.java.swing.plaf.gtk.GTKLookAndFeel;

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
					UIManager.setLookAndFeel(new GTKLookAndFeel());
				} catch (Exception e) {
					UIManager.setLookAndFeel(new PlasticLookAndFeel());
				}
			}
		} catch (Exception e) {
			// Likely the Looks library is not in the class path; ignore.
		}
	}

}
