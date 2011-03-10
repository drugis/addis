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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.Note;
import org.drugis.addis.gui.wizard.AddStudyWizard;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class NoteView extends JDialog {

	private final List<Note> d_notes;
	private final String d_description;

	public NoteView(JFrame parent, String description, List<Note> notes) {
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
				"p"
				);
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		int row = 1;
		builder.addLabel("Notes for " + d_description, cc.xy(1, row));
		for (Note note : d_notes) {
			LayoutUtil.addRow(layout);
			row += 2;
			
			String text = "";/*"<i>2010-01-01</i> "*/ 
			switch (note.getSource()) {
			case CLINICALTRIALS:
				text += "<b>" + AddStudyWizard.DEFAULT_NOTETITLE + "</b><br/>";
				break;
			case MANUAL:
				text += "<b>User Note:</b><br/>";
			}
			text += note.getText().replace("\n", "<br/>\n");
			JComponent noteField = AuxComponentFactory.createHtmlField(text);
			//noteField.se
			builder.add(noteField, cc.xy(1, row));
		}
		
		return builder.getPanel();
	}
}
