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

package org.drugis.addis.mcmcmodel;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class InverseLogitArgumentMakerTest {
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testGetArgumentFirstArray() {
		InverseLogitArgumentMaker am = new InverseLogitArgumentMaker(0);
		double[] inputs = new double[] { -2.3, 3.5, -0.1, 0.0, 8.4, 3.5 };
		double[] expected = new double[inputs.length];
		for (int i = 0; i < inputs.length; ++i) {
			expected[i] = MathUtil.ilogit(inputs[i]);
		}
		double[] actual = am.getArgument(new double[][] { inputs });
		for (int i = 0; i < inputs.length; ++i) {
			assertEquals(expected[i], actual[i], 0.0000001);
		}
	}
	
	@Test
	public void testGetArgumentOtherArray() {
		double expected = MathUtil.ilogit(2.0);
		
		assertEquals(expected, new InverseLogitArgumentMaker(2).getArgument(new double[][] { {0.0}, {4.0}, {2.0}, {3.0} })[0], 0.0000001);
	}
}
