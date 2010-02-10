package org.drugis.addis.presentation;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;

@SuppressWarnings("serial")
public class PopulationCharTableModel extends MeasurementTableModel {
	public PopulationCharTableModel(Study study, PresentationModelFactory pmf) {
		super(study, pmf, Variable.class);
		
		for (Variable v : d_study.getVariables(Variable.class)) {
			d_study.getMeasurement(v).addPropertyChangeListener(d_measurementListener);
		}		
	}

	@Override
	public int getColumnCount() {
		return super.getColumnCount() + 1;
	}

	public Object getValueAt(int row, int col) {
		if (col == getColumnCount() - 1) {
			return d_study.getMeasurement(getCharAt(row));
		} else {
			return super.getValueAt(row, col);
		}
	}

	private Variable getCharAt(int charIdx) {
		return d_study.getVariables(Variable.class).get(charIdx);
	}
	
	@Override
	public String getColumnName(int col) {
		if (col == getColumnCount() - 1) {
			return "Overall";
		} else {
			return super.getColumnName(col);
		}
	}
}
