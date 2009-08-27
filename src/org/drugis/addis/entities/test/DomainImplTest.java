package org.drugis.addis.entities.test;

import static org.junit.Assert.assertEquals;

import java.util.Collections;


import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Study;
import org.junit.Before;
import org.junit.Test;

public class DomainImplTest {

	private DomainImpl d_domain;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
	}
	
	@Test
	public void testGetDependents() {
		TestData.initDefaultData(d_domain);
		Drug fluox = TestData.buildDrugFluoxetine();
		assertEquals(d_domain.getStudies(), d_domain.getDependents(fluox));
		Drug viagra = TestData.buildDrugViagra();
		assertEquals(Collections.singleton(TestData.buildDefaultStudy2()), d_domain.getDependents(viagra));
		Study s = TestData.buildDefaultStudy();
		assertEquals(Collections.emptySet(), d_domain.getDependents(s));
		Endpoint d1 = TestData.buildEndpointHamd();
		assertEquals(d_domain.getStudies(), d_domain.getDependents(d1));
	}	
}
