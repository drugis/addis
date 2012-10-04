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

package org.drugis.addis.entities.treatment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.util.EntityUtil;

public class TreatmentDefinition extends AbstractEntity implements Comparable<TreatmentDefinition> {
	public static TreatmentDefinition createTrivial(Collection<Drug> drugs) {
		Set<Category> categories = new HashSet<Category>();
		for (Drug d : drugs) {
			categories.add(Category.createTrivial(d));
		}
		return new TreatmentDefinition(categories);
	}

	public static TreatmentDefinition createTrivial(Drug drug) {
		return createTrivial(Collections.singleton(drug));
	}

	private SortedSet<Category> d_contents;
	
	public TreatmentDefinition(Category category) {
		this(Collections.singleton(category));
	}
	
	public TreatmentDefinition(Collection<Category> contents) {
		d_contents = new TreeSet<Category>(contents);
	}
	
	public TreatmentDefinition() {
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
		if (o == null || !(o instanceof TreatmentDefinition)) {
			return false;
		}
		TreatmentDefinition other = (TreatmentDefinition) o;
		return other.getContents().equals(getContents());
	}
	
	@Override
	public boolean deepEquals(Entity o) {
		if(!equals(o)) {
			return false;
		}
		TreatmentDefinition other = (TreatmentDefinition) o;
		return EntityUtil.deepEqual(getContents(), other.getContents());
	}
	
	@Override
	public int hashCode() {
		return d_contents.hashCode();
	}

	@Override
	public int compareTo(TreatmentDefinition o) {
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
		return "TreatmentDefinition " + d_contents;
	}

	/**
	 * Determine whether the given TreatmentActivity matches this TreatmentDefinition.
	 * The {@link TreatmentActivity} must contain exactly the set of drugs for which we have categories.
	 * Moreover, each {@link DrugTreatment} must be accepted by the corresponding {@link Category}.
	 */
	public boolean match(TreatmentActivity act) {
		if (act == null) {
			return false;
		}
		Map<Drug, Category> toMatch = new HashMap<Drug, Category>();
		for (Category cat : getContents()) {
			toMatch.put(cat.getDrug(), cat);
		}
		
		for (DrugTreatment t : act.getTreatments()) {
			Category cat = toMatch.get(t.getDrug());
			if (cat == null || !cat.match(t)) {
				return false;
			}
			toMatch.remove(t.getDrug());
		}
		
		return toMatch.isEmpty();
	}
	
	/**
	 * Determine whether the {@link TreatmentActivity} in the default epoch of
	 * the given ({@link Study}, {@link Arm}) match this TreatmentDefinition.
	 * @see {@link #match(TreatmentActivity)}
	 */
	public boolean match(Study study, Arm arm) {
		return match(study.getTreatment(arm));
	}
}