package org.drugis.addis.presentation;

import javax.swing.table.AbstractTableModel;

import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.convergence.GelmanRubinConvergence;

@SuppressWarnings("serial")
public class ConvergenceDiagnosticTableModel extends AbstractTableModel{

	private static final int COL_PARAM = 0;
	private static final int COL_ESTIMATE = 1;
	private MCMCResults d_results;

	public ConvergenceDiagnosticTableModel(MixedTreatmentComparison mtc) {
		d_results = mtc.getResults();
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
			return d_results.getParameters()[rowIndex];
		} else if (columnIndex == COL_ESTIMATE) {
			GelmanRubinConvergence.diagnose(d_results, d_results.getParameters()[rowIndex]);
		}
		return null;
	}

}
