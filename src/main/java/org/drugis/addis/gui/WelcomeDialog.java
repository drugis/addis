package org.drugis.addis.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import org.drugis.addis.AppInfo;
import org.drugis.addis.FileNames;
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
		setResizable(false);
		pack();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);				
			}
			@Override
			public void windowClosed(WindowEvent e) {
				d_main.setVisible(true);
			}
		});
	}
	
	private void initComps() {
		FormLayout layout = new FormLayout(
				"left:pref, 10px, left:pref", 
				"p, 3dlu, p, 1dlu, p, 1dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();	
		JLabel label = new JLabel();
		label.setIcon(ImageLoader.getIcon(FileNames.IMAGE_HEADER));
		builder.add(label, cc.xyw(1, 1, 3));
		
		AbstractAction exampleAction = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				d_main.loadExampleDomain();	
				setVisible(false);
				dispose();
			}
		};
		
		AbstractAction loadAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if(d_main.fileLoadActions() != JFileChooser.CANCEL_OPTION) {
					setVisible(false);
					dispose();
				}
			}
		};
			
		AbstractAction newAction = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				d_main.newDomain();
				setVisible(false);
				dispose();
			}
		};
		
		builder.add(createButton("Load example data", FileNames.ICON_TIP, exampleAction), cc.xy(1, 3));
		builder.add(
				createLabel("Example studies and analyses with anti-depressants. Recommended for first time users."),
				cc.xy(3, 3));
		builder.add(createButton("Load data from file", FileNames.ICON_OPENFILE, loadAction), cc.xy(1, 5));
		builder.add(
				createLabel("Load an existing ADDIS data file stored on your computer."),
				cc.xy(3, 5));
		builder.add(createButton("Begin with no data", FileNames.ICON_NEWFILE, newAction), cc.xy(1, 7));
		builder.add(
				createLabel("Start with an empty file to build up your own analyses."),
				cc.xy(3, 7));
		
		JLabel labelFooter = new JLabel();
		labelFooter.setIcon(ImageLoader.getIcon(FileNames.IMAGE_FOOTER));
		builder.add(labelFooter, cc.xyw(1, 9, 3));
		setContentPane(builder.getPanel());
	}

	private JButton createButton(String text, String icon, AbstractAction action) {
		JButton button = new JButton(text, ImageLoader.getIcon(icon));
		button.setPreferredSize(new Dimension(151, 65));
		button.addActionListener(action);
		return button;
	}

	private JTextPane createLabel(String txt) {
		JTextPane pane = new JTextPane();
		pane.setText(txt);
		pane.setPreferredSize(new Dimension(285, 65));
		pane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 3));
		return pane;
	}

}
