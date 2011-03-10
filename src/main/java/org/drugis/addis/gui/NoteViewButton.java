package org.drugis.addis.gui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Note;
import org.drugis.common.ImageLoader;

@SuppressWarnings("serial")
public class NoteViewButton extends JButton {
	Window d_noteView;
	private List<Note> d_notes;
	private JFrame d_parent;
	private final String d_description;
	
	public NoteViewButton(JFrame parent, String description, List<Note> notes) {
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
			d_noteView = new NoteView(d_parent, d_description, d_notes);
		}
		d_noteView.setVisible(true);
	}
}
