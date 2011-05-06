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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.drugis.addis.entities.StudyActivity.UsedBy;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class StudyActivityTest {
	private StudyActivity d_undefined;
	private StudyActivity d_randomization;
	private String d_rndTitle;
	private PredefinedActivity d_rndActivity;
	private Arm d_arm;
	private Epoch d_epoch;
	private StudyActivity d_main;
	private Drug d_fluoxetine;
	
	@Before
	public void setUp() throws DatatypeConfigurationException {
		d_undefined = new StudyActivity(null, null);
		d_rndTitle = "Randomization";
		d_rndActivity = PredefinedActivity.RANDOMIZATION;
		d_randomization = new StudyActivity(d_rndTitle, d_rndActivity);
		d_epoch = new Epoch("Main phase", DatatypeFactory.newInstance().newDuration("PT5H"));
		d_arm = new Arm("Group", 12);
		d_fluoxetine = new Drug("Fluoxetine", null);
		Activity treatment = new TreatmentActivity(d_fluoxetine, new FixedDose(10.0, SIUnit.MILLIGRAMS_A_DAY));
		d_main = new StudyActivity("treatment", treatment);
	}

	@Test
	public void testConstruction() {
		assertEquals(null, d_undefined.getName());
		assertEquals(null, d_undefined.getActivity());
		assertEquals(d_rndTitle, d_randomization.getName());
		assertEquals(d_rndActivity, d_randomization.getActivity());
		assertEquals(Collections.emptyList(), d_randomization.getNotes());
		assertEquals(Collections.emptySet(), d_randomization.getUsedBy());
	}
	
	@Test
	public void testSetName() {
		JUnitUtil.testSetter(d_undefined, StudyActivity.PROPERTY_NAME, null, "Screening");
	}
	
	@Test
	public void testSetActivity() {
		JUnitUtil.testSetter(d_randomization, StudyActivity.PROPERTY_ACTIVITY, d_rndActivity, PredefinedActivity.SCREENING);
	}
	
	@Test
	public void testUsedByEquals() throws DatatypeConfigurationException {
		Epoch e = new Epoch("Randomization",DatatypeFactory.newInstance().newDuration("PT2H"));
		UsedBy ub = new UsedBy(d_arm, d_epoch);
		UsedBy ub2 = new UsedBy(d_arm, d_epoch);
		UsedBy ub3 = new UsedBy(d_arm, e);
		UsedBy ub4 = new UsedBy(new Arm("New Group", 8), e);
		assertEquals(ub, ub2);
		JUnitUtil.assertNotEquals(ub, ub3);
		JUnitUtil.assertNotEquals(ub3, ub4);
		assertEquals(ub.hashCode(), ub2.hashCode());
	}
	
	@Test
	public void testEquals() {
		// equality is defined on the NAME field.
		JUnitUtil.assertNotEquals(d_undefined, d_randomization);
		d_undefined.setName(d_rndTitle);
		assertEquals(d_undefined, d_randomization);
		assertEquals(d_undefined.hashCode(), d_randomization.hashCode());
		
		// deep equality is defined by equality of the object graph
		assertTrue(d_randomization.deepEquals(d_randomization));
		assertFalse(d_undefined.deepEquals(d_randomization));
		d_undefined.setActivity(d_rndActivity);
		assertTrue(d_randomization.deepEquals(d_randomization));
		d_undefined.setUsedBy(Collections.singleton(new UsedBy(d_arm, d_epoch)));
		assertFalse(d_undefined.deepEquals(d_randomization));
		d_undefined.setUsedBy(Collections.<UsedBy>emptySet());
		d_undefined.getNotes().add(new Note());
		assertFalse(d_undefined.deepEquals(d_randomization));
	}
	 
	@Test
	public void testDependencies() {
		assertEquals(Collections.emptySet(), d_randomization.getDependencies());
		assertEquals(Collections.singleton(d_fluoxetine), d_main.getDependencies());
	}
	
	@Test
	public void testSetUsedBy() {
		UsedBy ub = new UsedBy(d_arm, d_epoch);
		JUnitUtil.testSetter(d_randomization, StudyActivity.PROPERTY_USED_BY, Collections.emptySet(), 
				Collections.singleton(ub));
	}
	
	@Test
	public void testNotes() {
		assertEquals(Collections.emptyList(), d_randomization.getNotes());
		Note n = new Note(Source.MANUAL, "Zis is a note");
		d_randomization.getNotes().add(n);
		assertEquals(Collections.singletonList(n), d_randomization.getNotes());
	}
}