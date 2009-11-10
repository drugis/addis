package org.drugis.addis.plot;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.RelativeEffect;
import org.drugis.common.Interval;

public class RelativeEffectPlot implements Plot {
	// TODO: Own presentation model.
	private List<RelativeEffectBar> d_bars;
	private List<RelativeEffect<?>> d_relEffects;
	private BinnedScale d_scale;

	public RelativeEffectPlot (List<RelativeEffect<?>> relEffects) {
		d_relEffects = relEffects;
		d_bars = new ArrayList<RelativeEffectBar>();
		
		d_scale = new BinnedScale(new LinearScale(getRange()), 1, 201);
		
		int yPos = 11;
		
		for (int i=0; i < d_relEffects.size(); ++i) {		
			d_bars.add(new RelativeEffectBar(d_scale, yPos, (RelativeEffect<?>) d_relEffects.get(i)));
			yPos += 21;
		}
	}
	
	public Interval<Double> getRange() {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (int i = 0; i < d_relEffects.size(); ++i) {
			double lowerBound = d_relEffects.get(i).getConfidenceInterval().getLowerBound();
			min = (lowerBound < min) ? lowerBound : min;
			double upperBound = d_relEffects.get(i).getConfidenceInterval().getUpperBound();
			max = (upperBound > max) ? upperBound : max;
		}
		
		Interval<Double> ret = d_relEffects.size() != 0 ? niceInterval(min,max) : new Interval<Double>(-1D,1D);
		
		return ret;
	}
	
	public void paint(Graphics2D g2d) {
		for (int i=0; i < d_bars.size(); ++i) {
			d_bars.get(i).paint(g2d);
		}
		paintAxis(g2d);
	}
	
	public void paintAxis(Graphics2D g2d) {
		g2d.drawLine(1, 21 * d_bars.size(), 201, 21 * d_bars.size());
		g2d.drawLine(d_scale.getBin(0.0).bin, 0, d_scale.getBin(0.0).bin, 21 * d_bars.size());
		
		String bottomText = "Relative Effect (" + d_relEffects.get(0).getName() + ")";
		
		Rectangle2D stringBounds = g2d.getFontMetrics().getStringBounds(bottomText, g2d);
		g2d.drawString(bottomText, (int) (d_scale.getBin(0).bin - stringBounds.getWidth() / 2), (int) (21 * (d_bars.size() + .5)));
	}

	private Interval<Double> niceInterval(double min, double max) {
		double signMax = Math.floor(Math.log10(Math.abs(max)));
		double signMin = Math.floor(Math.log10(Math.abs(min)));
		
		double sign = Math.max(signMax, signMin);
		
		double minM = Math.floor(min / Math.pow(10, sign)) * Math.pow(10, sign);
		double maxM = Math.ceil(max / Math.pow(10, sign)) * Math.pow(10, sign);
		
		return new Interval<Double>(Math.min(0, minM), Math.max(0, maxM));
	}
	
}
