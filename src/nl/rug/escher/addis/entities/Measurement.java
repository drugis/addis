package nl.rug.escher.addis.entities;

import java.io.Serializable;

import com.jgoodies.binding.beans.Observable;

public interface Measurement extends Observable, Serializable {
	public static final String PROPERTY_LABEL = "label";
	public static final String PROPERTY_ENDPOINT = "endpoint";
	public static final String PROPERTY_SAMPLESIZE = "sampleSize";

	public String getLabel();

	public Endpoint getEndpoint();

	public Integer getSampleSize();
}