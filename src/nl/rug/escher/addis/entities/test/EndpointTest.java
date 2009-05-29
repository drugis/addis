package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.*;
import nl.rug.escher.addis.entities.BasicContinuousMeasurement;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Test;

public class EndpointTest {

	@Test
	public void testSetDescription() {
		JUnitUtil.testSetter(new Endpoint(), Endpoint.PROPERTY_DESCRIPTION, null, "My Description");
	}

	@Test
	public void testSetName() {
		JUnitUtil.testSetter(new Endpoint(), Endpoint.PROPERTY_NAME, null, "My Name");
	}
	
	@Test
	public void testSetType() {
		JUnitUtil.testSetter(new Endpoint(), Endpoint.PROPERTY_TYPE, null, Endpoint.Type.CONTINUOUS);
	}
	
	@Test
	public void testBuildMeasurement() {
		Endpoint e = new Endpoint();
		e.setType(Endpoint.Type.RATE);
		assertTrue(e.buildMeasurement() instanceof BasicRateMeasurement);
		assertEquals(e, e.buildMeasurement().getEndpoint());
		e.setType(Endpoint.Type.CONTINUOUS);
		assertTrue(e.buildMeasurement() instanceof BasicContinuousMeasurement);
		assertEquals(e, e.buildMeasurement().getEndpoint());
	}
	
	@Test
	public void testToString() {
		String name = "TestName";
		Endpoint e = new Endpoint();
		e.setName(name);
		assertEquals(name, e.toString());
	}
	

	@Test
	public void testEquals() {
		fail();
		// also test hashCode() for one case where equals is true
	}
}
