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

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AbstractOutcomeMeasure;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
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
		d_wizard = new MetaAnalysisWizardPresentation(d_domain, new PresentationModelFactory(d_domain));
	}
	
	@Test
	public void testGetIndicationSet() {
		assertTrue(d_domain.getIndications().containsAll(d_wizard.getIndicationListModel().getValue()));
		assertEquals(d_domain.getIndications().size(), d_wizard.getIndicationListModel().getValue().size());
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
		List<Endpoint> expected = new ArrayList<Endpoint>();
		expected.add(ExampleData.buildEndpointCgi());
		expected.add(ExampleData.buildEndpointHamd());
		assertEquals(expected, d_wizard.getOutcomeMeasureListModel().getValue());
	}
	
	@Test
	public void testGetEndpointSetNoIndication() {
		assertNotNull(d_wizard.getOutcomeMeasureListModel().getValue());
		assertTrue(d_wizard.getOutcomeMeasureListModel().getValue().isEmpty());
	}
	
	@Test
	public void testLabelEndpointEvents() {
		List<Indication> indList = d_wizard.getIndicationListModel().getValue();
		d_wizard.getIndicationModel().setValue(indList.get(indList.size()-1));
		
		List<OutcomeMeasure> outcomeList = d_wizard.getOutcomeMeasureListModel().getValue();
		OutcomeMeasure firstEndp = outcomeList.get(0);
		OutcomeMeasure lastEndp = outcomeList.get(outcomeList.size() - 1);
		
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
		List<Indication> indListModel = d_wizard.getIndicationListModel().getValue();
		d_wizard.getIndicationModel().setValue(indListModel.get(0));
		
		Indication indic = indListModel.get(0);	
		Indication lastIndic = indListModel.get(indListModel.size()-1);
		
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
		d_wizard.getIndicationModel().setValue(d_wizard.getIndicationListModel().getValue().get(0));
		d_wizard.getEndpointModel().setValue(d_wizard.getOutcomeMeasureListModel().getValue().get(0));		
		
		Indication indic = d_wizard.getIndicationListModel().getValue().get(0);
		AbstractOutcomeMeasure endp = (AbstractOutcomeMeasure) d_wizard.getEndpointModel().getValue();
		
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
		AbstractOutcomeMeasure newValue = ExampleData.buildEndpointHamd();
		ValueModel vm = d_wizard.getEndpointModel();
		JUnitUtil.testSetter(vm, null, newValue);
		
		assertEquals(newValue, d_wizard.getEndpointModel().getValue());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetInvalidEndpoint() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		AbstractOutcomeMeasure newValue = ExampleData.buildEndpointCVdeath();
		
		assertTrue(!d_wizard.getOutcomeMeasureListModel().getValue().contains(newValue));
		
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
		AbstractOutcomeMeasure ep = ExampleData.buildEndpointHamd();
		
		List<Drug> expected = new ArrayList<Drug>();
		expected.add(ExampleData.buildDrugFluoxetine());
		expected.add(ExampleData.buildDrugParoxetine());
		expected.add(ExampleData.buildDrugSertraline());
		
		d_wizard.getIndicationModel().setValue(ind);
		d_wizard.getEndpointModel().setValue(ep);
		
		assertEquals(expected, d_wizard.getDrugListModel().getValue());
	}
	
	@Test
	public void testGetDrugSetNoEndpoint() {
		Indication ind = ExampleData.buildIndicationDepression();
		
		d_wizard.getIndicationModel().setValue(ind);
		assertNull(d_wizard.getEndpointModel().getValue());
		assertNotNull(d_wizard.getDrugListModel().getValue());
		
		assertTrue(d_wizard.getDrugListModel().getValue().isEmpty());
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
		AbstractOutcomeMeasure ep = ExampleData.buildEndpointHamd();
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
		AbstractOutcomeMeasure ep = ExampleData.buildEndpointHamd();
		Drug d = ExampleData.buildDrugCandesartan();
		d_wizard.getIndicationModel().setValue(ind);
		d_wizard.getEndpointModel().setValue(ep);
		
		assertTrue(!d_wizard.getDrugListModel().getValue().contains(d));
		
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
	public void testGetOutcomeMeasureListModel() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		List<OutcomeMeasure> expected = d_wizard.getOutcomeMeasureListModel().getValue();
		ListHolder<OutcomeMeasure> omList = d_wizard.getOutcomeMeasureListModel();
		assertEquals(expected, omList.getValue());
	}
	
	@Test
	public void testEndpointListModelEventOnIndicationChange() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationChronicHeartFailure());
		List<OutcomeMeasure> newValue = d_wizard.getOutcomeMeasureListModel().getValue();
		
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		ValueModel endpointList = d_wizard.getOutcomeMeasureListModel();
		PropertyChangeListener l = JUnitUtil.mockListener(endpointList, "value", null, newValue);
		
		endpointList.addValueChangeListener(l);
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationChronicHeartFailure());
		verify(l);
	}
	
	@Test
	public void testGetDrugListModel() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		List<Drug> expected = d_wizard.getDrugListModel().getValue();
		ListHolder<Drug> drugList = d_wizard.getDrugListModel();
		assertEquals(expected, drugList.getValue());
	}
	
	@Test
	public void testDrugListModelEventOnEndpointChange() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointCgi());
		List<Drug> newValue = d_wizard.getDrugListModel().getValue();
		
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
		List<Study> expected = new ArrayList<Study>();
		expected.add(ExampleData.buildStudyChouinard());
		
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getFirstDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		d_wizard.getSecondDrugModel().setValue(ExampleData.buildDrugParoxetine());
		
		assertEquals(expected, d_wizard.getStudyListModel().getIncludedStudies().getValue());
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
		assertTrue(d_wizard.getStudyListModel().getIncludedStudies().getValue().isEmpty());
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
		
		assertEquals(d_wizard.getStudyListModel().getIncludedStudies().getValue(), d_wizard.getStudyListModel().getSelectedStudiesModel().getValue());
	}
	
	@Test
	public void testCreateMetaAnalysis() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getFirstDrugModel().setValue(ExampleData.buildDrugFluoxetine());
		d_wizard.getSecondDrugModel().setValue(ExampleData.buildDrugParoxetine());
		
		RandomEffectsMetaAnalysis ma = d_wizard.getMetaAnalysisModel().getBean();
		assertEquals(ma.getFirstDrug(), d_wizard.getFirstDrugModel().getValue());
		assertEquals(ma.getSecondDrug(), d_wizard.getSecondDrugModel().getValue());
		JUnitUtil.assertAllAndOnly((Collection<?>) d_wizard.getStudyListModel().getSelectedStudiesModel().getValue(), (Collection<?>) ma.getStudies());
		assertEquals(ma.getEndpoint(), d_wizard.getEndpointModel().getValue());
		assertEquals(ma.getIndication(), d_wizard.getIndicationModel().getValue());
	}
}
