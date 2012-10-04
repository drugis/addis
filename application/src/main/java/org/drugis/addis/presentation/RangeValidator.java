/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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

import org.drugis.addis.presentation.ValueHolder;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public final class RangeValidator extends AbstractValueModel implements ValueHolder<Boolean> {
	private static final String PROPERTY_VALID = "value";
	private final ValueHolder<Double> d_range;
	private final double d_maximum;
	private final double d_minimum;
	private boolean d_valid = false;
	public RangeValidator(ValueHolder<Double> range, double minimum, double maximum) {
		d_range = range;
		d_maximum = maximum;
		d_minimum = minimum;
		d_range.addValueChangeListener(new PropertyChangeListener() {		
			public void propertyChange(PropertyChangeEvent evt) {
				validate();
			}
		});
		validate();
	}
	
	@Override
	public Boolean getValue() {
		return d_valid;
	}

	@Override
	public void setValue(Object newValue) {
		throw new UnsupportedOperationException("Cannot set value on validators");
	}
	
	public void validate() { 
		boolean oldValue = d_valid;
		d_valid =  d_range.getValue() < d_maximum && d_range.getValue() > d_minimum;
		firePropertyChange(PROPERTY_VALID, oldValue, d_valid);
	}
}