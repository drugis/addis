/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyListPresentation;
import org.drugis.addis.presentation.TreatmentDefinitionsGraphModel;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.JUnitUtil;
import org.drugis.common.event.ListDataEventMatcher;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

public class NetworkMetaAnalysisWizardPMTest {

	private Domain d_domain;
	private NetworkMetaAnalysisWizardPM d_pm;
	private TreatmentDefinition d_paroxSet;
	private TreatmentDefinition d_fluoxSet;
	private TreatmentDefinition d_sertrSet;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pm = new NetworkMetaAnalysisWizardPM(d_domain, new PresentationModelFactory(d_domain));
		d_paroxSet = TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine());
		d_fluoxSet = TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine());
		d_sertrSet = TreatmentDefinition.createTrivial(ExampleData.buildDrugSertraline());
	}
	
	/*
	 * After the initial selection of X = (Indication, OutcomeMeasure) the set of available studies
	 * (getStudiesEndpointAndIndication) should contain exactly those studies that include X and
	 * have at least two arms with valid measurements.
	 */
	@Test
	public void testOnlyIncludeStudiesWithAtLeastTwoMeasuredDrugs() {
		makeMissing();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
	
		assertEquals(Collections.singletonList(ExampleData.buildStudyChouinard()), d_pm.getStudiesEndpointAndIndication());
	}

	/*
	 * After the initial selection of X = (Indication, OutcomeMeasure) the set of available alternatives
	 * (getAvailableRawTreatmentDefinitions) should contain exactly those alternatives measured in the
	 * available studies (getStudiesEndpointAndIndication).
	 * These should be trivial TreatmentDefinitions.
	 */
	@Test
	public void testAvailableRawIncludesOnlyAlternativesMeasuredInSomeStudy() {
		makeMissing();
		addCitalopram();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		
		TreatmentDefinition[] expected = new TreatmentDefinition[] {
				TreatmentDefinition.createTrivial(ExampleData.buildDrugCitalopram()),
				d_fluoxSet,
				d_paroxSet
			};
		
		assertEquals(Arrays.asList(expected), d_pm.getAvailableRawTreatmentDefinitions());
	}

	/*
	 * The raw selection (selection of trivial TreatmentDefinitions) should be considered
	 * complete if they form a connected subgraph of the getRawAlternativesGraph and they
	 * contain at least one TreatmentDefinition.
	 * 
	 * FIXME: this test probably tests for *at least two*, should be fixed.
	 */
	@Test
	public void testRawSelectionCompleteModel() {
		ValueModel completeModel = d_pm.getRawSelectionCompleteModel();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.rebuildRawAlternativesGraph();
		assertTrue((Boolean)completeModel.getValue());
		
		ArrayList<TreatmentDefinition> newList = new ArrayList<TreatmentDefinition>();
		newList.add(d_sertrSet);
		d_pm.getSelectedRawTreatmentDefinitions().clear();
		d_pm.getSelectedRawTreatmentDefinitions().addAll(newList);
		assertFalse((Boolean)completeModel.getValue());
		
		newList = new ArrayList<TreatmentDefinition>(newList);
		newList.add(d_paroxSet);
		d_pm.getSelectedRawTreatmentDefinitions().clear();
		d_pm.getSelectedRawTreatmentDefinitions().addAll(newList);
		assertFalse((Boolean)completeModel.getValue());
		
		newList = new ArrayList<TreatmentDefinition>(newList);		
		newList.add(d_fluoxSet);
		d_pm.getSelectedRawTreatmentDefinitions().clear();
		d_pm.getSelectedRawTreatmentDefinitions().addAll(newList);
		assertTrue((Boolean)completeModel.getValue());		
	}
	
	/*
	 * The selected drugs are those drugs occurring in at least one of the getSelectedRawTreatmentDefinitions.
	 */
	@Test
	public void testSelectedDrugs() {
		Drug fluox = ExampleData.buildDrugFluoxetine();
		Drug parox = ExampleData.buildDrugParoxetine();
		Drug sertra = ExampleData.buildDrugSertraline();
		Collection<Drug> allDrugs = Arrays.asList(fluox, parox, sertra);
		
		d_domain.getStudies().add(ExampleData.realBuildStudyCombinationTreatment());
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.rebuildRawAlternativesGraph();
		JUnitUtil.assertAllAndOnly(allDrugs, d_pm.getSelectedDrugs());
		
		d_pm.getRawAlternativesGraph().getSelectedDefinitions().remove(TreatmentDefinition.createTrivial(fluox));
		JUnitUtil.assertAllAndOnly(allDrugs, d_pm.getSelectedDrugs());
		
		d_pm.getRawAlternativesGraph().getSelectedDefinitions().remove(TreatmentDefinition.createTrivial(Arrays.asList(fluox, parox)));
		JUnitUtil.assertAllAndOnly(Arrays.asList(parox, sertra), d_pm.getSelectedDrugs());
		
		d_pm.getRawAlternativesGraph().getSelectedDefinitions().remove(TreatmentDefinition.createTrivial(parox));
		JUnitUtil.assertAllAndOnly(Arrays.asList(sertra), d_pm.getSelectedDrugs());
		
		d_pm.getRawAlternativesGraph().getSelectedDefinitions().add(TreatmentDefinition.createTrivial(Arrays.asList(fluox, parox)));
		JUnitUtil.assertAllAndOnly(allDrugs, d_pm.getSelectedDrugs());
	}
	
	/*
	 * A categorization should be chosen for each of the getSelectedDrugs.
	 */
	@Test
	public void testCategorizationModel() {
		Drug fluox = ExampleData.buildDrugFluoxetine();
		Drug parox = ExampleData.buildDrugParoxetine();
		
		d_domain.getStudies().add(ExampleData.realBuildStudyCombinationTreatment());
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.rebuildRawAlternativesGraph();
		
		TreatmentCategorization fluoxCatz = TreatmentCategorization.createTrivial(fluox);
		TreatmentCategorization paroxCatz = TreatmentCategorization.createTrivial(parox);
		
		assertEquals(fluoxCatz, d_pm.getCategorizationModel(fluox).getValue());
		assertEquals(paroxCatz, d_pm.getCategorizationModel(parox).getValue());
		
		assertEquals(Arrays.asList(fluoxCatz), d_pm.getAvailableCategorizations(fluox));
		assertEquals(Arrays.asList(paroxCatz), d_pm.getAvailableCategorizations(parox));
		
		TreatmentCategorization fixedCatz = ExampleData.buildCategorizationFixedDose(fluox);
		d_domain.getTreatmentCategorizations().add(fixedCatz);
		assertEquals(Arrays.asList(fluoxCatz, fixedCatz), d_pm.getAvailableCategorizations(fluox));
	}

	/*
	 * Based on the getSelectedRawTreatmentDefinitions and the getCategorizationModel for each drug,
	 * the available refined categorizations should be all permutations of categories of the drugs
	 * in each raw TreatmentDefintion.
	 * 
	 * Example: if we have {Fluoxetine + Paroxetine, Setraline} and we have selected "Fluoxetine LD/HD" for
	 * Fluoxetine and trivial categorizations for the other two drugs, we get:
	 * {Fluoxetine LD + Paroxetine, Fluoxetine HD + Paroxetine, Setraline}.
	 * 
	 * This list should further be filtered for measured-ness but it is worth testing the above behaviour
	 * separately first. Also, the filtering of studies/definitions also happens for the "raw" selection
	 * so could be extracted to be tested only once.
	 */
	@Test
	public void testPermuteTreatmentDefinitions() {
		fail("Not implemented");
	}
	
	//// Beyond here we should have REFINED TreatmentDefinitions.
	
	
	@Test
	public void testUnmeasuredDefinitionsIgnoredInArmSelection() {
		Study study = ExampleData.buildStudyMultipleArmsperDrug().clone();
		study.createAndAddArm("Sertraline-0", 54, ExampleData.buildDrugSertraline(), null);
		Arm parox0 = study.getArms().get(0);
		assertEquals("Paroxetine-0", parox0.getName()); // Assumption check
		study.getMeasurement(ExampleData.buildEndpointHamd(), parox0).setSampleSize(null);
		d_domain.getStudies().remove(ExampleData.buildStudyMultipleArmsperDrug());
		d_domain.getStudies().add(study);
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.getSelectedRawTreatmentDefinitions().clear();
		d_pm.getSelectedRawTreatmentDefinitions().addAll(Arrays.asList(new TreatmentDefinition[] {
				d_fluoxSet,
				d_paroxSet,
				d_sertrSet}));
	
		assertTrue(d_pm.getSelectedStudiesModel().contains(study));
		assertNotNull(d_pm.getSelectedArmModel(study, d_fluoxSet));
		assertNotNull(d_pm.getSelectedArmModel(study, d_paroxSet));
		assertNull(d_pm.getSelectedArmModel(study, d_sertrSet));
		
		assertEquals(Collections.singletonList(study.getArms().get(1)), d_pm.getArmsPerStudyPerDefinition(study, d_paroxSet));
		assertEquals(Collections.singletonList(study.getArms().get(2)), d_pm.getArmsPerStudyPerDefinition(study, d_fluoxSet));
		assertEquals(Collections.emptyList(), d_pm.getArmsPerStudyPerDefinition(study, d_sertrSet));
	}

	@Test
	public void testSelectableStudyListPM() {
		SelectableStudyListPresentation listModel = d_pm.getSelectableStudyListPM();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.rebuildRawAlternativesGraph();

		ArrayList<Study> newList = new ArrayList<Study>();
		newList.addAll(d_pm.getStudiesEndpointAndIndication());
		assertEquals(newList, listModel.getAvailableStudies());

		ArrayList<TreatmentDefinition> selectionList = new ArrayList<TreatmentDefinition>();
		selectionList.add(d_sertrSet);
		selectionList.add(d_paroxSet);
		
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(listModel.getAvailableStudies(), ListDataEvent.INTERVAL_REMOVED, 0, newList.size() - 1)));
		replay(mock);
		
		listModel.getAvailableStudies().addListDataListener(mock);
		d_pm.getSelectedRawTreatmentDefinitions().clear();
		d_pm.getSelectedRawTreatmentDefinitions().addAll(selectionList);
		verify(mock);
	}
	
	@Test
	public void testSelectableStudiesObservesSelectedAlternatives() {
		SelectableStudyListPresentation selectableStudiesPM = d_pm.getSelectableStudyListPM();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		ArrayList<Study> allStudiesList = new ArrayList<Study>(d_pm.getStudiesEndpointAndIndication());
		
		ArrayList<TreatmentDefinition> selectionList = new ArrayList<TreatmentDefinition>();
		selectionList.add(d_sertrSet);
		selectionList.add(d_paroxSet);
		
		d_pm.getSelectedRawTreatmentDefinitions().clear();
		d_pm.getSelectedRawTreatmentDefinitions().addAll(new ArrayList<TreatmentDefinition>(selectionList));
		
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(selectableStudiesPM.getAvailableStudies(), ListDataEvent.INTERVAL_ADDED, 0, allStudiesList.size() - 1)));
		replay(mock);

		selectableStudiesPM.getAvailableStudies().addListDataListener(mock);
		selectionList.add(d_fluoxSet);	
		d_pm.getSelectedRawTreatmentDefinitions().clear();
		d_pm.getSelectedRawTreatmentDefinitions().addAll(selectionList);		
		verify(mock);
	}
	
	@Test
	public void testOverviewGraphObservesSelectedAlternatives() {
		TreatmentDefinitionsGraphModel graphModel = d_pm.getOverviewGraph();

		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.rebuildRawAlternativesGraph();
		
		d_pm.rebuildOverviewGraph();
		assertEquals(3, graphModel.vertexSet().size());
		assertEquals(2, graphModel.edgeSet().size());
		
		ArrayList<TreatmentDefinition> selectionList = new ArrayList<TreatmentDefinition>();
		selectionList.add(d_sertrSet);
		selectionList.add(d_paroxSet);
		d_pm.getSelectedRawTreatmentDefinitions().clear();
		d_pm.getSelectedRawTreatmentDefinitions().addAll(selectionList);
		d_pm.rebuildOverviewGraph();
		assertEquals(2, graphModel.vertexSet().size());
		assertEquals(0, graphModel.edgeSet().size());
	}
	
	@Test
	/* This test is motivated by bug #337, in which multiple drugs had only missing measurements;
	 * this is rendered incorrectly, hiding some (non-connected) drugs behind others, thus disabling "next"
	 */
	public void testHandleStudyWithMissingMeasurements() {
		makeMissing();
		TreatmentDefinitionsGraphModel graphModel = d_pm.getOverviewGraph();

		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_pm.rebuildRawAlternativesGraph();
		
		d_pm.rebuildOverviewGraph();
		assertEquals(2, graphModel.vertexSet().size());
		assertEquals(1, graphModel.edgeSet().size());
		
		addCitalopram();
		graphModel = d_pm.getOverviewGraph();

		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_pm.rebuildRawAlternativesGraph();
		
		d_pm.rebuildOverviewGraph();
		assertEquals(3, graphModel.vertexSet().size());
		assertEquals(2, graphModel.edgeSet().size());
	}

	private void addCitalopram() {
		Arm arm = ExampleData.buildStudyBennie().createAndAddArm("Citalopram-2", 100, 
				ExampleData.buildDrugCitalopram(), new FixedDose(12, DoseUnit.MILLIGRAMS_A_DAY));
		ExampleData.buildStudyBennie().setMeasurement(ExampleData.buildEndpointCgi(), arm, 
				new BasicContinuousMeasurement(3.0, 1.2, 103));
		d_pm = new NetworkMetaAnalysisWizardPM(d_domain, new PresentationModelFactory(d_domain));
	}

	private void makeMissing() {
		// make setraline missing in study bennie
		ExampleData.buildStudyBennie().setMeasurement(ExampleData.buildEndpointCgi(), 
				ExampleData.buildStudyBennie().findArm("Sertraline-1"), null);
		
		d_pm = new NetworkMetaAnalysisWizardPM(d_domain, new PresentationModelFactory(d_domain));
	}
	
	@Test
	public void testOverviewGraphOnlySelectedStudies() {
		TreatmentDefinitionsGraphModel graphModel = d_pm.getOverviewGraph();

		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.rebuildRawAlternativesGraph();

		// Remove Parox studies
		d_pm.getSelectableStudyListPM().getSelectedStudyBooleanModel(
				ExampleData.buildStudyMultipleArmsperDrug()).setValue(false);
		d_pm.getSelectableStudyListPM().getSelectedStudyBooleanModel(
				ExampleData.buildStudyDeWilde()).setValue(false);
		d_pm.getSelectableStudyListPM().getSelectedStudyBooleanModel(
				ExampleData.buildStudyChouinard()).setValue(false);
		
		d_pm.rebuildOverviewGraph();
		assertEquals(3, graphModel.vertexSet().size());
		assertEquals(1, graphModel.edgeSet().size());
	}
	
	@Test
	public void testOverviewGraphConnectedModel() {
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.rebuildRawAlternativesGraph();
		
		d_pm.rebuildOverviewGraph();
		ValueHolder<Boolean> completeModel = d_pm.getOverviewGraphConnectedModel();
		assertTrue(completeModel.getValue());
		
		PropertyChangeListener mock = JUnitUtil.mockAnyTimesListener(completeModel, "value", true, false);
		completeModel.addValueChangeListener(mock);
		
		// Remove Parox studies
		ArrayList<Study> studyList = new ArrayList<Study>();
		studyList.add(ExampleData.buildStudyBennie());
		d_pm.getSelectableStudyListPM().getSelectedStudyBooleanModel(
				ExampleData.buildStudyMultipleArmsperDrug()).setValue(false);
		d_pm.getSelectableStudyListPM().getSelectedStudyBooleanModel(
				ExampleData.buildStudyDeWilde()).setValue(false);
		d_pm.getSelectableStudyListPM().getSelectedStudyBooleanModel(
				ExampleData.buildStudyChouinard()).setValue(false);
		
		d_pm.rebuildOverviewGraph();
		verify(mock);
		assertFalse(completeModel.getValue());
	}

	
	@Test
	public void testCreateMetaAnalysis() {
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.getSelectedRawTreatmentDefinitions().clear();
		d_pm.getSelectedRawTreatmentDefinitions().addAll(Arrays.asList(new TreatmentDefinition[] {
				d_fluoxSet,
				d_paroxSet,
				d_sertrSet}));
		
		Study multiple = ExampleData.buildStudyMultipleArmsperDrug();
		List<Arm> arms = new ArrayList<Arm>(multiple.getArms());
		arms.remove(d_pm.getSelectedArmModel(multiple, d_paroxSet).getValue());
		arms.remove(d_pm.getSelectedArmModel(multiple, d_fluoxSet).getValue());
		Arm arm = arms.get(0); // The currently unused arm 
		d_pm.getSelectedArmModel(multiple, d_paroxSet).setValue(arm);
		
		NetworkMetaAnalysis ma = d_pm.createAnalysis("name");
		
		assertEquals(d_pm.getSelectedRawTreatmentDefinitions(), ma.getAlternatives());
		JUnitUtil.assertAllAndOnly(ma.getIncludedStudies(),
				d_pm.getSelectableStudyListPM().getSelectedStudiesModel());
		assertEquals(d_pm.getOutcomeMeasureModel().getValue(), ma.getOutcomeMeasure());
		assertEquals(d_pm.getIndicationModel().getValue(), ma.getIndication());
		assertEquals(arm, ma.getArm(multiple, d_paroxSet));
		for (Study s : ma.getIncludedStudies()) {
			for (TreatmentDefinition d : s.getTreatmentDefinition()) {
				assertNotNull(ma.getArm(s, d));
			}
		}
	}
}
