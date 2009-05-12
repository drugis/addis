package nl.rug.escher.addis.entities;


public class ContinuousMeasurement extends Measurement {
	private Double d_mean;
	private Double d_stdDev;
	
	public ContinuousMeasurement() {
		
	}
	
	public ContinuousMeasurement(Endpoint e) {
		super(e);
		d_mean = 0.0; // FIXME
		d_stdDev = 0.0; // FIXME
	}
	
	@Override
	public Double getMean() {
		return d_mean;
	}
	
	public void setMean(Double mean) {
		String oldLabel = getLabel();
		Double oldVal = d_mean;
		d_mean = mean;
		firePropertyChange(PROPERTY_MEAN, oldVal, d_mean);
		firePropertyChange(PROPERTY_LABEL, oldLabel, getLabel());
	}
	
	@Override
	public Double getStdDev() {
		return d_stdDev;
	}
	
	public void setStdDev(Double stdDev) {
		String oldLabel = getLabel();
		Double oldVal = d_stdDev;
		d_stdDev = stdDev;
		firePropertyChange(PROPERTY_STDDEV, oldVal, d_stdDev);
		firePropertyChange(PROPERTY_LABEL, oldLabel, getLabel());
	}
	
	@Override
	public String getLabel() {
		return toString();
	}

	@Override
	public String toString() {
		if (d_mean == null || d_stdDev == null) {
			return "INCOMPLETE"; 
		}
		return d_mean.toString() + " \u00B1 " + d_stdDev.toString();
	}
}