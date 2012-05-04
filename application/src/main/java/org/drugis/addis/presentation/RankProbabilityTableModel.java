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

import javax.swing.table.AbstractTableModel;

import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.summary.RankProbabilitySummary;

public class RankProbabilityTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 6045183840617200792L;
	private final RankProbabilitySummary d_summary;

	public RankProbabilityTableModel(RankProbabilitySummary summary) {
		d_summary = summary;
		d_summary.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				fireTableDataChanged();
			}
		});
	}

	public int getColumnCount() {
		return d_summary.getTreatments().size() + 1;
	}

	public int getRowCount() {
		return d_summary.getTreatments().size();
	}
	
	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return "Drug";
		} else {
			return "Rank " + column;
		}
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		if (column == 0) {
			return String.class;
		} else {
			return Double.class;
		}
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		Treatment treatment = d_summary.getTreatments().get(rowIndex);
		if (columnIndex == 0) {
			return treatment.getId();
		} else {
			return d_summary.getDefined() ? d_summary.getValue(treatment, columnIndex) : "";
		}
	}
}
