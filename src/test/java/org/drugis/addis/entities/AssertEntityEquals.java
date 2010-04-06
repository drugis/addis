package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.drugis.addis.entities.Study.MeasurementKey;
import org.drugis.common.JUnitUtil;

public class AssertEntityEquals {
	
	private static class MeasurementComparator implements Comparator<Measurement> {
		// FIXME
		
		private double calcValue(Measurement m) {
			if (m instanceof ContinuousMeasurement)
				return calcValue((ContinuousMeasurement) m);
			else if (m instanceof RateMeasurement)
				return calcValue((RateMeasurement) m);
			else 
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
		//assertEquals(expected.getName(),actual.getName());
		//assertEquals (expected.getAtcCode(), actual.getAtcCode());
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
		}
		else if (expected instanceof RateMeasurement) 
			assertEquals( ((RateMeasurement) expected).getRate() , ((RateMeasurement) actual).getRate() );
		else {
			System.err.println("Measurement type not recognized.");
			fail();
		}
	}
	
	public static void assertEntityEquals(Study expected, Study actual) {
		assertEquals(expected, actual);
		
		// ade's
		assertEquals(expected.getAdverseEvents().size(), actual.getAdverseEvents().size());
		for (int i=0; i<expected.getAdverseEvents().size(); ++i)
			assertEntityEquals(expected.getAdverseEvents().get(i), actual.getAdverseEvents().get(i));
		
		// arms
		assertEquals(expected.getArms().size(), actual.getArms().size());
		for (int i=0; i<expected.getArms().size(); ++i)
			assertEntityEquals(expected.getArms().get(i), actual.getArms().get(i));
		
		// endpoints
		assertEquals(expected.getEndpoints().size(), actual.getEndpoints().size());
		for (int i=0; i<expected.getEndpoints().size(); ++i)
			assertEntityEquals(expected.getEndpoints().get(i), actual.getEndpoints().get(i));
		
		// indication
		assertEntityEquals(expected.getIndication(), actual.getIndication());
		
		// outcomemeasures
		assertEquals(expected.getOutcomeMeasures().size(), actual.getOutcomeMeasures().size());
		for (int i=0; i<expected.getOutcomeMeasures().size(); ++i)
			assertEntityEquals(expected.getOutcomeMeasures().get(i), actual.getOutcomeMeasures().get(i));
		
		// population characteristics
		assertEquals(expected.getPopulationCharacteristics().size(), actual.getPopulationCharacteristics().size());
		for (int i=0; i<expected.getPopulationCharacteristics().size(); ++i)
			assertEntityEquals(expected.getPopulationCharacteristics().get(i), actual.getPopulationCharacteristics().get(i));
		
		// drugs
		assertEquals(expected.getDrugs().size(), actual.getDrugs().size());
		Iterator<Drug> expDrugIterator = expected.getDrugs().iterator();
		Iterator<Drug> actDrugIterator = expected.getDrugs().iterator();
		while (expDrugIterator.hasNext())
			assertEntityEquals(expDrugIterator.next(), actDrugIterator.next());
		
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
			//System.out.println(curChar);
			assertEquals(expected.getCharacteristic(curChar), actual.getCharacteristic(curChar));
		}
		
		//notes
		assertEquals(expected.getNotes().keySet().size(), actual.getNotes().keySet().size());
	}
	
	public static void assertEntityEquals(Entity expected, Entity actual){
		System.err.println("AssertEntityEquals::AssertEntityEquals(Entity, Entity) should never be called.");
		fail();
	}

}
