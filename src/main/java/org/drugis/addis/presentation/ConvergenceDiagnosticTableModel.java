package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.ConvergenceSummary;

@SuppressWarnings("serial")
public class ConvergenceDiagnosticTableModel extends AbstractTableModel{

	private static final String NA = "N/A";
	private static final int COL_PARAM = 0;
	private static final int COL_ESTIMATE = 1;
	private MCMCResults d_results;
	private Map<Parameter, ConvergenceSummary> d_summaries = new HashMap<Parameter, ConvergenceSummary>();
	private PropertyChangeListener d_listener;
	private static final NumberFormat s_format = new DecimalFormat("#.00");

	public ConvergenceDiagnosticTableModel(MixedTreatmentComparison mtc, ValueHolder<Boolean> modelBuiltModel) {		
		d_results = mtc.getResults();

		d_listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fireTableDataChanged();
			}
		}; 
		modelBuiltModel.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				initializeSummaries();
			}
		});
		if (modelBuiltModel.getValue()) {
			initializeSummaries();
		}
	}

	private void initializeSummaries() {
		for (Parameter p : d_results.getParameters()) {
			ConvergenceSummary value = new ConvergenceSummary(d_results, p);
			value.addPropertyChangeListener(d_listener);
			d_summaries.put(p, value);
		}
		fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int index) {
		if (index == COL_PARAM) {
			return "Parameter";
		} else if(index == COL_ESTIMATE) {
			return "PSRF";
		}
		return null;
	}

	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return d_results.getParameters().length;
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == COL_PARAM) {
			return getParameter(rowIndex);
		} else if (columnIndex == COL_ESTIMATE) {
			return getConvergence(rowIndex);
		}
		return null;
	}

	private String getConvergence(int rowIndex) {
		ConvergenceSummary summary = d_summaries.get(getParameter(rowIndex));
		return summary.getDefined() ? formatNumber(summary) : NA;
	}

	private String formatNumber(ConvergenceSummary summary) {
		if (Double.isNaN(summary.getScaleReduction())) {
			return NA;
		}
		return s_format.format(summary.getScaleReduction());
	}

	private Object getParameter(int rowIndex) {
		return d_results.getParameters()[rowIndex];
	}
}
