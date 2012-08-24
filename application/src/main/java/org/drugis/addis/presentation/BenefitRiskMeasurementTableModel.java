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

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MeasurementSource.Listener;
import org.drugis.addis.entities.relativeeffect.Distribution;

@SuppressWarnings("serial")
public class BenefitRiskMeasurementTableModel<Alternative extends Entity> extends AbstractTableModel {
	
	protected final BenefitRiskAnalysis<Alternative> d_br;
	
	public BenefitRiskMeasurementTableModel(BenefitRiskAnalysis<Alternative> bra) {
		d_br = bra;
		d_br.getMeasurementSource().addMeasurementsChangedListener(new Listener() {
			public void notifyMeasurementsChanged() {
				fireTableDataChanged();
			}
		});
	}
	
	public int getColumnCount() {
		return d_br.getCriteria().size()+1;
	}

	public int getRowCount() {
		return d_br.getAlternatives().size();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	@Override
	public String getColumnName(int index) {
		if (index == 0) {
			return "Alternative";
		}
		return d_br.getCriteria().get(index-1).toString();	
	}
	
	@Override
	public Class<?> getColumnClass(int index) {
		if (index == 0) {
			return String.class;
		}
		return Distribution.class;	
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Alternative a = d_br.getAlternatives().get(rowIndex);

		if (columnIndex == 0) return a.getLabel();

		OutcomeMeasure om = d_br.getCriteria().get(columnIndex-1);
		return d_br.getMeasurement(om, a);
	}
}
