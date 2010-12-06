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

import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.InconsistencyParameter;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.NormalSummary;

@SuppressWarnings("serial")
public class NetworkInconsistencyFactorsTableModel  extends AbstractTableModel implements TableModelWithDescription{
	private static final String NA = "N/A";
	private NetworkMetaAnalysisPresentation d_pm;
	private PresentationModelFactory d_pmf;
	private PropertyChangeListener d_listener;
	private boolean d_listenersAttached;

	public NetworkInconsistencyFactorsTableModel(NetworkMetaAnalysisPresentation pm, PresentationModelFactory pmf) {
		d_pm = pm;
		d_pmf = pmf;
		
		d_listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fireTableDataChanged();
			}
		};
		
		if (d_pm.getInconsistencyModelConstructedModel().getValue().equals(true)) {
			attachListeners();
		}
		
		d_pm.getInconsistencyModelConstructedModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue().equals(true)) {
					fireTableStructureChanged();					
					attachListeners();
				}
			}
		});
	}
	
	private void attachListeners() {
		if (d_listenersAttached) return;
		
		List<Parameter> parameterList = d_pm.getInconsistencyFactors();
		for(Parameter p : parameterList ) {
			NormalSummary normalSummary = d_pm.getNormalSummary(getModel(), p);
			normalSummary.addPropertyChangeListener(d_listener);
		}
		d_listenersAttached = true;
	}
	
	@Override
	public String getColumnName(int column) {
		return column == 0 ? "Cycle" : "Confidence Interval";
	}

	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		if(d_pm.getInconsistencyModelConstructedModel().getValue().equals(true))
			return d_pm.getInconsistencyFactors().size();
		return 0;
	}
	
	public String getValueAt(int row, int col) {
		if(d_pm.getInconsistencyModelConstructedModel().getValue().equals(false)){
			return NA;
		}
		InconsistencyModel model = getModel();
		InconsistencyParameter ip = 
			(InconsistencyParameter)model.getInconsistencyFactors().get(row);
		if(col == 0){
			String out = "";
			for (int i=0; i<ip.treatmentList().size() - 1; ++i){
				out += ip.treatmentList().get(i).id() + ", ";
			}
			return out.substring(0, out.length()-2);
		} else if (model.isReady()){
			NormalSummary summary = d_pm.getNormalSummary(model, ip);
			Gaussian dist = new Gaussian(summary.getMean(), summary.getStandardDeviation());
			return (String) d_pmf.getLabeledModel(dist).getLabelModel().getValue();
		} else
			return NA;
	}

	private InconsistencyModel getModel() {
		return (InconsistencyModel) d_pm.getInconsistencyModel();
	}

	public String getDescription() {
		return "Inconsistency Factors";
	}

	public String getTitle() {
		return getDescription();
	}
}
