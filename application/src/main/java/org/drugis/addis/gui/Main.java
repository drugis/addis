/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainManager;
import org.drugis.addis.presentation.DomainChangedModel;
import org.drugis.addis.util.jaxb.JAXBHandler;
import org.drugis.addis.util.jaxb.JAXBHandler.XmlFormatType;
import org.drugis.common.ImageLoader;
import org.drugis.common.beans.AbstractObservable;
import org.drugis.common.gui.FileLoadDialog;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.event.TaskFailedEvent;

@SuppressWarnings("serial")
public class Main extends AbstractObservable {
	public static final ImageLoader IMAGELOADER = new ImageLoader("/org/drugis/addis/gfx/");
	public static class ErrorDialogExceptionHandler {
		public void handle(Throwable e) {
			e.printStackTrace();
			ErrorDialog.showDialog(e, "Unexpected error.");
		}
	}

	private static final String EXAMPLE_XML = "defaultData.addis";
	private static final String PRINT_SCREEN = "F12"; // control p ... alt x ... etc
	static final String DISPLAY_EXAMPLE = "Example Data";
	static final String DISPLAY_NEW = "New File";
	public static final String PROPERTY_DISPLAY_NAME = "displayName";

	private static AddisWindow s_window;

	private DomainManager d_domainMgr;
	private String d_curFilename = null;
	private String d_displayName = null;
	private DomainChangedModel d_domainChanged;
	private final boolean d_headless;

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
							cause.printStackTrace();
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
		GUIHelper.centerWindow(welcome);
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
	
	private boolean loadDomainFromXMLFile(String fileName) {
		File f = new File(fileName);
		if (f.exists() && f.isFile()) {
			XmlFormatType loadedVersion;
			try {
				FileInputStream in = new FileInputStream(f);
				loadedVersion = loadDomainFromInputStream(in);
			} catch (Exception e) {
				ErrorDialog.showDialog(e, "Error loading file", "Error loading data from \"" + fileName + "\"", false);
				return false;
			}
			if (!loadedVersion.isValid()) {
				JOptionPane.showMessageDialog(s_window, "The file you are attempting to load is not formatted as a valid ADDIS XML file.",
						"Error loading file", JOptionPane.ERROR_MESSAGE);
				return false;
			} else if (loadedVersion.isFuture()) {
				JOptionPane.showMessageDialog(s_window, "The XML file was created with a newer version of ADDIS than you are using. Please download the new version to read it.",
						"Error loading file", JOptionPane.ERROR_MESSAGE);
				return false;
			} else if (loadedVersion.isLegacy()) {
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
		d_domainChanged = new DomainChangedModel(getDomain(), false);
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
		loadDomainFromInputStream(fis);
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
		ThreadHandler.getInstance().clear();	// Terminate all running threads.
		attachDomainChangedModel();
		disposeMainWindow();
	}

	private void disposeMainWindow() {
		if (s_window != null) {
			s_window.setVisible(false);
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
		System.setProperty("sun.awt.exception.handler", ErrorDialogExceptionHandler.class.getName());
		
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
				Main main = new Main(args, false);
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
			GUIHelper.centerWindow(s_window);
			s_window.setVisible(true);
		}
	}

	/**
	 * Beware of the singleton! 
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
	
}
