package nl.rug.escher.addis.entities.test;

import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.BasicMeasurement;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Test;

public class BasicMeasurementTest {
	public BasicMeasurement instance() {
		return new BasicMeasurement() {
			public String getLabel() {
				return null;
			}
		};
	}
	
	@Test
	public void testSetPatientGroup() {
		JUnitUtil.testSetter(instance(), BasicMeasurement.PROPERTY_PATIENTGROUP, null, new PatientGroup());
	}
	
	@Test
	public void testSetEndpoint() {
		JUnitUtil.testSetter(instance(), BasicMeasurement.PROPERTY_ENDPOINT, null, new Endpoint());
	}
}
