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

import static org.drugis.common.JUnitUtil.assertAllAndOnly;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.EqualsUtil;

public class AssertEntityEquals {
	
	public static void assertEntityEquals(Indication expected, Indication actual) {
		assertTrue(EntityUtil.deepEqual(expected, actual));
	}
	
	public static void assertEntityEquals(Drug expected, Drug actual) {
		assertTrue(EntityUtil.deepEqual(expected, actual));
	}
	
	public static boolean armsEqual(Arm expected, Arm actual) {
		if (expected == null || actual == null) {
			return expected == actual;
		}
		return expected.deepEquals(actual);
	}

	public static void assertEntityEquals(Arm expected, Arm actual) {
		if (!armsEqual(expected, actual)) {
			throw new AssertionError("Expected " + expected + " but got " + actual);
		}
	}
	
	public static void assertEntityEquals(Variable expected, Variable actual) {
		assertTrue(EntityUtil.deepEqual(expected, actual));
	}
	
	public static void assertEntityEquals(Measurement expected, Measurement actual) {
		assertTrue(EntityUtil.deepEqual(expected, actual));
	}
	

	public static void assertEntityEquals(SortedSet<? extends Entity> expected, SortedSet<? extends Entity> actual) {
		assertEntityEquals(asList(expected), asList(actual));
	}

	private static List<? extends Entity> asList(SortedSet<? extends Entity> expected) {
		List<Entity> expList = new ArrayList<Entity>();
		expList.addAll(expected);
		return expList;
	}
	
	public static void assertEntityEquals(List<? extends Entity> expected, List<? extends Entity> actual) {
		assertEquals(expected.size(), actual.size());
		Iterator<? extends Entity> expectedIterator = expected.iterator();
		Iterator<? extends Entity> actualIterator = actual.iterator();
		while (expectedIterator.hasNext()) 
			assertEntityEquals(expectedIterator.next(), actualIterator.next());
	}
	
	public static void assertEntityEquals(Study expected, Study actual) {
		assertTrue(EntityUtil.deepEqual(expected, actual));
	}
	
	public static void assertEntityEquals(MetaAnalysis expected, MetaAnalysis actual) {
		assertTrue(EntityUtil.deepEqual(expected, actual));
	}
	
	public static void assertEntityEquals(MetaBenefitRiskAnalysis expected, MetaBenefitRiskAnalysis actual) {
		assertTrue(EntityUtil.deepEqual(expected, actual));
	}

	public static void assertEntityEquals(StudyBenefitRiskAnalysis expected, StudyBenefitRiskAnalysis actual) {
		assertTrue(EntityUtil.deepEqual(expected, actual));
	}
	
	public static void assertEntityEquals(Entity expected, Entity actual){
		if (expected instanceof Arm)
			assertEntityEquals((Arm) expected, (Arm) actual);
		else if (expected instanceof Drug)
			assertEntityEquals((Drug) expected, (Drug) actual);
		else if (expected instanceof Indication)
			assertEntityEquals((Indication) expected, (Indication) actual);
		else if (expected instanceof Measurement)
			assertEntityEquals((Measurement) expected, (Measurement) actual);
		else if (expected instanceof Study)
			assertEntityEquals((Study) expected, (Study) actual);
		else if (expected instanceof Variable)
			assertEntityEquals((Variable) expected, (Variable) actual);
		else if (expected instanceof MetaAnalysis) {
			assertEntityEquals((MetaAnalysis)expected, (MetaAnalysis)actual);
		} else if (expected instanceof CharacteristicsMap) {
			CharacteristicsMap expCh = (CharacteristicsMap) expected;
			CharacteristicsMap actCh = (CharacteristicsMap) actual;
			assertAllAndOnly(expCh.keySet(), actCh.keySet());
			for (Characteristic key : expCh.keySet()) {
				Object expValue = expCh.get(key);
				Object actValue = actCh.get(key);
				if (expValue instanceof Entity) {
					assertEntityEquals((Entity)expValue, (Entity)actValue);
				} else {
					assertEquals(expValue, actValue);
				}
			}
		} else if (expected instanceof MetaBenefitRiskAnalysis) {
			assertEntityEquals((MetaBenefitRiskAnalysis)expected, (MetaBenefitRiskAnalysis)actual);
		} else if (expected instanceof StudyBenefitRiskAnalysis) {
			assertEntityEquals((StudyBenefitRiskAnalysis)expected, (StudyBenefitRiskAnalysis)actual);
		} else {
			System.err.println("No test for the equality of this entity: " + expected.getClass());
			fail();
		}
	}

	public static void assertDomainEquals(Domain d1, Domain d2) {
		assertTrue(EqualsUtil.equal(d1, d2));
	}

}
