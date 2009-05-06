package nl.rug.escher.entities;

import java.util.ArrayList;
import java.util.List;

import com.jgoodies.binding.beans.Model;

public class PatientGroup extends Model {
	private Study d_study;
	private Drug d_drug;
	private Dose d_dose;
	private List<Measurement> d_measurements = new ArrayList<Measurement>();
	
	public static final String PROPERTY_STUDY = "study";
	public static final String PROPERTY_DRUG = "drug";
	public static final String PROPERTY_DOSE = "dose";
	public static final String PROPERTY_MEASUREMENTS = "measurements";
	
	public Study getStudy() {
		return d_study;
	}
	
	public void setStudy(Study study) {
		Study oldVal = d_study;
		d_study = study;
		firePropertyChange(PROPERTY_STUDY, oldVal, d_study);
	}
	
	public Drug getDrug() {
		return d_drug;
	}
	
	public void setDrug(Drug drug) {
		Drug oldVal = d_drug;
		d_drug = drug;
		firePropertyChange(PROPERTY_DRUG, oldVal, d_drug);
	}
	
	public Dose getDose() {
		return d_dose;
	}
	
	public void setDose(Dose dose) {
		Dose oldVal = d_dose;
		d_dose = dose;
		firePropertyChange(PROPERTY_DOSE, oldVal, d_dose);
	}

	public List<Measurement> getMeasurements() {
		return d_measurements;
	}

	public void setMeasurements(List<Measurement> measurements) {
		List<Measurement> oldVal = d_measurements;
		d_measurements = measurements;
		firePropertyChange(PROPERTY_MEASUREMENTS, oldVal, d_measurements);
	}
	
	public void addMeasurement(Measurement m) {
		List<Measurement> newVal = new ArrayList<Measurement>(d_measurements);
		newVal.add(m);
		setMeasurements(newVal);
	}
	
	public String toString() {
		return d_drug.toString() + " " + d_dose.toString();
	}
}
