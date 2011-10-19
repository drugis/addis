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

package org.drugis.addis.gui;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.Source;
import org.junit.Test;

public class GUIFactoryTest {

	@Test
	public void testCreateToolTip() {
		assertEquals(
				"<html><b>From ClinicalTrials.gov</b><br>\ntest</html>",
				GUIFactory.createToolTip(new Note(Source.CLINICALTRIALS, "test")));
	}
	
	@Test
	public void testCreateToolTipHTMLEntities() {
		assertEquals(
				"<html><b>From ClinicalTrials.gov</b><br>\ntest &gt; you</html>",
				GUIFactory.createToolTip(new Note(Source.CLINICALTRIALS, "test > you")));
	}
}
