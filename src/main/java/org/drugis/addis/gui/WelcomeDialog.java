package org.drugis.addis.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import org.drugis.addis.AppInfo;
import org.drugis.addis.FileNames;
import org.drugis.addis.util.threading.ThreadHandler;
import org.drugis.common.ImageLoader;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;



@SuppressWarnings("serial")
public class WelcomeDialog extends JDialog {
	
	private Main d_main;

	public WelcomeDialog(Main parent) {
		super(parent);
		d_main = parent;
		setTitle("Welcome to " + AppInfo.getAppName());		
		initComps();
		setPreferredSize(new Dimension(446, 325));
		setResizable(false);
		//setAlwaysOnTop(true);
		//setModalityType(Dialog.ModalityType.TOOLKIT_MODAL);
		pack();
		addWindowListener(new WindowListener() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);				
			}
			public void windowClosed(WindowEvent e) {
				d_main.setVisible(true);
			}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
		});
	}
	
	private void initComps() {
		FormLayout layout = new FormLayout(
				"pref:grow, right:pref, 5dlu, pref:grow, 5dlu", 
				"0dlu, p, 10dlu, p, 3dlu, p, 3dlu, p, 10dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();	
		JLabel label = new JLabel();
		label.setIcon(ImageLoader.getIcon(FileNames.IMAGE_HEADER));
		builder.add(label, cc.xyw(1, 2, 5));
		
		/*
		builder.add(new JLabel("<html><center>Please choose an option from the list.<br />" +
				"If you are new to the software we recommend that you look over the example data.</center></html>"),
				cc.xyw(1, 2, 4));
		*/
		JButton exampleData = GUIFactory.createIconButton(FileNames.ICON_TIP, 
				"<html> Open an example file which contains <br />a complete dataset for analysis. </html>");
		exampleData.setPreferredSize(new Dimension(40, 40));
		exampleData.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				d_main.loadDomain();	
				setVisible(false);
				dispose();
			}
			});
		JButton loadData = GUIFactory.createIconButton(FileNames.ICON_OPENFILE, 
				"<html> Load data from existing file <br /> to visualize, edit or perform analysis. </html>");
		loadData.setPreferredSize(new Dimension(40, 40));
		loadData.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if(d_main.fileLoadActions() != JFileChooser.CANCEL_OPTION) {			
					setVisible(false);
					dispose();
				}
			}
			});
		JButton newData = GUIFactory.createIconButton(FileNames.ICON_NEWFILE, 
				"<html> Create an empty file with no exisitng entities. <br /> You need to enter data to perform an analysis. </html>");
		newData.setPreferredSize(new Dimension(40, 40));
		newData.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				ThreadHandler.getInstance().clear();	// Terminate all running threads.
				d_main.getDomain().clearDomain();	
				d_main.getPmManager().clearCache();		// Empty the PresentationModelFactory cache.
				d_main.setTitle(AppInfo.getAppName() + " v" + AppInfo.getAppVersion() + " - New File");
				d_main.setCurFilename(null);
				d_main.setDataChanged(false);
				setVisible(false);
				dispose();
			}});
		
		builder.add(exampleData, cc.xy(2, 4));
		builder.add(new JLabel("Open a complete dataset example"), cc.xy(4, 4));
		builder.add(loadData, cc.xy(2, 6));
		builder.add(new JLabel("Load data from previously saved file"), cc.xy(4, 6));
		builder.add(newData, cc.xy(2, 8));
		builder.add(new JLabel("Create new dataset"), cc.xy(4, 8));
		
		JLabel labelFooter = new JLabel();
		labelFooter.setIcon(ImageLoader.getIcon(FileNames.IMAGE_FOOTER));
		builder.add(labelFooter, cc.xyw(1, 10, 5));
		builder.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		setContentPane(builder.getPanel());
	}

}
