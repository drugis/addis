package org.drugis.addis.entities.metaanalysis;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Set;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.common.Interval;

public class MetaAnalysisRelativeEffect<T extends Measurement> extends AbstractEntity implements RelativeEffect<T> {
	public Interval<Double> d_confidenceInterval;
	public double d_relativeEffect;
	public int d_totalSampleSize;
	public Double d_stdDev;
	
	public MetaAnalysisRelativeEffect(Interval<Double> confidenceInterval, double relativeEffect, 
			int totalSampleSize, double stdDev) {
		d_confidenceInterval = confidenceInterval;
		d_relativeEffect = relativeEffect;
		d_totalSampleSize = totalSampleSize;
		d_stdDev = stdDev;
	}
	public RelativeEffect.AxisType getAxisType() {
		return AxisType.LOGARITHMIC;
	}

	public Interval<Double> getConfidenceInterval() {
		return d_confidenceInterval;
	}

	public Double getRelativeEffect() {
		return d_relativeEffect;
	}
	
	public Integer getSampleSize() {
		return d_totalSampleSize;
	}

	public String getName() {
		return "Random Effects";
	}		

	public T getSubject() {
		throw new RuntimeException("Cannot get a Subject Measurement from Random Effects (Meta-Analysis)");
	}
	
	public T getBaseline() {
		throw new RuntimeException("Cannot get a Baseline Measurement from Random Effects (Meta-Analysis)");
	}

	public Double getError() {
		return d_stdDev;
	}
	
	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	public boolean isDefined() {
		return true;
	}	
	
	public String toString() {
		DecimalFormat formatter = new DecimalFormat("##0.000");
		return formatter.format(d_relativeEffect) + " (" + 
		       formatter.format(d_confidenceInterval.getLowerBound()) + ", " + 
		       formatter.format(d_confidenceInterval.getUpperBound()) + ")";
	}
}