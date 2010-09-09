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
		if (d_study.getNote(d_key) == null) {
			return null;
		}
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
