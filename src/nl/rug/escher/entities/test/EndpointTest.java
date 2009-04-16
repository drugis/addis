package nl.rug.escher.entities.test;

import static org.junit.Assert.*;
import nl.rug.escher.entities.Endpoint;

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
	public void testToString() {
		String name = "TestName";
		Endpoint e = new Endpoint();
		e.setName(name);
		assertEquals(name, e.toString());
	}

}
