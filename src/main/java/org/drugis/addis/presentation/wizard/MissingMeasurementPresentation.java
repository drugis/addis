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

package org.drugis.addis.presentation.wizard;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.Study.StudyOutcomeMeasure;
import org.drugis.addis.entities.Study.WhenTaken;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class MissingMeasurementPresentation {

	private BasicMeasurement d_m;
	ValueModel d_missingModel = new AbstractValueModel() {
		private static final long serialVersionUID = 1897234897896897266L;

		@Override
		public Object getValue() {
			return d_study.getMeasurement(d_v.getValue(), d_a, d_wt) == null;
		}

		@Override
		public void setValue(Object newValue) {
			Object oldValue = getValue();
			if (newValue.equals(Boolean.TRUE)) {
				d_study.getMeasurements().remove(new Study.MeasurementKey(d_v, d_a, d_wt));
			} else {
				d_study.getMeasurements().put(new Study.MeasurementKey(d_v, d_a, d_wt), d_m);
			}
			fireValueChange(oldValue, newValue);
		}
	};
	private final Study d_study;
	private final StudyOutcomeMeasure<? extends Variable> d_v;
	private final Arm d_a;
	private final WhenTaken d_wt;
	
	public MissingMeasurementPresentation(final Study s, final StudyOutcomeMeasure<? extends Variable> v, WhenTaken wt, final Arm a) {
		d_study = s;
		d_v = v;
		d_wt = wt;
		d_a = a;
		d_m = d_study.getMeasurement(d_v.getValue(), d_a, d_wt) == null ? 
				d_study.buildDefaultMeasurement(d_v.getValue(), d_a) : 
					d_study.getMeasurement(d_v.getValue(), d_a, d_wt);
	}
	
	public BasicMeasurement getMeasurement() {
		return d_m;
	}
	
	public ValueModel getMissingModel() {
		return d_missingModel;
	}

	public String getDescription() {
		return d_missingModel.getValue().equals(Boolean.TRUE) ? "MISSING" : d_m.toString();
	}
}
