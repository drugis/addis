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

package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Study;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class StudyCharacteristicHolder extends AbstractValueModel {
	protected Study d_study;
	protected Characteristic d_char;
	
	public StudyCharacteristicHolder(Study study, Characteristic characteristic) {
		d_study = study;
		d_char = characteristic; 
		d_study.addPropertyChangeListener(new CharChangedListener());
	}

	public void setValue(Object newValue) {
		throw new RuntimeException("This CharacteristicHolder is immutable");
	}

	public Object getValue() {
		return d_study.getCharacteristic(d_char);
	}
	
	public Characteristic getCharacteristic() {
		return d_char;
	}
	
	private class CharChangedListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(Study.PROPERTY_CHARACTERISTICS)) {
				if (evt.getNewValue().equals(d_char))
					firePropertyChange("value", null, d_study.getCharacteristic(d_char));
			}
		}
	}
}
