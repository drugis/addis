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

package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.relativeeffect.BasicStandardisedMeanDifference;
import org.junit.Before;
import org.junit.Test;

public class StandardisedMeanDifferencedTableModelTest extends RelativeEffectTableModelBaseTest {
	@Before
	public void setUp() {
		baseSetUpContinuous();	
		d_stdModel = new StandardisedMeanDifferenceTableModel(d_standardStudy, d_endpoint, d_pmf);
		d_threeArmModel = new StandardisedMeanDifferenceTableModel(d_threeArmStudy, d_endpoint, d_pmf);
		d_relativeEffectClass = BasicStandardisedMeanDifference.class;
	}

	@Test
	public void testGetTitle() {
		String title = "Standardised Mean Difference Table";
		assertEquals(title, d_threeArmModel.getTitle());
		
	}
}
