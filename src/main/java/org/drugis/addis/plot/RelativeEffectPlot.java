package org.drugis.addis.plot;

import java.awt.Graphics2D;

import org.drugis.addis.entities.RelativeEffect;

public class RelativeEffectPlot implements Plot {
	private int d_confStart;
	private int d_confEnd;
	private int d_diamondCenter;
	private int d_diamondSize;
	private int d_yCentre;

	public RelativeEffectPlot(BinnedScale scale, int yCentre, RelativeEffect<?> effect) {
		d_yCentre = yCentre;
		
		// Calculate parameters of confidence interval line.
		d_confStart = scale.getBin(effect.getConfidenceInterval().getLowerBound()).bin;
		d_confEnd = scale.getBin(effect.getConfidenceInterval().getUpperBound()).bin;
		
		// Calculate parameters of mean-diamond.
		d_diamondCenter = scale.getBin(effect.getRelativeEffect()).bin;
		d_diamondSize = 5; // FIXME: calculate weight
	}
	
	public void paint(Graphics2D g2d) {
		// Draw the confidence interval line.
		g2d.drawLine( d_confStart, d_yCentre, d_confEnd, d_yCentre);
		
		// Draw the mean-diamond
		g2d.fillRect(d_diamondCenter - (d_diamondSize - 1)/2, d_yCentre - (d_diamondSize - 1)/2, d_diamondSize, d_diamondSize);
	}
}
