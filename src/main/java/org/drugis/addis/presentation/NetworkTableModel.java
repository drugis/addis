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

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.ContinuousMeasurementEstimate;
import org.drugis.addis.entities.LogContinuousMeasurementEstimate;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.Treatment;

@SuppressWarnings("serial")
public class NetworkTableModel  extends AbstractTableModel implements TableModelWithDescription{
	private NetworkMetaAnalysisPresentation d_pm;
	private PresentationModelFactory d_pmf;
	MixedTreatmentComparison d_networkModel;

	public NetworkTableModel(NetworkMetaAnalysisPresentation pm, PresentationModelFactory pmf, MixedTreatmentComparison networkModel) {
		d_pm = pm;
		d_pmf = pmf;
		d_networkModel = networkModel;
	}

	public int getColumnCount() {
		return d_pm.getBean().getIncludedDrugs().size();
	}

	public int getRowCount() {
		return d_pm.getBean().getIncludedDrugs().size();
	}
	
	public String getDescriptionAt(int row, int col) {
		if (row == col) {
			return null;
		}
		return "\"" + d_pm.getBean().getIncludedDrugs().get(col) + "\" relative to \"" + d_pm.getBean().getIncludedDrugs().get(row) + "\"";
	}
	
	public Object getValueAt(int row, int col) {
		if (row == col) {
			//return d_pmf.getModel(d_pm.getBean().getIncludedDrugs().get(row));
			return d_pmf.getModel(d_pm.getBean().getArmList().get(row));
		} else if(!d_networkModel.isReady()){
			return d_pmf.getModel(new LogContinuousMeasurementEstimate(null, null));
		} 

		final Treatment drug1 = d_pm.getBean().getBuilder().getTreatment(d_pm.getBean().getIncludedDrugs().get(row).toString());
		final Treatment drug2 = d_pm.getBean().getBuilder().getTreatment(d_pm.getBean().getIncludedDrugs().get(col).toString());
		
		Estimate relEffect = d_networkModel.getRelativeEffect(drug1, drug2);

		
		if(d_pm.getBean().isContinuous())
			return d_pmf.getModel(new ContinuousMeasurementEstimate(relEffect.getMean(), relEffect.getStandardDeviation()));
		else
			// convert to Log Odds-ratio
			return d_pmf.getModel(new LogContinuousMeasurementEstimate(relEffect.getMean(), relEffect.getStandardDeviation()));
	}

	public String getDescription() {
		String result = "Network Meta-Analysis";
		if (d_networkModel instanceof InconsistencyModel)
			result += " (Inconsistency Model)";
		else if (d_networkModel instanceof ConsistencyModel)
			result += " (Consistency Model)";
		return result;
	}

	public String getTitle() {
		return getDescription();
	}
}
