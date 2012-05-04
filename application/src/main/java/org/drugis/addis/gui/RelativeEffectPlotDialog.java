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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.drugis.addis.forestplot.ForestPlot;
import org.drugis.addis.gui.components.RelativeEffectCanvas;
import org.drugis.addis.presentation.ForestPlotPresentation;
import org.drugis.common.gui.ImageExporter;

@SuppressWarnings("serial")
public class RelativeEffectPlotDialog extends JDialog {
	public RelativeEffectPlotDialog(final JDialog parent, ForestPlotPresentation pres, String title) { 
		super(parent, title);

		JPanel panel = new JPanel(new BorderLayout());

		JButton closeButton = new JButton("Close");
		closeButton.setMnemonic('c');
		closeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				dispose();
			}
		});

		final RelativeEffectCanvas canvas = new RelativeEffectCanvas(pres);

		JButton saveButton = new JButton("Save Image");
		
		saveButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				ForestPlot plot = canvas.getPlot();
				ImageExporter.writeImage(parent, plot, (int) plot.getSize().getWidth(),(int) plot.getSize().getHeight());
			}
		});
		
		panel.add(canvas, BorderLayout.NORTH);

		panel.add(saveButton, BorderLayout.CENTER);
		panel.add(closeButton, BorderLayout.SOUTH);
		panel.setBackground(Color.WHITE);
		
		getComponent(0).setBackground(Color.WHITE);
		setContentPane(panel);
		pack();
	}	
}
