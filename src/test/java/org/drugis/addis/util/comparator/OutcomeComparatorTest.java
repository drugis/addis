/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

package org.drugis.addis.util.comparator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.OutcomeMeasure;
import org.junit.Before;
import org.junit.Test;

public class OutcomeComparatorTest {
	private OutcomeComparator d_comparator;

	@Before
	public void setup(){
		d_comparator = new OutcomeComparator();
	}

	@Test
	public void testCompareOutcomeMeasure(){
		ArrayList<OutcomeMeasure> list = new ArrayList<OutcomeMeasure>();
		list.add(ExampleData.buildAdverseEventConvulsion());
		list.add(ExampleData.buildEndpointHamd());
		list.add(ExampleData.buildEndpointCgi());
		Collections.sort(list, d_comparator);
		assertEquals(ExampleData.buildEndpointCgi(), list.get(0));
		assertEquals(ExampleData.buildEndpointHamd(), list.get(1));
		assertEquals(ExampleData.buildAdverseEventConvulsion(), list.get(2));
	}
}
