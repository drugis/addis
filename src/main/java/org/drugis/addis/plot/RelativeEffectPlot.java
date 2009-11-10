package org.drugis.addis.plot;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.RelativeEffect;
import org.drugis.common.Interval;

public class RelativeEffectPlot implements Plot {
	List<RelativeEffectBar> d_bars;
	
	// TODO: Own presentation model.
	List<RelativeEffect<?>> d_relEffects;

	public RelativeEffectPlot (List<RelativeEffect<?>> relEffects) {
		d_relEffects = relEffects;
		d_bars = new ArrayList<RelativeEffectBar>();
		
		int yPos = 11;
		for (int i=0; i < d_relEffects.size(); ++i) {
			d_bars.add(new RelativeEffectBar(new BinnedScale(new LinearScale(getRange()), 1, 201), yPos, (RelativeEffect<?>) d_relEffects.get(i)));
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
		
		return d_relEffects.size() != 0 ? new Interval<Double>(min, max) : new Interval<Double>(-1D,1D);
	}
	
	public void paint(Graphics2D g2d) {
		for (int i=0; i < d_bars.size(); ++i) {
			d_bars.get(i).paint(g2d);
		}
		paintAxis(g2d);
	}
	
	public void paintAxis(Graphics2D g2d) {
	}

	
}
