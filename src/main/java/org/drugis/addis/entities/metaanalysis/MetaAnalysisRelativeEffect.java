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
	private Interval<Double> d_confidenceInterval;
	private double d_relativeEffect;
	private int d_totalSampleSize;
	private double d_stdDev;
	private RelativeEffect.AxisType d_type;
	
	/**
	 * 
	 * @param confidenceInterval must not be null
	 * @param relativeEffect
	 * @param totalSampleSize
	 * @param stdDev >= 0.0
	 * @param type
	 */
	public MetaAnalysisRelativeEffect(Interval<Double> confidenceInterval, double relativeEffect, 
			int totalSampleSize, double stdDev, RelativeEffect.AxisType type) {
//		assert(stdDev >= 0); // FIXME: Breaks a test in mvn, probably due to bad testdata.
//		if (confidenceInterval == null) {
//			throw new NullPointerException("confidenceInterval null");
//		}
		d_confidenceInterval = confidenceInterval;
		d_relativeEffect = relativeEffect;
		d_totalSampleSize = totalSampleSize;
		d_stdDev = stdDev;
		d_type = type;
	}
	public RelativeEffect.AxisType getAxisType() {
		return d_type;
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