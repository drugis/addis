package org.drugis.addis.entities;

import java.util.Set;

public class CategoricalVariable extends AbstractEntity {
	private static final long serialVersionUID = 8700874872019027607L;
	private String[] d_categories;
	private String d_name;
	
	public CategoricalVariable(String name, String[] categories) {
		d_name = name;
		d_categories = categories;
	}

	public String[] getCategories() {
		return d_categories;
	}

	public String getName() {
		return d_name;
	}

	@Override
	public Set<Entity> getDependencies() {
		return null;
	}

}
