package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.ExampleData;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.presentation.MetaAnalysisWizardPresentation.AbstractListHolder;
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
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		PropertyChangeListener l = JUnitUtil.mockListener(
				d_wizard.getEndpointModel(), "value", ExampleData.buildEndpointHamd(), null);
		d_wizard.getEndpointModel().addValueChangeListener(l);
		
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationChronicHeartFailure());
		assertNull(d_wizard.getEndpointModel().getValue());
		verify(l);
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
	
	@Test
	public void testGetFirstDrugModel() {
		testDrugModelHelper(d_wizard.getFirstDrugModel());
	}

	@Test
	public void testGetSecondDrugModel() {
		testDrugModelHelper(d_wizard.getSecondDrugModel());
	}
	
	private void testDrugModelHelper(ValueModel drugModel) {
		assertNotNull(drugModel);
		assertEquals(null, drugModel.getValue());
	}

	@Test
	public void testSetFirstDrug(){
		testSetDrugHelper(d_wizard.getFirstDrugModel());
	}
	
	@Test
	public void testSetSecondDrug(){
		testSetDrugHelper(d_wizard.getSecondDrugModel());
	}

	private void testSetDrugHelper(ValueModel vm) {
		Indication ind = ExampleData.buildIndicationDepression();
		Endpoint ep = ExampleData.buildEndpointHamd();
		Drug d = ExampleData.buildDrugFluoxetine();
		d_wizard.getIndicationModel().setValue(ind);
		d_wizard.getEndpointModel().setValue(ep);
		
		JUnitUtil.testSetter(vm, null, d);
		
		assertEquals(d, vm.getValue());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetInvalidFirstDrug(){
		testSetInvalidDrugHelper(d_wizard.getFirstDrugModel());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetInvalidSecondDrug(){
		testSetInvalidDrugHelper(d_wizard.getSecondDrugModel());
	}

	private void testSetInvalidDrugHelper(ValueModel vm) {
		Indication ind = ExampleData.buildIndicationDepression();
		Endpoint ep = ExampleData.buildEndpointHamd();
		Drug d = ExampleData.buildDrugCandesartan();
		d_wizard.getIndicationModel().setValue(ind);
		d_wizard.getEndpointModel().setValue(ep);
		
		assertTrue(!d_wizard.getDrugSet().contains(d));
		
		vm.setValue(d);
	}
	
	@Test
	public void testDrugCoupling() {
		fail();
	}
	
	@Test
	public void testGetIndicationListModel() {
		List<Indication> expected = new ArrayList<Indication>(d_wizard.getIndicationSet());
		AbstractListHolder<Indication> indicationList = d_wizard.getIndicationListModel();
		List<Indication> list = indicationList.getValue();
		assertEquals(expected, list);
	}
	
	@Test
	public void testGetEndpointListModel() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		List<Endpoint> expected = new ArrayList<Endpoint>(d_wizard.getEndpointSet());
		AbstractListHolder<Endpoint> endpointList = d_wizard.getEndpointListModel();
		List<Endpoint> list = endpointList.getValue();
		assertEquals(expected, list);
	}
	
	@Test
	public void testEndpointListModelEventOnIndicationChange() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationChronicHeartFailure());
		List<Endpoint> newValue = new ArrayList<Endpoint>(d_wizard.getEndpointSet());
		
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		ValueModel endpointList = d_wizard.getEndpointListModel();
		PropertyChangeListener l = JUnitUtil.mockListener(endpointList, "value", null, newValue);
		
		endpointList.addValueChangeListener(l);
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationChronicHeartFailure());
		verify(l);
	}
	
	@Test
	public void testGetDrugListModel() {
		fail();
	}
}
