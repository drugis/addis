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

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class ArmTest {
	
	private Arm d_pg;

	@Before
	public void setUp() {
		d_pg = new Arm(null, null, 0);
	}
	
	@Test
	public void testSetSize() {
		JUnitUtil.testSetter(d_pg, Arm.PROPERTY_SIZE, 0, 1);
	}
	
	@Test
	public void testSetDrug() {
		JUnitUtil.testSetter(d_pg, Arm.PROPERTY_DRUG, null, new Drug("D", "atc"));
	}
	
	@Test
	public void testSetDose() {
		JUnitUtil.testSetter(d_pg, Arm.PROPERTY_DOSE, null, new FixedDose(1.0, SIUnit.MILLIGRAMS_A_DAY));
	}
}
