package nl.rug.escher.addis.entities;

public interface MutableStudy extends Study {
	public void setMeasurement(Endpoint e, PatientGroup g, Measurement m);
}
