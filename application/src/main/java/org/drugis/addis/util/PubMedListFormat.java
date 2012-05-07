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

package org.drugis.addis.util;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;

@SuppressWarnings("serial")
public class PubMedListFormat extends Format {

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		if (!(obj instanceof List<?>)) {
			throw new IllegalArgumentException();
		}
		List<?> list = (List<?>) obj;
		if(list.size() >= 1) {
			toAppendTo.append(list.get(0));
			for (int i = 1; i < list.size(); i++) {
				toAppendTo.append(", ");
				toAppendTo.append(list.get(i));
			}
		}
		return toAppendTo;
	}

	private static final Pattern s_nonDigit = Pattern.compile("[^0-9]");
	private static final Pattern s_leadingZeros = Pattern.compile("^0*");

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		pos.setIndex(source.length() + 1);
		PubMedIdList list = new PubMedIdList();
		
		StringTokenizer tokenizer = new StringTokenizer(source, ",");
		while (tokenizer.hasMoreTokens()) {
			validatePubMedID(tokenizer.nextToken(), list);	
	    }
		
		return list;
	}

	private void validatePubMedID(String source, PubMedIdList list) {
		Matcher removeNonDigits = s_nonDigit.matcher(source);
		Matcher removeZeros = s_leadingZeros.matcher(removeNonDigits.replaceAll(""));
		String processed = removeZeros.replaceFirst("");
		if(processed.length() != 0) {
			list.add(new PubMedId(processed));
		}
	}

}
