package org.drugis.addis.presentation;

import java.util.List;

import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.plot.BinnedScale;
import org.drugis.addis.plot.LinearScale;
import org.drugis.common.Interval;


public class ForestPlotPresentation {

	private List<RelativeEffect<?>> d_relEffects;
	private BinnedScale d_scale;

	public ForestPlotPresentation(List<RelativeEffect<?>> relEffects) {
		d_relEffects = relEffects;
		d_scale = new BinnedScale(new LinearScale(getRange()), 1, 201);
	}
	
	public int getNumRelativeEffects() {
		return d_relEffects.size();
	}
	
	public RelativeEffect<?> getRelativeEffectAt(int i) {
		return d_relEffects.get(i);
	}
	
	public BinnedScale getScale() {
		return d_scale;
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
	
	private Interval<Double> niceInterval(double min, double max) {
		double signMax = Math.floor(Math.log10(Math.abs(max)));
		double signMin = Math.floor(Math.log10(Math.abs(min)));
		
		double sign = Math.max(signMax, signMin);
		
		double minM = Math.floor(min / Math.pow(10, sign)) * Math.pow(10, sign);
		double maxM = Math.ceil(max / Math.pow(10, sign)) * Math.pow(10, sign);
		
		return new Interval<Double>(Math.min(0, minM), Math.max(0, maxM));
	}

}
