package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.drugis.mtc.BasicParameter;
import org.drugis.mtc.NodeSplitModel;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.NodeSplitPValueSummary;
import org.drugis.mtc.summary.QuantileSummary;
import org.drugis.mtc.summary.Summary;

@SuppressWarnings("serial")
public class PvalueTableModel extends AbstractTableModel {

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
	
	public PvalueTableModel(NetworkMetaAnalysisPresentation pm) {
		d_pm = pm;
		
		d_listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fireTableDataChanged();
			}
		};
		
		if(d_pm.getSplitParameters().size() > 0) {
			initialiseTable();
		}
	}

	private void initialiseTable() {
		d_rowcount = d_pm.getSplitParameters().size();
		d_parameters = new ArrayList<BasicParameter>();
		for (BasicParameter p : d_pm.getSplitParameters()) {
			d_parameters.add(p);
			Summary value = d_pm.getQuantileSummary(d_pm.getConsistencyModel(), p);
			value.addPropertyChangeListener(d_listener);
			d_quantileSummaries.put(p, value);
			
			NodeSplitModel splitModel = d_pm.getNodeSplitModel(p);
			Parameter direct = splitModel.getDirectEffect();
			Parameter indirect = splitModel.getIndirectEffect();
			QuantileSummary valueDirect = d_pm.getQuantileSummary(splitModel, direct);
			valueDirect.addPropertyChangeListener(d_listener);
			d_quantileSummaries.put(direct, valueDirect);
			
			QuantileSummary valueIndirect = d_pm.getQuantileSummary(splitModel, indirect);
			valueIndirect.addPropertyChangeListener(d_listener);
			d_quantileSummaries.put(indirect, valueIndirect);
			
			NodeSplitPValueSummary valuePvalue = d_pm.getNodeSplitPValueSummary(p);
			valuePvalue.addPropertyChangeListener(d_listener);
			d_pValueSummaries.put(p, valuePvalue);
		}
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
			return getParameter(rowIndex).getName();
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
