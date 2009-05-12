package nl.rug.escher.addis.entities;

public interface ContinuousMeasurement extends Measurement {

	public static final String PROPERTY_MEAN = "mean";
	public static final String PROPERTY_STDDEV = "stdDev";

	public Double getMean();

	public Double getStdDev();

}