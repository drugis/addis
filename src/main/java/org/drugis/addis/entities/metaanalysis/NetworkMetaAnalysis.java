package org.drugis.addis.entities.metaanalysis;

import java.util.Collections;
import java.util.Set;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Entity;

public class NetworkMetaAnalysis extends AbstractEntity implements MetaAnalysis {
	private static final long serialVersionUID = 8649933965921651708L;
	private String d_name;
	
	public NetworkMetaAnalysis(String name) {
		d_name = name;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}

	public String getName() {
		return d_name;
	}

	public void setName(String newValue) {
		String oldValue = d_name;
		d_name = newValue;
		firePropertyChange(PROPERTY_NAME, oldValue, newValue);
	}
	
	public String toString(){
		return d_name;
	}

	public int compareTo(MetaAnalysis arg0) {
		return d_name.compareTo(arg0.getName());
	}
}
