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

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;

@SuppressWarnings("serial")
public class PopulationCharTableModel extends StudyMeasurementTableModel {
	public PopulationCharTableModel(Study study, PresentationModelFactory pmf) {
		super(study, pmf, Variable.class);
		
		for (Variable v : d_study.getVariables(Variable.class)) {
			d_study.getMeasurement(v).addPropertyChangeListener(d_measurementListener);
		}		
	}

	@Override
	public int getColumnCount() {
		return super.getColumnCount() + 1;
	}

	public Object getValueAt(int row, int col) {
		if (col == getColumnCount() - 1) {
			return d_study.getMeasurement(getCharAt(row));
		} else {
			return super.getValueAt(row, col);
		}
	}

	private Variable getCharAt(int charIdx) {
		return d_study.getVariables(Variable.class).get(charIdx);
	}
	
	@Override
	public String getColumnName(int col) {
		if (col == getColumnCount() - 1) {
			return "Overall";
		} else {
			return super.getColumnName(col);
		}
	}
}
