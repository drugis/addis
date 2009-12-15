package org.drugis.addis.entities;

import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;

public class StandardisedMeanDifference extends AbstractRelativeEffect<ContinuousMeasurement> {
	private static final long serialVersionUID = -8753337320258281527L;
	/*
	 * The Standardised Mean Difference is calculated through Cohen's d and adjusted with J(degrees of freedom)
	 * to result in Hedges g. All formulas are based on The Handbook of Research Synthesis and Meta-Analysis 
	 * by Cooper et al. 2nd Edition pages 225-230
	 */
	
	public StandardisedMeanDifference(ContinuousMeasurement subject,
			ContinuousMeasurement baseline) throws IllegalArgumentException {
		super(subject, baseline);
	}

	public Interval<Double> getConfidenceInterval() {
		double t = StudentTTable.getT(getDegreesOfFreedom());

		return new Interval<Double>(getRelativeEffect() - t * getError(), getRelativeEffect() + t * getError());
	}

	public Double getRelativeEffect() {
		return getCorrectionJ() * getCohenD();
	}
	
	public Double getError() {
		return Math.sqrt(square(getCorrectionJ()) * getCohenVariance());
	}
	
	private double square(double x) {
		return x*x;
	}

	// Package access only:
	double getCohenD() {
		return (d_subject.getMean() - d_baseline.getMean()) / getPooledStdDev();
	}

	double getCohenVariance() {
		double frac1 = (double) getSampleSize() / ((double) d_subject.getSampleSize() *
				(double) d_baseline.getSampleSize());
		double frac2 = square(getCohenD()) / (2D * (double) getSampleSize());
		return (frac1 + frac2);
	}
	
	double getCorrectionJ() {
		return (1 - (3 / (4 * (double) getDegreesOfFreedom() - 1)));
	}
	
	private double getPooledStdDev() {
		double numerator = ((double) d_subject.getSampleSize() - 1) * square(d_subject.getStdDev()) 
							+ ((double) d_baseline.getSampleSize() - 1) * square(d_baseline.getStdDev());
		return Math.sqrt(numerator/(double) getDegreesOfFreedom());
	}
	
	private int getDegreesOfFreedom() {
		return getSampleSize() - 2;
	}

	public String getName() {
		return "Standardised Mean Difference";
	}
	
	public AxisType getAxisType() {
		return AxisType.LINEAR;
	}
}
