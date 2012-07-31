/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.FixedDose;
import org.junit.Test;

public class DoseQuantityChoiceNodeTest {
	@Test
	public void testGetValue() {
		FixedDose dose = new FixedDose(13, DoseUnit.MILLIGRAMS_A_DAY);
		ChoiceNode choiceNode = new DoseQuantityChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, DoseUnit.MILLIGRAMS_A_DAY);
		assertEquals(dose.getQuantity(), choiceNode.getValue(dose));
	}
	
	@Test
	public void testGetValueConversion() {
		FixedDose dose = new FixedDose(2400, DoseUnit.MILLIGRAMS_A_DAY);
		ChoiceNode choiceNode = new DoseQuantityChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, ExampleData.KILOGRAMS_PER_HOUR);
		assertEquals(0.0001, (Double)choiceNode.getValue(dose), 0.000001);
	}
}
