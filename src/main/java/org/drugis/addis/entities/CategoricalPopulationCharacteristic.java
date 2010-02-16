package org.drugis.addis.entities;

public class CategoricalPopulationCharacteristic extends AbstractVariable implements PopulationCharacteristic {
	private static final long serialVersionUID = 8700874872019027607L;
	private String[] d_categories;
	
	public CategoricalPopulationCharacteristic(String name, String[] categories) {
		super(name, Type.CATEGORICAL);
		d_categories = categories;
		d_description = "";
	}

	public String[] getCategories() {
		return d_categories;
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
}
