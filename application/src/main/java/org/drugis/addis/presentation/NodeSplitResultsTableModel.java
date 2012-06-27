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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.collections15.BidiMap;
import org.drugis.addis.entities.DrugSet;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.presentation.MTCModelWrapper;
import org.drugis.mtc.summary.NodeSplitPValueSummary;
import org.drugis.mtc.summary.QuantileSummary;
import org.drugis.mtc.summary.Summary;

@SuppressWarnings("serial")
public class NodeSplitResultsTableModel extends AbstractTableModel {

	private static final String NA = "N/A";
	private static final int COL_NAME = 0;
	private static final int COL_DIRECT_EFFECT = 1;
	private static final int COL_INDIRECT_EFFECT = 2;
	private static final int COL_OVERALL = 3;
	private static final int COL_P_VALUE = 4;
	private NetworkMetaAnalysisPresentation d_pm;
	private Map<Parameter, Summary> d_quantileSummaries = new HashMap<Parameter, Summary>();
	private Map<Parameter, Summary> d_pValueSummaries = new HashMap<Parameter, Summary>();
	private PropertyChangeListener d_listener;
	private int d_rowcount;
	private List<BasicParameter> d_parameters;
	
	public NodeSplitResultsTableModel(NetworkMetaAnalysisPresentation pm) {
		d_pm = pm;
		
		d_listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fireTableDataChanged();
			}
		};
		
		if(d_pm.getSplitParameters().size() > 0) {
			initializeTable();
		}
	}

	private void initializeTable() {
		d_rowcount = d_pm.getSplitParameters().size();
		d_parameters = new ArrayList<BasicParameter>();
		for (BasicParameter p : d_pm.getSplitParameters()) {
			d_parameters.add(p);
			attachQuantileSummary(d_pm.getConsistencyModel(), p);
			attachQuantileSummary(d_pm.getNodeSplitModel(p), d_pm.getNodeSplitModel(p).getDirectEffect());
			attachQuantileSummary(d_pm.getNodeSplitModel(p), d_pm.getNodeSplitModel(p).getIndirectEffect());
			
			NodeSplitPValueSummary valuePvalue = d_pm.getNodeSplitModel(p).getNodeSplitPValueSummary();
			valuePvalue.addPropertyChangeListener(d_listener);
			d_pValueSummaries.put(p, valuePvalue);
		}
	}

	private void attachQuantileSummary(MTCModelWrapper<DrugSet> model, Parameter param) {
		QuantileSummary summary = model.getQuantileSummary(param);
		if(summary != null) { 
			summary.addPropertyChangeListener(d_listener); 
		}
		d_quantileSummaries.put(param, summary);
	}
	
	@Override
	public String getColumnName(int index) {
		switch(index) {
			case COL_NAME : return "Name"; 
			case COL_DIRECT_EFFECT : return "Direct Effect";
			case COL_INDIRECT_EFFECT : return "Indirect Effect";
			case COL_OVERALL : return "Overall";
			case COL_P_VALUE : return "P-Value";
			default: return null;
		}
	}
	
	public int getColumnCount() {
		return 5;
	}

	public int getRowCount() {
		return d_rowcount;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == COL_NAME) {
			return getDescription(getParameter(rowIndex));
		} else if (columnIndex >= COL_DIRECT_EFFECT && columnIndex <= COL_P_VALUE) {
			return getSummary(rowIndex, columnIndex);
		} 
		return null;
	}

	private Object getSummary(int rowIndex, int columnIndex) {
		switch(columnIndex) {
			case COL_DIRECT_EFFECT : return d_quantileSummaries.get(d_pm.getNodeSplitModel(getParameter(rowIndex)).getDirectEffect());
			case COL_INDIRECT_EFFECT : return d_quantileSummaries.get(d_pm.getNodeSplitModel(getParameter(rowIndex)).getIndirectEffect());
			case COL_OVERALL : return d_quantileSummaries.get(getParameter(rowIndex));
			case COL_P_VALUE : return d_pValueSummaries.get(getParameter(rowIndex)); 
			default : return NA;
		}
	}

	private BasicParameter getParameter(int rowIndex) {
		return d_parameters.get(rowIndex);
	}
	
	private String getDescription(BasicParameter p) { 
		BidiMap<DrugSet, Treatment> treatmentMap = d_pm.getBean().getBuilder().getTreatmentMap();
		return treatmentMap.getKey(p.getBaseline()).getLabel() + ", " + treatmentMap.getKey(p.getSubject()).getLabel();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex) {
			case COL_NAME : return String.class;
			case COL_DIRECT_EFFECT : return QuantileSummary.class;
			case COL_INDIRECT_EFFECT : return QuantileSummary.class;
			case COL_OVERALL : return QuantileSummary.class;
			case COL_P_VALUE : return NodeSplitPValueSummary.class;  
			default : return Object.class;
		}
	}
}
