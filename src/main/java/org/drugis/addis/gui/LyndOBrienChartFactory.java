package org.drugis.addis.gui;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.addis.lyndobrien.LyndOBrienModel;
import org.drugis.addis.lyndobrien.ScatterPlotDataset;
import org.drugis.addis.lyndobrien.pValueDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

public class LyndOBrienChartFactory {
	public static JFreeChart buildScatterPlot(LyndOBrienModel model) {
		XYDataset data = new ScatterPlotDataset(model);
		JFreeChart chart = ChartFactory.createScatterPlot("Benefit-Risk plane", model.getXAxisName(), model.getYAxisName(),
				data, PlotOrientation.VERTICAL, false, false, false);

		if (model.getBenefitAxisType() == AxisType.LOGARITHMIC)
		{
			final LogarithmicAxis domainAxis = new LogarithmicAxis(model.getXAxisName());
			domainAxis.setAllowNegativesFlag(true);
			 chart.getXYPlot().setDomainAxis(domainAxis);
		}

		if (model.getRiskAxisType() == AxisType.LOGARITHMIC)
		{
			final LogarithmicAxis rangeAxis = new LogarithmicAxis(model.getYAxisName());
			rangeAxis.setAllowNegativesFlag(true);
			chart.getXYPlot().setRangeAxis(rangeAxis);
		}

		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
		renderer.setSeriesOutlinePaint(0, Color.black);
		renderer.setUseOutlinePaint(true);
		renderer.setSeriesShape(0, new Ellipse2D.Double(-2.0, 2.0, 4.0, 4.0));

		//FIXME: Ensure that the origin is always shown
//		Range range = chart.getXYPlot().getDomainAxis().getRange();
//		if(!range.contains(0))
//		{
//			range = Range.expandToInclude(range, 0);
//		}
		
		// draw lines through origin.
		chart.getXYPlot().setDomainZeroBaselineVisible(true);
		chart.getXYPlot().setRangeZeroBaselineVisible(true);
		
		return chart;
	}
	
	public static JFreeChart buildRiskAcceptabilityCurve(LyndOBrienModel model) {
		XYDataset data = new pValueDataset(model);
		JFreeChart chart = ChartFactory.createXYLineChart("Benefit-Risk Acceptability curve", 
				"Acceptability threshold \u03BC", "Probability", data, PlotOrientation.VERTICAL, false, false, false);
		return chart;
	}
	
}
