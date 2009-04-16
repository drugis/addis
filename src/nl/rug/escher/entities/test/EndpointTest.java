package nl.rug.escher.entities.test;

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

}
