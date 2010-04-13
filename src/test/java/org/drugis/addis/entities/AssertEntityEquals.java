package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.drugis.common.JUnitUtil;

public class AssertEntityEquals {
	
	private static class MeasurementComparator implements Comparator<Measurement> {
		
		// FIXME: This compare does not necessarily work; Different objects could return the same comparator value.   
		private double calcValue(Measurement m) {
			if (m instanceof ContinuousMeasurement) {
				return calcValue((ContinuousMeasurement) m);
			} else if (m instanceof RateMeasurement) {
				return calcValue((RateMeasurement) m);
			} else 
				throw new IllegalArgumentException("Unknown type of measurement");
		}
		
		private double calcValue(ContinuousMeasurement m) {
			return - (Math.exp(m.getMean())*Math.exp(m.getStdDev())*Math.exp(m.getSampleSize()));
		}
		
		private double calcValue(RateMeasurement m) {
			return Math.exp(m.getRate()) * Math.exp(m.getSampleSize());
		}
		
		public int compare(Measurement o1, Measurement o2) {
			return (int) (calcValue(o1) - calcValue(o2));
		}
	}
	
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

	public static void assertEntityEquals(Arm expected, Arm actual) {
		assertEntityEquals(expected.getDrug(), actual.getDrug());
		assertEquals(expected.getDose(), actual.getDose());
		assertEquals(expected.getSize(), actual.getSize());
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
		else {
			System.err.println("Measurement type not recognized.");
			fail();
		}
	}
	
	public static void assertEntityEquals(Collection<? extends Entity> expected, Collection<? extends Entity> actual) {
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
		assertEntityEquals(expected.getDrugs(), actual.getDrugs());
		
		// indication
		assertEntityEquals(expected.getIndication(), actual.getIndication());
		
		// measurements
		assertEquals(expected.getMeasurements().keySet().size(), actual.getMeasurements().keySet().size());
		SortedSet<Measurement> expectedMeasurementSet = new TreeSet<Measurement>(new MeasurementComparator());
		expectedMeasurementSet.addAll(expected.getMeasurements().values());
		SortedSet<Measurement> actualMeasurementSet = new TreeSet<Measurement>(new MeasurementComparator());
		actualMeasurementSet.addAll(actual.getMeasurements().values());
		JUnitUtil.assertAllAndOnly(expectedMeasurementSet, actualMeasurementSet);
		
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
		else if (expected instanceof CharacteristicsMap){
			//TODO: maps not tested yet!
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
		assertEntityEquals(d1.getVariables(), d2.getVariables());
		assertEntityEquals(d1.getStudies(), d2.getStudies());
		// assertEntityEquals(d1.getMetaAnalyses(), d2.getMetaAnalyses()); // FIXME	
	}
}
