package nl.rug.escher.addis.entities;

import com.jgoodies.binding.beans.Model;


public class RateContinuousAdapter extends Model implements ContinuousMeasurement {
	private static final long serialVersionUID = 3646088897115931916L;
	private RateMeasurement d_measurement;
	
	public RateContinuousAdapter(RateMeasurement m) {
		d_measurement = m;
	}

	public Double getMean() {
		return (double)d_measurement.getRate() / (double)d_measurement.getSampleSize();
	}

	public Double getStdDev() {
		return getMean() / Math.sqrt(d_measurement.getSampleSize());
	}

	public Endpoint getEndpoint() {
		return d_measurement.getEndpoint();
	}

	public String getLabel() {
		return getMean().toString() + " \u00B1 " + getStdDev().toString();
	}

	public Integer getSampleSize() {
		return d_measurement.getSampleSize();
	}
}
