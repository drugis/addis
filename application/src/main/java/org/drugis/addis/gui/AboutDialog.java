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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.drugis.addis.AppInfo;
import org.drugis.addis.FileNames;
import org.drugis.addis.util.CopyrightInfo;
import org.drugis.common.ImageLoader;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

	public AboutDialog(JFrame parent) {
		super(parent);
		setTitle("About " + AppInfo.getAppName());		
		initComps();
		pack();
	}
	
	private void initComps() {
		CopyrightInfo copyInfo = new CopyrightInfo();
		setPreferredSize(new Dimension(450, copyInfo.getAboutLineCount() * 26));
		String title = AppInfo.getAppName() + " v" + AppInfo.getAppVersion();
		
		JPanel panel = new JPanel(new BorderLayout());
		
		JLabel titleLabel = new JLabel(title);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		Font font = titleLabel.getFont();
		font = font.deriveFont(Font.BOLD, 14F);
		titleLabel.setFont(font);
		panel.add(titleLabel, BorderLayout.NORTH);
		
		
		JPanel licPanel = new JPanel(new BorderLayout());
		JTextArea licArea = new JTextArea(copyInfo.getAboutText()) {
			@Override
			public Insets getInsets() {
				return new Insets(5, 5, 5, 5);
			}
		};
		licArea.setWrapStyleWord(true);
		licArea.setLineWrap(true);
		licArea.setEditable(false);
		licArea.setOpaque(false);
		
		licPanel.add(licArea, BorderLayout.CENTER);
		JLabel linkLabel = GUIFactory.buildSiteLink();
		linkLabel.setHorizontalAlignment(SwingConstants.CENTER);
		licPanel.add(linkLabel, BorderLayout.SOUTH);
		licPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		panel.add(licPanel, BorderLayout.CENTER);
		JLabel label = new JLabel();
		label.setIcon(ImageLoader.getIcon(FileNames.ICON_DOCTOR));
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.add(label, BorderLayout.EAST);
		
		JButton closeButton = new JButton("Close");
		closeButton.setMnemonic('c');
		closeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				dispose();
			}
		});
		
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.add(closeButton, BorderLayout.SOUTH);		
		
		setContentPane(panel);
	}
}
