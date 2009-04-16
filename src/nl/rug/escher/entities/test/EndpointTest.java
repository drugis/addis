package nl.rug.escher.entities.test;

import nl.rug.escher.entities.Endpoint;

import org.junit.Before;
import org.junit.Test;


public class EndpointTest { 
	private Endpoint d_endpoint;

	@Before
	public void setUp() throws Exception {
		d_endpoint = new Endpoint();
	}
	
	@Test
	public void testSetDescription() {
		Helper.testSetter(d_endpoint, Endpoint.PROPERTY_DESCRIPTION, null, "My Description");
	}

	@Test
	public void testSetName() {
		Helper.testSetter(d_endpoint, Endpoint.PROPERTY_NAME, null, "My Name");
	}

}
