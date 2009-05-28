package nl.rug.escher.addis.entities;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.jgoodies.binding.beans.Model;

@PersistenceCapable(detachable="true")
@FetchGroup(name="pg-full", members={
		@Persistent(name="measurements"),
		@Persistent(name="size"), 
		@Persistent(name="drug"),
		@Persistent(name="dose"),
		@Persistent(name="study")
		})
public class PatientGroup extends Model {
	private Study d_study;
	private Integer d_size;
	private Drug d_drug;
	private Dose d_dose;
	private List<BasicMeasurement> d_measurements = new ArrayList<BasicMeasurement>();
	
	public static final String PROPERTY_STUDY = "study";
	public static final String PROPERTY_SIZE = "size";
	public static final String PROPERTY_DRUG = "drug";
	public static final String PROPERTY_DOSE = "dose";
	public static final String PROPERTY_MEASUREMENTS = "measurements";
	public static final String PROPERTY_LABEL = "label";
	
	@Persistent(mappedBy="patientGroups")
	public Study getStudy() {
		return d_study;
	}
	
	public void setStudy(Study study) {
		Study oldVal = d_study;
		d_study = study;
		firePropertyChange(new PropertyChangeEvent(this, PROPERTY_STUDY, oldVal, d_study));
	}
	
	@Persistent
	public Drug getDrug() {
		return d_drug;
	}
	
	public void setDrug(Drug drug) {
		String oldLabel = getLabel();
		Drug oldVal = d_drug;
		d_drug = drug;
		firePropertyChange(new PropertyChangeEvent(this, PROPERTY_DRUG, oldVal, d_drug));
		firePropertyChange(PROPERTY_LABEL, oldLabel, getLabel());
	}
	
	@Persistent
	public Dose getDose() {
		return d_dose;
	}
	
	public void setDose(Dose dose) {
		String oldLabel = getLabel();
		Dose oldVal = d_dose;
		d_dose = dose;
		firePropertyChange(new PropertyChangeEvent(this, PROPERTY_DOSE, oldVal, d_dose)); // TODO: WTF?
		// apparently, in the above call, jgoodies tries to set source=null in stead of source=this..
		firePropertyChange(PROPERTY_LABEL, oldLabel, getLabel());
	}

	@Element
	public List<BasicMeasurement> getMeasurements() {
		return d_measurements;
	}

	public void setMeasurements(List<BasicMeasurement> measurements) {
		List<BasicMeasurement> oldVal = d_measurements;
		d_measurements = measurements;
		//firePropertyChange(new PropertyChangeEvent(this, PROPERTY_MEASUREMENTS, oldVal, d_measurements));
	}
	
	public void addMeasurement(BasicMeasurement m) {
		List<BasicMeasurement> newVal = new ArrayList<BasicMeasurement>(d_measurements);
		newVal.add(m);
		m.setPatientGroup(this);
		setMeasurements(newVal);
	}
	
	/**
	 * Get Measurement by Endpoint.
	 * @param endpoint Endpoint to get measurement for.
	 * @return Measurement if Endpoint is measured, null otherwise.
	 */
	public BasicMeasurement getMeasurement(Endpoint endpoint) {
		for (BasicMeasurement m : getMeasurements()) {
			if (m.getEndpoint().equals(endpoint)) {
				return m;
			}
		}
		return null;
	}
	
	public String getLabel() {
		if (d_drug == null || d_dose == null) {
			return "INCOMPLETE";
		}
		return d_drug.toString() + " " + d_dose.toString();
	}
	
	/*
	@Override
	public String toString() {
		return "PatientGroup(" + d_drug + ", " + d_dose + ", " + d_size + ")";
	}
	*/

	@Persistent
	public Integer getSize() {
		return d_size;
	}

	public void setSize(Integer size) {
		Integer oldVal = d_size;
		d_size = size;
		firePropertyChange(new PropertyChangeEvent(this, PROPERTY_SIZE, oldVal, d_size));
	}
}
