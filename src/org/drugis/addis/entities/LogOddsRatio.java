package org.drugis.addis.entities;

import java.text.DecimalFormat;

public class LogOddsRatio extends OddsRatio implements ContinuousMeasurement {
	private static final long serialVersionUID = -9012075635937781733L;
	
	public LogOddsRatio(RateMeasurement denominator, RateMeasurement numerator) {
		super(denominator, numerator);
	}

	public Double getMean() {
		return Math.log(getRatio());
	}

	public Double getStdDev() {
		return Math.sqrt(invEffect(d_denominator) + invNoEffect(d_denominator) +
				invEffect(d_numerator) + invNoEffect(d_numerator));
	}
	
	public String getLabel() {
		DecimalFormat format = new DecimalFormat("0.00");
		return format.format(getMean()) + "\u00B1" + format.format(getStdDev());
	}
	
	public boolean isOfType(Endpoint.Type type) {
		return type.equals(Endpoint.Type.CONTINUOUS);
	}
	
	private double invEffect(RateMeasurement m) {
		return 1.0 / m.getRate();
	}
	
	private double invNoEffect(RateMeasurement m) {
		return 1.0 / (m.getSampleSize() - m.getRate());
	}
}
