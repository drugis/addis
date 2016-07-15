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

package org.drugis.addis.gui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainManager;
import org.drugis.addis.presentation.DomainChangedModel;
import org.drugis.addis.util.jaxb.JAXBHandler;
import org.drugis.addis.util.jaxb.JAXBHandler.XmlFormatType;
import org.drugis.common.ImageLoader;
import org.drugis.common.beans.AbstractObservable;
import org.drugis.common.gui.ErrorDialog;
import org.drugis.common.gui.FileLoadDialog;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.threading.ThreadHandler;

import com.sun.org.apache.xerces.internal.util.XMLChar;

@SuppressWarnings("serial")
public class Main extends AbstractObservable {
	public static final ImageLoader IMAGELOADER = new ImageLoader("/org/drugis/addis/gfx/");

	public enum Examples {
		DEPRESSION("Severe depression", "depressionExample.addis"), HYPERTENSION("Hypertension", "hypertensionExample.addis");

		final public String file;
		final public String name;

		Examples(String name, String file) {
			this.name = name;
			this.file = file;
		}

		public static Examples findByName(String name) {
			for(Examples example : values()) {
				if (example.name.equals(name)) return example;
			}
			throw new IllegalArgumentException("Could not find example  for " + name);
		}

		public static String findFileName(String name) {
			for(Examples example : values()) {
				if (example.name.equals(name)) return example.file;
			}
			throw new IllegalArgumentException("Could not find example data file for " + name);
		}
	}

	private static final String PRINT_SCREEN = "F12"; // control p ... alt x ... etc
	static final String DISPLAY_EXAMPLE = "Example Data";
	static final String DISPLAY_NEW = "New File";
	public static final String PROPERTY_DISPLAY_NAME = "displayName";
	private static final String BUG_REPORTING_TEXT = "<html>This is probably a bug. " +
			"Help us improve ADDIS by reporting this problem to us.<br/>" +
			"Attaching the stack trace and the .addis data file would be very helpful.<br/>" +
			"See <a href=\"http://drugis.org/addis-bug\">http://drugis.org/addis-bug</a> for instructions.<br/><br/>" +
			"Consider restarting ADDIS.</html>";

	private static AddisWindow s_window;

	private DomainManager d_domainMgr;
	private String d_curFilename = null;
	private String d_displayName = null;
	private DomainChangedModel d_domainChanged;
	private final boolean d_headless;
	private XmlFormatType d_xmlType = null;

	public Main(String[] args, boolean headless) {
		d_headless = headless;

		if (!d_headless) {
			GUIHelper.initializeLookAndFeel();
			UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
			ToolTipManager.sharedInstance().setInitialDelay(0);

			GUIFactory.configureJFreeChartLookAndFeel();
		}

		initializeDomain();

		if (args.length > 0) {
			d_curFilename = args[0];
		}
	}

