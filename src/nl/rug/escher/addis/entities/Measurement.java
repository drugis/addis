package nl.rug.escher.addis.entities;

import com.jgoodies.binding.beans.Observable;

public interface Measurement extends Observable {
	public static final String PROPERTY_LABEL = "label";
	public static final String PROPERTY_ENDPOINT = "endpoint";
	public static final String PROPERTY_SAMPLESIZE = "sampleSize";

	public String getLabel();

	public Endpoint getEndpoint();

	public Integer getSampleSize();
}