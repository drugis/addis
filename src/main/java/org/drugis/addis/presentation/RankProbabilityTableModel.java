package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.table.AbstractTableModel;

import org.drugis.mtc.Treatment;
import org.drugis.mtc.summary.RankProbabilitySummary;

public class RankProbabilityTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 6045183840617200792L;
	private final RankProbabilitySummary d_summary;

	public RankProbabilityTableModel(RankProbabilitySummary summary) {
		d_summary = summary;
		d_summary.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				fireTableDataChanged();
			}
		});
	}

	public int getColumnCount() {
		return d_summary.getTreatments().size() + 1;
	}

	public int getRowCount() {
		return d_summary.getTreatments().size();
	}
	
	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return "Drug";
		} else {
			return "Rank " + column;
		}
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		if (column == 0) {
			return String.class;
		} else {
			return Double.class;
		}
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		Treatment treatment = d_summary.getTreatments().get(rowIndex);
		if (columnIndex == 0) {
			return treatment.id();
		} else {
			return d_summary.getDefined() ? d_summary.getValue(treatment, columnIndex) : "";
		}
	}
}
