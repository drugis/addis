package org.drugis.addis.entities;

import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;

public class RiskDifference extends AbstractRelativeEffect<RateMeasurement> {
	private static final long serialVersionUID = -6459490310869138478L;

	public RiskDifference(RateMeasurement denominator, RateMeasurement numerator) {
		super(numerator, denominator);
	}

	public Double getRelativeEffect() {
		double a = getSubject().getRate();
		double n1 = getSubject().getSampleSize();
		double c = getBaseline().getRate();
		double n2 = getBaseline().getSampleSize();
		
		return (a/n1 - c/n2);
	}
	
	/**
	 * Confidence interval for the mean difference.
	 */
	public Interval<Double> getConfidenceInterval() {
		double t = StudentTTable.getT(getSampleSize() - 2);
		double upper = getRelativeEffect() + t*getError();
		double lower = getRelativeEffect() - t*getError();
		
		return new Interval<Double>(lower, upper);
	}


	// Here: gets the STANDARD ERROR of the RISK DIFFERENCE
	public Double getError() {
		double a = getSubject().getRate();
		double n1 = getSubject().getSampleSize();
		double b = n1 - a;
		double c = getBaseline().getRate();
		double n2 = getBaseline().getSampleSize();
		double d = n2 - c;
		
		return new Double(Math.sqrt(a*b/Math.pow(n1,3) + c*d/Math.pow(n2,3)));
	}

	public String getName() {
		return "Risk Difference";
	}
	
	public AxisType getAxisType() {
		return AxisType.LINEAR;
	}
}
