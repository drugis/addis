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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.util.EntityUtil;

public class TreatmentCategorySet extends AbstractEntity implements Comparable<TreatmentCategorySet> {
	public static TreatmentCategorySet createTrivial(Collection<Drug> drugs) {
		Set<Category> categories = new HashSet<Category>();
		for (Drug d : drugs) {
			categories.add(Category.createTrivial(d));
		}
		return new TreatmentCategorySet(categories);
	}

	public static TreatmentCategorySet createTrivial(Drug drug) {
		return createTrivial(Collections.singleton(drug));
	}

	private SortedSet<Category> d_contents;
	
	public TreatmentCategorySet(Category category) {
		this(Collections.singleton(category));
	}
	
	public TreatmentCategorySet(Collection<Category> contents) {
		d_contents = new TreeSet<Category>(contents);
	}
	
	public TreatmentCategorySet() {
		this(Collections.<Category>emptySet());
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return getContents();
	}

	public SortedSet<Category> getContents() {
		return d_contents;
	}
	
	public String getLabel() {
		List<String> labels = new ArrayList<String>();
		for (Category cat : d_contents) {
			labels.add(cat.getLabel());
		}
		return StringUtils.join(labels, " + ");
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof TreatmentCategorySet)) {
			return false;
		}
		TreatmentCategorySet other = (TreatmentCategorySet) o;
		return other.getContents().equals(getContents());
	}
	
	@Override
	public boolean deepEquals(Entity other) {
		if(!equals(other)) {
			return false;
		}
		TreatmentCategorySet ds = (TreatmentCategorySet) other;
		return EntityUtil.deepEqual(getContents(), ds.getContents());
	}
	
	@Override
	public int hashCode() {
		return d_contents.hashCode();
	}

	@Override
	public int compareTo(TreatmentCategorySet o) {
		Iterator<Category> i1 = getContents().iterator();
		Iterator<Category> i2 = o.getContents().iterator();
		while (i1.hasNext() && i2.hasNext()) {
			int compVal = i1.next().compareTo(i2.next());
			if (compVal != 0) {
				return compVal;
			}
		}
		if (i1.hasNext()) {
			return 1;
		}
		if (i2.hasNext()) {
			return -1;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return "TreatmentCategorySet" + d_contents;
	}
}