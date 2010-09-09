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

package org.drugis.addis.imports;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;

import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class PubMedIDRetrieverTest {
	@Before
	public void setUp() {
	}
	
	@Test
	public void testImportPubMedID() throws MalformedURLException, IOException {
		PubMedIdList testPubMedIDs = new PubMedIDRetriever().importPubMedID("NCT00000400");
		JUnitUtil.assertAllAndOnly(Collections.singletonList(new PubMedId("19401368")), testPubMedIDs);
	}
	
	@Test
	public void testImportPubMedNoID() throws MalformedURLException, IOException {
		PubMedIdList testPubMedIDs = new PubMedIDRetriever().importPubMedID("NCT");
		JUnitUtil.assertAllAndOnly(Collections.emptyList(), testPubMedIDs);
	}
}
