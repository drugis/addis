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
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.util.EntityUtil;

public class AssertEntityEquals {
	
	public static void assertEntityEquals(Indication expected, Indication actual) {
		assertEquals(expected.getClass(), actual.getClass());
		assertEquals(expected.getCode(),  actual.getCode());
		assertEquals(expected.getName(),  actual.getName());	
	}

	public static void assertEntityEquals(OutcomeMeasure expected, OutcomeMeasure actual) {
		assertEquals(expected, actual);
		assertEquals(expected.getType(),actual.getType());
		assertEquals(expected.getDirection(),actual.getDirection());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(expected.getUnitOfMeasurement(), actual.getUnitOfMeasurement());
	}
	
	public static void assertEntityEquals(Drug expected, Drug actual) {
		assertEquals(expected.getName(),actual.getName());
		assertEquals (expected.getAtcCode(), actual.getAtcCode());
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
		assertEquals(expected, actual);
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getType(), actual.getType());
		assertEquals(expected.getDescription(),actual.getDescription());
		assertEquals(expected.getUnitOfMeasurement(), actual.getUnitOfMeasurement());	
	}
	
	public static void assertEntityEquals(CategoricalPopulationCharacteristic expected, CategoricalPopulationCharacteristic actual) {
		assertEntityEquals((Variable) expected, (Variable) actual);
		assertEquals(expected.getCategories().length, actual.getCategories().length);
		for(int i = 0; i < expected.getCategories().length; ++i)
			assertEquals(expected.getCategories()[i], actual.getCategories()[i]);
	}
	
	public static void assertEntityEquals(Measurement expected, Measurement actual) {
		EntityUtil.deepEqual(expected, actual);
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
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getType(), actual.getType());
		assertEquals(expected.getIncludedStudies(), actual.getIncludedStudies());
		assertEquals(expected.getIncludedDrugs(), actual.getIncludedDrugs());
		assertEquals(expected.getSampleSize(), actual.getSampleSize());
		assertEquals(expected.getOutcomeMeasure(), actual.getOutcomeMeasure());
		assertEquals(expected.getIndication(), actual.getIndication());
		assertEquals(expected.getDependencies(), actual.getDependencies());
		if (expected instanceof NetworkMetaAnalysis) {
			assertTrue(actual instanceof NetworkMetaAnalysis);
			NetworkMetaAnalysis expNetwork = (NetworkMetaAnalysis) expected;
			NetworkMetaAnalysis actNetwork = (NetworkMetaAnalysis) actual;
			for (DrugSet d : expNetwork.getIncludedDrugs()) {
				for (Study s : expNetwork.getIncludedStudies()) {
					assertEntityEquals(expNetwork.getArm(s, d), actNetwork.getArm(s, d));
				}
			}
		} else {
			assertTrue(actual instanceof RandomEffectsMetaAnalysis);
			RandomEffectsMetaAnalysis expPairWise = (RandomEffectsMetaAnalysis) expected;
			RandomEffectsMetaAnalysis actPairWise = (RandomEffectsMetaAnalysis) actual;
			for (StudyArmsEntry s : expPairWise.getStudyArms()) {
				assertEntityEquals(s.getBase(), actPairWise.getArm(s.getStudy(), expPairWise.getFirstDrug()));
				assertEntityEquals(s.getSubject(), actPairWise.getArm(s.getStudy(), expPairWise.getSecondDrug()));
			}
		}
	}
	
	public static void assertEntityEquals(MetaBenefitRiskAnalysis expected, MetaBenefitRiskAnalysis actual) {
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getBaseline(), actual.getBaseline());
		assertEquals(expected.getIndication(), actual.getIndication());
		assertEquals(expected.getMetaAnalyses(), actual.getMetaAnalyses());
		assertEquals(expected.getDrugs(), actual.getDrugs());
	}

	public static void assertEntityEquals(StudyBenefitRiskAnalysis expected, StudyBenefitRiskAnalysis actual) {
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getIndication(), actual.getIndication());
		assertEquals(expected.getStudy(), actual.getStudy());
		assertEquals(expected.getAlternatives().size(), actual.getAlternatives().size());
		for (int i = 0; i < expected.getAlternatives().size(); ++i) {
			assertEntityEquals(expected.getAlternatives().get(i), actual.getAlternatives().get(i));
		}
		assertEquals(expected.getCriteria().size(), actual.getCriteria().size());
		for (int i = 0; i < expected.getCriteria().size(); ++i) {
			assertEntityEquals(expected.getCriteria().get(i), actual.getCriteria().get(i));
		}
		
		
	}
	
	public static void assertEntityEquals(Entity expected, Entity actual){
		if (expected instanceof Endpoint)
			assertEntityEquals((Endpoint) expected, (Endpoint) actual);
		else if (expected instanceof Arm)
			assertEntityEquals((Arm) expected, (Arm) actual);
		else if (expected instanceof CategoricalPopulationCharacteristic)
			assertEntityEquals((CategoricalPopulationCharacteristic) expected, (CategoricalPopulationCharacteristic) actual);
		else if (expected instanceof Drug)
			assertEntityEquals((Drug) expected, (Drug) actual);
		else if (expected instanceof Indication)
			assertEntityEquals((Indication) expected, (Indication) actual);
		else if (expected instanceof Measurement)
			assertEntityEquals((Measurement) expected, (Measurement) actual);
		else if (expected instanceof OutcomeMeasure)
			assertEntityEquals((OutcomeMeasure) expected, (OutcomeMeasure) actual);
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
		assertEntityEquals(d1.getEndpoints(), d2.getEndpoints());
		assertEntityEquals(d1.getDrugs(), d2.getDrugs());
		assertEntityEquals(d1.getIndications(), d2.getIndications());
		assertEntityEquals(d1.getAdverseEvents(), d2.getAdverseEvents());
		assertEntityEquals(d1.getPopulationCharacteristics(), d2.getPopulationCharacteristics());
		assertEntityEquals(d1.getStudies(), d2.getStudies());
		assertEntityEquals(d1.getMetaAnalyses(), d2.getMetaAnalyses());
		assertEntityEquals(d1.getBenefitRiskAnalyses(), d2.getBenefitRiskAnalyses());
	}

}
