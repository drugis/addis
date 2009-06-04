package nl.rug.escher.addis.entities.test;

import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.BasicMeasurement;
import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Test;

public class BasicMeasurementTest {
	@SuppressWarnings("serial")
	public BasicMeasurement instance() {
		return new BasicMeasurement() {
			public String getLabel() {
				return null;
			}
		};
	}
	
	@Test
	public void testSetPatientGroup() {
		JUnitUtil.testSetter(instance(), BasicMeasurement.PROPERTY_PATIENTGROUP, null, 
				new BasicPatientGroup(null, null, null, 0));
	}
	
	@Test
	public void testSetEndpoint() {
		JUnitUtil.testSetter(instance(), BasicMeasurement.PROPERTY_ENDPOINT, null, new Endpoint());
	}
}
