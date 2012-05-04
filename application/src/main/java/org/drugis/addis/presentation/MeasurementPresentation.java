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

import org.drugis.addis.entities.Measurement;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;


public class MeasurementPresentation extends PresentationModel<Measurement> {
	private static final long serialVersionUID = 7208656609682779611L;
	
	private AbstractValueModel d_pgSize;
	
	public MeasurementPresentation(Measurement bean, AbstractValueModel abstractValueModel) {
		super(bean);
		d_pgSize = abstractValueModel;
		this.addBeanPropertyChangeListener(new MeasurementChangeListener());
		d_pgSize.addPropertyChangeListener(new MeasurementChangeListener());
	}
	
	private class MeasurementChangeListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			int mySize = (Integer) getValue("sampleSize");
			if (evt.getSource() == d_pgSize) {
				setValue("sampleSize", d_pgSize.getValue());
			}
			else if (mySize > (Integer) d_pgSize.getValue()){
				setValue("sampleSize", d_pgSize.getValue());
			}
		}
	}
}
