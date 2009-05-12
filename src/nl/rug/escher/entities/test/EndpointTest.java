package nl.rug.escher.entities.test;

import static org.junit.Assert.*;
import nl.rug.escher.entities.ContinuousMeasurement;
import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.entities.RateMeasurement;

import org.junit.Test;

public class EndpointTest {

	@Test
	public void testSetDescription() {
		Helper.testSetter(new Endpoint(), Endpoint.PROPERTY_DESCRIPTION, null, "My Description");
	}

	@Test
	public void testSetName() {
		Helper.testSetter(new Endpoint(), Endpoint.PROPERTY_NAME, null, "My Name");
	}
	
	@Test
	public void testSetType() {
		Helper.testSetter(new Endpoint(), Endpoint.PROPERTY_TYPE, null, Endpoint.Type.CONTINUOUS);
	}
	
	@Test
	public void testBuildMeasurement() {
		Endpoint e = new Endpoint();
		e.setType(Endpoint.Type.RATE);
		assertTrue(e.buildMeasurement() instanceof RateMeasurement);
		assertEquals(e, e.buildMeasurement().getEndpoint());
		e.setType(Endpoint.Type.CONTINUOUS);
		assertTrue(e.buildMeasurement() instanceof ContinuousMeasurement);
		assertEquals(e, e.buildMeasurement().getEndpoint());
	}
	
	@Test
	public void testToString() {
		String name = "TestName";
		Endpoint e = new Endpoint();
		e.setName(name);
		assertEquals(name, e.toString());
	}

}
