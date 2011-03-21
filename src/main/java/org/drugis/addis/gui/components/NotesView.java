package org.drugis.addis.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.Source;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.presentation.NotesModel;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class NotesView extends JPanel {
	private static final String DEFAULT_NOTE_TEXT = "To add a note, click here then press the button to the right";
	private final NotesModel d_notes;
	private final boolean d_editable;

	public NotesView(NotesModel notes, boolean editable) {
		super(new BorderLayout());
		d_notes = notes;
		d_editable = editable;
		add(buildPanel(), BorderLayout.CENTER);
		d_notes.addPropertyChangeListener(new NotesListener());
	}
	
	public NotesView(NotesModel notes) {
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
		for (Note note : d_notes.getNotes()) {
			builder.add(AuxComponentFactory.createNoteView(note, d_editable), cc.xyw(1, row, 3));			
			LayoutUtil.addRow(layout);
			row += 2;
		}
		if (d_editable) {
			final ValueModel model = new ValueHolder(DEFAULT_NOTE_TEXT);
			JScrollPane editNote = AuxComponentFactory.createTextArea(model, true);
			final JButton addNoteButton = new JButton(ImageLoader.getIcon(FileNames.ICON_NOTE_NEW));
			addNoteButton.setEnabled(false);
			
			final JTextArea area = (JTextArea) editNote.getViewport().getView();
			
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
					d_notes.addNote(new Note(Source.MANUAL, (String) model.getValue()));
				}});
		}
		
		return builder.getPanel();
	}
	
	private class NotesListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			setVisible(false);
			removeAll();
			add(buildPanel(), BorderLayout.CENTER);
			setVisible(true);
		}
	}
}
