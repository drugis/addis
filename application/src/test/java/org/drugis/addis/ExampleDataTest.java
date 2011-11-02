package org.drugis.addis;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Study;
import org.junit.Test;

public class ExampleDataTest {

	@Test
	public void testCloneAllStudies() {
		Domain d = new DomainImpl();
		ExampleData.initDefaultData(d);
		for(Study s : d.getStudies()) {
			s.clone();
		}
	}
	
}
