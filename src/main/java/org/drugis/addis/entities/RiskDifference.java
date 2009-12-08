package org.drugis.addis.entities;

import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;

public class RiskDifference extends AbstractRatio {
	private static final long serialVersionUID = -6459490310869138478L;

	public RiskDifference(RateMeasurement denominator, RateMeasurement numerator) {
		super(numerator, denominator);
	}

	public Double getRelativeEffect() {
		double a = getSubject().getRate();
		double n1 = getSubject().getPatientGroup().getSize();
		double c = getBaseline().getRate();
		double n2 = getBaseline().getPatientGroup().getSize();
		
		return (a/n1 - c/n2);
	}
	
	/**
	 * Confidence interval for the mean difference.
	 */
	@Override
	public Interval<Double> getConfidenceInterval() {
		double t = StudentTTable.getT(getSampleSize() - 2);
		double upper = getRelativeEffect() + t*getError();
		double lower = getRelativeEffect() - t*getError();
		
		return new Interval<Double>(lower, upper);
	}


	// Here: gets the STANDARD ERROR of the RISK DIFFERENCE
	public Double getError() {
		double a = getSubject().getRate();
		double n1 = getSubject().getPatientGroup().getSize();
		double b = n1 - a;
		double c = getBaseline().getRate();
		double n2 = getBaseline().getPatientGroup().getSize();
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
