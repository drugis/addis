/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class BasicArmTest {
	
	private BasicArm d_pg;

	@Before
	public void setUp() {
		d_pg = new BasicArm(null, null, 0);
	}
	
	@Test
	public void testSetSize() {
		JUnitUtil.testSetter(d_pg, BasicArm.PROPERTY_SIZE, 0, 1);
	}
	
	@Test
	public void testSetDrug() {
		JUnitUtil.testSetter(d_pg, BasicArm.PROPERTY_DRUG, null, new Drug("D", "atc"));
	}
	
	@Test
	public void testSetDose() {
		JUnitUtil.testSetter(d_pg, BasicArm.PROPERTY_DOSE, null, new FixedDose(1.0, SIUnit.MILLIGRAMS_A_DAY));
	}
	
	@Test
	public void testSetCharacteristic() {
		d_pg.setCharacteristic(PopulationCharacteristic.MALE, 20);
		d_pg.getCharacteristics().containsKey(PopulationCharacteristic.MALE);
		assertEquals(20, d_pg.getCharacteristic(PopulationCharacteristic.MALE));
	}
}
