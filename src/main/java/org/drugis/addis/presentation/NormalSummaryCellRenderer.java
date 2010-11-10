package org.drugis.addis.presentation;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.LogGaussian;
import org.drugis.mtc.summary.NormalSummary;

public class NormalSummaryCellRenderer
		implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object cellContents,
			boolean isSelected, boolean hasFocus, int row, int column) {
		NormalSummary re = (NormalSummary)cellContents;
		
		String str = "N/A";
		if (re != null && re.isDefined()) {
			double mu = re.getMean();
			double sigma = re.getStandardDeviation();
			str = mu + " +/- " + sigma;
		}
		
		return (new DefaultTableCellRenderer()).getTableCellRendererComponent(table, str, isSelected, hasFocus, row, column);
	}

	
}
