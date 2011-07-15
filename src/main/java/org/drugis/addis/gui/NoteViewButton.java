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

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Note;
import org.drugis.common.ImageLoader;

import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class NoteViewButton extends JButton {
	Window d_noteView;
	private ObservableList<Note> d_notes;
	private Window d_parent;
	private final String d_description;
	
	public NoteViewButton(Window parent, String description, ObservableList<Note> notes) {
		super(ImageLoader.getIcon(FileNames.ICON_NOTE));
		d_parent = parent;
		d_description = description;
		d_notes = notes;
		
		if (d_notes == null || d_notes.isEmpty()) {
			setEnabled(false);
		}
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showView();
			}
		});
	}

	private void showView() {
		if (d_noteView == null) {
			d_noteView = new NotesViewDialog(d_parent, d_description, d_notes);
		}
		d_noteView.setVisible(true);
	}
}
