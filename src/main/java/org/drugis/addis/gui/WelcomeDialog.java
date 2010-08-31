package org.drugis.addis.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.drugis.addis.AppInfo;
import org.drugis.addis.FileNames;
import org.drugis.addis.util.threading.ThreadHandler;
import org.drugis.common.gui.FileLoadDialog;

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
		setPreferredSize(new Dimension(400, 250));
		pack();
		setResizable(false);
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
				"10dlu, p, 10dlu, p, 3dlu, p, 3dlu, p, 5dlu");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();	
		
		builder.add(new JLabel("<html><center>Please choose an option from the list.<br />" +
				"If you are new to the software we recommend that you look over the example data.</center></html>"),
				cc.xyw(1, 2, 4));
		
		JButton exampleData = GUIFactory.createIconButton(FileNames.ICON_TIP, "Load example data");
		exampleData.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
					try {
						d_main.fileLoadActions("src/main/resources/org/drugis/addis/defaultData.xml", "xml");
						d_main.setTitle(AppInfo.getAppName() + " v" + AppInfo.getAppVersion() + " - Example data");
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(d_main, "Couldn't open example file");
						e1.printStackTrace();
					}
					setVisible(false);
					dispose();
			}
			});
		JButton loadData = GUIFactory.createIconButton(FileNames.ICON_OPENFILE, "Load data from file");
		loadData.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				new FileLoadDialog(d_main, "xml", "XML files") {
					@Override
					public void doAction(String path, String extension) {
						try {
							d_main.fileLoadActions(path, extension);
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(d_main, "Couldn't open file " + path);
							e1.printStackTrace();
						}
						setVisible(false);
						dispose();
					}
				};
			}
			});
		JButton newData = GUIFactory.createIconButton(FileNames.ICON_NEWFILE, "Begin with no data");
		newData.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				ThreadHandler.getInstance().clear();	// Terminate all running threads.
				d_main.getDomain().clearDomain();	
				d_main.getPmManager().clearCache();		// Empty the PresentationModelFactory cache.
				setVisible(false);
				dispose();
			}});
		
		builder.add(exampleData, cc.xy(2, 4));
		builder.add(new JLabel("Load example data"), cc.xy(4, 4));
		builder.add(loadData, cc.xy(2, 6));
		builder.add(new JLabel("Load data from file"), cc.xy(4, 6));
		builder.add(newData, cc.xy(2, 8));
		builder.add(new JLabel("Begin with no data"), cc.xy(4, 8));
		
		setContentPane(builder.getPanel());
	}

}
