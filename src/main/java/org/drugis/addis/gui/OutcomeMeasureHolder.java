/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package org.drugis.addis.gui;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;


import com.jgoodies.binding.beans.Model;

@SuppressWarnings("serial")
public class OutcomeMeasureHolder extends Model { // TODO: implement as adapter or something?
	
	private OutcomeMeasure d_om;
	
	public static final String PROPERTY_OUTCOME_MEASURE = "outcomeMeasure";
	
	public void setEndpoint(Endpoint e) {
		OutcomeMeasure oldVal = d_om;
		d_om = e;
		firePropertyChange(PROPERTY_OUTCOME_MEASURE, oldVal, d_om);
	}
	public OutcomeMeasure getEndpoint() {
		return d_om;
	}
	public List<OutcomeMeasure> asList() {
		List<OutcomeMeasure> list = new ArrayList<OutcomeMeasure>();
		if (d_om != null) {
			list.add(d_om);
		}
		return list;
	}
}