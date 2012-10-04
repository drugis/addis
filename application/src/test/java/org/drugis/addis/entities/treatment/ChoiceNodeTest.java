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

package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.junit.Test;

public class ChoiceNodeTest {
	@Test
	public void testGetValue() {
		Drug drug = new Drug("Fluoxetine", "3");
		ChoiceNode choiceNode = new ChoiceNode(Drug.class, Drug.PROPERTY_NAME);
		assertEquals(drug.getName(), choiceNode.getValue(drug));
		assertEquals("Name", choiceNode.getName());
	}
	
	@Test
	public void testGetValueClass() {
		ChoiceNode choiceNode = new ChoiceNode(String.class, "class");
		assertEquals(String.class, choiceNode.getValue("X"));
		assertEquals("Class", choiceNode.getName());
	}
	
	@Test
	public void testMultiWordName() {
		ChoiceNode choiceNode = new ChoiceNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE);
		assertEquals("Min Dose", choiceNode.getName());
	}
	
	@Test(expected=RuntimeException.class)
	public void testIllegalProperty() {
		new ChoiceNode(Drug.class, FixedDose.PROPERTY_QUANTITY);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIllegalObject() {
		new ChoiceNode(Drug.class, Drug.PROPERTY_NAME).getValue("X");
	}
}
