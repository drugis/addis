/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyOutcomeMeasure;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.TreatmentDefinitionsGraphModel;
import org.drugis.common.JUnitUtil;
import org.drugis.common.event.ListDataEventMatcher;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class PairWiseMetaAnalysisWizardPresentationTest {
	
	private Domain d_domain;
	private PairWiseMetaAnalysisWizardPresentation d_wizard;
	private TreatmentDefinition d_fluoxSet;
	private TreatmentDefinition d_paroxSet;
	private TreatmentDefinition d_sertrSet;
	private TreatmentDefinition d_escitSet;
	private TreatmentDefinition d_citalSet;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_wizard = new PairWiseMetaAnalysisWizardPresentation(d_domain, new PresentationModelFactory(d_domain));
		
		d_fluoxSet = TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine());
		d_paroxSet = TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine());
		d_sertrSet = TreatmentDefinition.createTrivial(ExampleData.buildDrugSertraline());
		d_escitSet = TreatmentDefinition.createTrivial(ExampleData.buildDrugEscitalopram());
		d_citalSet = TreatmentDefinition.createTrivial(ExampleData.buildDrugCitalopram());
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
		assertEquals(expected, d_wizard.getAvailableOutcomeMeasures());
	}
	
	@Test
	public void testGetEndpointSetForAdverseEvent() {
		d_domain.getStudies().get(0).getAdverseEvents().add(new StudyOutcomeMeasure<AdverseEvent>(ExampleData.buildAdverseEventConvulsion()));
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		List<OutcomeMeasure> expected = new ArrayList<OutcomeMeasure>();
		expected.add(ExampleData.buildEndpointCgi());
		expected.add(ExampleData.buildAdverseEventConvulsion());
		expected.add(ExampleData.buildEndpointHamd());
		JUnitUtil.assertAllAndOnly(expected, d_wizard.getAvailableOutcomeMeasures());
	}
	
	@Test
	public void testGetEndpointSetNoIndication() {
		assertNotNull(d_wizard.getAvailableOutcomeMeasures());
		assertTrue(d_wizard.getAvailableOutcomeMeasures().isEmpty());
	}
	
	@Test
	public void testLabelEndpointEvents() {
		List<Indication> indList = d_wizard.getIndicationsModel();
		d_wizard.getIndicationModel().setValue(indList.get(indList.size()-1));
		
		List<OutcomeMeasure> outcomeList = d_wizard.getAvailableOutcomeMeasures();
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
		d_wizard.getOutcomeMeasureModel().setValue(d_wizard.getAvailableOutcomeMeasures().get(0));		
		
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
	public void testGetTreatmentDefinitions() {
		Indication ind = ExampleData.buildIndicationDepression();
		OutcomeMeasure ep = ExampleData.buildEndpointHamd();
		
		List<TreatmentDefinition> expected = new ArrayList<TreatmentDefinition>();
		expected.add(d_fluoxSet);
		expected.add(d_paroxSet);
		expected.add(d_sertrSet);
		
		d_wizard.getIndicationModel().setValue(ind);
		d_wizard.getOutcomeMeasureModel().setValue(ep);
		d_wizard.rebuildRawAlternativesGraph();
		assertEquals(expected, d_wizard.getAvailableRawTreatmentDefinitions());
	}
	
	@Test
	public void testGetTreatmentDefinitionsNoEndpoint() {
		Indication ind = ExampleData.buildIndicationDepression();
		
		d_wizard.getIndicationModel().setValue(ind);
		assertNull(d_wizard.getOutcomeMeasureModel().getValue());
		assertNotNull(d_wizard.getAvailableRawTreatmentDefinitions());
		
		assertTrue(d_wizard.getAvailableRawTreatmentDefinitions().isEmpty());
	}
	
	@Test
	public void testGetFirstDrugModel() {
		testDrugModelHelper(d_wizard.getRawFirstDefinitionModel());
	}

	@Test
	public void testGetSecondDrugModel() {
		testDrugModelHelper(d_wizard.getRawSecondDefinitionModel());
	}
	
	private void testDrugModelHelper(ValueModel drugModel) {
		assertNotNull(drugModel);
		assertEquals(null, drugModel.getValue());
	}

	@Test
	public void testSetFirstDrug(){
		testSetDrugHelper(d_wizard.getRawFirstDefinitionModel());
	}
	
	@Test
	public void testSetSecondDrug(){
		testSetDrugHelper(d_wizard.getRawSecondDefinitionModel());
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
		d_wizard.getRefinedSecondDefinitionModel().setValue(d_fluoxSet);
		d_wizard.getRefinedFirstDefinitionModel().setValue(d_fluoxSet);
		assertNull(d_wizard.getRefinedSecondDefinitionModel().getValue());
	}
	
	@Test
	public void testDrugCouplingSecond2First() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getRefinedFirstDefinitionModel().setValue(d_fluoxSet);
		d_wizard.getRefinedSecondDefinitionModel().setValue(d_fluoxSet);
		assertNull(d_wizard.getRefinedFirstDefinitionModel().getValue());
	}
	
	@Test
	public void testSelectedDrugList() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.rebuildRawAlternativesGraph();

		assertEquals(Collections.<TreatmentDefinition>emptyList(), d_wizard.getSelectedRawTreatmentDefinitions());
		
		ListDataListener mock = createMock(ListDataListener.class);
		mock.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_wizard.getSelectedRawTreatmentDefinitions(),
				ListDataEvent.INTERVAL_ADDED, 0, 0)));
		mock.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_wizard.getSelectedRawTreatmentDefinitions(),
				ListDataEvent.INTERVAL_ADDED, 1, 1)));
		mock.contentsChanged(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_wizard.getSelectedRawTreatmentDefinitions(),
				ListDataEvent.CONTENTS_CHANGED, 0, 0)));
		mock.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_wizard.getSelectedRawTreatmentDefinitions(),
				ListDataEvent.INTERVAL_REMOVED, 1, 1)));
		replay(mock);
		d_wizard.getSelectedRawTreatmentDefinitions().addListDataListener(mock);
	
		d_wizard.getRawFirstDefinitionModel().setValue(d_fluoxSet);

		assertEquals(Collections.<TreatmentDefinition>singletonList(d_fluoxSet), d_wizard.getSelectedRawTreatmentDefinitions());
		
		d_wizard.getRawSecondDefinitionModel().setValue(d_sertrSet);

		assertEquals(Arrays.asList(d_fluoxSet, d_sertrSet), d_wizard.getSelectedRawTreatmentDefinitions());
		
		d_wizard.getRawFirstDefinitionModel().setValue(null);

		assertEquals(Collections.<TreatmentDefinition>singletonList(d_sertrSet),
				d_wizard.getSelectedRawTreatmentDefinitions());
		verify(mock);
	}

	@Test
	public void testSelectedDrugListReplace() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.getRawFirstDefinitionModel().setValue(d_fluoxSet);
		d_wizard.getRawSecondDefinitionModel().setValue(d_sertrSet);
		d_wizard.rebuildRawAlternativesGraph();
		assertEquals(Arrays.asList(d_fluoxSet, d_sertrSet), d_wizard.getSelectedRawTreatmentDefinitions()); // just a sanity check
		
		ListDataListener mock = createMock(ListDataListener.class);
		mock.contentsChanged(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_wizard.getSelectedRawTreatmentDefinitions(), ListDataEvent.CONTENTS_CHANGED,
				1, 1)));
		d_wizard.getSelectedRawTreatmentDefinitions().addListDataListener(mock);
		replay(mock);

		d_wizard.getRawSecondDefinitionModel().setValue(d_paroxSet);
		assertEquals(Arrays.asList(d_fluoxSet, d_paroxSet), d_wizard.getSelectedRawTreatmentDefinitions());

		verify(mock);
	}
	
	@Test
	public void testStudyGraphPresentationModel() {
		TreatmentDefinitionsGraphModel model = d_wizard.getRawAlternativesGraph();
		
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.rebuildRawAlternativesGraph();
		
		List<TreatmentDefinition> drugs = new ArrayList<TreatmentDefinition>();
		drugs.add(d_fluoxSet);
		drugs.add(d_paroxSet);
		drugs.add(d_sertrSet);		
		
		assertEquals(drugs, model.getDefinitions());
	}
	
	@Test
	public void testGetOutcomeMeasureListModel() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		List<OutcomeMeasure> expected = d_wizard.getAvailableOutcomeMeasures();
		ObservableList<OutcomeMeasure> omList = d_wizard.getAvailableOutcomeMeasures();
		assertEquals(expected, omList);
	}
	
	@Test
	public void testEndpointListModelEventOnIndicationChange() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		ObservableList<OutcomeMeasure> endpointList = d_wizard.getAvailableOutcomeMeasures();
		
		ListDataListener l = createMock(ListDataListener.class);
		l.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(endpointList, ListDataEvent.INTERVAL_REMOVED, 0, 2)));
		l.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(endpointList, ListDataEvent.INTERVAL_ADDED, 0, 0)));
		replay(l);
		
		endpointList.addListDataListener(l);
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationChronicHeartFailure());
		verify(l);
	}
	
	@Test
	public void testGetDrugListModel() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		List<TreatmentDefinition> expected = d_wizard.getAvailableRawTreatmentDefinitions();
		ObservableList<TreatmentDefinition> drugList = d_wizard.getAvailableRawTreatmentDefinitions();
		assertEquals(expected, drugList);
	}
	
	@Test
	public void testDrugListModelEventOnEndpointChange() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.rebuildRawAlternativesGraph();

		ObservableList<TreatmentDefinition> definitionList = d_wizard.getAvailableRawTreatmentDefinitions();
		
		ListDataListener l = createMock(ListDataListener.class);
		l.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(definitionList, ListDataEvent.INTERVAL_REMOVED, 0, 2)));
		l.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(definitionList, ListDataEvent.INTERVAL_ADDED, 0, 2)));

		replay(l);
		definitionList.addListDataListener(l);
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.rebuildRawAlternativesGraph();

		verify(l);
	}
	
	@Test
	public void testEndpointChangeUnsetDrugs() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getRawFirstDefinitionModel().setValue(d_fluoxSet);
		d_wizard.getRawSecondDefinitionModel().setValue(d_paroxSet);

		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		
		assertNull(d_wizard.getRawFirstDefinitionModel().getValue());
		assertNull(d_wizard.getRawSecondDefinitionModel().getValue());
	}

	@Test
	public void testSameEndpointChangeKeepDrugs() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getRawFirstDefinitionModel().setValue(d_fluoxSet);
		d_wizard.getRawSecondDefinitionModel().setValue(d_paroxSet);

		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		
		assertNotNull(d_wizard.getRawFirstDefinitionModel().getValue());
		assertNotNull(d_wizard.getRawSecondDefinitionModel().getValue());
	}

	
	@Test
	public void testGetStudySet() {
		List<Study> expected = new ArrayList<Study>();
		expected.add(ExampleData.buildStudyChouinard());
		
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getRawFirstDefinitionModel().setValue(d_fluoxSet);
		d_wizard.getRawSecondDefinitionModel().setValue(d_paroxSet);
		d_wizard.rebuildAllGraphs();
		d_wizard.populateSelectableStudies();
		assertEquals(expected, d_wizard.getSelectableStudyListPM().getAvailableStudies());
	}
	
	@Test
	public void testGetStudySetNoFirstDrug() {
		testGetStudySetNoDrugHelper(d_wizard.getRawSecondDefinitionModel(), d_wizard.getRawFirstDefinitionModel());
	}

	@Test
	public void testGetStudySetNoSecondDrug() {
		testGetStudySetNoDrugHelper(d_wizard.getRawFirstDefinitionModel(), d_wizard.getRawSecondDefinitionModel());
	}
	
	private void testGetStudySetNoDrugHelper(ValueModel setDrugModel,
			ValueModel unsetDrugModel) {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		setDrugModel.setValue(d_fluoxSet);
		
		// sanity checks
		assertNull(unsetDrugModel.getValue());
		assertTrue(d_wizard.getSelectableStudyListPM().getAvailableStudies().isEmpty());
	}
	
	@Test
	public void testCascadeOfIndicationEndpointDrugs() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getRawFirstDefinitionModel().setValue(d_fluoxSet);
		d_wizard.getRawSecondDefinitionModel().setValue(d_paroxSet);

		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationChronicHeartFailure());
		
		assertNull(d_wizard.getRawFirstDefinitionModel().getValue());
		assertNull(d_wizard.getRawSecondDefinitionModel().getValue());
	}
	
	@Test
	public void testGetSelectedStudiesWithoutChange() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getRawFirstDefinitionModel().setValue(d_fluoxSet);
		d_wizard.getRawSecondDefinitionModel().setValue(d_paroxSet);
		
		assertEquals(d_wizard.getSelectableStudyListPM().getAvailableStudies(), d_wizard.getSelectableStudyListPM().getSelectedStudiesModel());
	}
	
	@Test
	public void testCreateMetaAnalysis() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.rebuildRawAlternativesGraph();

		d_wizard.getRawFirstDefinitionModel().setValue(d_fluoxSet);
		d_wizard.getRawSecondDefinitionModel().setValue(d_paroxSet);
		d_wizard.rebuildRefinedAlternativesGraph();
		d_wizard.populateSelectableStudies();
		d_wizard.rebuildArmSelection();
		
		RandomEffectsMetaAnalysis ma = (RandomEffectsMetaAnalysis) d_wizard.createAnalysis("name");
		assertEquals(ma.getFirstAlternative(), d_wizard.getRawFirstDefinitionModel().getValue());
		assertEquals(ma.getSecondAlternative(), d_wizard.getRawSecondDefinitionModel().getValue());
		JUnitUtil.assertAllAndOnly((Collection<?>) d_wizard.getSelectableStudyListPM().getSelectedStudiesModel(), (Collection<?>) ma.getIncludedStudies());
		assertEquals(ma.getOutcomeMeasure(), d_wizard.getOutcomeMeasureModel().getValue());
		assertEquals(ma.getIndication(), d_wizard.getIndicationModel().getValue());
	}
	
	@Test
	public void testGetSelectedStudyBooleanModel() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.getRawFirstDefinitionModel().setValue(d_fluoxSet);
		d_wizard.getRawSecondDefinitionModel().setValue(d_paroxSet);
		d_wizard.rebuildRawAlternativesGraph();
		d_wizard.rebuildRefinedAlternativesGraph();
		d_wizard.populateSelectableStudies();
		assertTrue((Boolean) d_wizard.getMetaAnalysisCompleteModel().getValue());
		d_wizard.getSelectableStudyListPM().getSelectedStudyBooleanModel(ExampleData.buildStudyChouinard()).setValue(false);
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
		d_wizard.getRawFirstDefinitionModel().setValue(d_fluoxSet);
		d_wizard.getRawSecondDefinitionModel().setValue(d_paroxSet);
		d_wizard.rebuildAllGraphs();
		d_wizard.populateSelectableStudies();
		d_wizard.getSelectableStudyListPM().getSelectedStudyBooleanModel(ExampleData.buildStudyChouinard()).setValue(false);
		d_wizard.getSelectableStudyListPM().getSelectedStudyBooleanModel(ExampleData.buildStudyDeWilde()).setValue(false);
		d_wizard.rebuildAllGraphs();

		assertEquals(expected, d_wizard.getArmsPerStudyPerDefinition(multipleArmsPerStudyPerDrug, d_paroxSet));
	}
	
	@Test
	public void testSelectedStudiesPropagate() {
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_wizard.rebuildRawAlternativesGraph();
		d_wizard.getRawFirstDefinitionModel().setValue(d_fluoxSet);
		d_wizard.getRawSecondDefinitionModel().setValue(d_paroxSet);
		d_wizard.rebuildRefinedAlternativesGraph();
		d_wizard.populateSelectableStudies();
		d_wizard.rebuildArmSelection();
		List<Study> studies = new ArrayList<Study>(d_wizard.getSelectableStudyListPM().getSelectedStudiesModel());
		assertAllAndOnly(studies, d_wizard.getMetaAnalysisModel().getStudyListPresentation().getIncludedStudies());
		
		d_wizard.getSelectableStudyListPM().getSelectedStudyBooleanModel(
				ExampleData.buildStudyChouinard()).setValue(false);
		studies.remove(ExampleData.buildStudyChouinard());
		
		assertAllAndOnly(studies, d_wizard.getMetaAnalysisModel().getStudyListPresentation().getIncludedStudies());
	}
	
	@Test
	public void testForNullPointersWhenSwitchingRateToContinuous() {
		d_domain.getDrugs().add(ExampleData.buildDrugCitalopram());
		d_domain.getDrugs().add(ExampleData.buildDrugEscitalopram());
		d_domain.getEndpoints().add(ExampleData.buildEndpointMadrs());
		d_domain.getAdverseEvents().add(ExampleData.buildAdverseEventDiarrhea());
		Study burke = ExampleData.buildStudyBurke();
		d_domain.getStudies().add(burke);
		
		d_wizard.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_wizard.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_wizard.rebuildRawAlternativesGraph();
		d_wizard.getRawFirstDefinitionModel().setValue(d_citalSet);
		d_wizard.getRawSecondDefinitionModel().setValue(d_escitSet);
		d_wizard.rebuildRefinedAlternativesGraph();
		d_wizard.populateSelectableStudies();
		d_wizard.rebuildArmSelection();

		d_wizard.getSelectedArmModel(burke, d_escitSet);

		TreatmentDefinition placeSet = TreatmentDefinition.createTrivial(ExampleData.buildPlacebo());
		d_wizard.getRawSecondDefinitionModel().setValue(placeSet);

		d_wizard.getSelectedArmModel(burke, placeSet);
	}
}
