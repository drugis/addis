package org.drugis.addis.gui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.drugis.addis.presentation.ForestPlotPresentation;
import org.drugis.addis.treeplot.ForestPlot;

@SuppressWarnings("serial")
public class RelativeEffectCanvas extends JPanel {
	
	private ForestPlot d_plot;

	public RelativeEffectCanvas(ForestPlotPresentation model) {
		d_plot = new ForestPlot(model);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		d_plot.paint((Graphics2D) g);		
	}
	
	@Override
	public Dimension getPreferredSize() {
		return d_plot.getPlotSize();
	}
}