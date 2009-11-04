package org.drugis.addis.entities;

import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;

public class RiskDifference extends AbstractRatio {
	private static final long serialVersionUID = -6459490310869138478L;

	public RiskDifference(RateMeasurement denominator,
			RateMeasurement numerator) {
		super(denominator, numerator);
	}

	/**
	 * The mean difference.
	 */
	@Override
	public Double getRelativeEffect() {
		double a = getNumerator().getRate();
		double n1 = getNumerator().getSampleSize();
		double c = getDenominator().getRate();
		double n2 = getDenominator().getSampleSize();
		
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

	@Override
	protected double getMean(RateMeasurement m) {
		return 0;
	}

	@Override
	// Here: gets the STANDARD ERROR of the RISK DIFFERENCE
	public Double getError() {
		double a = getNumerator().getRate();
		double n1 = getNumerator().getSampleSize();
		double b = n1 - a;
		double c = getDenominator().getRate();
		double n2 = getDenominator().getSampleSize();
		double d = n2 - c;
		
		return new Double(Math.sqrt(a*b/Math.pow(n1,3) + c*d/Math.pow(n2,3)));
	}
	
}
