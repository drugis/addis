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

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyListPresentation;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.JUnitUtil;
import org.drugis.common.event.ListDataEventMatcher;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

public class NetworkMetaAnalysisWizardPMTest {

	private Domain d_domain;
	private NetworkMetaAnalysisWizardPM d_pm;
	private DrugSet d_paroxSet;
	private DrugSet d_fluoxSet;
	private DrugSet d_sertrSet;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pm = new NetworkMetaAnalysisWizardPM(d_domain, new PresentationModelFactory(d_domain));
		d_paroxSet = new DrugSet(ExampleData.buildDrugParoxetine());
		d_fluoxSet = new DrugSet(ExampleData.buildDrugFluoxetine());
		d_sertrSet = new DrugSet(ExampleData.buildDrugSertraline());
	}
	
	@Test
	public void testDrugsSelectedCompleteListener() {
		ValueModel completeModel = d_pm.getConnectedDrugsSelectedModel();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.updateStudyGraphModel();
		assertTrue((Boolean)completeModel.getValue());
		
		ArrayList<DrugSet> newList = new ArrayList<DrugSet>();
		newList.add(d_sertrSet);
		d_pm.getSelectedDrugsModel().clear();
		d_pm.getSelectedDrugsModel().addAll(newList);
		assertFalse((Boolean)completeModel.getValue());
		
		newList = new ArrayList<DrugSet>(newList);
		newList.add(d_paroxSet);
		d_pm.getSelectedDrugsModel().clear();
		d_pm.getSelectedDrugsModel().addAll(newList);
		assertFalse((Boolean)completeModel.getValue());
		
		newList = new ArrayList<DrugSet>(newList);		
		newList.add(d_fluoxSet);
		d_pm.getSelectedDrugsModel().clear();
		d_pm.getSelectedDrugsModel().addAll(newList);
		assertTrue((Boolean)completeModel.getValue());		
	}
	
	@Test
	public void testStudyListModel() {
		SelectableStudyListPresentation listModel = d_pm.getStudyListModel();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.updateStudyGraphModel();
		
		ArrayList<Study> newList = new ArrayList<Study>();
		newList.addAll(d_pm.getStudiesEndpointAndIndication());
		assertEquals(newList, listModel.getAvailableStudies());

		ArrayList<DrugSet> selectionList = new ArrayList<DrugSet>();
		selectionList.add(d_sertrSet);
		selectionList.add(d_paroxSet);
		
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(listModel.getAvailableStudies(), ListDataEvent.INTERVAL_REMOVED, 0, newList.size() - 1)));
		replay(mock);
		
		listModel.getAvailableStudies().addListDataListener(mock);
		d_pm.getSelectedDrugsModel().clear();
		d_pm.getSelectedDrugsModel().addAll(selectionList);
		verify(mock);
	}
	
	@Test
	public void testStudyListModelAdds() {
		SelectableStudyListPresentation listModel = d_pm.getStudyListModel();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		ArrayList<Study> allStudiesList = new ArrayList<Study>(d_pm.getStudiesEndpointAndIndication());
		
		ArrayList<DrugSet> selectionList = new ArrayList<DrugSet>();
		selectionList.add(d_sertrSet);
		selectionList.add(d_paroxSet);
		
		d_pm.getSelectedDrugsModel().clear();
		d_pm.getSelectedDrugsModel().addAll(new ArrayList<DrugSet>(selectionList));
		
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(listModel.getAvailableStudies(), ListDataEvent.INTERVAL_ADDED, 0, allStudiesList.size() - 1)));
		replay(mock);

		listModel.getAvailableStudies().addListDataListener(mock);
		selectionList.add(d_fluoxSet);	
		d_pm.getSelectedDrugsModel().clear();
		d_pm.getSelectedDrugsModel().addAll(selectionList);		
		verify(mock);
	}
	
	@Test
	public void testGetSelectedStudyGraphUpdateDrugs() {
		StudyGraphModel graphModel = d_pm.getSelectedStudyGraphModel();

		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.updateStudyGraphModel();
		
		d_pm.updateSelectedStudyGraphModel();
		assertEquals(3, graphModel.vertexSet().size());
		assertEquals(2, graphModel.edgeSet().size());
		
		ArrayList<DrugSet> selectionList = new ArrayList<DrugSet>();
		selectionList.add(d_sertrSet);
		selectionList.add(d_paroxSet);
		d_pm.getSelectedDrugsModel().clear();
		d_pm.getSelectedDrugsModel().addAll(selectionList);
		
		d_pm.updateSelectedStudyGraphModel();
		assertEquals(2, graphModel.vertexSet().size());
		assertEquals(0, graphModel.edgeSet().size());
	}
	
	@Test
	/* This test is motivated by bug #337, in which multiple drugs had only missing measurements;
	 * this is rendered incorrectly, hiding some (non-connected) drugs behind others, thus disabling "next"
	 */
	public void testHandleStudyWithMissingMeasurements() {
		makeMissing();
		StudyGraphModel graphModel = d_pm.getSelectedStudyGraphModel();

		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_pm.updateStudyGraphModel();
		
		d_pm.updateSelectedStudyGraphModel();
		assertEquals(2, graphModel.vertexSet().size());
		assertEquals(1, graphModel.edgeSet().size());
		
		addCitalopram();
		graphModel = d_pm.getSelectedStudyGraphModel();

		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		d_pm.updateStudyGraphModel();
		
		d_pm.updateSelectedStudyGraphModel();
		assertEquals(3, graphModel.vertexSet().size());
		assertEquals(2, graphModel.edgeSet().size());
	}

	private void addCitalopram() {
		Arm arm = ExampleData.buildStudyBennie().createAndAddArm("Citalopram-2", 100, 
				ExampleData.buildDrugCitalopram(), new FixedDose(12, ExampleData.MILLIGRAMS_A_DAY));
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
	public void testOnlyIncludeStudiesWithAtLeastTwoMeasuredDrugs() {
		makeMissing();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());

		assertEquals(Collections.singletonList(ExampleData.buildStudyChouinard()), d_pm.getStudiesEndpointAndIndication());
	}
	
	@Test
	public void testIncludeOnlyDrugsMeasuredInSomeStudy() {
		makeMissing();
		addCitalopram();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		
		DrugSet[] expected = new DrugSet[] {
				new DrugSet(ExampleData.buildDrugCitalopram()),
				d_fluoxSet,
				d_paroxSet
			};
		
		assertEquals(Arrays.asList(expected), d_pm.getDrugListModel());
	}
	
	@Test
	public void testGetSelectedStudyGraphUpdateStudies() {
		StudyGraphModel graphModel = d_pm.getSelectedStudyGraphModel();

		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.updateStudyGraphModel();

		// Remove Parox studies
		ArrayList<Study> studyList = new ArrayList<Study>();
		studyList.add(ExampleData.buildStudyBennie());
		d_pm.getStudyListModel().getSelectedStudyBooleanModel(
				ExampleData.buildStudyMultipleArmsperDrug()).setValue(false);
		d_pm.getStudyListModel().getSelectedStudyBooleanModel(
				ExampleData.buildStudyDeWilde()).setValue(false);
		d_pm.getStudyListModel().getSelectedStudyBooleanModel(
				ExampleData.buildStudyChouinard()).setValue(false);
		
		d_pm.updateSelectedStudyGraphModel();
		assertEquals(3, graphModel.vertexSet().size());
		assertEquals(1, graphModel.edgeSet().size());
	}
	
	@Test
	public void testSelectedStudyGraphConnectedModel() {
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.updateStudyGraphModel();
		
		d_pm.updateSelectedStudyGraphModel();
		ValueHolder<Boolean> completeModel = d_pm.getSelectedStudyGraphConnectedModel();
		assertTrue(completeModel.getValue());
		
		PropertyChangeListener mock = JUnitUtil.mockAnyTimesListener(completeModel, "value", true, false);
		completeModel.addValueChangeListener(mock);
		
		// Remove Parox studies
		ArrayList<Study> studyList = new ArrayList<Study>();
		studyList.add(ExampleData.buildStudyBennie());
		d_pm.getStudyListModel().getSelectedStudyBooleanModel(
				ExampleData.buildStudyMultipleArmsperDrug()).setValue(false);
		d_pm.getStudyListModel().getSelectedStudyBooleanModel(
				ExampleData.buildStudyDeWilde()).setValue(false);
		d_pm.getStudyListModel().getSelectedStudyBooleanModel(
				ExampleData.buildStudyChouinard()).setValue(false);
		
		d_pm.updateSelectedStudyGraphModel();
		verify(mock);
		assertFalse(completeModel.getValue());
	}

	
	@Test
	public void testCreateMetaAnalysis() {
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.getSelectedDrugsModel().clear();
		d_pm.getSelectedDrugsModel().addAll(Arrays.asList(new DrugSet[] {
				d_fluoxSet,
				d_paroxSet,
				d_sertrSet}));
		
		Study multiple = ExampleData.buildStudyMultipleArmsperDrug();
		List<Arm> arms = new ArrayList<Arm>(multiple.getArms());
		arms.remove(d_pm.getSelectedArmModel(multiple, d_paroxSet).getValue());
		arms.remove(d_pm.getSelectedArmModel(multiple, d_fluoxSet).getValue());
		Arm arm = arms.get(0); // The currently unused arm 
		d_pm.getSelectedArmModel(multiple, d_paroxSet).setValue(arm);
		
		NetworkMetaAnalysis ma = d_pm.createMetaAnalysis("name");
		
		assertEquals(d_pm.getSelectedDrugsModel(), ma.getIncludedDrugs());
		JUnitUtil.assertAllAndOnly(ma.getIncludedStudies(),
				d_pm.getStudyListModel().getSelectedStudiesModel());
		assertEquals(d_pm.getOutcomeMeasureModel().getValue(), ma.getOutcomeMeasure());
		assertEquals(d_pm.getIndicationModel().getValue(), ma.getIndication());
		assertEquals(arm, ma.getArm(multiple, d_paroxSet));
		for (Study s : ma.getIncludedStudies()) {
			for (DrugSet d : s.getDrugs()) {
				assertNotNull(ma.getArm(s, d));
			}
		}
	}
	
	@Test
	public void testArmAndDrugSetExclusions() {
		Study study = ExampleData.buildStudyMultipleArmsperDrug().clone();
		study.createAndAddArm("Sertraline-0", 54, ExampleData.buildDrugSertraline(), null);
		Arm parox0 = study.getArms().get(0);
		assertEquals("Paroxetine-0", parox0.getName()); // Assumption check
		study.getMeasurement(ExampleData.buildEndpointHamd(), parox0).setSampleSize(null);
		d_domain.getStudies().remove(ExampleData.buildStudyMultipleArmsperDrug());
		d_domain.getStudies().add(study);
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.getSelectedDrugsModel().clear();
		d_pm.getSelectedDrugsModel().addAll(Arrays.asList(new DrugSet[] {
				d_fluoxSet,
				d_paroxSet,
				d_sertrSet}));

		assertTrue(d_pm.getSelectedStudiesModel().contains(study));
		assertNotNull(d_pm.getSelectedArmModel(study, d_fluoxSet));
		assertNotNull(d_pm.getSelectedArmModel(study, d_paroxSet));
		assertNull(d_pm.getSelectedArmModel(study, d_sertrSet));
		
		assertEquals(Collections.singletonList(study.getArms().get(1)), d_pm.getArmsPerStudyPerDrug(study, d_paroxSet));
		assertEquals(Collections.singletonList(study.getArms().get(2)), d_pm.getArmsPerStudyPerDrug(study, d_fluoxSet));
		assertEquals(Collections.emptyList(), d_pm.getArmsPerStudyPerDrug(study, d_sertrSet));
	}
}
