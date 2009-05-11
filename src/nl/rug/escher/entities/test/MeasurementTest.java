package nl.rug.escher.entities.test;

import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.entities.Measurement;
import nl.rug.escher.entities.PatientGroup;

import org.junit.Test;

public class MeasurementTest {
	public Measurement instance() {
		return new Measurement(){
			@Override public String getLabel() { return null; }
			@Override public Double getMean() { return null; }
			@Override public Double getStdDev() { return null; }
		};
	}
	
	@Test
	public void testSetPatientGroup() {
		Helper.testSetter(instance(), Measurement.PROPERTY_PATIENTGROUP, null, new PatientGroup());
	}
	
	@Test
	public void testSetEndpoint() {
		Helper.testSetter(instance(), Measurement.PROPERTY_ENDPOINT, null, new Endpoint());
	}
}
