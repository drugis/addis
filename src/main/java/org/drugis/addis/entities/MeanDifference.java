package org.drugis.addis.entities;

import java.util.Set;

import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;


public class MeanDifference extends AbstractEntity implements RelativeEffectContinuous{
	private static final long serialVersionUID = 5993936352514545950L;

	private ContinuousMeasurement d_subject;
	private ContinuousMeasurement d_baseline;

	/**
	 * The MeanDifference of two ContinuousMeasurements.
	 * In a forest plot, the numerator will be on the right and the denominator on the left.
	 * @param baseline
	 * @param subject
	 */
	
	public MeanDifference(ContinuousMeasurement baseline, ContinuousMeasurement subject) {
		d_subject = subject;
		d_baseline = baseline;
	}
	
	public Interval<Double> getConfidenceInterval() {
		double t = StudentTTable.getT(d_subject.getSampleSize() + d_baseline.getSampleSize() - 2);
		return new Interval<Double>(getRatio() - t*getError(),getRatio() + t*getError());
	}

	public ContinuousMeasurement getDenominator() {
		return d_baseline;
	}

	public Endpoint getEndpoint() {
		return d_subject.getEndpoint();
	}

	public ContinuousMeasurement getNumerator() {
		return d_subject;
	}

	public Double getRatio() {
		return d_subject.getMean() - d_baseline.getMean();
	}
	
	public Double getError() {
		return Math.sqrt(square(d_subject.getStdDev())/(double)d_subject.getSampleSize() 
						+ square(d_baseline.getStdDev())/(double)d_baseline.getSampleSize());
	}

	public Integer getSampleSize() {
		return d_subject.getSampleSize() + d_baseline.getSampleSize();
	}
	
	private Double square(double x) {
		return x*x;
	}

	@Override
	public Set<Entity> getDependencies() {
		return null;
	}
}
