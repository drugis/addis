package nl.rug.escher.addis.entities;

import nl.rug.escher.addis.entities.Endpoint.Type;

public class RiskRatio extends Ratio implements ContinuousMeasurement {
	private static final long serialVersionUID = 3178825436484450721L;

	public RiskRatio(RateMeasurement denominator, RateMeasurement numerator) {
		super(denominator, numerator);
	}

	@Override
	protected double getMean(RateMeasurement m) {
		return (double)m.getRate() / m.getSampleSize();
	}

	public Double getMean() {
		return getRatio();
	}

	public Double getStdDev() {
		return getConfidenceInterval().getLength() / (2 * 1.96);
	}

	public boolean isOfType(Type type) {
		return type.equals(Type.CONTINUOUS);
	}
	
	@Override
	public String toString() {
		return "[" + d_denominator.toString() + "] / [" 
		+ d_numerator.toString() + "]";
	}
}
