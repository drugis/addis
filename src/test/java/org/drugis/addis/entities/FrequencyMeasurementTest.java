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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.drugis.addis.ExampleData;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class FrequencyMeasurementTest {

	private CategoricalPopulationCharacteristic d_cv;
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
	
	@Test
	public void testSetFrequencyFires() {
		d_meas.setFrequency(d_cv.getCategories()[0], 5);
		Map<String, Integer> map = new HashMap<String, Integer>(d_meas.getFrequencies());
		Map<String, Integer> newMap = new HashMap<String, Integer>(d_meas.getFrequencies());		
		newMap.put("Male", 25);
		PropertyChangeListener l = JUnitUtil.mockListener(d_meas, FrequencyMeasurement.PROPERTY_FREQUENCIES,
				map, newMap);
		d_meas.addPropertyChangeListener(l);
		d_meas.setFrequency(d_cv.getCategories()[0], 25);
		verify(l);
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
		for (Variable.Type t : Variable.Type.values()) {
			assertEquals(t.equals(Variable.Type.CATEGORICAL), d_meas.isOfType(t));
		}
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
		FrequencyMeasurement m = d_meas.clone();
		assertArrayEquals(d_meas.getCategories(), m.getCategories());
		assertEquals(new Integer(25), m.getFrequency(d_cv.getCategories()[0]));
		assertEquals(new Integer(0), m.getFrequency(d_cv.getCategories()[1]));		
		
		d_meas.setFrequency(d_cv.getCategories()[0], 50);
		assertEquals(new Integer(25), m.getFrequency(d_cv.getCategories()[0]));		
	}
	
	@Test
	public void testEquals() {
		FrequencyMeasurement m = d_meas.clone();
		d_meas.setFrequency(d_cv.getCategories()[0], 25);
		d_meas.setFrequency(d_cv.getCategories()[1], 50);
		
		assertFalse(d_meas.equals(m));
		m = d_meas.clone();
		assertEquals(d_meas, m);
		
		assertFalse(d_meas.equals(null));
		assertFalse(d_meas.equals(""));
	}
	
	@Test
	public void testGetCategories() {
		assertArrayEquals(d_cv.getCategories(), d_meas.getCategories());
	}
	
	@Test
	public void testAdd() {
		FrequencyMeasurement m = d_meas.clone();
		d_meas.setFrequency(d_cv.getCategories()[0], 25);
		d_meas.setFrequency(d_cv.getCategories()[1], 20);
	
		m.add(d_meas);
		assertEquals(new Integer(25), m.getFrequency(d_cv.getCategories()[0]));
		assertEquals(new Integer(20), m.getFrequency(d_cv.getCategories()[1]));
		
		m.add(d_meas);
		assertEquals(new Integer(50), m.getFrequency(d_cv.getCategories()[0]));
		assertEquals(new Integer(40), m.getFrequency(d_cv.getCategories()[1]));
	}
	
	@Test
	public void testClone() {
		d_meas.setFrequency(d_cv.getCategories()[0], 25);
		d_meas.setFrequency(d_cv.getCategories()[1], 20);
		assertEquals(d_meas, d_meas.clone());
		assertFalse(d_meas == d_meas.clone());
	}
}
