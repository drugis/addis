package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.ContinuousVariable;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Variable;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;


public class VariablePresentationModelTest {
	
	private Domain d_domain;
	private PresentationModelFactory d_pmf;
	private Variable d_omEndpoint;
	private VariablePresentationModel d_pmEndpoint;

	//OutcomePresentationModel d_opm = new OutcomePresentationModel(om, );
	
	
	@Before
	public void setUp() {
		
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pmf = new PresentationModelFactory(d_domain);
		
		d_omEndpoint = new Endpoint("testendpoint", Variable.Type.CONTINUOUS);
		d_pmEndpoint = (VariablePresentationModel) d_pmf.getModel(d_omEndpoint);
	}
	
	@Test
	public void testGetNameEndpoint() {
		assertEquals ("Endpoint", d_pmEndpoint.getCategoryName());
	}	
	
	@Test
	public void testGetNameADE() {
		Variable omAde = new AdverseEvent("testade", Variable.Type.CONTINUOUS);
		VariablePresentationModel pm_ade = (VariablePresentationModel) d_pmf.getModel(omAde);
		assertEquals ("Adverse drug event", pm_ade.getCategoryName());
	}
	
	@Test
	public void testGetNamePopChar() {
		Variable omAde = new ContinuousVariable("testvar");
		VariablePresentationModel pm = (VariablePresentationModel) d_pmf.getModel(omAde);
		assertEquals ("Population characteristic", pm.getCategoryName());
	}
	
	@Test
	public void testGetLabelModel() {
		assertEquals("testendpoint", d_pmEndpoint.getLabelModel().getString());
	}
	
	@Test
	public void testGetIncludedStudies() {
		VariablePresentationModel pmCardovascular = (VariablePresentationModel) d_pmf.getModel((Variable) ExampleData.buildEndpointCVdeath());
		JUnitUtil.assertAllAndOnly(Collections.singleton(ExampleData.buildStudyMcMurray()), pmCardovascular.getIncludedStudies().getValue());
	}

}
