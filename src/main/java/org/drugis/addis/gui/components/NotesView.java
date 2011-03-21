package org.drugis.addis.gui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
			final ValueModel model = new ValueHolder("Click button to add note");
			JScrollPane editNote = AuxComponentFactory.createTextArea(model, true);
			JButton addNoteButton = new JButton(ImageLoader.getIcon(FileNames.ICON_NOTE_NEW));
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
