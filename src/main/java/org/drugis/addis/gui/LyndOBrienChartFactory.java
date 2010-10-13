package org.drugis.addis.gui;

import org.drugis.addis.lyndobrien.LyndOBrienModel;
import org.drugis.addis.lyndobrien.ScatterPlotDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;

public class LyndOBrienChartFactory {
	public static JFreeChart buildScatterPlot(LyndOBrienModel model) {
		XYDataset data = new ScatterPlotDataset(model);
		JFreeChart plot = ChartFactory.createScatterPlot("Benefit-Risk plane", "\"Benefit\"", "\"Risk\"",
				data, PlotOrientation.VERTICAL, false, false, false);

		// please god, let us set sensible renderers.
		
		// draw lines through origin.
		plot.getXYPlot().setDomainZeroBaselineVisible(true);
		plot.getXYPlot().setRangeZeroBaselineVisible(true);
		
		return plot;
	}
}
