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

package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.FrequencyMeasurement;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class FrequencyMeasurementPresentation extends PresentationModel<FrequencyMeasurement>
		implements LabeledPresentation {
	
	public class FrequencyModel extends AbstractValueModel implements PropertyChangeListener {
		private String d_cat;

		public FrequencyModel(String category) {
			d_cat = category;
			getBean().addPropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(FrequencyMeasurement.PROPERTY_FREQUENCIES)) {
				fireValueChange(null, getValue());
			}
		}

		public Object getValue() {
			return getBean().getFrequency(d_cat);
		}

		public void setValue(Object newValue) {
			if (newValue instanceof Integer)
				getBean().setFrequency(d_cat, (Integer)newValue);
			else
				throw new IllegalArgumentException("Can only set frequencies with an Integer");
		}
	}
	
	public FrequencyMeasurementPresentation(FrequencyMeasurement bean) {
		super(bean);
	}
	
	public AbstractValueModel getFrequencyModel(String category) {
		return new FrequencyModel(category);
	}

	public AbstractValueModel getLabelModel() {
		return new DefaultLabelModel(getBean());
	}
}
