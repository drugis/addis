package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.drugis.mtc.Treatment;
import org.drugis.mtc.summary.RankProbabilitySummary;
import org.jfree.data.category.DefaultCategoryDataset;

@SuppressWarnings("serial")
public class RankProbabilityDataset extends DefaultCategoryDataset {
	private RankProbabilitySummary d_summary;

	public RankProbabilityDataset(RankProbabilitySummary rankProbabilitySummary) {
		d_summary = rankProbabilitySummary;
		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				fireDatasetChanged();
			}
		};
		d_summary.addPropertyChangeListener(listener);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int getRowIndex(Comparable key) {
		if (!(key instanceof String)) {
			return -1;
		}
		String str = (String) key;
		int idx = Integer.parseInt(str.substring(5)) - 1;
		if (idx < 0 || idx >= d_summary.getTreatments().size()) {
			return -1;
		}
		return idx;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int getColumnIndex(Comparable key) {
		if (!(key instanceof String)) {
			return -1;
		}
		String str = (String) key;
		return d_summary.getTreatments().indexOf(new Treatment(str));
	}
	
	@Override
	public String getRowKey(int row) {
		return "Rank " + (row + 1);
	}
	
	@Override
	public String getColumnKey(int column) {
		return d_summary.getTreatments().get(column).id(); 
	}
	
	@Override
	public List<String> getRowKeys() {
		List<String> keys = new ArrayList<String>();
		for (int i = 0; i < d_summary.getTreatments().size(); ++i) {
			keys.add("Rank " + (i+1));
		}
		return keys;
	}
	
	@Override
	public List<String> getColumnKeys() {
		List<String> keys = new ArrayList<String>();
		for (Treatment t : d_summary.getTreatments()) {
			keys.add(t.id());
		}
		return keys;
	}
	
	@Override
	public int getRowCount() {
		return d_summary.getTreatments().size();
	}

	@Override
	public int getColumnCount() {
		return d_summary.getTreatments().size();
	}
	
	@Override
	public Number getValue(int row, int column) {
		return d_summary.getValue(d_summary.getTreatments().get(column), row + 1);
	}
	
	@Override
	public Number getValue(Comparable rowKey, Comparable columnKey) {
		return getValue(getRowIndex(rowKey), getColumnIndex(columnKey));
	}
}