	public static void bindPrintScreen(Container container) {
		final JComponent content = (JComponent) container;
		content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(PRINT_SCREEN), "printWindow");
		content.getActionMap().put("printWindow",
		new AbstractAction("printWindow") {
			public void actionPerformed(ActionEvent evt) {
				try {
					ImageExporter.writeImage(content, content, content.getWidth(), content.getHeight());
				}
				catch (Exception e) {
					throw new RuntimeException("Error writing image: " + e.getMessage(), e);
				}
			}
		});
	}

	protected void showWelcome() {
		final WelcomeDialog welcome = new WelcomeDialog(this);
		welcome.setLocationByPlatform(true);
		welcome.setVisible(true);
	}

	protected void quitApplication() {
		if(isDataChanged()) {
			if(s_window == null || s_window.saveChangesDialog()) {
				quit();
			}
		} else {
			quit();
		}
	}

	private void quit() {
		if (s_window != null) {
			s_window.dispose();
		}
		System.exit(0);
	}


	public static class XMLStreamFilter extends FilterOutputStream {
		public XMLStreamFilter(OutputStream out) {
			super(out);
		}

		@Override
		public void write(int b) throws IOException {
			if (XMLChar.isValid((char)b)) {
				super.write(b);
			} else {
				System.err.println("Removing invalid character while marshalling XML: " + (char)b);
			}
		}

		public static List<Character> getCharacters() {
			List<Character> invalids = new ArrayList<Character>();
			for (int i = 0x0; i <= 0xFFFD; i++) {
				if (!((i == 0x9) || (i == 0xA) || (i == 0xD) || ((i >= 0x20) && (i <= 0xD7FF)))) {
					invalids.add((char) i);
				}
			}
			return invalids;
		}

	}

	private void saveDomainToXMLFile(String fileName) throws IOException {
		d_domainMgr.saveXMLDomain(new File(fileName));
	}


	private void initializeDomain() {
		d_domainMgr = new DomainManager();
		attachDomainChangedModel();
	}

	public void loadExampleDomain(String exampleFile) {
		try {
			loadDomainFromXMLResource(exampleFile);
		} catch (Exception e) {
			throw new RuntimeException("Error loading default data: " + e.getMessage(), e);
		}
		setDisplayName(DISPLAY_EXAMPLE);
		setDataChanged(false);
		showMainWindow();
	}

	public Domain getDomain() {
		return d_domainMgr.getDomain();
	}

	private boolean loadDomainFromXMLFile(String fileName) {
		File f = new File(fileName);
		if (f.exists() && f.isFile()) {
			try {
				FileInputStream in = new FileInputStream(f);
				d_xmlType = loadDomainFromInputStream(in);
			} catch (Exception e) {
				ErrorDialog.showDialog(e, "Error loading file", "Error loading data from \"" + fileName + "\"", false);
				return false;
			}
			if (!d_xmlType.isValid()) {
				JOptionPane.showMessageDialog(s_window, "The file you are attempting to load is not formatted as a valid ADDIS XML file.",
						"Error loading file", JOptionPane.ERROR_MESSAGE);
				return false;
			} else if (d_xmlType.isFuture()) {
				JOptionPane.showMessageDialog(s_window, "The XML file was created with a newer version of ADDIS than you are using. Please download the new version to read it.",
						"Error loading file", JOptionPane.ERROR_MESSAGE);
				return false;
			} else if (d_xmlType.isLegacy()) {
				askToConvertToNew(fileName);
				return true;
			} else {
				setFileNameAndReset(fileName);
				return true;
			}
		} else {
			JOptionPane.showMessageDialog(s_window, "File \"" + fileName + "\" not found.",
					"Error loading file", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private XmlFormatType loadDomainFromInputStream(InputStream in)	throws IOException {
		BufferedInputStream fis = new BufferedInputStream(in);
		XmlFormatType xmlType = JAXBHandler.determineXmlType(fis);
		if (!xmlType.isValid() || xmlType.isFuture()) {
			return xmlType;
		} else if (xmlType.isLegacy()) {
			d_domainMgr.loadLegacyXMLDomain(fis);
		} else {
			d_domainMgr.loadXMLDomain(fis, xmlType.getVersion());
		}
		attachDomainChangedModel();
		return xmlType;
	}

	private void attachDomainChangedModel() {
		d_domainChanged = new DomainChangedModel(getDomain(), d_xmlType != null ? d_xmlType.isPast() : false);
	}

	private void askToConvertToNew(String fileName) {
		int response = JOptionPane.showConfirmDialog(s_window,
				"The data format for ADDIS has changed.\n\nWould you like to save this file in the new file format (.addis)?",
				"Save in new format?", JOptionPane.YES_NO_OPTION);

		if (response == JOptionPane.OK_OPTION) {
			if(fileName.endsWith(".xml")) {
				fileName = fileName.replace(".xml", ".addis");
			} else {
				fileName += ".addis";
			}
			saveDomainToFile(fileName);
			setFileNameAndReset(fileName);
		} else {
			setCurFilename(null);
			setDisplayName(DISPLAY_NEW);
			setDataChanged(true);
		}
	}

	private void loadDomainFromXMLResource(String fileName) throws IOException, ClassNotFoundException {
		InputStream fis = Main.class.getResourceAsStream("/org/drugis/addis/" + fileName);
		d_xmlType = loadDomainFromInputStream(fis);
	}

	void newFileActions() {
		newDomain();
		resetDomain();
		showMainWindow();
	}

	public void newDomain() {
		d_domainMgr.resetDomain(); // Create an empty domain.
		setCurFilename(null);
		setDisplayName(DISPLAY_NEW);
		setDataChanged(false);
	}

	private void resetDomain() {
		ThreadHandler.getInstance().clear(); // Terminate all running threads.
		attachDomainChangedModel();
		disposeMainWindow();
	}

	private void disposeMainWindow() {
		if (s_window != null) {
			s_window.dispose();
			s_window = null;
		}
	}

	public int fileLoadActions() {
		final boolean[] loaded = { false };
		FileLoadDialog d = new FileLoadDialog(s_window, new String[][] {{"addis", "xml"}, {"addis"}, {"xml"}}, new String[] {"ADDIS or legacy XML files", "ADDIS data files", "ADDIS legacy XML files"}) {
			@Override
			public void doAction(String path, String extension) {
				loaded[0] = loadDomainFromFile(path);
			}
		};
		d.loadActions();
		return loaded[0] ? d.getReturnValue() : JFileChooser.ERROR_OPTION;
	}

	public boolean loadDomainFromFile(String path) {
		if (loadDomainFromXMLFile(path)) {
			resetDomain();
			showMainWindow();
			return true;
		}
		return false;
	}

	public void saveDomainToFile(String path) {
		try {
			saveDomainToXMLFile(path);
			setFileNameAndReset(path);
		} catch (Exception e) {
			throw new RuntimeException("Error saving data to " + path + ": " + e.getMessage(), e);
		}
	}

	private void setFileNameAndReset(String path) {
		setCurFilename(path);
		int x = path.lastIndexOf(".");
		int y = path.lastIndexOf("/");
		setDisplayName(path.substring(y+1, x));
		setDataChanged(false);
	}

	public static void main(final String[] args) {
		new Thread(new Runnable() {
			public void run() {
				JAXBHandler.JAXB.getInstance(); // Initializes JAXBContext
			}
		}).start();
		GUIHelper.startApplicationWithErrorHandler(new Runnable() {
			public void run() {
				Main main = new Main(args, false);
				main.startGUI();
			}
		}, BUG_REPORTING_TEXT);
	}

	private void startGUI() {
		if (d_curFilename == null) {
			showWelcome();
		} else {
			try {
				loadDomainFromXMLFile(d_curFilename);
			} catch (Exception e) {
				ErrorDialog.showDialog(e, "Could not load file.", e.getMessage(), false);
			} finally {
				showMainWindow();
			}
		}
	}

	public void leftTreeFocus(Object node) {
		s_window.leftTreeFocus(node);
	}

	public void setCurFilename(String curFilename) {
		d_curFilename = curFilename;
	}

	public String getCurFilename() {
		return d_curFilename;
	}

	public void setDataChanged(boolean dataChanged) {
		d_domainChanged.setValue(dataChanged);
	}

	public boolean isDataChanged() {
		return d_domainChanged.getValue();
	}

	public void showMainWindow() {
		if (!d_headless) {
			s_window = new AddisWindow(this, getDomain());
			s_window.pack();
			s_window.setLocationByPlatform(true);
			s_window.setVisible(true);
		}
	}

	/**
	 * @return returns the only AddisWindow currently in existence for this process
	 */
	public static AddisWindow getMainWindow() {
		return s_window;
	}

	public DomainChangedModel getDomainChangedModel() {
		return d_domainChanged;
	}

	private void setDisplayName(String displayName) {
		String oldValue = d_displayName;
		d_displayName = displayName;
		firePropertyChange(PROPERTY_DISPLAY_NAME, oldValue, displayName);
	}

	public String getDisplayName() {
		return d_displayName;
	}

	public DomainManager getDomainManager() {
		return d_domainMgr;
	}

}
