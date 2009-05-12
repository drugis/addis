package nl.rug.escher.addis.entities;

public interface RateMeasurement extends Measurement {

	public static final String PROPERTY_RATE = "rate";
	public static final String PROPERTY_SIZE = "size";

	public Integer getSize();

	public Integer getRate();

}