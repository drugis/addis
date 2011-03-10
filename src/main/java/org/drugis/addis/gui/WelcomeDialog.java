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

import java.awt.Cursor;
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
import javax.swing.border.EtchedBorder;

import org.drugis.addis.AppInfo;
import org.drugis.addis.FileNames;
import org.drugis.common.ImageLoader;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class WelcomeDialog extends JDialog { // FIXME: should be JFrame
	private static final int COMP_HEIGHT = 65;
	private static final int FULL_WIDTH = 446; // width of the header image
	private static final int SPACING = 3;
	private static final int BUTTON_WIDTH = 151;
	private static final int TEXT_WIDTH = FULL_WIDTH - SPACING - BUTTON_WIDTH;
	
	private Main d_main;

	public WelcomeDialog(Main main) {
		super();
		d_main = main;
		setTitle("Welcome to " + AppInfo.getAppName());		
		initComps();
		setResizable(false);
		pack();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);				
			}
		});
	}
	
	private void closeWelcome() {
		setVisible(false);
		dispose();
	}
	
	private void initComps() {
		final AbstractAction exampleAction = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				d_main.loadExampleDomain();
				closeWelcome();
			}
		};
		
		final AbstractAction loadAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if(d_main.fileLoadActions() != JFileChooser.CANCEL_OPTION) {
					closeWelcome();
				}
			}
		};
			
		final AbstractAction newAction = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				d_main.newFileActions();
				closeWelcome();
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
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.addActionListener(action);
		return button;
	}

	private JTextPane createLabel(String txt) {
		JTextPane pane = new JTextPane();
		pane.setText(txt);
		pane.setPreferredSize(new Dimension(TEXT_WIDTH, COMP_HEIGHT));
		pane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		pane.setEditable(false);
		return pane;
	}

}
