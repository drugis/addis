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

import static org.junit.Assert.*;

import javax.xml.datatype.Duration;

import org.drugis.addis.entities.WhenTaken.RelativeTo;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class WhenTakenTest {
	private Epoch d_epoch1;
	private Epoch d_epoch2;
	private WhenTaken d_wt;
	private Duration d_duration1;

	@Before
	public void setUp() {
		d_epoch1 = new Epoch("Je moeder", EntityUtil.createDuration("P31D"));
		d_epoch2 = new Epoch("Je vader", EntityUtil.createDuration("P33D"));
		d_duration1 = EntityUtil.createDuration("P29D");
		d_wt = new WhenTaken(d_duration1, RelativeTo.FROM_EPOCH_START, d_epoch1);
	}
	
	@Test
	public void testSetEpoch() {
		JUnitUtil.testSetter(d_wt, WhenTaken.PROPERTY_EPOCH, d_epoch1, d_epoch2);
	}
	
	@Test
	public void testSetRelativeTo() {
		JUnitUtil.testSetter(d_wt, WhenTaken.PROPERTY_RELATIVE_TO, RelativeTo.FROM_EPOCH_START, RelativeTo.BEFORE_EPOCH_END);
	}
	
	@Test
	public void testSetOffset() {
		Duration duration2 = EntityUtil.createDuration("P28D"); 
		JUnitUtil.testSetter(d_wt, WhenTaken.PROPERTY_OFFSET, d_duration1, duration2);
	}
	
	@Test
	public void testCompare() {
		WhenTaken wt2 = new WhenTaken(d_duration1, RelativeTo.FROM_EPOCH_START, d_epoch2);
		assertTrue(d_wt.compareTo(wt2) < 0);
		assertTrue(d_wt.compareTo(d_wt) == 0);
		d_epoch1.setName(d_epoch2.getName());
		assertTrue(d_wt.compareTo(wt2) == 0);
		wt2.setRelativeTo(RelativeTo.BEFORE_EPOCH_END);
		assertTrue(d_wt.compareTo(wt2) < 0);
		wt2.setRelativeTo(RelativeTo.FROM_EPOCH_START);
		wt2.setDuration(EntityUtil.createDuration("P20D"));
		assertTrue(d_wt.compareTo(wt2) > 0);
	}
}
