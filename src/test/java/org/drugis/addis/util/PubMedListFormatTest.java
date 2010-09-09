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

package org.drugis.addis.util;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.PubMedId;
import org.junit.Test;

public class PubMedListFormatTest {
	private PubMedListFormat d_format = new PubMedListFormat();
	
	@Test
	public void testFormatEmptyList() {
		assertEquals("", d_format.format(Collections.emptyList()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNonListShouldThrow() {
		d_format.format("X");
	}
	
	@Test
	public void testFormatSingleEntry() { 
		String str1 = "12345";
		assertEquals(str1, d_format.format(Collections.singletonList(new PubMedId(str1))));
		String str2 = "123456";
		assertEquals(str2, d_format.format(Collections.singletonList(new PubMedId(str2))));
	}

	@Test
	public void testFormatMultipleEntry() {
		String str1 = "123";
		String str2 = "456";
		List<PubMedId> lst = new ArrayList<PubMedId>();
		lst.add(new PubMedId(str1));
		lst.add(new PubMedId(str2));
		assertEquals(str1 + ", " + str2, d_format.format(lst));
	}

	@Test
	public void testParseEmptyString() throws ParseException {
		assertEquals(Collections.emptyList(), d_format.parseObject(""));
	}
	
	@Test
	public void testParseSingleEntry() throws ParseException {
		String str1 = "12345";
		assertEquals(Collections.singletonList(new PubMedId(str1)), d_format.parseObject(str1));
	}
	
	@Test
	public void testParseSingleEntryWithNonDigits() throws ParseException {
		assertEquals(Collections.singletonList(new PubMedId("12345")), d_format.parseObject("12345a"));
		assertEquals(Collections.singletonList(new PubMedId("12345")), d_format.parseObject("-1x23\n45a"));
		assertEquals(Collections.singletonList(new PubMedId("12345")), d_format.parseObject("0012345"));
	}
	
	@Test
	public void testParseSingleInvalidEntry() throws ParseException {
		assertEquals(Collections.emptyList(), d_format.parseObject("notanid"));
	}
	
	@Test
	public void testParseMultipleEntries() throws ParseException {
		String str1 = "123";
		String str2 = "456";
		List<PubMedId> lst = new ArrayList<PubMedId>();
		lst.add(new PubMedId(str1));
		lst.add(new PubMedId(str2));
		assertEquals(lst, d_format.parseObject(str1 + ", " + str2));
		assertEquals(lst, d_format.parseObject(str1 + "," + str2));
	}
	
	@Test
	public void testParseMultipleWithInvalidEntry() throws ParseException {
		assertEquals(Collections.singletonList(new PubMedId("12345")), d_format.parseObject("12345a, x"));
		assertEquals(Collections.emptyList(), d_format.parseObject("x, y"));
	}
}
