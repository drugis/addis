package nl.rug.escher.addis.entities;

import java.util.List;

public interface MutablePatientGroup extends PatientGroup {

	public void setMeasurements(List<BasicMeasurement> measurements);

	public void addMeasurement(BasicMeasurement m);

}