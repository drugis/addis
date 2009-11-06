package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.MetaAnalysis;
import org.drugis.addis.entities.Study;
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
	public void testSameIndicationKeepEndpoint() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		assertNotNull(d_wizard.getEndpointModel().getValue());
	}

	@Test
	public void testGetDrugSet() {
		Indication ind = ExampleData.buildIndicationDepression();
		Endpoint ep = ExampleData.buildEndpointHamd();
		
		SortedSet<Drug> expected = new TreeSet<Drug>();
		expected.add(ExampleData.buildDrugFluoxetine());
		expected.add(ExampleData.buildDrugParoxetine());
		expected.add(ExampleData.buildDrugSertraline());
		
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
	public void testDrugCouplingFirst2Second() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getSecondDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		d_wizard.getFirstDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		assertNull(d_wizard.getSecondDrugModel().getValue());
	}
	
	@Test
	public void testDrugCouplingSecond2First() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getFirstDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		d_wizard.getSecondDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		assertNull(d_wizard.getFirstDrugModel().getValue());
	}
	
	@Test
	public void testGetIndicationListModel() {
		List<Indication> expected = new ArrayList<Indication>(d_wizard.getIndicationSet());
		ListHolder<Indication> indicationList = d_wizard.getIndicationListModel();
		List<Indication> list = indicationList.getValue();
		assertEquals(expected, list);
	}
	
	@Test
	public void testGetEndpointListModel() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		List<Endpoint> expected = new ArrayList<Endpoint>(d_wizard.getEndpointSet());
		ListHolder<Endpoint> endpointList = d_wizard.getEndpointListModel();
		assertEquals(expected, endpointList.getValue());
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
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		List<Drug> expected = new ArrayList<Drug>(d_wizard.getDrugSet());
		ListHolder<Drug> drugList = d_wizard.getDrugListModel();
		assertEquals(expected, drugList.getValue());
	}
	
	@Test
	public void testDrugListModelEventOnEndpointChange() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointCgi());
		List<Drug> newValue = new ArrayList<Drug>(d_wizard.getDrugSet());
		
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		ValueModel drugList = d_wizard.getDrugListModel();
		PropertyChangeListener l = JUnitUtil.mockListener(drugList, "value", null, newValue);
		
		drugList.addValueChangeListener(l);
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointCgi());
		verify(l);
	}
	
	@Test
	public void testEndpointChangeUnsetDrugs() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getFirstDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		d_wizard.getSecondDrugModel().setValue(ExampleData.buildDrugParoxetine());

		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		
		assertNull(d_wizard.getFirstDrugModel().getValue());
		assertNull(d_wizard.getSecondDrugModel().getValue());
	}

	@Test
	public void testSameEndpointChangeKeepDrugs() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getFirstDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		d_wizard.getSecondDrugModel().setValue(ExampleData.buildDrugParoxetine());

		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointCgi());
		
		assertNotNull(d_wizard.getFirstDrugModel().getValue());
		assertNotNull(d_wizard.getSecondDrugModel().getValue());
	}

	
	@Test
	public void testGetStudySet() {
		SortedSet<Study> expected = new TreeSet<Study>();
		expected.add(ExampleData.buildDefaultStudy1());
		
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getFirstDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		d_wizard.getSecondDrugModel().setValue(ExampleData.buildDrugParoxetine());
		
		assertEquals(expected, d_wizard.getStudySet());
	}
	
	@Test
	public void testGetStudyListModel() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getFirstDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		d_wizard.getSecondDrugModel().setValue(ExampleData.buildDrugParoxetine());
		
		List<Study> expected = new ArrayList<Study>(d_wizard.getStudySet());
		ListHolder<Study> studyList = d_wizard.getStudyListModel();
		assertEquals(expected, studyList.getValue());
	}
	
	@Test
	public void testGetStudySetNoFirstDrug() {
		testGetStudySetNoDrugHelper(d_wizard.getSecondDrugModel(), d_wizard.getFirstDrugModel());
	}

	@Test
	public void testGetStudySetNoSecondDrug() {
		testGetStudySetNoDrugHelper(d_wizard.getFirstDrugModel(), d_wizard.getSecondDrugModel());
	}
	
	private void testGetStudySetNoDrugHelper(ValueModel setDrugModel,
			ValueModel unsetDrugModel) {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		setDrugModel.setValue(ExampleData.buildDrugFluoxetine());
		
		// sanity checks
		assertNull(unsetDrugModel.getValue());
		assertNotNull(d_wizard.getStudySet());
		
		assertEquals(new TreeSet<Study>(), d_wizard.getStudySet());
	}
	
	@Test
	public void testCascadeOfIndicationEndpointDrugs() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getFirstDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		d_wizard.getSecondDrugModel().setValue(ExampleData.buildDrugParoxetine());

		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationChronicHeartFailure());
		
		assertNull(d_wizard.getFirstDrugModel().getValue());
		assertNull(d_wizard.getSecondDrugModel().getValue());
	}
	
	@Test
	public void testGetSelectedStudiesWithoutChange() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getFirstDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		d_wizard.getSecondDrugModel().setValue(ExampleData.buildDrugParoxetine());
		
		assertEquals(d_wizard.getStudySet(), d_wizard.getSelectedStudySet());
	}
	
	@Test
	public void testGetSelectedStudiesWithChange() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getFirstDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		d_wizard.getSecondDrugModel().setValue(ExampleData.buildDrugParoxetine());
		
		d_wizard.getSelectedStudyBooleanModel(d_wizard.getStudySet().first()).setValue(false);
		SortedSet<Study> set = d_wizard.getStudySet();
		set.remove(d_wizard.getStudySet().first());
		assertTrue(!set.isEmpty());
		assertEquals(set, d_wizard.getSelectedStudySet());	
	}
	
	@Test
	public void testCreateMetaAnalysis() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getFirstDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		d_wizard.getSecondDrugModel().setValue(ExampleData.buildDrugParoxetine());
		
		MetaAnalysis ma = d_wizard.createMetaAnalysis();
		assertTrue(ma.getDrugs().contains(d_wizard.getFirstDrugModel().getValue()));
		assertTrue(ma.getDrugs().contains(d_wizard.getSecondDrugModel().getValue()));
		assertEquals(2, ma.getDrugs().size());
		JUnitUtil.assertAllAndOnly((Collection<?>) d_wizard.getSelectedStudySet(), (Collection<?>) ma.getStudies());
		assertEquals(d_wizard.getEndpointModel().getValue(), ma.getEndpoint());
		assertEquals(d_wizard.getIndicationModel().getValue(), ma.getIndication());
	}

	@Test
	public void testBuildMetaAnalysisThreeArm() {
		d_domain.addStudy(ExampleData.buildAdditionalStudyThreeArm());

		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getFirstDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		d_wizard.getSecondDrugModel().setValue(ExampleData.buildDrugParoxetine());
		
		for (Study s : d_wizard.getStudySet()) {
			if (!s.equals(ExampleData.buildAdditionalStudyThreeArm())) {
				d_wizard.getSelectedStudyBooleanModel(s).setValue(false);
			}
		}
		SortedSet<Study> set = new TreeSet<Study>();
		set.add(ExampleData.buildAdditionalStudyThreeArm());
		assertEquals(set, d_wizard.getSelectedStudySet());	

		MetaAnalysis ma = d_wizard.createMetaAnalysis();
		assertEquals(2, ma.getDrugs().size());
	}
}
