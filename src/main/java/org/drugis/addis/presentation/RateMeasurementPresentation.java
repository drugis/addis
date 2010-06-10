/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

import org.drugis.addis.entities.RateMeasurement;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class RateMeasurementPresentation extends PresentationModel<RateMeasurement> implements LabeledPresentationModel {
	public class LabelModel extends AbstractValueModel implements PropertyChangeListener {

		public LabelModel() {
			getBean().addPropertyChangeListener(this);
		}

		private Integer getSize() {
			return getBean().getSampleSize();
		}
		
		private Integer getRate() {
			return getBean().getRate();
		}
		
		private String generateLabel(Integer rate, Integer size) {
			if (rate == null || size == null) {
				return "INCOMPLETE";
			}
			return rate.toString() + "/" + size.toString();
		}
		
		public String getValue() {
			return generateLabel(getRate(), getSize());
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(RateMeasurement.PROPERTY_RATE)) {
				firePropertyChange("value", generateLabel((Integer) evt.getOldValue(), getSize()), generateLabel((Integer) evt.getNewValue(), getSize()));
			}
			else if (evt.getPropertyName().equals(RateMeasurement.PROPERTY_SAMPLESIZE)) {
				firePropertyChange("value", generateLabel(getRate(), (Integer) evt.getOldValue()), generateLabel(getRate(), (Integer) evt.getNewValue()));
			}
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("Label is Read-Only");
		}
	}

	public RateMeasurementPresentation(RateMeasurement bean) {
		super(bean);
	}

	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
	
	@Override
	public String toString() {
		return (String) getLabelModel().getValue(); 
	}
}
