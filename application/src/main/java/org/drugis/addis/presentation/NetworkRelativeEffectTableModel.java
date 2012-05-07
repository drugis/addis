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
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.DrugSet;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.summary.QuantileSummary;

@SuppressWarnings("serial")
public class NetworkRelativeEffectTableModel extends AbstractTableModel {
	private NetworkMetaAnalysisPresentation d_pm;
	MixedTreatmentComparison d_networkModel;
	private final PropertyChangeListener d_listener;
	
	public NetworkRelativeEffectTableModel(NetworkMetaAnalysisPresentation pm, MixedTreatmentComparison networkModel) {
		d_pm = pm;
		d_networkModel = networkModel;
		d_listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fireTableDataChanged();
			}
		};
		
		// Listen to summaries
		List<DrugSet> drugs = d_pm.getIncludedDrugs();
		for(DrugSet d1 : drugs) {
			for (DrugSet d2 : drugs) {
				if (!d1.equals(d2)) {
					attachListener(networkModel, d1, d2);
				}
			}
		}
	}

	private void attachListener(MixedTreatmentComparison networkModel, DrugSet d1, DrugSet d2) {
		QuantileSummary quantileSummary = getSummary(d_pm.getBean().getTreatment(d1), d_pm.getBean().getTreatment(d2));
		quantileSummary.addPropertyChangeListener(d_listener);
	}

	public int getColumnCount() {
		return d_pm.getIncludedDrugs().size();
	}

	public int getRowCount() {
		return d_pm.getIncludedDrugs().size();
	}
	
	public String getDescriptionAt(int row, int col) {
		if (row == col) {
			return null;
		}
		return "\"" + getDrugAt(col).getLabel() + "\" relative to \"" + getDrugAt(row).getLabel() + "\"";
	}

	private DrugSet getDrugAt(int idx) {
		return d_pm.getIncludedDrugs().get(idx);
	}
	
	public Object getValueAt(int row, int col) {
		if (row == col) {
			return getDrugAt(row);
		}
		return getSummary(getTreatment(row), getTreatment(col));
	}
	
	private QuantileSummary getSummary(final Treatment drug1, final Treatment drug2) {
		return d_pm.getQuantileSummary(d_networkModel, d_networkModel.getRelativeEffect(drug1, drug2));
	}

	private Treatment getTreatment(int idx) {
		return d_pm.getBean().getTreatment(getDrugAt(idx));
	}
}
