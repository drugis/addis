package org.drugis.addis.presentation;

import javax.swing.table.AbstractTableModel;


import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.NormalSummary;

public class MixedComparisonTableModel extends AbstractTableModel implements TableModelWithDescription {

	private static final int RANDOM_EFFECTS = 0;
	private NetworkMetaAnalysisPresentation d_pm;
	private PresentationModelFactory d_pmf;
	private MixedTreatmentComparison d_mtc;
	
	public MixedComparisonTableModel(NetworkMetaAnalysisPresentation pm, PresentationModelFactory pmf, MixedTreatmentComparison mtc) {
		d_pm = pm;
		d_pmf = pmf;
		d_mtc = mtc;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0) {
			return String.class;
		} else {
			return NormalSummary.class;
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return column == 0 ? " " : "Median (95% CrI)";
	}
	
	public String getDescription() {
		return null;
	}

	public int getRowCount() {
		return isInconsistency() ? 2 : 1;
	}

	private boolean isInconsistency() {
		return (d_mtc instanceof InconsistencyModel);
	}

	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return getRowDescription(row);
		} else {
			return getEstimate(row);
		}
	}

	private NormalSummary getEstimate(int row) {
		InconsistencyModel model = d_pm.getBean().getInconsistencyModel();
		if (model.isReady()){
			if (row == RANDOM_EFFECTS) {
				return getRandomEffectsSummary();
			} else {
				return getInconsistencySummary();
			}
		}
		return null;
	}

	private NormalSummary getInconsistencySummary() {
		Parameter p = ((InconsistencyModel) d_mtc).getInconsistencyVariance();
		NormalSummary summary = d_pm.getBean().getNormalSummary(d_mtc, p);
		return summary;
	}

	private NormalSummary getRandomEffectsSummary() {
		Parameter p = d_mtc.getRandomEffectsVariance();
		NormalSummary summary = d_pm.getBean().getNormalSummary(d_mtc, p);
		return summary;
	}

	private String getRowDescription(int row) {
		if (row == RANDOM_EFFECTS) {
			return "Random Effects Variance";
		} else {
			return "Inconsistency Variance";
		}
	}

	public int getColumnCount() {
		return 2;
	}
}
