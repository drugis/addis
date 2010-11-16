/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.gui;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import org.drugis.addis.lyndobrien.AcceptabilityCurveDataset;
import org.drugis.addis.lyndobrien.LyndOBrienModel;
import org.drugis.addis.lyndobrien.ScatterPlotDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

public class LyndOBrienChartFactory {
	public static JFreeChart buildScatterPlot(LyndOBrienModel model) {
		XYDataset data = new ScatterPlotDataset(model);
		JFreeChart chart = ChartFactory.createScatterPlot("Benefit-Risk plane", model.getXAxisName(), model.getYAxisName(),
				data, PlotOrientation.VERTICAL, false, false, false);

		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
		renderer.setSeriesOutlinePaint(0, Color.black);
		renderer.setUseOutlinePaint(true);
		renderer.setSeriesShape(0, new Ellipse2D.Double(-2.0, 2.0, 4.0, 4.0));
		
		// draw lines through origin.
		chart.getXYPlot().setDomainZeroBaselineVisible(true);
		chart.getXYPlot().setRangeZeroBaselineVisible(true);
		
		return chart;
	}
	
	public static JFreeChart buildRiskAcceptabilityCurve(LyndOBrienModel model) {
		XYDataset data = new AcceptabilityCurveDataset(model);
		JFreeChart chart = ChartFactory.createXYLineChart("Benefit-Risk Acceptability curve", 
				"Acceptability threshold \u03BC", "Probability", data, PlotOrientation.VERTICAL, false, false, false);
		chart.getXYPlot().getRangeAxis().setRange(0, 1);
		return chart;
	}
	
}
