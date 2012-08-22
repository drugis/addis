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

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.Activity;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.OtherActivity;
import org.drugis.addis.entities.PredefinedActivity;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class StudyActivityPresentationTest {
	private ArrayListModel<StudyActivity> d_emptyList;
	private StudyActivity d_activity;
	private Drug d_drug;
	private FixedDose d_dose;
	private DrugTreatment d_treatment;
	private StudyActivity d_treatmentActivity;

	@Before
	public void setUp() {
		d_emptyList = new ArrayListModel<StudyActivity>();
		d_activity = new StudyActivity("Randomization", PredefinedActivity.RANDOMIZATION);
		
		d_drug = new Drug("Fluoxetine", "SomeCode");
		d_dose = new FixedDose(10.0, DoseUnit.createMilliGramsPerDay());
		d_treatment = new DrugTreatment(d_drug, d_dose);
		d_treatmentActivity = new StudyActivity("Treatment", new TreatmentActivity(d_treatment));
	}
	
	@Test
	public void testIsEditing() {
		assertTrue(new StudyActivityPresentation(d_emptyList, null, d_activity).isEditing());
		assertFalse(new StudyActivityPresentation(d_emptyList, null).isEditing());
	}
	
	@Test
	public void testNameModel() {
		StudyActivityPresentation pm1 = new StudyActivityPresentation(d_emptyList, null, d_activity);
		assertEquals(d_activity.getName(), pm1.getNameModel().getValue());
		pm1.getNameModel().setValue("Test");
		assertEquals("Test", pm1.getNameModel().getValue());
		JUnitUtil.assertNotEquals(d_activity.getName(), pm1.getNameModel().getValue());
	}
	
	@Test
	public void testActivityModel() {
		StudyActivityPresentation pm1 = new StudyActivityPresentation(d_emptyList, null, d_activity);
		assertEquals(d_activity.getActivity(), pm1.getActivityModel().getValue());
		pm1.getActivityModel().setValue(PredefinedActivity.FOLLOW_UP);
		assertEquals(PredefinedActivity.FOLLOW_UP, pm1.getActivityModel().getValue());
		JUnitUtil.assertNotEquals(d_activity.getActivity(), pm1.getActivityModel().getValue());
	}
	
	@Test
	public void testTreatmentModel() {
		// Test empty initialization
		StudyActivityPresentation pm1 = new StudyActivityPresentation(d_emptyList, null, d_activity);
		assertEquals(new TreatmentActivity(new DrugTreatment(null, null)), pm1.getTreatmentModel().getBean());

		pm1.getTreatmentModel().getTreatmentModels().get(0).getModel(DrugTreatment.PROPERTY_DRUG).setValue(d_drug);
		assertEquals(new TreatmentActivity(new DrugTreatment(d_drug, null)), pm1.getTreatmentModel().getBean());

		StudyActivity activity = new StudyActivity("Treatment", new TreatmentActivity(d_treatment));
		StudyActivityPresentation pm2 = new StudyActivityPresentation(d_emptyList, null, activity);
		assertEquals(new TreatmentActivity(d_treatment), pm2.getTreatmentModel().getBean());
		
		// Test changes persist and proper cloning
		pm2.getTreatmentModel().getTreatmentModels().get(0).getModel(DrugTreatment.PROPERTY_DRUG).setValue(null);
		assertEquals(new TreatmentActivity(new DrugTreatment(null, d_dose)), pm2.getTreatmentModel().getBean());
		JUnitUtil.assertNotEquals(new DrugTreatment(null, d_dose), d_treatment);
	}
	
	@Test
	public void testActivityOptions() {
		// Test empty initialization
		StudyActivityPresentation pm1 = new StudyActivityPresentation(d_emptyList, null, d_activity);
		List<Activity> list = new ArrayList<Activity>(Arrays.asList(PredefinedActivity.values()));
		list.add(new TreatmentActivity(new DrugTreatment(null, null)));
		list.add(new OtherActivity("Other"));
		assertEquals(list, pm1.getActivityOptions());
		
		// Test initialization with treatment
		StudyActivity activity = new StudyActivity("Treatment", new TreatmentActivity(d_treatment));
		StudyActivityPresentation pm2 = new StudyActivityPresentation(d_emptyList, null, activity);
		list = new ArrayList<Activity>(Arrays.asList(PredefinedActivity.values()));
		list.add(new TreatmentActivity(d_treatment));
		list.add(new OtherActivity("Other"));
		assertEquals(list, pm2.getActivityOptions());

		// Changing the TreatmentActivity in the PM should affect the option list
		pm2.getTreatmentModel().getTreatmentModels().get(0).getBean().setDrug(null);
		d_treatment.setDrug(null);
		assertEquals(list, pm2.getActivityOptions());

		// Test initialization with combination treatment
		TreatmentActivity ct = new TreatmentActivity();
		ct.getTreatments().add(d_treatment);
		ct.getTreatments().add(new DrugTreatment(new Drug("Fluoxeparatinose", "secret"), new FixedDose(12.0, DoseUnit.createMilliGramsPerDay())));
		StudyActivity activity2 = new StudyActivity("Treatment", ct);
		StudyActivityPresentation pm3 = new StudyActivityPresentation(d_emptyList, null, activity2);
		list = new ArrayList<Activity>(Arrays.asList(PredefinedActivity.values()));
		list.add(ct);
		list.add(new OtherActivity("Other"));
		assertEquals(list, pm3.getActivityOptions());

	}
	
	@Test
	public void testNotesModel() {
		StudyActivityPresentation pm1 = new StudyActivityPresentation(d_emptyList, null, d_activity);
		assertEquals(Collections.emptyList(), pm1.getNotesModel());
		d_activity.getNotes().add(new Note());
		assertEquals(Collections.emptyList(), pm1.getNotesModel());
		StudyActivityPresentation pm2 = new StudyActivityPresentation(d_emptyList, null, d_activity);
		assertEquals(Collections.singletonList(new Note()), pm2.getNotesModel());
	}
	
	@Test
	public void testValidModel() {
		assertTrue(new StudyActivityPresentation(d_emptyList, null, d_activity).getValidModel().getValue());
		assertFalse(new StudyActivityPresentation(d_emptyList, null).getValidModel().getValue());
		d_activity.setName("");
		assertFalse(new StudyActivityPresentation(d_emptyList, null, d_activity).getValidModel().getValue());
		d_activity.setName(null);
		assertFalse(new StudyActivityPresentation(d_emptyList, null, d_activity).getValidModel().getValue());
		
		// Test treatmentActivity
		assertTrue(new StudyActivityPresentation(d_emptyList, null, d_treatmentActivity).getValidModel().getValue());
		d_treatment.setDrug(null);
		assertFalse(new StudyActivityPresentation(d_emptyList, null, d_treatmentActivity).getValidModel().getValue());
		
		StudyActivityPresentation pm1 = new StudyActivityPresentation(d_emptyList, null, d_treatmentActivity);
		PropertyChangeListener mockListener = JUnitUtil.mockListener(pm1.getValidModel(), "value", null, true);
		pm1.getValidModel().addValueChangeListener(mockListener);
		pm1.getTreatmentModel().getTreatmentModels().get(0).getBean().setDrug(d_drug);
		verify(mockListener);
		pm1.getValidModel().removeValueChangeListener(mockListener);
		
		mockListener = JUnitUtil.mockListener(pm1.getValidModel(), "value", null, false);
		pm1.getValidModel().addValueChangeListener(mockListener);
		pm1.getNameModel().setValue("");
		verify(mockListener);
	}
	
	@Test
	public void testValidModelNameUnique() {
		ObservableList<StudyActivity> list = new ArrayListModel<StudyActivity>();
		list.add(d_activity);
		StudyActivityPresentation pm1 = new StudyActivityPresentation(list, null);
		pm1.getNameModel().setValue("Test");
		pm1.getActivityModel().setValue(PredefinedActivity.SCREENING);
		assertTrue(pm1.getValidModel().getValue());
		pm1.getNameModel().setValue(d_activity.getName());
		assertFalse(pm1.getValidModel().getValue());
		
		StudyActivityPresentation pm2 = new StudyActivityPresentation(list, null, d_activity);
		assertTrue(pm2.getValidModel().getValue());
	}
	
	@Test
	public void testCommit() {
		ObservableList<StudyActivity> list1 = new ArrayListModel<StudyActivity>();
		StudyActivityPresentation pm1 = new StudyActivityPresentation(list1, null);
		pm1.getNameModel().setValue("Screening");
		pm1.getActivityModel().setValue(PredefinedActivity.SCREENING);
		pm1.commit();
		assertEquals(Collections.singletonList(new StudyActivity("Screening", PredefinedActivity.SCREENING)), list1);
		
		// editing should not produce duplicates
		ObservableList<StudyActivity> list2 = new ArrayListModel<StudyActivity>();
		list2.add(d_activity);
		StudyActivityPresentation pm2 = new StudyActivityPresentation(list2, null, d_activity);
		pm2.commit();
		assertEquals(Collections.singletonList(d_activity), list2);
	}
	
	@Test
	public void testUpdateNameActivityChange() {
		StudyActivityPresentation pm1 = new StudyActivityPresentation(d_emptyList, null);
		PropertyChangeListener mockListener = JUnitUtil.mockListener(pm1.getNameModel(), "value", null, PredefinedActivity.FOLLOW_UP.toString());
		pm1.getNameModel().addValueChangeListener(mockListener);
		pm1.getActivityModel().setValue(PredefinedActivity.FOLLOW_UP);
		assertEquals(PredefinedActivity.FOLLOW_UP.toString(), pm1.getNameModel().getValue());
		verify(mockListener);
	}
	
	@Test
	public void testUpdateNameDrugChange() {
		StudyActivityPresentation pm1 = new StudyActivityPresentation(d_emptyList, null);
		pm1.getActivityModel().setValue(pm1.getTreatmentModel().getBean());
		PropertyChangeListener mockListener = JUnitUtil.mockListener(pm1.getNameModel(), "value", "MISSING", d_drug.toString());
		pm1.getNameModel().addValueChangeListener(mockListener);
		pm1.getTreatmentModel().getTreatmentModels().get(0).getBean().setDrug(d_drug);
		assertEquals(d_drug.toString(), pm1.getNameModel().getValue());
		verify(mockListener);
	}
}
