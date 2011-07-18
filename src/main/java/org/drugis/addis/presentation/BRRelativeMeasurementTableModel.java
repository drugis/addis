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

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.DrugSet;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MeasurementSource.Listener;
import org.drugis.addis.entities.relativeeffect.Distribution;

@SuppressWarnings("serial")
public class BRRelativeMeasurementTableModel extends AbstractTableModel {	
	protected final MetaBenefitRiskAnalysis d_br;
	
	public BRRelativeMeasurementTableModel(MetaBenefitRiskAnalysis bra) {
		d_br = bra;
		d_br.getMeasurementSource().addMeasurementsChangedListener(new Listener() {
			public void notifyMeasurementsChanged() {
				fireTableDataChanged();
			}
		});
	}
	
	public int getColumnCount() {
		// One column for each non-baseline alternative and one for the criteria names.
		return d_br.getAlternatives().size();
	}

	public int getRowCount() {
		return d_br.getCriteria().size();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	@Override
	public String getColumnName(int index) {
		if (index == 0) {
			return "Criterion";
		}
		return d_br.getNonBaselineAlternatives().get(index-1).toString();	
	}
	
	@Override
	public Class<?> getColumnClass(int index) {
		if (index == 0) {
			return String.class;
		}
		return Distribution.class;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		OutcomeMeasure om = d_br.getCriteria().get(rowIndex);
		if (columnIndex == 0) return om.toString();

		DrugSet a = d_br.getNonBaselineAlternatives().get(columnIndex - 1);
		return d_br.getRelativeEffectDistribution(a, om);
	}
}
