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
}
