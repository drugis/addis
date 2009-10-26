package org.drugis.addis.presentation;

import static org.junit.Assert.*;

import java.util.SortedSet;
import java.util.TreeSet;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.ExampleData;
import org.drugis.addis.entities.Indication;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

public class MetaAnalysisWizardPresentationTest {
	
	private Domain d_domain;
	private MetaAnalysisWizardPresentation d_wizard;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_wizard = new MetaAnalysisWizardPresentation(d_domain);
	}
	
	@Test
	public void testGetIndicationSet() {
		assertEquals(d_domain.getIndications(), d_wizard.getIndicationSet());
	}
	
	@Test
	public void testGetIndicationModel() {
		assertNotNull(d_wizard.getIndicationModel());
		assertEquals(null, d_wizard.getIndicationModel().getValue());
	}
	
	@Test
	public void testSetIndication() {
		Indication newValue = d_domain.getIndications().first();
		ValueModel vm = d_wizard.getIndicationModel();
		JUnitUtil.testSetter(vm, null, newValue);
		
		assertEquals(newValue, d_wizard.getIndicationModel().getValue());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetInvalidIndication() {
		Indication newValue = new Indication(0L, "");
		assertTrue(!d_domain.getIndications().contains(newValue));
		
		ValueModel vm = d_wizard.getIndicationModel();
		vm.setValue(newValue);
	}
	
	@Test
	public void testGetEndpointSet() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		SortedSet<Endpoint> expected = new TreeSet<Endpoint>();
		expected.add(ExampleData.buildEndpointCgi());
		expected.add(ExampleData.buildEndpointHamd());
		assertEquals(expected, d_wizard.getEndpointSet());
	}
	
	@Test
	public void testGetEndpointSetNoIndication() {
		assertNotNull(d_wizard.getEndpointSet());
		assertTrue(d_wizard.getEndpointSet().isEmpty());
	}
}
