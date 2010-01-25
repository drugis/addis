package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Study;

import com.jgoodies.binding.value.AbstractValueModel;

public class StudyNoteHolder extends AbstractValueModel {
	private static final long serialVersionUID = 173822319864850880L;
	
	protected Study d_study;
	protected Object d_key;
	
	public StudyNoteHolder(Study study, Object key) {
		d_study = study;
		d_key = key;
		d_study.addPropertyChangeListener(new NoteChangedListener());
	}
	
	public String getValue() {
		return d_study.getNote(d_key).getText();
	}

	public void setValue(Object newValue) {
		throw new RuntimeException("This StudyNoteHolder is immutable");	
	}

	private class NoteChangedListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(Study.PROPERTY_NOTE)) {
				if (evt.getNewValue().equals(d_key))
					firePropertyChange("value", null, d_study.getNote(d_key).getText());
			}
		}
	}
}
