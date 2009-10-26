package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
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

import com.jgoodies.binding.value.AbstractValueModel;
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
	public void testLabelEndpointEvents() {
		d_wizard.getIndicationModel().setValue(d_wizard.getIndicationSet().last());
		
		Endpoint firstEndp = d_wizard.getEndpointSet().first();
		Endpoint lastEndp = d_wizard.getEndpointSet().last();
		
		d_wizard.getEndpointModel().setValue(firstEndp);
		
		ValueModel model = d_wizard.getStudiesMeasuringLabelModel();

		Object newValue = model.getValue();
		d_wizard.getEndpointModel().setValue(lastEndp);
		
		PropertyChangeListener studiesLabelListener = JUnitUtil.mockListener(model, AbstractValueModel.PROPERTYNAME_VALUE, null, newValue);
		model.addValueChangeListener(studiesLabelListener);
		
		d_wizard.getEndpointModel().setValue(firstEndp);
		verify(studiesLabelListener);
	}
		
	@Test
	public void testLabelIndicationEvents() {
		d_wizard.getIndicationModel().setValue(d_wizard.getIndicationSet().first());
		d_wizard.getEndpointModel().setValue(d_wizard.getEndpointSet().first());
		
		Endpoint endp = d_wizard.getEndpointSet().first();	
		Indication indic = d_wizard.getIndicationSet().first();	
		Indication lastIndic = d_wizard.getIndicationSet().last();		
		
		ValueModel model = d_wizard.getStudiesMeasuringLabelModel();
		
		Object newValue = model.getValue();
		d_wizard.getIndicationModel().setValue(lastIndic);
		
		PropertyChangeListener studiesLabelListener2 = JUnitUtil.mockListener(model, AbstractValueModel.PROPERTYNAME_VALUE, null, newValue);
		model.addValueChangeListener(studiesLabelListener2);
		
		d_wizard.getIndicationModel().setValue(indic);
		verify(studiesLabelListener2);
	}
	
	@Test
	public void testGetStudiesMeasuringLabelModel() {
		d_wizard.getIndicationModel().setValue(d_wizard.getIndicationSet().first());
		d_wizard.getEndpointModel().setValue(d_wizard.getEndpointSet().first());		
		
		Indication indic = d_wizard.getIndicationSet().first();
		Endpoint endp = (Endpoint) d_wizard.getEndpointModel().getValue();
		
		d_wizard.getIndicationModel().setValue(indic);		
		d_wizard.getEndpointModel().setValue(endp);		
		ValueModel model = d_wizard.getStudiesMeasuringLabelModel();
		String endpVal = endp.toString();
		String indVal = indic.toString();
		String correctString = "Studies measuring " + indVal  + " on " + endpVal;
		assertEquals(correctString, model.getValue());
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
}
