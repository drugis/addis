package org.drugis.addis.entities;

import static org.junit.Assert.*;

import java.util.Collections;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Endpoint.Type;
import org.junit.Before;
import org.junit.Test;

public class FrequencyMeasurementTest {

	private CategoricalVariable d_cv;
	private FrequencyMeasurement d_meas;

	@Before
	public void setUp() {
		d_cv = ExampleData.buildGenderVariable();
		d_meas = new FrequencyMeasurement(d_cv);
	}
	
	@Test
	public void testGetSampleSize() {
		d_meas.setFrequency(d_cv.getCategories()[0], 5);
		assertEquals(new Integer(5), d_meas.getSampleSize());
	}
	
	@Test
	public void testSetFrequency() {
		d_meas.setFrequency(d_cv.getCategories()[0], 5);
		assertEquals(new Integer(5), d_meas.getFrequency(d_cv.getCategories()[0]));
	}
		
	@Test(expected=IllegalArgumentException.class)
	public void testGetFrequencyThrows() {
		d_meas.getFrequency("illegalCat");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetFrequencyThrows() {
		d_meas.setFrequency("illegalCat", 0);
	}	
	
	@Test
	public void testIsOfType() {
		for (Type t : Endpoint.Type.values()) {
			assertFalse(d_meas.isOfType(t));
		}
	}
	
	@Test
	public void testGetDependencies() {
		assertEquals(Collections.singleton(d_cv), d_meas.getDependencies());
	}
	
	@Test
	public void testToString() {
		d_meas.setFrequency(d_cv.getCategories()[0], 25);
		d_meas.setFrequency(d_cv.getCategories()[1], 50);
		String expected = "Male = 25 / Female = 50";
		assertEquals(expected, d_meas.toString());
	}
	
	@Test
	public void testDeepCopy() {
		d_meas.setFrequency(d_cv.getCategories()[0], 25);		
		FrequencyMeasurement m = d_meas.deepCopy();
		assertTrue(d_meas.getCategoricalVariable() == m.getCategoricalVariable());
		assertEquals(new Integer(25), m.getFrequency(d_cv.getCategories()[0]));
		assertEquals(new Integer(0), m.getFrequency(d_cv.getCategories()[1]));		
		
		d_meas.setFrequency(d_cv.getCategories()[0], 50);
		assertEquals(new Integer(25), m.getFrequency(d_cv.getCategories()[0]));		
	}
	
	@Test
	public void testEquals() {
		FrequencyMeasurement m = d_meas.deepCopy();
		d_meas.setFrequency(d_cv.getCategories()[0], 25);
		d_meas.setFrequency(d_cv.getCategories()[1], 50);
		
		assertFalse(d_meas.equals(m));
		m = d_meas.deepCopy();
		assertEquals(d_meas, m);
		
		assertFalse(d_meas.equals(null));
		assertFalse(d_meas.equals(""));
	}
	
	@Test
	public void testGetCategoricalVariable() {
		assertEquals(d_cv, d_meas.getCategoricalVariable());
	}
	
	@Test
	public void testAdd() {
		FrequencyMeasurement m = d_meas.deepCopy();
		d_meas.setFrequency(d_cv.getCategories()[0], 25);
		d_meas.setFrequency(d_cv.getCategories()[1], 20);
	
		m.add(d_meas);
		assertEquals(new Integer(25), m.getFrequency(d_cv.getCategories()[0]));
		assertEquals(new Integer(20), m.getFrequency(d_cv.getCategories()[1]));
		
		m.add(d_meas);
		assertEquals(new Integer(50), m.getFrequency(d_cv.getCategories()[0]));
		assertEquals(new Integer(40), m.getFrequency(d_cv.getCategories()[1]));
		
	}
}
