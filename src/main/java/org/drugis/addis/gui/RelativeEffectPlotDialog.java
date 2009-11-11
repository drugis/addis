package org.drugis.addis.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JDialog;

import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.plot.ForestPlot;
import org.drugis.addis.presentation.ForestPlotPresentation;

@SuppressWarnings("serial")
public class RelativeEffectPlotDialog extends JDialog {
	List<RelativeEffect<?>> d_effects;
	
	public RelativeEffectPlotDialog(JDialog parent, List<RelativeEffect<?>> cellModels, String title) { 
		super(parent, title);
		d_effects = cellModels;
		initComps();
		pack();
	}
	
	private void initComps() {
		Canvas canvas = new Canvas() {
			public void paint (Graphics g) {
				ForestPlot plot = new ForestPlot(new ForestPlotPresentation(d_effects));
				plot.paint((Graphics2D) g);
			}
		};
		canvas.setPreferredSize(new Dimension(201, 21 * (d_effects.size() + 1)));
		canvas.setBackground(Color.WHITE);
		add(canvas);
	}
}
