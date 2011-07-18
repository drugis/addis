/**
 * 
 */
package org.drugis.addis.entities.analysis;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;

public class DrugSet extends AbstractEntity {
	private Set<Drug> d_contents;

	public DrugSet(Collection<Drug> contents) {
		d_contents = new HashSet<Drug>(contents);
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

	public Set<Drug> getContents() {
		return d_contents;
	}
	
	public String getName() {
		return toString(); // FIXME
	}
	
}