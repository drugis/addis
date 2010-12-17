/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainManager;
import org.drugis.addis.util.ImageExporterExperimental;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.FileLoadDialog;
import org.drugis.common.gui.FileSaveDialog;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.event.TaskFailedEvent;

@SuppressWarnings("serial")
public class Main {
	
	private static final String EXAMPLE_XML = "defaultData.xml";
	public static final String PRINT_SCREEN = "F12"; // control p ... alt x ... etc
	private static final String DISPLAY_EXAMPLE = "Example Data";
	private static final String DISPLAY_NEW = "New File";

	private AddisWindow d_window;

	private DomainManager d_domainMgr;
	private String d_curFilename = null;
	private String d_displayName = null;
	private DomainChangedModel d_domainChanged;

	public Main() {
		ImageLoader.setImagePath("/org/drugis/addis/gfx/");
		
		GUIHelper.initializeLookAndFeel();
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
		ToolTipManager.sharedInstance().setInitialDelay(0);

		GUIFactory.configureJFreeChartLookAndFeel();

		initializeDomain();		
		
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
							System.err.println("Task Failed: " + taskEvent.getSource());
							cause.printStackTrace();
							JOptionPane.showMessageDialog(null, cause.toString(),
									"Task Failed: " + taskEvent.getSource(),
									JOptionPane.ERROR_MESSAGE);
							
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
				ImageExporterExperimental.writeSVG(path, component, component.getWidth(), component.getHeight());
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
		d_domainChanged = new DomainChangedModel(getDomain(), false);
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

	private void loadDomainFromXMLFile(String fileName) throws IOException,	ClassNotFoundException {
		File f = new File(fileName);
		if (f.exists() && f.isFile()) {
			FileInputStream fis = new FileInputStream(f);
			d_domainMgr.loadXMLDomain(fis);
		} else {
			throw new FileNotFoundException(fileName + " not found");
		}
	}

	private void loadDomainFromXMLResource(String fileName) throws IOException, ClassNotFoundException {
		InputStream fis = Main.class.getResourceAsStream("/org/drugis/addis/" + fileName);
		d_domainMgr.loadXMLDomain(fis);
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
		d_domainChanged = new DomainChangedModel(getDomain(), false);
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
		FileLoadDialog d = new FileLoadDialog(d_window, "xml", "XML files") {
			@Override
			public void doAction(String path, String extension) {
				try {
					resetDomain();
					loadDomainFromXMLFile(path);
					showMainWindow();
					setFileNameAndReset(path);
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

	public static void main(String[] args) {
		ThreadGroup threadGroup = new ThreadGroup("ExceptionGroup") {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.toString(),
						"Unexpected Error", JOptionPane.ERROR_MESSAGE);
			}
		};
		
		Thread mainThread = new Thread(threadGroup, "Main thread") {
			@Override
			public void run() {
				Main main = new Main();
				main.showWelcome();
			}
		};
		mainThread.start();
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
