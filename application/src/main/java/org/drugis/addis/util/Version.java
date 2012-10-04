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

package org.drugis.addis.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Version representation for ADDIS (to be able to compare versions).
 * For now, we ignore any non-numeric suffix.
 */
public final class Version implements Comparable<Version> {
	private final String d_versionStr;
	private final List<Integer> d_components;
	private static final Pattern s_pattern = Pattern.compile("^([0-9\\.]*)"); 

	public Version(String versionStr) {
		d_versionStr = versionStr;
		Matcher m = s_pattern.matcher(versionStr);
		if (m.find()) {
			d_components = getComponents(m.group(0));
		} else {
			d_components = Collections.emptyList();
		}
	}

	public int compareTo(Version other) {
		int numComponents = Math.max(componentCount(), other.componentCount());
		for (int i = 0; i < numComponents; ++i) {
			if(componentAt(i).compareTo(other.componentAt(i)) !=0 ){
				return componentAt(i).compareTo(other.componentAt(i));
			}
		}
		return 0;
	}
	
	private int componentCount() {
		return d_components.size();
	}
	
	private Integer componentAt(int pos) {
		return pos < d_components.size() ? d_components.get(pos) : 0;
	}

	private List<Integer> getComponents(String str) {
		StringTokenizer tokenizer = new StringTokenizer(str, ".");
		List<Integer> components = new ArrayList<Integer>();
		while (tokenizer.hasMoreTokens()) {
			components.add(Integer.parseInt(tokenizer.nextToken()));
		}
		return components;
	}

	@Override
	public String toString() {
		return d_versionStr;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Version) {
			Version other = (Version)o;
			return compareTo(other) == 0;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
}
