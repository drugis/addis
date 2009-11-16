package org.drugis.addis.plot;

import java.awt.Graphics2D;

import org.drugis.addis.entities.RelativeEffect;

public class RelativeEffectBar implements Plot {
	private int d_confStart;
	private int d_confEnd;
	private int d_diamondCenter;
	private int d_diamondSize;
	private int d_yCentre;

	public RelativeEffectBar(BinnedScale scale, int yCentre, RelativeEffect<?> effect, int diamondW) {
		d_yCentre = yCentre;
		
		// Calculate parameters of confidence interval line.
		d_confStart = scale.getBin(effect.getConfidenceInterval().getLowerBound()).bin;
		d_confEnd = scale.getBin(effect.getConfidenceInterval().getUpperBound()).bin;
		
		// Calculate parameters of mean-diamond.
		d_diamondCenter = scale.getBin(effect.getRelativeEffect()).bin;
		d_diamondSize = diamondW; 
	}
	
	public void paint(Graphics2D g2d) {
		// Draw the confidence interval line.
		g2d.drawLine( d_confStart, d_yCentre, d_confEnd, d_yCentre);
		
		// Draw the mean-diamond
		int x = d_diamondCenter - (d_diamondSize - 1)/2;
		int y = d_yCentre - (d_diamondSize - 1)/2;
		g2d.fillRect(x, y, d_diamondSize, d_diamondSize);
	}
}
