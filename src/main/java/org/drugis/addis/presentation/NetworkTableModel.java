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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.LogGaussian;
import org.drugis.addis.entities.relativeeffect.NetworkRelativeEffect;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.Treatment;
import org.drugis.mtc.summary.NormalSummary;

@SuppressWarnings("serial")
public class NetworkTableModel extends AbstractTableModel {
	private final Object d_na;
	private NetworkMetaAnalysisPresentation d_pm;
	private PresentationModelFactory d_pmf;
	MixedTreatmentComparison d_networkModel;
	private final PropertyChangeListener d_listener;
	private NetworkMetaAnalysis d_model;
	
	public NetworkTableModel(NetworkMetaAnalysisPresentation pm, PresentationModelFactory pmf, MixedTreatmentComparison networkModel) {
		d_pm = pm;
		d_pmf = pmf;
		d_networkModel = networkModel;
		d_na = d_pmf.getLabeledModel(new NetworkRelativeEffect<Measurement>());
		d_model = d_pm.getBean();
		d_listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fireTableDataChanged();
			}
		};
		
		// Listen to summaries
		List<Drug> drugs = d_pm.getIncludedDrugs();
		for(Drug d1 : drugs) {
			for (Drug d2 : drugs) {
				if (!d1.equals(d2)) {
					attachListener(networkModel, d1, d2);
				}
			}
		}
	}

	private void attachListener(MixedTreatmentComparison networkModel, Drug d1, Drug d2) {
		NormalSummary normalSummary = getSummary(d_model.getTreatment(d1), d_model.getTreatment(d2));
		normalSummary.addPropertyChangeListener(d_listener);
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
		return "\"" + d_pm.getIncludedDrugs().get(col) + "\" relative to \"" + d_pm.getBean().getIncludedDrugs().get(row) + "\"";
	}
	
	public Object getValueAt(int row, int col) {
		if (row == col) {
			return d_pmf.getModel(d_pm.getIncludedDrugs().get(row));
		} if (!d_networkModel.isReady()) {
			return d_na;
		}

		NormalSummary re = getSummary(getTreatment(row), getTreatment(col));
		if (!re.getDefined()) {
			return d_na;
		}
		
		double mu = re.getMean();
		double sigma = re.getStandardDeviation();
		Distribution dist = (d_pm.isContinuous()) ?  new Gaussian(mu, sigma) : new LogGaussian(mu, sigma);
		
		return d_pmf.getLabeledModel(dist);
	}

	private NormalSummary getSummary(final Treatment drug1,
			final Treatment drug2) {
		return d_pm.getNormalSummary(d_networkModel, d_networkModel.getRelativeEffect(drug1, drug2));
	}

	private Treatment getTreatment(int idx) {
		return d_model.getTreatments().get(idx);
	}
}
