package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.SortedSet;
import java.util.TreeSet;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
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
	
	@Test
	public void testGetEndpointModel() {
		assertNotNull(d_wizard.getEndpointModel());
		assertEquals(null, d_wizard.getEndpointModel().getValue());
	}
	
	@Test
	public void testSetEndpoint() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		Endpoint newValue = ExampleData.buildEndpointHamd();
		ValueModel vm = d_wizard.getEndpointModel();
		JUnitUtil.testSetter(vm, null, newValue);
		
		assertEquals(newValue, d_wizard.getEndpointModel().getValue());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetInvalidEndpoint() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		Endpoint newValue = ExampleData.buildEndpointCVdeath();
		
		assertTrue(!d_wizard.getEndpointSet().contains(newValue));
		
		ValueModel vm = d_wizard.getEndpointModel();
		vm.setValue(newValue);
	}
	
	@Test
	public void testChangeIndicationUnsetEndpoint() {
		fail();
	}
		
	@Test
	public void testGetDrugSet() {
		Indication ind = ExampleData.buildIndicationDepression();
		Endpoint ep = ExampleData.buildEndpointCgi();
		
		SortedSet<Drug> expected = new TreeSet<Drug>();
		expected.addAll(ExampleData.buildDefaultStudy().getDrugs());
		
		d_wizard.getIndicationModel().setValue(ind);
		d_wizard.getEndpointModel().setValue(ep);
		
		assertEquals(expected, d_wizard.getDrugSet());
	}
	
	@Test
	public void testGetDrugSetNoEndpoint() {
		Indication ind = ExampleData.buildIndicationDepression();
		
		d_wizard.getIndicationModel().setValue(ind);
		assertNull(d_wizard.getEndpointModel().getValue());
		assertNotNull(d_wizard.getDrugSet());
		
		assertEquals(new TreeSet<Drug>(), d_wizard.getDrugSet());
	}
}
