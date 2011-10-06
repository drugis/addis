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

package org.drugis.addis.entities;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Study.MeasurementKey;
import org.drugis.addis.entities.Study.StudyOutcomeMeasure;
import org.drugis.addis.entities.StudyActivity.UsedBy;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class StudyTest {
	private Study d_orig;
	private Study d_clone;
	private Note d_note;
	private Study d_empty;

	@Before
	public void setUp() {
		d_note = new Note(Source.CLINICALTRIALS, "Original text Yo!");
		d_orig = ExampleData.buildStudyFava2002();
		
		// Add some notes to test them being cloned.
		d_orig.getArms().get(1).getNotes().add(d_note);
		d_orig.getAdverseEvents().get(0).getNotes().add(d_note);
		d_orig.getNotes().add(d_note);
		ObjectWithNotes<Object> val = new ObjectWithNotes<Object>(null);
		val.getNotes().add(d_note);
		d_orig.setCharacteristicWithNotes(BasicStudyCharacteristic.SOURCE,
				val);
		
		d_clone = d_orig.clone();
		d_empty = new Study("empty", ExampleData.buildIndicationDepression());
	}
	
	@Test
	public void testSetId() {
		JUnitUtil.testSetter(new Study("X", new Indication(0L, "")), Study.PROPERTY_NAME, "X", "NCT00351273");
	}
	
	@Test
	public void testInitialArms() {
		Study study = new Study("X", new Indication(0L, ""));
		assertNotNull(study.getArms());
		assertTrue(study.getArms().isEmpty());
	}
	
	@Test
	public void testGetDrugs() {
		Study s = ExampleData.buildStudyDeWilde();
		Set<DrugSet> expected = new HashSet<DrugSet>();
		expected.add(new DrugSet(ExampleData.buildDrugFluoxetine()));
		expected.add(new DrugSet(ExampleData.buildDrugParoxetine()));
		assertEquals(expected, s.getDrugs());
	}
	
	@Test
	public void testToString() {
		String id = "NCT00351273";
		Study study = new Study(id, new Indication(0L, ""));
		assertEquals(id, study.toString());
	}
	
	@Test
	public void testSetStudyActivityAt() throws DatatypeConfigurationException {
		Arm arm1 = new Arm("testArm1", 100);
		Arm arm2 = new Arm("testArm2", 200);
		Epoch epoch1 = new Epoch("testEpoch1", DatatypeFactory.newInstance().newDuration(10000));
		StudyActivity randomization = new StudyActivity("Randomization", PredefinedActivity.RANDOMIZATION);
		d_empty.getEpochs().add(epoch1);
		d_empty.getArms().add(arm1);
		d_empty.getStudyActivities().add(randomization);
		d_empty.setStudyActivityAt(arm1, epoch1, randomization);
		Set<UsedBy> usedByRandomization = new HashSet<UsedBy>();
		UsedBy usedByarm1epoch1 = new UsedBy(arm1, epoch1);
		usedByRandomization.add(usedByarm1epoch1);
		assertEquals(usedByRandomization, randomization.getUsedBy());

		// adding again should not change anything
		d_empty.setStudyActivityAt(arm1, epoch1, randomization);
		assertEquals(usedByRandomization, randomization.getUsedBy());

		// adding new UsedBy should change UsedBy
		d_empty.getArms().add(arm2);
		d_empty.setStudyActivityAt(arm2, epoch1, randomization);
		UsedBy usedByarm2epoch1 = new UsedBy(arm2, epoch1);
		usedByRandomization.add(usedByarm2epoch1);
		assertEquals(usedByRandomization, randomization.getUsedBy());
		
		// adding new activity for an (arm, epoch) pair should remove any other activity at those coordinates
		StudyActivity screening = new StudyActivity("Screening", PredefinedActivity.SCREENING);
		d_empty.getStudyActivities().add(screening);
		d_empty.setStudyActivityAt(arm1, epoch1, screening);		
		Set<UsedBy> usedByScreening= new HashSet<UsedBy>();
		usedByScreening.add(usedByarm1epoch1);
		usedByRandomization.remove(usedByarm1epoch1);
		assertEquals(usedByScreening, screening.getUsedBy());
		assertEquals(usedByRandomization, randomization.getUsedBy());
		
		// adding <null> activity should clear item
		d_empty.setStudyActivityAt(arm2, epoch1, null);
		assertEquals(Collections.emptySet(), randomization.getUsedBy());
	}
	
	@Test
	public void testGetStudyActivityAt() throws DatatypeConfigurationException {
		Arm arm1 = new Arm("testArm1", 100);
		Arm arm2 = new Arm("testArm2", 200);
		Arm arm3 = new Arm("testArm3", 300);
		Epoch epoch1 = new Epoch("Trias", DatatypeFactory.newInstance().newDuration(10000));
		Epoch epoch2 = new Epoch("Jura", DatatypeFactory.newInstance().newDuration(10000));
		StudyActivity randomization = new StudyActivity("Randomization", PredefinedActivity.RANDOMIZATION);
		StudyActivity skriening = new StudyActivity("Screening", PredefinedActivity.SCREENING);
		d_empty.getEpochs().add(epoch1);
		d_empty.getEpochs().add(epoch2);
		d_empty.getArms().add(arm1);
		d_empty.getArms().add(arm2);
		d_empty.getStudyActivities().add(randomization);
		d_empty.getStudyActivities().add(skriening);
		d_empty.setStudyActivityAt(arm1, epoch1, randomization);
		d_empty.setStudyActivityAt(arm2, epoch1, randomization);
		d_empty.setStudyActivityAt(arm2, epoch1, skriening);
		d_empty.setStudyActivityAt(arm1, epoch2, skriening);
		
		assertEquals(randomization, d_empty.getStudyActivityAt(arm1, epoch1));
		assertEquals(skriening, d_empty.getStudyActivityAt(arm2, epoch1));
		assertEquals(skriening, d_empty.getStudyActivityAt(arm1, epoch2));
		assertEquals(null, d_empty.getStudyActivityAt(arm3, epoch1));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testArmNotExistsException() throws DatatypeConfigurationException {
		Arm arm1 = new Arm("testArm1", 100);
		Epoch epoch1 = new Epoch("testEpoch1", DatatypeFactory.newInstance().newDuration(10000));
		StudyActivity randomization = new StudyActivity("Randomization", PredefinedActivity.RANDOMIZATION);
		d_empty.getEpochs().add(epoch1);
		d_empty.getStudyActivities().add(randomization);
		d_empty.setStudyActivityAt(arm1, epoch1, randomization);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testEpochNotExistsException() throws DatatypeConfigurationException {
		Arm arm1 = new Arm("testArm1", 100);
		Epoch epoch1 = new Epoch("testEpoch1", DatatypeFactory.newInstance().newDuration(10000));
		StudyActivity randomization = new StudyActivity("Randomization", PredefinedActivity.RANDOMIZATION);
		d_empty.getArms().add(arm1);
		d_empty.getStudyActivities().add(randomization);
		d_empty.setStudyActivityAt(arm1, epoch1, randomization);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testStudyActivityNotExistsException() throws DatatypeConfigurationException {
		Arm arm1 = new Arm("testArm1", 100);
		Epoch epoch1 = new Epoch("testEpoch1", DatatypeFactory.newInstance().newDuration(10000));
		StudyActivity randomization = new StudyActivity("Randomization", PredefinedActivity.RANDOMIZATION);
		d_empty.getArms().add(arm1);
		d_empty.getEpochs().add(epoch1);
		d_empty.setStudyActivityAt(arm1, epoch1, randomization);
	}

	@Test
	public void testSetMeasurement() {
		Study study = new Study("X", new Indication(0L, ""));
		ExampleData.addDefaultEpochs(study);
		Endpoint endpoint = new Endpoint("e", Endpoint.convertVarType(Variable.Type.RATE));
		study.getEndpoints().add(new StudyOutcomeMeasure<Endpoint>(endpoint));
		Arm group = study.createAndAddArm("", 100, null, null);
		BasicRateMeasurement m = new BasicRateMeasurement(0, group.getSize());
		m.setRate(12);
		study.setMeasurement(study.getOutcomeMeasures().iterator().next(), study.getArms().get(0), m);
		
		assertEquals(m, study.getMeasurement(study.getOutcomeMeasures().iterator().next(), study.getArms().get(0)));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetMeasurementThrowsException1() {
		Study study = new Study("X", new Indication(0L, ""));
		Endpoint e = new Endpoint("E", Endpoint.convertVarType(Variable.Type.RATE));
		Arm pg = new Arm("", 100);
		study.setMeasurement(e, pg, 
				new BasicRateMeasurement(100, pg.getSize()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetMeasurementThrowsException2() {
		Study study = new Study("X", new Indication(0L, ""));
		ExampleData.addDefaultEpochs(study);
		Endpoint e = new Endpoint("e", Endpoint.convertVarType(Variable.Type.RATE));
		study.getEndpoints().add(new StudyOutcomeMeasure<Endpoint>(e));
		Arm group = study.createAndAddArm("", 100, null, null);
		
		BasicMeasurement m = new BasicRateMeasurement(12, group.getSize());
		
		study.getOutcomeMeasures().iterator().next().setVariableType(new ContinuousVariableType());
		study.setMeasurement(study.getOutcomeMeasures().iterator().next(), study.getArms().get(0), m);
	}
	
	
	@Test
	public void testEquals() {
		String name1 = "Study A";
		String name2 = "Study B";
		Indication i = new Indication(0L, "");
		
		assertEquals(new Study(name1, i), new Study(name1, i));
		JUnitUtil.assertNotEquals(new Study(name1, i), new Study(name2, i));
		assertEquals(new Study(name1, i).hashCode(), new Study(name1, i).hashCode());
	}
	
	@Test
	public void testDeepEquals() {
		// Test ID
		Study study1 = new Study("Title", ExampleData.buildIndicationDepression());
		Study study2 = new Study("Other Title", ExampleData.buildIndicationDepression());
		assertFalse(study1.deepEquals(study2));
		study2.setName("Title");
		assertTrue(study1.deepEquals(study2));
		
		// indication
		study2.setIndication(ExampleData.buildIndicationChronicHeartFailure());
		assertFalse(study1.deepEquals(study2));
		study2.setIndication(ExampleData.buildIndicationDepression());
		
		// characteristics
		study2.setCharacteristic(BasicStudyCharacteristic.TITLE, "This is terrible");
		assertFalse(study1.deepEquals(study2));
		study1.setCharacteristic(BasicStudyCharacteristic.TITLE, "This is terrible");
		assertTrue(study1.deepEquals(study2));
		study2.getNotes().add(new Note(Source.CLINICALTRIALS, "Official title"));
		assertFalse(study1.deepEquals(study2));
		study2.getNotes().clear();
		
		// endpoints
		study2.getEndpoints().add(new StudyOutcomeMeasure<Endpoint>(ExampleData.buildEndpointCgi()));
		assertFalse(study1.deepEquals(study2));
		study1.getEndpoints().add(new StudyOutcomeMeasure<Endpoint>(ExampleData.buildEndpointCgi()));
		assertTrue(study1.deepEquals(study2));
		// Here we might test if the equality is based on .equals or .deepEquals of Endpoint
		
		// adverseEvents
		study2.getAdverseEvents().add(new StudyOutcomeMeasure<AdverseEvent>(ExampleData.buildAdverseEventConvulsion()));
		assertFalse(study1.deepEquals(study2));
		study1.getAdverseEvents().add(new StudyOutcomeMeasure<AdverseEvent>(ExampleData.buildAdverseEventConvulsion()));
		assertTrue(study1.deepEquals(study2));
		// Here we might test if the equality is based on .equals or .deepEquals of AdverseEvent
		
		// populationCharacteristics
		study2.getPopulationChars().add(new StudyOutcomeMeasure<PopulationCharacteristic>(ExampleData.buildAgeVariable()));
		assertFalse(study1.deepEquals(study2));
		study1.getPopulationChars().add(new StudyOutcomeMeasure<PopulationCharacteristic>(ExampleData.buildAgeVariable()));
		assertTrue(study1.deepEquals(study2));
		study2.getPopulationChars().add(new StudyOutcomeMeasure<PopulationCharacteristic>(ExampleData.buildGenderVariable()));
		// Here we DO test if the equality is based on .equals or .deepEquals of PopulationCharacteristic
		PopulationCharacteristic pc = new PopulationCharacteristic(ExampleData.buildGenderVariable().getName(), new CategoricalVariableType(Arrays.asList((new String[] { "Mars", "Venus" }))));
		study1.getPopulationChars().add(new StudyOutcomeMeasure<PopulationCharacteristic>(pc));
		assertFalse(study1.deepEquals(study2));
		study1.getPopulationChars().clear();
		study1.getPopulationChars().addAll(Study.wrapVariables(Collections.<PopulationCharacteristic>emptyList()));
		study2.getPopulationChars().clear();
		study2.getPopulationChars().addAll(Study.wrapVariables(Collections.<PopulationCharacteristic>emptyList()));
		assertTrue(study1.deepEquals(study2));
		
		Arm arm = new Arm("Arm1", 9001);
		study2.getArms().add(arm);
		assertFalse(study1.deepEquals(study2));
		study1.getArms().add(arm);
		assertTrue(study1.deepEquals(study2));
		
		study1.getEpochs().add(new Epoch("Epoch1", null));
		assertFalse(study1.deepEquals(study2));
		study2.getEpochs().add(new Epoch("Epoch1", null));
		assertTrue(study1.deepEquals(study2));
		
		StudyActivity randomization1 = new StudyActivity("Dancing", PredefinedActivity.RANDOMIZATION);
		StudyActivity randomization2 = new StudyActivity("Dancing", PredefinedActivity.RANDOMIZATION);
		study1.getStudyActivities().add(randomization1);
		assertFalse(study1.deepEquals(study2));
		study2.getStudyActivities().add(randomization2);
		assertTrue(study1.deepEquals(study2));

		study1.setStudyActivityAt(arm, new Epoch("Epoch1", null), randomization1);
		assertFalse(study1.deepEquals(study2));
		study2.setStudyActivityAt(arm, new Epoch("Epoch1", null), randomization1);
		assertTrue(study1.deepEquals(study2));
		
		study1.setMeasurement(ExampleData.buildAdverseEventConvulsion(), arm, new BasicRateMeasurement(50, 100));
		assertFalse(study1.deepEquals(study2));
		study2.setMeasurement(ExampleData.buildAdverseEventConvulsion(), arm, new BasicRateMeasurement(50, 100));
		assertTrue(study1.deepEquals(study2));
		
		study1.getNotes().add(new Note(Source.MANUAL, "testnote"));
		assertFalse(study1.deepEquals(study2));
		study2.getNotes().add(new Note(Source.MANUAL, "testnote"));
		assertTrue(study1.deepEquals(study2));
	}
	
	@Test
	public void testGetDependencies() {
		Study s = ExampleData.buildStudyDeWilde();
		assertFalse(s.getOutcomeMeasures().isEmpty());
		assertFalse(s.getDrugs().isEmpty());
		
		Set<Entity> dep = new HashSet<Entity>(s.getOutcomeMeasures());
		for (DrugSet d : s.getDrugs()) {
			dep.addAll(d.getContents());
		}
		for (StudyActivity sa: s.getStudyActivities()) {
			if (sa.getActivity() instanceof TreatmentActivity) {
				TreatmentActivity ta = (TreatmentActivity) sa.getActivity();
				for (AbstractDose d: ta.getDoses()) {
					dep.add(d.getDoseUnit().getUnit());
				}
			}
		}
		dep.add(s.getIndication());
		assertEquals(dep, s.getDependencies());
	}	
	
	@Test
	public void testSetCharacteristic() {
		Study study = new Study("X", new Indication(0L, ""));
		
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(study.getCharacteristics(), 
				MapBean.PROPERTY_CONTENTS,null, null);		
		study.getCharacteristics().addPropertyChangeListener(listener);

		study.setCharacteristic(BasicStudyCharacteristic.CENTERS, new Integer(2));
		verify(listener);
	}
	
	@Test
	public void testSetCharacteristicKeepsNotes() {
		Study study = new Study("X", new Indication(0L, ""));
		study.setCharacteristic(BasicStudyCharacteristic.TITLE, null);
		Note note = new Note(Source.MANUAL, "My text");
		study.getCharacteristicWithNotes(BasicStudyCharacteristic.TITLE).getNotes().add(note);
		study.setCharacteristic(BasicStudyCharacteristic.TITLE, "My title");
		assertEquals(Collections.singletonList(note), study.getCharacteristicWithNotes(BasicStudyCharacteristic.TITLE).getNotes());
	}
	
	@Test
	public void testGetSampleSize() {
		Study s = new Study("s1", new Indication(01L, "i"));
		ExampleData.addDefaultEpochs(s);
		s.createAndAddArm("pg1", 25, null, null);
		s.createAndAddArm("pg2", 35, null, null);
		assertEquals(60, s.getSampleSize());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetPopulationCharNotPresent() {
		Variable v = new PopulationCharacteristic("Age", new ContinuousVariableType());
		Study s = new Study("X", new Indication(0L, "Y"));
		s.setMeasurement(v, new BasicContinuousMeasurement(0.0, 1.0, 5));
	}
	
	@Test
	public void testSetPopulationChar() {
		PopulationCharacteristic v = new PopulationCharacteristic("Age", new ContinuousVariableType());
		Study s = new Study("X", new Indication(0L, "Y"));
		ExampleData.addDefaultEpochs(s);
		s.createAndAddArm("X", 200, new Drug("X", "ATC3"), new FixedDose(5, ExampleData.MILLIGRAMS_A_DAY));
		s.getPopulationChars().clear();
		s.getPopulationChars().addAll(Study.wrapVariables(Collections.singletonList(v)));
		BasicContinuousMeasurement m = new BasicContinuousMeasurement(0.0, 1.0, 5);
		
		s.setMeasurement(v, m);
		assertEquals(m, s.getMeasurement(v));
		
		s.setMeasurement(v, s.getArms().get(0), m);
		assertEquals(m, s.getMeasurement(v, s.getArms().get(0)));
	}
	
	@Test
	public void testChangePopulationCharRetainMeasurements() {
		Study s = new Study("X", new Indication(0L, "Y"));
		ExampleData.addDefaultEpochs(s);
		Arm arm1 = s.createAndAddArm("X", 200, new Drug("X", "ATC3"), new FixedDose(5, ExampleData.MILLIGRAMS_A_DAY));
		
		PopulationCharacteristic v1 = new PopulationCharacteristic("Age1", new ContinuousVariableType());
		PopulationCharacteristic v2 = new PopulationCharacteristic("Age2", new ContinuousVariableType());
		PopulationCharacteristic v3 = new PopulationCharacteristic("Age3", new ContinuousVariableType());
		
		ArrayList<PopulationCharacteristic> vars1 = new ArrayList<PopulationCharacteristic>();
		vars1.add(v1);
		vars1.add(v2);
		s.getPopulationChars().addAll(Study.wrapVariables(vars1));
		
		BasicMeasurement m10 = new BasicContinuousMeasurement(3.0, 2.0, 150);
		BasicMeasurement m11 = new BasicContinuousMeasurement(3.0, 2.0, 150);
		BasicMeasurement m20 = new BasicContinuousMeasurement(3.0, 2.0, 150);
		BasicMeasurement m21 = new BasicContinuousMeasurement(3.0, 2.0, 150);
		s.setMeasurement(v1, m10);
		s.setMeasurement(v1, arm1, m11);
		s.setMeasurement(v2, m20);
		s.setMeasurement(v2, arm1, m21);

		s.getPopulationChars().remove(new StudyOutcomeMeasure<PopulationCharacteristic>(v1));
		s.getPopulationChars().add(new StudyOutcomeMeasure<PopulationCharacteristic>(v3));
		
		assertEquals(m20, s.getMeasurement(v2));
		assertEquals(m21, s.getMeasurement(v2, arm1));
		
		assertNull(s.getMeasurement(v1));
		assertNull(s.getMeasurement(v1, arm1));
	}

	@Test
	public void testCloneReturnsEqualEntity() {
		assertEquals(d_orig, d_clone);
		AssertEntityEquals.assertEntityEquals(d_orig, d_clone);
	}
	
	@Test
	public void testCloneReturnsDistinctObject() {
		assertFalse(d_orig == d_clone);
	}
	
	@Test
	public void testCloneReturnsDistinctArms() {
		assertFalse(d_orig.getArms() == d_clone.getArms());
		for (int i = 0; i < d_orig.getArms().size(); ++i) {
			assertFalse(d_orig.getArms().get(i) == d_clone.getArms().get(i));
		}
	}
	
	@Test
	public void testCloneReturnsDistinctVariableLists() {
		assertFalse(Study.extractVariables(d_orig.getEndpoints()) == Study.extractVariables(d_clone.getEndpoints()));
		assertFalse(Study.extractVariables(d_orig.getAdverseEvents()) == Study.extractVariables(d_clone.getAdverseEvents()));
		assertFalse(Study.extractVariables(d_orig.getPopulationChars()) == Study.extractVariables(d_clone.getPopulationChars()));
	}
	
	@Test
	public void testCloneReturnsDistinctMeasurements() {
		assertFalse(d_orig.getMeasurements() == d_clone.getMeasurements());
		for (MeasurementKey key : d_orig.getMeasurements().keySet()) {
			assertNotSame(d_orig.getMeasurements().get(key), d_clone.getMeasurements().get(key));
		}
	}
	
	@Test
	public void testCloneReturnsDistinctOutcomeMeasures() {
		assertFalse(d_orig.getEndpoints() == d_clone.getEndpoints());
		assertFalse(d_orig.getAdverseEvents() == d_clone.getAdverseEvents());
		assertFalse(d_orig.getPopulationChars() == d_clone.getPopulationChars());
	}

	@Test
	public void testCloneHasCorrectMeasurementKeys() {
		Arm arm = d_clone.getArms().get(1);
		d_clone.getTreatment(arm).getTreatments().get(0).setDrug(ExampleData.buildDrugViagra());
		assertEquals(d_orig.getMeasurement(Study.extractVariables(d_orig.getEndpoints()).get(0), d_orig.getArms().get(1)),
				d_clone.getMeasurement(Study.extractVariables(d_clone.getEndpoints()).get(0), arm));
	}
	
	@Test
	public void testClonedUsedBysReferToClonedArmAndEpoch() {
		// Check that we're still testing what we think we're testing
		StudyActivity orig_sa = d_orig.getStudyActivities().get(0);
		assertEquals("Sertraline-0 treatment", orig_sa.getName());
		assertEquals(1, orig_sa.getUsedBy().size());
		
		StudyActivity clone_sa = d_clone.getStudyActivities().get(0);
		assertEquals("Sertraline-0 treatment", clone_sa.getName());
		assertEquals(1, clone_sa.getUsedBy().size());

		UsedBy orig_ub = orig_sa.getUsedBy().iterator().next();
		UsedBy clone_ub = clone_sa.getUsedBy().iterator().next();
		
		// The actual test
		assertSame(d_orig.getArms().get(0), orig_ub.getArm());
		assertEquals(d_orig.getArms().get(0), clone_ub.getArm());
		assertNotSame(d_orig.getArms().get(0), clone_ub.getArm());
		assertSame(d_clone.getArms().get(0), clone_ub.getArm());
		
		assertSame(d_orig.getEpochs().get(1), orig_ub.getEpoch());
		assertEquals(d_orig.getEpochs().get(1), clone_ub.getEpoch());
		assertNotSame(d_orig.getEpochs().get(1), clone_ub.getEpoch());
		assertSame(d_clone.getEpochs().get(1), clone_ub.getEpoch());
	}
	
	@Test
	public void testClonedMeasurementKeysReferences() {
		assertEquals(d_orig.getMeasurements(), d_clone.getMeasurements());
		MeasurementKey origKey = d_orig.getMeasurements().keySet().iterator().next();
		MeasurementKey cloneKey = d_clone.getMeasurements().keySet().iterator().next();
		assertNotSame(origKey, cloneKey);
		assertNotSame(origKey.getArm(), cloneKey.getArm());
		assertNotSame(origKey.getWhenTaken(), cloneKey.getWhenTaken());
		assertNotSame(origKey.getWhenTaken().getEpoch(), cloneKey.getWhenTaken().getEpoch());
	}
	
	@Test
	public void testCloneHasDistinctCharacteristics() {
		assertFalse(d_orig.getCharacteristics() == d_clone.getCharacteristics());
	}
	
	@Test
	public void testCloneHasDistinctNotes() {
		Note note = new Note(Source.MANUAL);
		
		assertTrue(d_clone.getEndpoints().get(0).getNotes().isEmpty());
		d_clone.getEndpoints().get(0).getNotes().add(note);
		assertTrue(d_orig.getEndpoints().get(0).getNotes().isEmpty());
		
		assertTrue(d_clone.getIndicationWithNotes().getNotes().isEmpty());
		d_clone.getIndicationWithNotes().getNotes().add(note);
		assertTrue(d_orig.getIndicationWithNotes().getNotes().isEmpty());
		
		assertTrue(d_clone.getCharacteristicWithNotes(BasicStudyCharacteristic.BLINDING).getNotes().isEmpty());
		d_clone.getCharacteristicWithNotes(BasicStudyCharacteristic.BLINDING).getNotes().add(note);
		assertTrue(d_orig.getCharacteristicWithNotes(BasicStudyCharacteristic.BLINDING).getNotes().isEmpty());
		
		assertTrue(d_clone.getArms().get(0).getNotes().isEmpty());
		d_clone.getArms().get(0).getNotes().add(note);
		assertTrue(d_orig.getArms().get(0).getNotes().isEmpty());
	}
	
	@Test
	public void testMeasuredDrugs() {
		assertEquals(d_clone.getDrugs(), d_clone.getMeasuredDrugs(ExampleData.buildEndpointHamd()));
		assertEquals(Collections.emptySet(), d_clone.getMeasuredDrugs(ExampleData.buildAdverseEventConvulsion()));
		BasicRateMeasurement m = new BasicRateMeasurement(null, 100);
		d_clone.setMeasurement(ExampleData.buildAdverseEventConvulsion(), d_clone.getArms().get(0), m);
		assertEquals(Collections.emptySet(), d_clone.getMeasuredDrugs(ExampleData.buildAdverseEventConvulsion()));
		m.setRate(20);
		DrugSet d = d_clone.getDrugs(d_clone.getArms().get(0));
		assertEquals(Collections.singleton(d), d_clone.getMeasuredDrugs(ExampleData.buildAdverseEventConvulsion()));
	}
	
	@Test
	public void testMeasuredArms() {
		Arm a1 = d_clone.getArms().get(0);
		DrugSet d1 = d_clone.getDrugs(a1);
		assertEquals(Collections.singletonList(a1), d_clone.getMeasuredArms(ExampleData.buildEndpointHamd(), d1));
		Arm a2 = d_clone.getArms().get(1);
		DrugSet d2 = d_clone.getDrugs(a2);
		assertEquals(Collections.singletonList(a2), d_clone.getMeasuredArms(ExampleData.buildEndpointHamd(), d2));
	
		assertEquals(Collections.emptyList(), d_clone.getMeasuredArms(ExampleData.buildAdverseEventConvulsion(), d1));
		
		assertEquals(1, d1.getContents().size()); 		// Sanity check
		d_clone.createAndAddArm("Bla", 100, d1.getContents().first(), new FixedDose());
		assertEquals(Collections.singletonList(a1), d_clone.getMeasuredArms(ExampleData.buildEndpointHamd(), d1));

	}
}
