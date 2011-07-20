/**
 * 
 */
package org.drugis.addis.entities;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.drugis.addis.util.EntityUtil;

public class DrugSet extends AbstractEntity implements Comparable<DrugSet> {
	private SortedSet<Drug> d_contents;

	public DrugSet(Collection<Drug> contents) {
		d_contents = new TreeSet<Drug>(contents);
	}
	
	public DrugSet(Drug drug) {
		this(Collections.singleton(drug));
	}
	
	public DrugSet() {
		this(Collections.<Drug>emptySet());
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return getContents();
	}

	public SortedSet<Drug> getContents() {
		return d_contents;
	}
	
	public String getDescription() {
		return StringUtils.join(d_contents, " + ");
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof DrugSet)) {
			return false;
		}
		DrugSet other = (DrugSet) o;
		return other.getContents().equals(getContents());
	}
	
	@Override
	public boolean deepEquals(Entity other) {
		if(!equals(other)) {
			return false;
		}
		DrugSet ds = (DrugSet) other;
		return EntityUtil.deepEqual(getContents(), ds.getContents());
	}
	
	@Override
	public int hashCode() {
		return d_contents.hashCode();
	}

	@Override
	public int compareTo(DrugSet o) {
		Iterator<Drug> i1 = getContents().iterator();
		Iterator<Drug> i2 = o.getContents().iterator();
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
		return "DrugSet" + d_contents;
	}
}