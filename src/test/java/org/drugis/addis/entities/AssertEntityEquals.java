/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map.Entry;

import org.drugis.addis.entities.Study.MeasurementKey;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.common.EqualsUtil;

public class AssertEntityEquals {
	
	public static void assertEntityEquals(Indication expected, Indication actual) {
		assertEquals(expected.getClass(), actual.getClass());
		assertEquals(expected.getCode(),  actual.getCode());
		assertEquals(expected.getName(),  actual.getName());	
	}

	public static void assertEntityEquals(OutcomeMeasure expected, OutcomeMeasure actual) {
		assertEquals(expected.getDirection(),actual.getDirection());
		assertEquals(expected.getType(),actual.getType());
		assertEquals(expected, actual);
	}
	
	public static void assertEntityEquals(Drug expected, Drug actual) {
		assertEquals(expected.getName(),actual.getName());
		assertEquals (expected.getAtcCode(), actual.getAtcCode());
	}
	
	public static boolean armsEqual(Arm expected, Arm actual) {
		if (expected == null || actual == null) {
			return expected == actual;
		}
		return EqualsUtil.equal(expected.getDrug(), actual.getDrug()) &&
			EqualsUtil.equal(expected.getDose(), actual.getDose()) &&
			EqualsUtil.equal(expected.getSize(), actual.getSize());
	}

	public static void assertEntityEquals(Arm expected, Arm actual) {
		if (!armsEqual(expected, actual)) {
			throw new AssertionError("Expected " + expected + " but got " + actual);
		}
	}
	
	public static void assertEntityEquals(Variable expected, Variable actual) {
		assertEquals(expected, actual);
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getDescription(),actual.getDescription());
		assertEquals(expected.getType(), actual.getType());
		assertEquals(expected.getUnitOfMeasurement(), actual.getUnitOfMeasurement());	
	}
	
	public static void assertEntityEquals(CategoricalPopulationCharacteristic expected, CategoricalPopulationCharacteristic actual) {
		assertEntityEquals((Variable) expected, (Variable) actual);
		assertEquals(expected.getCategories().length, actual.getCategories().length);
		for(int i = 0; i < expected.getCategories().length; ++i)
			assertEquals(expected.getCategories()[i], actual.getCategories()[i]);
	}
	
	public static void assertEntityEquals(Measurement expected, Measurement actual) {
		assertEquals(expected.getSampleSize(), actual.getSampleSize());
		if (expected instanceof ContinuousMeasurement) {
			assertEquals( ((ContinuousMeasurement) expected).getMean() , ((ContinuousMeasurement) actual).getMean() );
			assertEquals( ((ContinuousMeasurement) expected).getStdDev() , ((ContinuousMeasurement) actual).getStdDev() );
		} else if (expected instanceof RateMeasurement) 
			assertEquals( ((RateMeasurement) expected).getRate() , ((RateMeasurement) actual).getRate() );
		else if (expected instanceof FrequencyMeasurement) {
			assertEquals(((FrequencyMeasurement) expected).getCategoricalVariable() , ((FrequencyMeasurement) actual).getCategoricalVariable());
			assertEquals(((FrequencyMeasurement) expected).getFrequencies() , ((FrequencyMeasurement) actual).getFrequencies());
		} else {
			System.err.println("Measurement type not recognized.");
			fail();
		}
	}
	

	private static void assertEntityEquals(SortedSet<? extends Entity> expected, SortedSet<? extends Entity> actual) {
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
		assertEquals(expected, actual);
		assertEntityEquals(expected.getAdverseEvents(), actual.getAdverseEvents());
		assertEntityEquals(expected.getArms(), actual.getArms());
		assertEntityEquals(expected.getEndpoints(), actual.getEndpoints());
		assertEntityEquals(expected.getOutcomeMeasures(), actual.getOutcomeMeasures());
		assertEntityEquals(expected.getPopulationCharacteristics(), actual.getPopulationCharacteristics());
		assertEquals(expected.getDrugs(), actual.getDrugs());
		
		// indication
		assertEntityEquals(expected.getIndication(), actual.getIndication());
		
		// measurements
		assertEquals(expected.getMeasurements().keySet().size(), actual.getMeasurements().keySet().size());
		for (Entry<MeasurementKey, Measurement> entry : expected.getMeasurements().entrySet()) {
			Object actualKey = findMatchingKey(entry.getKey(), actual.getMeasurements().keySet());
			assertEquals(entry.getValue(), actual.getMeasurements().get(actualKey));
		}

		// characteristics
		assertEquals(expected.getCharacteristics().keySet().size(), actual.getCharacteristics().keySet().size());
		Iterator<Characteristic> charIterator = expected.getCharacteristics().keySet().iterator();
		while (charIterator.hasNext()) {
			Characteristic curChar = charIterator.next();
			if (expected.getCharacteristic(curChar) instanceof Date)
				assertEquals(expected.getCharacteristic(curChar).toString(), actual.getCharacteristic(curChar).toString());
			else
				assertEquals(expected.getCharacteristic(curChar), actual.getCharacteristic(curChar));
		}
		
		//notes
		assertEquals(expected.getNotes().keySet().size(), actual.getNotes().keySet().size());
	}
	
	private static Object findMatchingKey(MeasurementKey key, Set<MeasurementKey> keySet) {
		for (MeasurementKey otherKey : keySet) {
			if (EqualsUtil.equal(key.getVariable(), otherKey.getVariable()) &&
					armsEqual(key.getArm(), otherKey.getArm())) {
				return otherKey;
			}
		}
		return null;
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
	}
	
	@SuppressWarnings("unchecked")
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
		} else if (expected instanceof CharacteristicsMap){
			Map<Object,Object> expMap = (Map<Object,Object>) expected;
			Map<Object,Object> actMap = (Map<Object,Object>) actual;
			for(Entry e : expMap.entrySet() ){
				assertTrue(actMap.keySet().contains(e.getKey()));
				boolean objFound = false;
				String objToCompare = e.getValue().toString();
				for (Object o : expMap.values()) {
					if (o.toString().equals(objToCompare))
						objFound = true;
				}
				assertTrue(objFound);
			}
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
	}

}
