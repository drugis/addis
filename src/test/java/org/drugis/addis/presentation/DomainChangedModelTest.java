package org.drugis.addis.presentation;

import static org.junit.Assert.*;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class DomainChangedModelTest {

	private Domain d_domain;
	private DomainChangedModel d_model;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		d_model = new DomainChangedModel(d_domain, false);
	}
	
	@Test
	public void testInitial() {
		assertFalse(d_model.getValue());
		assertTrue(new DomainChangedModel(d_domain, true).getValue());
	}
	
	@Test
	public void testSetter() {
		JUnitUtil.testSetter(d_model, false, true);
		JUnitUtil.testSetter(d_model, true, false);
	}
	
	@Test
	public void testEndpoint() {
		d_domain.getEndpoints().add(ExampleData.buildEndpointCgi());
		assertTrue(d_model.getValue());
	}
	
	@Test
	public void testStudy() {
		Indication i = ExampleData.buildIndicationDepression();
		d_domain.getIndications().add(i);
		assertTrue(d_model.getValue());
		d_model.setValue(false);
		d_domain.getStudies().add(new Study("heavy", i ));
		assertTrue(d_model.getValue());
	}
	
}
