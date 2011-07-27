/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.presentation.wizard;

import static org.drugis.common.JUnitUtil.assertAllAndOnly;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Study.StudyOutcomeMeasure;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class MetaAnalysisWizardPresentationTest {
	
	private Domain d_domain;
	private MetaAnalysisWizardPresentation d_wizard;
	private DrugSet d_fluoxSet;
	private DrugSet d_paroxSet;
	private DrugSet d_sertrSet;
	private DrugSet d_escitSet;
	private DrugSet d_citalSet;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_wizard = new MetaAnalysisWizardPresentation(d_domain, new PresentationModelFactory(d_domain));
		
		d_fluoxSet = new DrugSet(ExampleData.buildDrugFluoxetine());
		d_paroxSet = new DrugSet(ExampleData.buildDrugParoxetine());
		d_sertrSet = new DrugSet(ExampleData.buildDrugSertraline());
		d_escitSet = new DrugSet(ExampleData.buildDrugEscitalopram());
		d_citalSet = new DrugSet(ExampleData.buildDrugCitalopram());
	}
	
	@Test
	public void testGetIndicationSet() {
		assertTrue(d_domain.getIndications().containsAll(d_wizard.getIndicationsModel()));
		assertEquals(d_domain.getIndications().size(), d_wizard.getIndicationsModel().size());
	}
	
	@Test
	public void testGetIndicationModel() {
		assertNotNull(d_wizard.getIndicationModel());
		assertEquals(null, d_wizard.getIndicationModel().getValue());
	}
	
	@Test
	public void testSetIndication() {
		Indication newValue = d_domain.getIndications().get(0);
		ValueModel vm = d_wizard.getIndicationModel();
		JUnitUtil.testSetter(vm, null, newValue);
		
		assertEquals(newValue, d_wizard.getIndicationModel().getValue());
	}
	
	@Test
	public void testGetEndpointSet() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		List<OutcomeMeasure> expected = new ArrayList<OutcomeMeasure>();
		expected.add(ExampleData.buildEndpointCgi());		
		expected.add(ExampleData.buildEndpointHamd());
		expected.add(ExampleData.buildAdverseEventConvulsion());
		assertEquals(expected, d_wizard.getOutcomeMeasureListModel().getValue());
	}
	
	@Test
	public void testGetEndpointSetForAdverseEvent() {
		d_domain.getStudies().get(0).getAdverseEvents().add(new StudyOutcomeMeasure<AdverseEvent>(ExampleData.buildAdverseEventConvulsion()));
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		List<OutcomeMeasure> expected = new ArrayList<OutcomeMeasure>();
		expected.add(ExampleData.buildEndpointCgi());
		expected.add(ExampleData.buildAdverseEventConvulsion());
		expected.add(ExampleData.buildEndpointHamd());
		JUnitUtil.assertAllAndOnly(expected, d_wizard.getOutcomeMeasureListModel().getValue());
	}
	
	@Test
	public void testGetEndpointSetNoIndication() {
		assertNotNull(d_wizard.getOutcomeMeasureListModel().getValue());
		assertTrue(d_wizard.getOutcomeMeasureListModel().getValue().isEmpty());
	}
	
	@Test
	public void testLabelEndpointEvents() {
		List<Indication> indList = d_wizard.getIndicationsModel();
		d_wizard.getIndicationModel().setValue(indList.get(indList.size()-1));
		
		List<OutcomeMeasure> outcomeList = d_wizard.getOutcomeMeasureListModel().getValue();
		OutcomeMeasure firstEndp = outcomeList.get(0);
		OutcomeMeasure lastEndp = outcomeList.get(outcomeList.size() - 1);
		
		d_wizard.getOutcomeMeasureModel().setValue(firstEndp);
		
		ValueModel model = d_wizard.getStudiesMeasuringLabelModel();

		Object newValue = model.getValue();
		d_wizard.getOutcomeMeasureModel().setValue(lastEndp);
		
		PropertyChangeListener studiesLabelListener = JUnitUtil.mockListener(model, AbstractValueModel.PROPERTYNAME_VALUE, null, newValue);
		model.addValueChangeListener(studiesLabelListener);
		
		d_wizard.getOutcomeMeasureModel().setValue(firstEndp);
		verify(studiesLabelListener);
	}
		
	@Test
	public void testLabelIndicationEvents() {
		List<Indication> indListModel = d_wizard.getIndicationsModel();
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
		d_wizard.getIndicationModel().setValue(d_wizard.getIndicationsModel().get(0));
		d_wizard.getOutcomeMeasureModel().setValue(d_wizard.getOutcomeMeasureListModel().getValue().get(0));		
		
		Indication indic = d_wizard.getIndicationsModel().get(0);
		OutcomeMeasure endp = (OutcomeMeasure) d_wizard.getOutcomeMeasureModel().getValue();
		
		d_wizard.getIndicationModel().setValue(indic);		
		d_wizard.getOutcomeMeasureModel().setValue(endp);		
		ValueModel model = d_wizard.getStudiesMeasuringLabelModel();
		String endpVal = endp.toString();
		String indVal = indic.toString();
		String correctString = "Studies measuring " + indVal  + " on " + endpVal;
		assertEquals(correctString, model.getValue());
	}
	
	@Test
	public void testGetEndpointModel() {
		assertNotNull(d_wizard.getOutcomeMeasureModel());
		assertEquals(null, d_wizard.getOutcomeMeasureModel().getValue());
	}
	
	@Test
	public void testSetEndpoint() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		OutcomeMeasure newValue = ExampleData.buildEndpointHamd();
		ValueModel vm = d_wizard.getOutcomeMeasureModel();
		JUnitUtil.testSetter(vm, null, newValue);
		
		assertEquals(newValue, d_wizard.getOutcomeMeasureModel().getValue());
	}

	@Test
	public void testChangeIndicationUnsetEndpoint() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		PropertyChangeListener l = JUnitUtil.mockListener(
				d_wizard.getOutcomeMeasureModel(), "value", ExampleData.buildEndpointHamd(), null);
		d_wizard.getOutcomeMeasureModel().addValueChangeListener(l);
		
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationChronicHeartFailure());
		assertNull(d_wizard.getOutcomeMeasureModel().getValue());
		verify(l);
	}

	@Test
	public void testSameIndicationKeepEndpoint() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		assertNotNull(d_wizard.getOutcomeMeasureModel().getValue());
	}

	@Test
	public void testGetDrugSet() {
		Indication ind = ExampleData.buildIndicationDepression();
		OutcomeMeasure ep = ExampleData.buildEndpointHamd();
		
		List<DrugSet> expected = new ArrayList<DrugSet>();
		expected.add(d_fluoxSet);
		expected.add(d_paroxSet);
		expected.add(d_sertrSet);
		
		d_wizard.getIndicationModel().setValue(ind);
		d_wizard.getOutcomeMeasureModel().setValue(ep);
		
		assertEquals(expected, d_wizard.getDrugListModel().getValue());
	}
	
	@Test
	public void testGetDrugSetNoEndpoint() {
		Indication ind = ExampleData.buildIndicationDepression();
		
		d_wizard.getIndicationModel().setValue(ind);
		assertNull(d_wizard.getOutcomeMeasureModel().getValue());
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
		OutcomeMeasure ep = ExampleData.buildEndpointHamd();
		d_wizard.getIndicationModel().setValue(ind);
		d_wizard.getOutcomeMeasureModel().setValue(ep);
		
		JUnitUtil.testSetter(vm, null, d_fluoxSet);
		
		assertEquals(d_fluoxSet, vm.getValue());
	}

	@Test
	public void testDrugCouplingFirst2Second() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getSecondDrugModel().setValue(d_fluoxSet);
		d_wizard.getFirstDrugModel().setValue(d_fluoxSet);
		assertNull(d_wizard.getSecondDrugModel().getValue());
	}
	
	@Test
	public void testDrugCouplingSecond2First() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getFirstDrugModel().setValue(d_fluoxSet);
		d_wizard.getSecondDrugModel().setValue(d_fluoxSet);
		assertNull(d_wizard.getFirstDrugModel().getValue());
	}
	
	@Test
	public void testSelectedDrugList() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getSelectedDrugsModel().getValue().equals(Collections.<Drug>emptyList());
		
		PropertyChangeListener mock = JUnitUtil.mockListener(d_wizard.getSelectedDrugsModel(), "value", null, 
				Collections.<DrugSet>singletonList(d_fluoxSet));
		d_wizard.getSelectedDrugsModel().addValueChangeListener(mock);
		d_wizard.getFirstDrugModel().setValue(d_fluoxSet);
		assertEquals(Collections.<DrugSet>singletonList(d_fluoxSet),
				d_wizard.getSelectedDrugsModel().getValue());
		verify(mock);
		
		List<DrugSet> drugs = new ArrayList<DrugSet>();
		drugs.add(d_fluoxSet);
		drugs.add(d_sertrSet);
		d_wizard.getSecondDrugModel().setValue(d_sertrSet);
		assertEquals(drugs, d_wizard.getSelectedDrugsModel().getValue());
		
		d_wizard.getFirstDrugModel().setValue(null);
		assertEquals(Collections.<DrugSet>singletonList(d_sertrSet),
				d_wizard.getSelectedDrugsModel().getValue());
	}
	
	@Test
	public void testStudyGraphPresentationModel() {
		StudyGraphModel model = d_wizard.getStudyGraphModel();
		
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		
		List<DrugSet> drugs = new ArrayList<DrugSet>();
		drugs.add(d_fluoxSet);
		drugs.add(d_paroxSet);
		drugs.add(d_sertrSet);		
		
		assertEquals(drugs, model.getDrugs());
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
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		List<DrugSet> expected = d_wizard.getDrugListModel().getValue();
		ListHolder<DrugSet> drugList = d_wizard.getDrugListModel();
		assertEquals(expected, drugList.getValue());
	}
	
	@Test
	public void testDrugListModelEventOnEndpointChange() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		List<DrugSet> newValue = d_wizard.getDrugListModel().getValue();
		
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		ValueModel drugList = d_wizard.getDrugListModel();
		PropertyChangeListener l = JUnitUtil.mockListener(drugList, "value", null, newValue);
		
		drugList.addValueChangeListener(l);
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		verify(l);
	}
	
	@Test
	public void testEndpointChangeUnsetDrugs() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getFirstDrugModel().setValue(d_fluoxSet);
		d_wizard.getSecondDrugModel().setValue(d_paroxSet);

		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		
		assertNull(d_wizard.getFirstDrugModel().getValue());
		assertNull(d_wizard.getSecondDrugModel().getValue());
	}

	@Test
	public void testSameEndpointChangeKeepDrugs() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getFirstDrugModel().setValue(d_fluoxSet);
		d_wizard.getSecondDrugModel().setValue(d_paroxSet);

		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		
		assertNotNull(d_wizard.getFirstDrugModel().getValue());
		assertNotNull(d_wizard.getSecondDrugModel().getValue());
	}

	
	@Test
	public void testGetStudySet() {
		List<Study> expected = new ArrayList<Study>();
		expected.add(ExampleData.buildStudyChouinard());
		
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getFirstDrugModel().setValue(d_fluoxSet);
		d_wizard.getSecondDrugModel().setValue(d_paroxSet);
		
		assertEquals(expected, d_wizard.getStudyListModel().getAvailableStudies());
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
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		setDrugModel.setValue(d_fluoxSet);
		
		// sanity checks
		assertNull(unsetDrugModel.getValue());
		assertTrue(d_wizard.getStudyListModel().getAvailableStudies().isEmpty());
	}
	
	@Test
	public void testCascadeOfIndicationEndpointDrugs() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getFirstDrugModel().setValue(d_fluoxSet);
		d_wizard.getSecondDrugModel().setValue(d_paroxSet);

		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationChronicHeartFailure());
		
		assertNull(d_wizard.getFirstDrugModel().getValue());
		assertNull(d_wizard.getSecondDrugModel().getValue());
	}
	
	@Test
	public void testGetSelectedStudiesWithoutChange() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getFirstDrugModel().setValue(d_fluoxSet);
		d_wizard.getSecondDrugModel().setValue(d_paroxSet);
		
		assertEquals(d_wizard.getStudyListModel().getAvailableStudies(), d_wizard.getStudyListModel().getSelectedStudiesModel());
	}
	
	@Test
	public void testCreateMetaAnalysis() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getFirstDrugModel().setValue(d_fluoxSet);
		d_wizard.getSecondDrugModel().setValue(d_paroxSet);
		
		RandomEffectsMetaAnalysis ma = d_wizard.createMetaAnalysis("name");
		assertEquals(ma.getFirstDrug(), d_wizard.getFirstDrugModel().getValue());
		assertEquals(ma.getSecondDrug(), d_wizard.getSecondDrugModel().getValue());
		JUnitUtil.assertAllAndOnly((Collection<?>) d_wizard.getStudyListModel().getSelectedStudiesModel(), (Collection<?>) ma.getIncludedStudies());
		assertEquals(ma.getOutcomeMeasure(), d_wizard.getOutcomeMeasureModel().getValue());
		assertEquals(ma.getIndication(), d_wizard.getIndicationModel().getValue());
	}
	
	@Test
	public void testGetSelectedStudyBooleanModel() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getFirstDrugModel().setValue(d_fluoxSet);
		d_wizard.getSecondDrugModel().setValue(d_paroxSet);
		assertTrue((Boolean) d_wizard.getMetaAnalysisCompleteModel().getValue());
		d_wizard.getStudyListModel().getSelectedStudyBooleanModel(ExampleData.buildStudyChouinard()).setValue(false);
		assertTrue(!(Boolean) d_wizard.getMetaAnalysisCompleteModel().getValue());
	}
	
	@Test
	public void testGetArmsPerStudyPerDrug(){
		Study multipleArmsPerStudyPerDrug = ExampleData.buildStudyMultipleArmsperDrug();
		
		// Paroxetine data 1
		Arm parox1 = multipleArmsPerStudyPerDrug.getArms().get(0);
		
		// Paroxetine data 2
		Arm parox2 = multipleArmsPerStudyPerDrug.getArms().get(1);

		List<Arm> expected = new ArrayList<Arm>();
		expected.add(parox1);
		expected.add(parox2);		
		
		/* Select only the MultipleArmsperDrugStudy */
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getFirstDrugModel().setValue(d_fluoxSet);
		d_wizard.getSecondDrugModel().setValue(d_paroxSet);
		d_wizard.getStudyListModel().getSelectedStudyBooleanModel(ExampleData.buildStudyChouinard()).setValue(false);
		d_wizard.getStudyListModel().getSelectedStudyBooleanModel(ExampleData.buildStudyDeWilde()).setValue(false);
		
		assertEquals(expected, d_wizard.getArmsPerStudyPerDrug(multipleArmsPerStudyPerDrug, d_paroxSet).getValue());
	}
	
	@Test
	public void testSelectedStudiesPropagate() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getFirstDrugModel().setValue(d_fluoxSet);
		d_wizard.getSecondDrugModel().setValue(d_paroxSet);
		
		List<Study> studies = new ArrayList<Study>(d_wizard.getStudyListModel().getSelectedStudiesModel());
		assertAllAndOnly(studies, d_wizard.getMetaAnalysisModel().getIncludedStudies());
		
		d_wizard.getStudyListModel().getSelectedStudyBooleanModel(
				ExampleData.buildStudyChouinard()).setValue(false);
		studies.remove(ExampleData.buildStudyChouinard());
		
		assertAllAndOnly(studies, d_wizard.getMetaAnalysisModel().getIncludedStudies());
	}
	
	@Test
	public void testSwitchingRateToContinuousDrugStep() {
		Study burke = ExampleData.buildStudyBurke();
		d_domain.getStudies().add(burke);
		d_domain.getDrugs().add(ExampleData.buildDrugCitalopram());
		d_domain.getDrugs().add(ExampleData.buildDrugEscitalopram());
		d_domain.getEndpoints().add(ExampleData.buildEndpointMadrs());
		d_domain.getAdverseEvents().add(ExampleData.buildAdverseEventDiarrhea());
		
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getFirstDrugModel().setValue(d_citalSet);
		d_wizard.getSecondDrugModel().setValue(d_escitSet);
		
		d_wizard.getSelectedArmModel(burke, d_escitSet);

		DrugSet placeSet = new DrugSet(ExampleData.buildPlacebo());
		d_wizard.getSecondDrugModel().setValue(placeSet);

		d_wizard.getSelectedArmModel(burke, placeSet);
	}
	
}
