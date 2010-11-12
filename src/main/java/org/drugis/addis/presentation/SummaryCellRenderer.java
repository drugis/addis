package org.drugis.addis.presentation;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.drugis.mtc.summary.NormalSummary;
import org.drugis.mtc.summary.QuantileSummary;

public class SummaryCellRenderer
		implements TableCellRenderer {

	private static final DecimalFormat s_format = new DecimalFormat("#.##");

	public Component getTableCellRendererComponent(JTable table, Object cellContents,
			boolean isSelected, boolean hasFocus, int row, int column) {
		String str = "N/A";
		if (cellContents instanceof NormalSummary) {
			str = getNormalSummaryString(cellContents);
		} else if (cellContents instanceof QuantileSummary) {
			str = getQuantileSummaryString(cellContents);
		}
		return (new DefaultTableCellRenderer()).getTableCellRendererComponent(table, str, isSelected, hasFocus, row, column);
	}

	private String getQuantileSummaryString(Object cellContents) {
		QuantileSummary re = (QuantileSummary)cellContents;
		
		String str = "N/A";
		if (re != null && re.getDefined()) {
			str = format(re.getQuantile(1)) + " (" + 
				format(re.getQuantile(0)) + ", " + format(re.getQuantile(2)) + ")";
		}
		return str;
	}
	
	private String getNormalSummaryString(Object cellContents) {
		NormalSummary re = (NormalSummary)cellContents;
		
		String str = "N/A";
		if (re != null && re.getDefined()) {
			String mu = format(re.getMean());
			String sigma = format(re.getStandardDeviation());
			
			str = mu + " +/- " + sigma;
		}
		return str;
	}
	
	 String format(double d) {
    	return s_format.format(d);
	 }
}