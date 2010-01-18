package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseDrugEvent;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Type;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;


public class OutcomePresentationModelTest {
	
	private Domain d_domain;
	private PresentationModelFactory d_pmf;
	private OutcomeMeasure d_omEndpoint;
	private OutcomePresentationModel d_pmEndpoint;

	//OutcomePresentationModel d_opm = new OutcomePresentationModel(om, );
	
	
	@Before
	public void setUp() {
		
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pmf = new PresentationModelFactory(d_domain);
		
		d_omEndpoint = new Endpoint("testendpoint", Type.CONTINUOUS);
		d_pmEndpoint = (OutcomePresentationModel) d_pmf.getModel(d_omEndpoint);
	}
	
	@Test
	public void testGetNameEndpoint() {
		assertEquals ("Endpoint", d_pmEndpoint.getCategoryName());
		
		OutcomeMeasure omAde = new AdverseDrugEvent("testade", Type.CONTINUOUS);
		OutcomePresentationModel pm_ade = (OutcomePresentationModel) d_pmf.getModel(omAde);
		assertEquals ("Adverse drug event", pm_ade.getCategoryName());
	}
	
	@Test
	public void testGetLabelModel() {
		assertEquals("testendpoint", d_pmEndpoint.getLabelModel().getString());
	}
	
	@Test
	public void testGetIncludedStudies() {
		OutcomePresentationModel pmCardovascular = (OutcomePresentationModel) d_pmf.getModel((OutcomeMeasure) ExampleData.buildEndpointCVdeath());
		JUnitUtil.assertAllAndOnly(Collections.singleton(ExampleData.buildStudyMcMurray()), pmCardovascular.getIncludedStudies().getValue());
	}

}
