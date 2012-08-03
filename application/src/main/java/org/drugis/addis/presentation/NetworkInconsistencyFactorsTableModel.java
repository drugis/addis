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
import java.util.Set;
import java.util.TreeSet;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.drugis.addis.entities.mtcwrapper.InconsistencyWrapper;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.parameterization.InconsistencyParameter;
import org.drugis.mtc.summary.QuantileSummary;
import org.drugis.mtc.summary.Summary;

@SuppressWarnings("serial")
public class NetworkInconsistencyFactorsTableModel  extends AbstractTableModel {
	private static final String NA = "N/A";
	private NetworkMetaAnalysisPresentation d_pm;
	private PropertyChangeListener d_listener;
	private boolean d_listenersAttached;
	private ValueHolder<Boolean> d_modelConstructed;

	public NetworkInconsistencyFactorsTableModel(NetworkMetaAnalysisPresentation pm) {
		d_pm = pm;
		
		d_listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fireTableDataChanged();
			}
		};
		
		d_modelConstructed = d_pm.getWrappedModel(d_pm.getInconsistencyModel()).isModelConstructed();
		if (d_modelConstructed.getValue().equals(true)) {
			attachListeners();
		}
		
		d_modelConstructed.addValueChangeListener(new PropertyChangeListener() {
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
		
		List<Parameter> parameterList = d_pm.getInconsistencyModel().getInconsistencyFactors();
		for(Parameter p : parameterList ) {
			QuantileSummary summary = d_pm.getInconsistencyModel().getQuantileSummary(p);
			summary.addPropertyChangeListener(d_listener);
		}
		d_listenersAttached = true;
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		return column == 0 ? String.class : Summary.class;
		
	}
	
	@Override
	public String getColumnName(int column) {
		return column == 0 ? "Cycle" : "Median (95% CrI)";
	}

	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		if(d_modelConstructed.getValue().equals(true))
			return d_pm.getInconsistencyModel().getInconsistencyFactors().size();
		return 0;
	}
	
	public Object getValueAt(int row, int col) {
		if (d_modelConstructed.getValue().equals(false)){
			return NA;
		}
		InconsistencyWrapper model = d_pm.getInconsistencyModel();
		InconsistencyParameter ip = (InconsistencyParameter)model.getInconsistencyFactors().get(row);
		if(col == 0) {
			Set<String> descriptions = new TreeSet<String>();
			for(Treatment t : ip.getCycle()) { 
				TreatmentDefinition key = d_pm.getBean().getBuilder().getTreatmentMap().getKey(t);
				descriptions.add(key.getLabel());
			}
			return StringUtils.join(descriptions, ", ");
		} else {
			return model.getQuantileSummary(ip);
		}
	}
}
