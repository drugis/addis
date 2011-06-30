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

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyListPresentation;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

public class NetworkMetaAnalysisWizardPMTest {

	private Domain d_domain;
	private NetworkMetaAnalysisWizardPM d_pm;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pm = new NetworkMetaAnalysisWizardPM(d_domain, new PresentationModelFactory(d_domain));
	}
	
	@Test
	public void testDrugsSelectedCompleteListener() {
		ValueModel completeModel = d_pm.getConnectedDrugsSelectedModel();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		assertTrue((Boolean)completeModel.getValue());
		
		ArrayList<Drug> newList = new ArrayList<Drug>();
		newList.add(ExampleData.buildDrugSertraline());
		d_pm.getSelectedDrugsModel().setValue(newList);
		assertFalse((Boolean)completeModel.getValue());
		
		newList = new ArrayList<Drug>(newList);
		newList.add(ExampleData.buildDrugParoxetine());
		d_pm.getSelectedDrugsModel().setValue(newList);
		assertFalse((Boolean)completeModel.getValue());
		
		newList = new ArrayList<Drug>(newList);		
		newList.add(ExampleData.buildDrugFluoxetine());
		d_pm.getSelectedDrugsModel().setValue(newList);
		assertTrue((Boolean)completeModel.getValue());		
	}
	
	@Test
	public void testStudyListModel() {
		SelectableStudyListPresentation listModel = d_pm.getStudyListModel();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		
		ArrayList<Study> newList = new ArrayList<Study>();
		newList.addAll(d_pm.getStudiesEndpointAndIndication());
		assertEquals(newList, listModel.getIncludedStudies().getValue());

		ArrayList<Drug> selectionList = new ArrayList<Drug>();
		selectionList.add(ExampleData.buildDrugSertraline());
		selectionList.add(ExampleData.buildDrugParoxetine());
		
		ArrayList<Study> expected = new ArrayList<Study>();
		PropertyChangeListener mock = JUnitUtil.mockListener(listModel.getIncludedStudies(), "value", newList, expected);
		listModel.getIncludedStudies().addValueChangeListener(mock);
		d_pm.getSelectedDrugsModel().setValue(selectionList);
		
		verify(mock);
	}
	
	@Test
	public void testStudyListModelAdds() {
		SelectableStudyListPresentation listModel = d_pm.getStudyListModel();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		ArrayList<Study> allStudiesList = new ArrayList<Study>(d_pm.getStudiesEndpointAndIndication());
		
		ArrayList<Drug> selectionList = new ArrayList<Drug>();
		selectionList.add(ExampleData.buildDrugSertraline());
		selectionList.add(ExampleData.buildDrugParoxetine());
		
		d_pm.getSelectedDrugsModel().setValue(new ArrayList<Drug>(selectionList));
		
		PropertyChangeListener mock = JUnitUtil.mockListener(listModel.getIncludedStudies(), 
				"value", new ArrayList<Study>(), allStudiesList);
		listModel.getIncludedStudies().addValueChangeListener(mock);

		selectionList.add(ExampleData.buildDrugFluoxetine());	
		
		d_pm.getSelectedDrugsModel().setValue(selectionList);		
		
		verify(mock);
	}
	
	@Test
	public void testGetSelectedStudyGraphUpdateDrugs() {
		StudyGraphModel graphModel = d_pm.getSelectedStudyGraphModel();

		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		assertEquals(3, graphModel.vertexSet().size());
		assertEquals(2, graphModel.edgeSet().size());
		
		ArrayList<Drug> selectionList = new ArrayList<Drug>();
		selectionList.add(ExampleData.buildDrugSertraline());
		selectionList.add(ExampleData.buildDrugParoxetine());
		d_pm.getSelectedDrugsModel().setValue(selectionList);

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

		assertEquals(2, graphModel.vertexSet().size());
		assertEquals(1, graphModel.edgeSet().size());
		
		addCitalopram();
		graphModel = d_pm.getSelectedStudyGraphModel();

		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointCgi());
		
		assertEquals(3, graphModel.vertexSet().size());
		assertEquals(2, graphModel.edgeSet().size());
	}

	private void addCitalopram() {
		Arm arm = ExampleData.buildStudyBennie().createAndAddArm("Citalopram-2", 100, 
				ExampleData.buildDrugCitalopram(), new FixedDose(12, SIUnit.MILLIGRAMS_A_DAY));
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
		
		Drug[] expected = new Drug[] {
				ExampleData.buildDrugCitalopram(),
				ExampleData.buildDrugFluoxetine(),
				ExampleData.buildDrugParoxetine()
			};
		
		assertEquals(Arrays.asList(expected), d_pm.getDrugListModel().getValue());
	}
	
	@Test
	public void testGetSelectedStudyGraphUpdateStudies() {
		StudyGraphModel graphModel = d_pm.getSelectedStudyGraphModel();

		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		
		// Remove Parox studies
		ArrayList<Study> studyList = new ArrayList<Study>();
		studyList.add(ExampleData.buildStudyBennie());
		d_pm.getStudyListModel().getSelectedStudyBooleanModel(
				ExampleData.buildStudyMultipleArmsperDrug()).setValue(false);
		d_pm.getStudyListModel().getSelectedStudyBooleanModel(
				ExampleData.buildStudyDeWilde()).setValue(false);
		d_pm.getStudyListModel().getSelectedStudyBooleanModel(
				ExampleData.buildStudyChouinard()).setValue(false);
		
		assertEquals(3, graphModel.vertexSet().size());
		assertEquals(1, graphModel.edgeSet().size());
	}
	
	@Test
	public void testStudySelectionCompleteModel() {
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());

		ValueHolder<Boolean> completeModel = d_pm.getStudySelectionCompleteModel();
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
		
		verify(mock);
		assertFalse(completeModel.getValue());
	}

	
	@Test
	public void testCreateMetaAnalysis() {
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getOutcomeMeasureModel().setValue(ExampleData.buildEndpointHamd());
		d_pm.getSelectedDrugsModel().setValue(Arrays.asList(new Drug[] {
				ExampleData.buildDrugFluoxetine(),
				ExampleData.buildDrugParoxetine(),
				ExampleData.buildDrugSertraline()}));
		
		Study multiple = ExampleData.buildStudyMultipleArmsperDrug();
		List<Arm> arms = new ArrayList<Arm>(multiple.getArms());
		arms.remove(d_pm.getSelectedArmModel(multiple, ExampleData.buildDrugParoxetine()).getValue());
		arms.remove(d_pm.getSelectedArmModel(multiple, ExampleData.buildDrugFluoxetine()).getValue());
		Arm arm = arms.get(0); // The currently unused arm 
		d_pm.getSelectedArmModel(multiple, ExampleData.buildDrugParoxetine()).setValue(arm);
		
		NetworkMetaAnalysis ma = d_pm.createMetaAnalysis("name");
		
		assertEquals(d_pm.getSelectedDrugsModel().getValue(), ma.getIncludedDrugs());
		JUnitUtil.assertAllAndOnly(ma.getIncludedStudies(),
				d_pm.getStudyListModel().getSelectedStudiesModel().getValue());
		assertEquals(d_pm.getOutcomeMeasureModel().getValue(), ma.getOutcomeMeasure());
		assertEquals(d_pm.getIndicationModel().getValue(), ma.getIndication());
		assertEquals(arm, ma.getArm(multiple, ExampleData.buildDrugParoxetine()));
		for (Study s : ma.getIncludedStudies()) {
			for (Drug d : s.getDrugs()) {
				assertNotNull(ma.getArm(s, d));
			}
		}
	}	
}
