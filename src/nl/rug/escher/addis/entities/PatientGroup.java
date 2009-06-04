package nl.rug.escher.addis.entities;

import java.util.List;

public interface PatientGroup {

	public static final String PROPERTY_STUDY = "study";
	public static final String PROPERTY_SIZE = "size";
	public static final String PROPERTY_DRUG = "drug";
	public static final String PROPERTY_DOSE = "dose";
	public static final String PROPERTY_MEASUREMENTS = "measurements";
	public static final String PROPERTY_LABEL = "label";

	public Study getStudy();

	public Drug getDrug();

	public Dose getDose();

	public List<? extends Measurement> getMeasurements();

	/**
	 * Get Measurement by Endpoint.
	 * @param endpoint Endpoint to get measurement for.
	 * @return Measurement if Endpoint is measured, null otherwise.
	 */
	public Measurement getMeasurement(Endpoint endpoint);

	public String getLabel();

	public Integer getSize();

}