package org.drugis.addis.entities;

public class LogContinuousMeasurementEstimate extends BasicContinuousMeasurement {
	private static final long serialVersionUID = -593325391463716636L;

	public LogContinuousMeasurementEstimate(double logMean, double logStdDev) {
		super(Math.exp(logMean), Math.exp(logStdDev), 0);
		// TODO Auto-generated constructor stub
	}
	
	public Double getMean() {
		return Math.log(super.getMean());
	}
	
	public void setMean(Double logMean) {
		super.setMean(Math.exp(logMean));
	}
	
	public Double getStdDev() {
		return Math.log(super.getStdDev());
	}
	
	public void setStdDev(Double logStdDev) {
		super.setStdDev(Math.exp(logStdDev));
	}

}
