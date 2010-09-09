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

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.InconsistencyParameter;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;

@SuppressWarnings("serial")
public class NetworkInconsistencyFactorsTableModel  extends AbstractTableModel implements TableModelWithDescription{
	private NetworkMetaAnalysisPresentation d_pm;
	private PresentationModelFactory d_pmf;

	public NetworkInconsistencyFactorsTableModel(NetworkMetaAnalysisPresentation pm, PresentationModelFactory pmf) {
		d_pm = pm;
		d_pmf = pmf;
		attachModelListener(d_pm.getBean().getInconsistencyModel());
	}
	
	private void attachModelListener(MixedTreatmentComparison networkModel) {
		networkModel.addProgressListener(new ProgressListener() {
			public void update(MCMCModel mtc, ProgressEvent event) {
				if(event.getType() == EventType.MODEL_CONSTRUCTION_FINISHED || event.getType() == EventType.SIMULATION_FINISHED)
					fireTableDataChanged();
			}
		});
	}
	
	@Override
	public String getColumnName(int column) {
		return column == 0 ? "Cycle" : "Confidence Interval";
	}

	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		if(d_pm.isModelConstructionFinished())
			return d_pm.getBean().getInconsistencyFactors().size();
		return 0;
	}
	
	public String getValueAt(int row, int col) {
		if(!d_pm.isModelConstructionFinished()){
			return "n/a";
		}
		InconsistencyParameter ip = d_pm.getBean().getInconsistencyModel().getInconsistencyFactors().get(row);
		if(col == 0){
			String out = "";
			for (int i=0; i<ip.treatmentList().size() - 1; ++i){
				out += ip.treatmentList().get(i).id() + ", ";
			}
			return out.substring(0, out.length()-2);
		} else if (d_pm.getBean().getInconsistencyModel().isReady()){
			Estimate ic = d_pm.getBean().getInconsistency(ip);

			BasicContinuousMeasurement contMeas = new BasicContinuousMeasurement(ic.getMean(), ic.getStandardDeviation(), 0);
			ContinuousMeasurementPresentation<BasicContinuousMeasurement> pm = 
								(ContinuousMeasurementPresentation<BasicContinuousMeasurement>) d_pmf.getModel(contMeas);
			return pm.normConfIntervalString();
		} else
			return "n/a";
	}

	public String getDescription() {
		return "Inconsistency Factors";
	}

	public String getTitle() {
		return getDescription();
	}
}
