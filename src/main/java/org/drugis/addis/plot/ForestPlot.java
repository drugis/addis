package org.drugis.addis.plot;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.presentation.ForestPlotPresentation;

public class ForestPlot implements Plot {
	private List<RelativeEffectBar> d_bars;
	private ForestPlotPresentation d_pm;

	public ForestPlot (ForestPlotPresentation pm) {
		d_pm = pm;
		d_bars = new ArrayList<RelativeEffectBar>();
		
		int yPos = 11;
		
		for (int i=0; i < d_pm.getNumRelativeEffects(); ++i) {		
			d_bars.add(new RelativeEffectBar(d_pm.getScale(), yPos, (RelativeEffect<?>) d_pm.getRelativeEffectAt(i)));
			yPos += 21;
		}
	}
	
	public void paint(Graphics2D g2d) {
		for (int i=0; i < d_bars.size(); ++i) {
			d_bars.get(i).paint(g2d);
		}
		paintAxis(g2d);
	}
	
	public void paintAxis(Graphics2D g2d) {
		g2d.drawLine(1, 21 * d_bars.size(), 201, 21 * d_bars.size());
		g2d.drawLine(d_pm.getScale().getBin(0.0).bin, 0, d_pm.getScale().getBin(0.0).bin, 21 * d_bars.size());
		
		String bottomText = "Relative Effect (" + d_pm.getRelativeEffectAt(0).getName() + ")";
		
		Rectangle2D stringBounds = g2d.getFontMetrics().getStringBounds(bottomText, g2d);
		g2d.drawString(bottomText, (int) (d_pm.getScale().getBin(0).bin - stringBounds.getWidth() / 2), (int) (21 * (d_bars.size() + .5)));
	}

}
