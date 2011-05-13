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

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.Note;
import org.drugis.addis.gui.components.NotesView;
import org.drugis.common.gui.GUIHelper;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class NotesViewDialog extends JDialog {

	private final ObservableList<Note> d_notes;
	private final String d_description;

	public NotesViewDialog(JFrame parent, String description, ObservableList<Note> notes) {
		super(parent, "Notes");
		d_description = description;
		d_notes = notes;
		
		setModal(true);
		setPreferredSize(new Dimension(500, 300));
		getContentPane().add(new JScrollPane(buildPanel()), BorderLayout.CENTER);
		pack();
		GUIHelper.centerWindow(this, parent);
	}

	private JPanel buildPanel() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout( 
				"fill:0:grow",
				"p, 3dlu, p"
				);
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		builder.addLabel("Notes for " + d_description, cc.xy(1, 1));
		builder.add(new NotesView(d_notes), cc.xy(1, 3));
		
		return builder.getPanel();
	}
}
