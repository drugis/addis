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

package org.drugis.addis.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.Source;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.Main;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class NotesView extends JPanel {
	private static final String DEFAULT_NOTE_TEXT = "To add a note, enter text here and then press the button to the right";
	private final ObservableList<Note> d_notes;
	private final boolean d_editable;

	public NotesView(ObservableList<Note> notes, boolean editable) {
		super(new BorderLayout());
		d_notes = notes;
		d_editable = editable;
		add(buildPanel(), BorderLayout.CENTER);
		d_notes.addListDataListener(new NotesListener());
	}
	
	public NotesView(ObservableList<Note> notes) {
		this(notes, false);
	}
	
	private JPanel buildPanel() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout( 
				"fill:0:grow, 3dlu, pref",
				"p"
				);
		PanelBuilder builder = new PanelBuilder(layout);
		
		int row = 1;
		for (Note note : d_notes) {
			builder.add(AuxComponentFactory.createNoteView(note, d_editable), cc.xyw(1, row, 3));			
			LayoutUtil.addRow(layout);
			row += 2;
		}
		if (d_editable) {
			final ValueModel model = new ValueHolder(DEFAULT_NOTE_TEXT);
			JScrollPane editNote = AuxComponentFactory.createTextArea(model, true);
			final JButton addNoteButton = new JButton(Main.IMAGELOADER.getIcon(FileNames.ICON_NOTE_NEW));
			addNoteButton.setEnabled(false);
			
			final JTextArea area = (JTextArea) editNote.getViewport().getView();
			area.setBackground(AuxComponentFactory.COLOR_NOTE_EDIT);
			
			area.getDocument().addDocumentListener(new DocumentListener() {
				private void validateComponents() {
					if(area.getText().equals("") || area.getText().equals(DEFAULT_NOTE_TEXT)) addNoteButton.setEnabled(false);
					else addNoteButton.setEnabled(true);
				}
				public void changedUpdate(DocumentEvent e) {
					validateComponents();
				}
				public void removeUpdate(DocumentEvent e) {
					validateComponents();
				}
				public void insertUpdate(DocumentEvent e) {
					validateComponents();
				}
			});
			area.addFocusListener(new FocusListener() {
				
				public void focusLost(FocusEvent e) {
					if (area.getText().equals("")) {
						area.setText(DEFAULT_NOTE_TEXT);
					}
				}
				
				public void focusGained(FocusEvent e) {
					if (area.getText().equals(DEFAULT_NOTE_TEXT)) {
						area.setText("");
					}
				}
			});
			
			builder.add(editNote, cc.xy(1, row));
			builder.add(addNoteButton, cc.xy(3, row));
			
			addNoteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					d_notes.add(new Note(Source.MANUAL, (String) model.getValue()));
				}});
		}
		
		return builder.getPanel();
	}
	
	private class NotesListener implements ListDataListener {
		public void update() {
			setVisible(false);
			removeAll();
			add(buildPanel(), BorderLayout.CENTER);
			setVisible(true);
		}
		@Override
		public void contentsChanged(ListDataEvent e) {
			update();
		}
		@Override
		public void intervalAdded(ListDataEvent e) {
			update();
		}
		@Override
		public void intervalRemoved(ListDataEvent e) {
			update();
		}
	}
}
