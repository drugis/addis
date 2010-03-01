package org.drugis.addis.entities.metaanalysis;

import org.drugis.addis.entities.Entity;

public interface MetaAnalysis extends Entity, Comparable<MetaAnalysis> {
	public static final String PROPERTY_NAME = "name";
	
	public String getName();
	public void setName(String name);
}
