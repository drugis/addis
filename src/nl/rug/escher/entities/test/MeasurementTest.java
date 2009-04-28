package nl.rug.escher.entities.test;

import org.junit.Test;

import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.entities.Measurement;
import nl.rug.escher.entities.PatientGroup;

public class MeasurementTest {
	@Test
	public void testSetMean() {
		Helper.testSetter(new Measurement(), Measurement.PROPERTY_MEAN, null, 25.91);
	}
	
	@Test
	public void testSetStdDev() {
		Helper.testSetter(new Measurement(), Measurement.PROPERTY_STDDEV, null, 0.46);
	}
	
	@Test
	public void testSetPatientGroup() {
		Helper.testSetter(new Measurement(), Measurement.PROPERTY_PATIENTGROUP, null, new PatientGroup());
	}
	
	@Test
	public void testSetEndpoint() {
		Helper.testSetter(new Measurement(), Measurement.PROPERTY_ENDPOINT, null, new Endpoint());
	}
	
}
