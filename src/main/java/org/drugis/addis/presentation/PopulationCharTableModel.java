package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;

@SuppressWarnings("serial")
public class PopulationCharTableModel extends AbstractTableModel {
	private Study d_study;
	private PresentationModelFactory d_pmf;

	public PopulationCharTableModel(Study study, PresentationModelFactory pmf) {
		d_study = study;
		d_pmf = pmf;
	}

	public int getColumnCount() {
		return d_study.getArms().size() + 2;
	}

	public int getRowCount() {
		return d_study.getPopulationCharacteristics().size();
	}

	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return getCharAt(row).getName();
		} else if (col == getColumnCount() - 1) {
			return getStudyChar(row);
		} else {
			return getArmChar(col - 1, row);
		}
	}

	private Measurement getArmChar(int armIdx, int charIdx) {
		return d_study.getArms().get(armIdx).getPopulationCharacteristic(getCharAt(charIdx));
	}

	private Measurement getStudyChar(int charIdx) {
		return d_study.getPopulationCharacteristic(getCharAt(charIdx));
	}

	private Variable getCharAt(int charIdx) {
		List<Variable> vars = new ArrayList<Variable>(d_study.getPopulationCharacteristics().keySet());
		Collections.sort(vars);
		return vars.get(charIdx);
	}
	
	@Override
	public String getColumnName(int col) {
		if (col == 0) {
			return "Variable";
		} else if (col == getColumnCount() - 1) {
			return "Overall";
		} else {
			return d_pmf.getLabeledModel(d_study.getArms().get(col - 1)).getLabelModel().getString();
		}
	}
}
