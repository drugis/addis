/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import java.util.Collections;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.math3.util.Precision;
import org.drugis.addis.ExampleData;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class DoseUnitTest {

	private DoseUnit d_mgDay;
	private DoseUnit d_kgHr;
	private Unit d_gram;
	private Unit d_meter;

	@Before
	public void setUp() {
		d_gram = new Unit("gram", "g");
		d_meter = new Unit("meter", "m");
		d_mgDay = DoseUnit.createMilliGramsPerDay().clone();
		d_kgHr = ExampleData.KILOGRAMS_PER_HOUR.clone();
	}
	
	@Test
	public void testEquals() {
		assertFalse(d_mgDay.equals(d_kgHr));
		assertEntityEquals(d_mgDay, d_mgDay);
		DoseUnit du = new DoseUnit(new Unit("gram", "gg"), ScaleModifier.MILLI, EntityUtil.createDuration("P1D"));
		DoseUnit du2 = new DoseUnit(new Unit("gram", "gg"), ScaleModifier.MILLI, EntityUtil.createDuration("P1D"));
		assertEntityEquals(du, du2);
		assertEquals(d_mgDay, du);
		assertFalse(d_mgDay.deepEquals(du));
	}
	
	@Test
	public void testEvents() {
		JUnitUtil.testSetter(d_mgDay, DoseUnit.PROPERTY_UNIT, d_gram, d_meter);
		JUnitUtil.testSetter(d_mgDay, DoseUnit.PROPERTY_SCALE_MODIFIER, ScaleModifier.MILLI, ScaleModifier.KILO);
		JUnitUtil.testSetter(d_mgDay, DoseUnit.PROPERTY_PER_TIME, EntityUtil.createDuration("P1D"), EntityUtil.createDuration("PT1H"));
	}
	
	@Test
	public void testClone() throws DatatypeConfigurationException {
		DoseUnit cloned = d_mgDay.clone();
		assertEntityEquals(d_mgDay, cloned);
		assertNotSame(d_mgDay, cloned);

		cloned.setScaleModifier(ScaleModifier.KILO);
		JUnitUtil.assertNotEquals(d_mgDay.getScaleModifier(), cloned.getScaleModifier());
		cloned.setScaleModifier(ScaleModifier.MILLI);
		assertEquals(d_mgDay.getScaleModifier(), cloned.getScaleModifier());
		
		cloned.setUnit(new Unit("nonsense", "ns"));
		JUnitUtil.assertNotEquals(d_mgDay.getUnit(), cloned.getUnit());
		cloned.setUnit(d_mgDay.getUnit());
		assertEquals(d_mgDay.getUnit(), cloned.getUnit());

		cloned.setPerTime(DatatypeFactory.newInstance().newDuration("P2D"));
		JUnitUtil.assertNotEquals(d_mgDay.getPerTime(), cloned.getPerTime());
		cloned.setPerTime(d_mgDay.getPerTime());
		assertEquals(d_mgDay.getPerTime(), cloned.getPerTime());
	}
	
	@Test
	public void testConvert() { 
		assertEquals(0.0001, DoseUnit.convert(2400, DoseUnit.createMilliGramsPerDay(), ExampleData.KILOGRAMS_PER_HOUR), Precision.EPSILON);
		
		DoseUnit gHour = new DoseUnit(new Unit("gram", "g"), ScaleModifier.UNIT, EntityUtil.createDuration("PT1H"));
		assertEquals(240000, DoseUnit.convert(10, gHour, DoseUnit.createMilliGramsPerDay()), Precision.EPSILON);
	}
	
	@Test
	public void testDependencies() {
		assertEquals(Collections.singleton(d_gram), d_mgDay.getDependencies());
		assertEquals(Collections.singleton(d_gram), d_kgHr.getDependencies());
		d_mgDay.setUnit(d_meter);
		assertEquals(Collections.singleton(d_meter), d_mgDay.getDependencies());
	}
}
