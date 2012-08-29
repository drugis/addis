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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class EpochTest {
	
	private Epoch d_null;
	private Epoch d_randomization;
	private Epoch d_mainphase;

	@Before
	public void setUp() throws DatatypeConfigurationException {
		d_null = new Epoch(null, null);
		d_randomization = new Epoch("Randomization", null);
		d_mainphase = new Epoch("Main phase", DatatypeFactory.newInstance().newDuration("P42D"));
	}
	
	@Test
	public void testConstruction() throws DatatypeConfigurationException {
		assertEquals(null, d_null.getName());
		assertEquals("Randomization", d_randomization.getName());
		assertEquals("Main phase", d_mainphase.getName());
		
		assertEquals(null, d_null.getDuration());
		assertEquals(DatatypeFactory.newInstance().newDuration("P42D"), d_mainphase.getDuration());
		
		assertEquals(Collections.emptyList(), d_null.getNotes());
		Note n = new Note(Source.MANUAL, "Zis is a note");
		d_mainphase.getNotes().add(n);
		assertEquals(Collections.singletonList(n), d_mainphase.getNotes());
	}
	
	@Test
	public void testDependencies() {
		assertEquals(Collections.emptySet(), d_mainphase.getDependencies());
	}
	
	@Test
	public void testEquals() throws DatatypeConfigurationException {
		Epoch e = new Epoch("Main phase", DatatypeFactory.newInstance().newDuration("P42D"));
		Epoch e2 = new Epoch("Main phase", null);
		Epoch e3 = new Epoch("Randomization", null);
		
		// equality is defined on the NAME field.
		assertEquals(e, e2);
		JUnitUtil.assertNotEquals(e2, e3);
		assertEquals(e.hashCode(), e2.hashCode());
		d_null.hashCode(); // hashCode() should also be defined for NULL name
		
		// deep equality is defined by equality of the object graph
		assertTrue(e.deepEquals(d_mainphase));
		assertFalse(e2.deepEquals(d_randomization));
		assertFalse(d_null.deepEquals(d_mainphase));
		assertTrue(e3.deepEquals(d_randomization));
		e3.getNotes().add(new Note());
		assertFalse(e3.deepEquals(d_randomization));
	}
	
	@Test
	public void testRename() {
		Epoch epoch = new Epoch("Main phase", null);
		
		assertNotSame(epoch, epoch.rename("Main phase"));
		assertEquals(epoch, epoch.rename("Main phase"));
		
		assertEquals("New name", epoch.rename("New name").getName());
	}

	@Test
	public void testSetDuration() throws DatatypeConfigurationException {
		JUnitUtil.testSetter(d_null, Epoch.PROPERTY_DURATION, null, DatatypeFactory.newInstance().newDuration("P5DT3H"));
	}

}
