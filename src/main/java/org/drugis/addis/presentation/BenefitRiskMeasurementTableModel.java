/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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
import org.drugis.addis.entities.analysis.MeasurementSource;
import org.drugis.addis.entities.analysis.MeasurementSource.Listener;

@SuppressWarnings("serial")
public class BenefitRiskMeasurementTableModel<Alternative extends Entity> extends AbstractTableModel {
	
	protected final BenefitRiskAnalysis<Alternative> d_br;
	private final PresentationModelFactory d_pmf;
	private final MeasurementSource<Alternative> d_source;
	
	public BenefitRiskMeasurementTableModel(BenefitRiskAnalysis<Alternative> bra, MeasurementSource<Alternative> source, PresentationModelFactory pmf) {
		d_br = bra;
		d_source = source;
		d_pmf = pmf;
		d_source.addMeasurementsChangedListener(new Listener() {
			public void notifyMeasurementsChanged() {
				fireTableDataChanged();
			}
		});
	}
	
	public int getColumnCount() {
		return d_br.getOutcomeMeasures().size()+1;
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
		return d_br.getOutcomeMeasures().get(index-1).toString();	
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Alternative a = d_br.getAlternatives().get(rowIndex);

		if (columnIndex == 0) return a.toString();

		OutcomeMeasure om = d_br.getOutcomeMeasures().get(columnIndex-1);
		return d_pmf.getLabeledModel(d_source.getMeasurement(a, om));
	}
}
