/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import java.awt.AWTException;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainManager;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.FileLoadDialog;
import org.drugis.common.gui.FileSaveDialog;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.event.TaskFailedEvent;

@SuppressWarnings("serial")
public class Main {
	
	private static final String EXAMPLE_XML = "defaultData.addis";
	public static final String PRINT_SCREEN = "F12"; // control p ... alt x ... etc
	private static final String DISPLAY_EXAMPLE = "Example Data";
	private static final String DISPLAY_NEW = "New File";

	private AddisWindow d_window;

	private DomainManager d_domainMgr;
	private String d_curFilename = null;
	private String d_displayName = null;
	private DomainChangedModel d_domainChanged;

	public Main(String[] args) {
		ImageLoader.setImagePath("/org/drugis/addis/gfx/");
		
		GUIHelper.initializeLookAndFeel();
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
		ToolTipManager.sharedInstance().setInitialDelay(0);

		GUIFactory.configureJFreeChartLookAndFeel();
 
		initializeDomain();
		
		if (args.length > 0) {
			d_curFilename = args[0];
		}
		
		addTaskFailureListener();
	}

	private void addTaskFailureListener() {
		ThreadHandler.getInstance().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getPropertyName().equals(ThreadHandler.PROPERTY_FAILED_TASK)) {
					final TaskFailedEvent taskEvent = (TaskFailedEvent) event.getNewValue();
					
					Runnable r = new Runnable() {
						public void run() {
							Throwable cause = taskEvent.getCause();
							ErrorDialog.showDialog(cause, taskEvent.getSource() + " failed");
						}
					};
					SwingUtilities.invokeLater(r);
				}
			}
		});
	}

	public static void bindPrintScreen(Container container) {
		final JComponent content = (JComponent) container;
		content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(PRINT_SCREEN), "printWindow");
		content.getActionMap().put("printWindow", 
				new AbstractAction("printWindow") { 
					public void actionPerformed(ActionEvent evt) {
							try { 
								printWindow(content);
							} catch (Exception e) {
								throw new RuntimeException("Error writing SVG: " + e.getMessage(), e);
							}
					} 
				} 
		);
	}
	
	public static void printWindow(final JComponent component) throws HeadlessException, AWTException {
		new FileSaveDialog(component, "svg", "SVG files") {
			@Override
			public void doAction(String path, String extension) {
				ImageExporter.writeSVG(path, component, component.getWidth(), component.getHeight());
			}
		};
	}
	
	protected void showWelcome() {
		final WelcomeDialog welcome = new WelcomeDialog(this);
		GUIHelper.centerWindow(welcome);
		welcome.setVisible(true);
	}

	protected void quitApplication() {
		if(isDataChanged()) {
			if(d_window == null || d_window.saveChangesDialog()) {
				quit();
			}
		} else {
			quit();
		}
	}

	private void quit() {
		if (d_window != null) {
			d_window.dispose(); 
		}
		System.exit(0);
	}
	

	private void saveDomainToXMLFile(String fileName) throws IOException {
		File f = new File(fileName);
		if (f.exists()) {
			f.delete();
		}

		FileOutputStream fos = new FileOutputStream(f);
		d_domainMgr.saveXMLDomain(fos);
	}

	private void initializeDomain() {
		d_domainMgr = new DomainManager();
		attachDomainChangedModel();
	}
	
	public void loadExampleDomain() {
		try {
			loadDomainFromXMLResource(EXAMPLE_XML);
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
	
	private enum XmlFormatType {
		LEGACY(0),
		SCHEMA1(1),
		SCHEMA2(2),
		SCHEMA_FUTURE(-1);
		
		private final int d_version;

		XmlFormatType(int version) {
			d_version = version;
		}

		public int getVersion() {
			return d_version;
		}
	}

	private void loadDomainFromXMLFile(String fileName) throws IOException,	ClassNotFoundException {
		File f = new File(fileName);
		if (f.exists() && f.isFile()) {
			FileInputStream in = new FileInputStream(f);
			if (loadDomainFromInputStream(in).equals(XmlFormatType.LEGACY)) {
				askToConvertToNew(fileName);
			} else {
				setFileNameAndReset(fileName);
			}
		} else {
			throw new FileNotFoundException(fileName + " not found");
		}
	}

	private XmlFormatType loadDomainFromInputStream(InputStream in)	throws IOException {
		BufferedInputStream fis = new BufferedInputStream(in);
		XmlFormatType xmlType = determineXmlType(fis);
		switch (xmlType) {
		case LEGACY:
			d_domainMgr.loadLegacyXMLDomain(fis);
			break;
		case SCHEMA_FUTURE:
			throw new IllegalArgumentException("The XML file was created with a newer version of ADDIS than you are using. Please download the new version to read it.");
		default: // SCHEMA*
			d_domainMgr.loadXMLDomain(fis, xmlType.getVersion());
			break;
		}
		attachDomainChangedModel();
		return xmlType;
	}

	private void attachDomainChangedModel() {
		d_domainChanged = new DomainChangedModel(getDomain(), false);
	}

	private void askToConvertToNew(String fileName) {
		int response = JOptionPane.showConfirmDialog(d_window, 
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

	private XmlFormatType determineXmlType(InputStream is) throws IOException {
		is.mark(1024);
		byte[] buffer = new byte[1024];
		int bytesRead = is.read(buffer);
		String str = new String(buffer, 0, bytesRead);
		Pattern pattern = Pattern.compile("http://drugis.org/files/addis-([0-9]*).xsd");
		Matcher matcher = pattern.matcher(str);
		XmlFormatType type = null;
		if (matcher.find()) {
			if (matcher.group(1).equals("1")) {
				type = XmlFormatType.SCHEMA1;
			} else if (matcher.group(1).equals("2")) {
				type = XmlFormatType.SCHEMA2;
			} else {
				type = XmlFormatType.SCHEMA_FUTURE;
			}
		} else {
			type = XmlFormatType.LEGACY;
		}
		is.reset();
		return type;
	}

	private void loadDomainFromXMLResource(String fileName) throws IOException, ClassNotFoundException {
		InputStream fis = Main.class.getResourceAsStream("/org/drugis/addis/" + fileName);
		loadDomainFromInputStream(fis);
	}

	void newFileActions() {
		resetDomain();
		newDomain();
		showMainWindow();
	}

	public void newDomain() {
		setCurFilename(null);
		setDisplayName(DISPLAY_NEW);		
		setDataChanged(false);
	}

	private void resetDomain() {
		ThreadHandler.getInstance().clear();	// Terminate all running threads.
		d_domainMgr.resetDomain(); // Create an empty domain.
		attachDomainChangedModel();
		disposeMainWindow();
	}

	private void disposeMainWindow() {
		if (d_window != null) {
			d_window.setVisible(false);
			d_window.dispose();
			d_window = null;
		}
	}

	public int fileLoadActions() {
		FileLoadDialog d = new FileLoadDialog(d_window, new String[][] {{"addis", "xml"}, {"addis"}, {"xml"}}, new String[] {"ADDIS or legacy XML files", "ADDIS data files", "ADDIS legacy XML files"}) {
			@Override
			public void doAction(String path, String extension) {
				try {
					resetDomain();
					loadDomainFromXMLFile(path);
					showMainWindow();
				} catch (Exception e) {
					throw new RuntimeException("Error loading data from " + path + ": " + e.getMessage(), e);
				}
			}
		};
		return d.getReturnValue();
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
		ThreadGroup threadGroup = new ThreadGroup("ExceptionGroup") {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				e.printStackTrace();
				ErrorDialog.showDialog(e, "Unexpected error.");
			}
		};
		
		Thread mainThread = new Thread(threadGroup, "Main thread") {
			@Override
			public void run() {
				Main main = new Main(args);
				main.startGUI();
			}
		};
		mainThread.start();
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
		d_window.leftTreeFocus(node);
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
		d_window = new AddisWindow(this, getDomain());
		d_window.pack();
		GUIHelper.centerWindow(d_window);
		d_window.setVisible(true);
	}

	public DomainChangedModel getDomainChangedModel() {
		return d_domainChanged;
	}

	private void setDisplayName(String displayName) {
		d_displayName = displayName;
	}

	public String getDisplayName() {
		return d_displayName;
	}
	
}
