package org.drugis.addis.entities;

import java.util.Arrays;
import java.util.List;

public class CategoricalPopulationCharacteristic extends AbstractVariable implements PopulationCharacteristic {
	private static final long serialVersionUID = 8700874872019027607L;
	private String[] d_categories;
	
	public static final String PROPERTY_CATEGORIESASLIST = "categoriesAsList";
	
	public CategoricalPopulationCharacteristic() {
		super("", Type.CATEGORICAL);
		d_categories = new String[]{};
	}
	
	public CategoricalPopulationCharacteristic(String name, String[] categories) {
		super(name, Type.CATEGORICAL);
		d_categories = categories;
		d_description = "";
	}
	
	public String[] getCategories() {
		return d_categories;
	}
	
	public void setCategories(String[] categories) {
		d_categories = categories;
	}
	
	public List<String> getCategoriesAsList() {
		return Arrays.asList(d_categories);
	}
	
	public void setCategoriesAsList(List<String> categories) {
		d_categories = (String[]) categories.toArray();
	}
	
	public FrequencyMeasurement buildMeasurement() {
		return new FrequencyMeasurement(this);
	}

	public int compareTo(Variable other) {
		return getName().compareTo(other.getName());
	}

	public FrequencyMeasurement buildMeasurement(int size) {
		FrequencyMeasurement m = new FrequencyMeasurement(this);
		m.setSampleSize(size);
		return m;
	}
	
	@Override
	public String[] getXmlExclusions() {
		return new String[] {"categoriesAsList"};
	}
}
