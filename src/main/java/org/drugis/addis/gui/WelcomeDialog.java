package org.drugis.addis.gui;

import java.awt.Color;
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
import javax.swing.SwingConstants;

import org.drugis.addis.AppInfo;
import org.drugis.addis.FileNames;
import org.drugis.common.ImageLoader;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class WelcomeDialog extends JDialog {
	private static final int COMP_HEIGHT = 65;
	private static final int FULL_WIDTH = 446; // width of the header image
	private static final int SPACING = 3;
	private static final int BUTTON_WIDTH = 151;
	private static final int TEXT_WIDTH = FULL_WIDTH - SPACING - BUTTON_WIDTH;
	
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
		final AbstractAction exampleAction = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				d_main.loadExampleDomain();	
				setVisible(false);
				dispose();
			}
		};
		
		final AbstractAction loadAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if(d_main.fileLoadActions() != JFileChooser.CANCEL_OPTION) {
					setVisible(false);
					dispose();
				}
			}
		};
			
		final AbstractAction newAction = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				d_main.newDomain();
				setVisible(false);
				dispose();
			}
		};
		
		FormLayout layout = new FormLayout(
				"left:pref, " + SPACING + "px, left:pref", 
				"p, 3dlu, p, " + SPACING + "px, p, " + SPACING + "px, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();	
		
		builder.add(createImageLabel(FileNames.IMAGE_HEADER), cc.xyw(1, 1, 3));
		
		builder.add(createButton("Load example", FileNames.ICON_TIP, exampleAction), cc.xy(1, 3));
		builder.add(
				createLabel("Example studies and analyses with anti-depressants. Recommended for first time users."),
				cc.xy(3, 3));
		
		builder.add(createButton("Open file", FileNames.ICON_OPENFILE, loadAction), cc.xy(1, 5));
		builder.add(
				createLabel("Load an existing ADDIS data file stored on your computer."),
				cc.xy(3, 5));
		
		builder.add(createButton("New dataset", FileNames.ICON_NEWFILE, newAction), cc.xy(1, 7));
		builder.add(
				createLabel("Start with an empty file to build up your own data and analyses."),
				cc.xy(3, 7));
		
		builder.add(createImageLabel(FileNames.IMAGE_FOOTER), cc.xyw(1, 9, 3));
		
		setContentPane(builder.getPanel());
	}

	private JLabel createImageLabel(String imageHeader) {
		JLabel label = new JLabel();
		label.setIcon(ImageLoader.getIcon(imageHeader));
		return label;
	}

	private JButton createButton(String text, String icon, AbstractAction action) {
		JButton button = new JButton(text, ImageLoader.getIcon(icon));
		button.setPreferredSize(new Dimension(BUTTON_WIDTH, COMP_HEIGHT));
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		button.addActionListener(action);
		return button;
	}

	private JTextPane createLabel(String txt) {
		JTextPane pane = new JTextPane();
		pane.setText(txt);
		pane.setPreferredSize(new Dimension(TEXT_WIDTH, COMP_HEIGHT));
		pane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		pane.setBackground(Color.WHITE);
		return pane;
	}

}
