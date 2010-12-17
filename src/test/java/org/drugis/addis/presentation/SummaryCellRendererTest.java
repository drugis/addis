package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.drugis.common.JUnitUtil;
import org.drugis.mtc.summary.NodeSplitPValueSummary;
import org.drugis.mtc.summary.NormalSummary;
import org.drugis.mtc.summary.QuantileSummary;
import org.drugis.mtc.test.ExampleResults;
import org.junit.Test;

public class SummaryCellRendererTest {
	@Test
	public void testGetTableCellRendererComponent() throws IOException {
		SummaryCellRenderer summaryCellRenderer = new SummaryCellRenderer();
		DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
		ExampleResults results = new ExampleResults();
		
		assertEquals(
				defaultRenderer.getTableCellRendererComponent(new JTable(), "N/A", true, true, 1, 1).toString(),
				summaryCellRenderer.getTableCellRendererComponent(new JTable(), "Test", true, true, 1, 1).toString()
				);
		
		JUnitUtil.assertNotEquals(
				defaultRenderer.getTableCellRendererComponent(new JTable(), "N/A", true, true, 1, 1).toString(),
				summaryCellRenderer.getTableCellRendererComponent(new JTable(), "Test", false, false, 2, 3).toString()
				);
		
		assertEquals(
				defaultRenderer.getTableCellRendererComponent(new JTable(), "N/A" , true, true, 1, 1).toString(),
				summaryCellRenderer.getTableCellRendererComponent(new JTable(), new NormalSummary(results, results.getParameters()[0]), true, true, 1, 1).toString()
				);
		
		assertEquals(
				defaultRenderer.getTableCellRendererComponent(new JTable(), "N/A" , true, true, 1, 1).toString(),
				summaryCellRenderer.getTableCellRendererComponent(new JTable(), new QuantileSummary(results, results.getParameters()[0]), true, true, 2, 2).toString()
				);
		
		assertEquals(
				defaultRenderer.getTableCellRendererComponent(new JTable(), "N/A" , true, true, 1, 1).toString(),
				summaryCellRenderer.getTableCellRendererComponent(new JTable(), new NodeSplitPValueSummary(results, results.getParameters()[0], results.getParameters()[1]), true, true, 3, 3).toString()
				);

		results.makeSamplesAvailable();
		
		assertEquals(
				defaultRenderer.getTableCellRendererComponent(new JTable(), "1.34 \u00B1 0.29" , true, true, 1, 1).toString(),
				summaryCellRenderer.getTableCellRendererComponent(new JTable(), new NormalSummary(results, results.getParameters()[0]), true, true, 1, 1).toString()
				);
		
		assertEquals(
				defaultRenderer.getTableCellRendererComponent(new JTable(), "1.35 (0.74, 1.87)" , true, true, 1, 1).toString(),
				summaryCellRenderer.getTableCellRendererComponent(new JTable(), new QuantileSummary(results, results.getParameters()[0]), true, true, 2, 2).toString()
				);
		
		assertEquals(
				defaultRenderer.getTableCellRendererComponent(new JTable(), "0.05" , true, true, 1, 1).toString(),
				summaryCellRenderer.getTableCellRendererComponent(new JTable(), new NodeSplitPValueSummary(results, results.getParameters()[0], results.getParameters()[1]), true, true, 3, 3).toString()
				);
	}
	
}