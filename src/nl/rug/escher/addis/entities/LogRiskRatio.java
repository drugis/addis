package nl.rug.escher.addis.entities;

import nl.rug.escher.common.Interval;

public class LogRiskRatio extends RiskRatio {

	public LogRiskRatio(RateMeasurement denominator, RateMeasurement numerator) {
		super(denominator, numerator);
	}
	
	@Override
	public Double getMean() {
		return Math.log(super.getMean());
	}
	
	@Override
	public Double getStdDev() {
		return Math.sqrt((1.0 / this.d_numerator.getRate()) +
		(1.0 / this.d_denominator.getRate()) -
		(1.0 / this.d_numerator.getSampleSize()) -
		(1.0 / this.d_denominator.getSampleSize()));
	}
	
	@Override
	public Interval<Double> getConfidenceInterval() {
		double lBound = Math.exp(getMean());
		lBound -= 1.96 * getStdDev();
		double uBound = Math.exp(getMean());
		uBound += 1.96 * getStdDev();
		return new Interval<Double>(lBound, uBound);
	}
}
