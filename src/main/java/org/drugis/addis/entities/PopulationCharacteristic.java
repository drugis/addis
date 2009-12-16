package org.drugis.addis.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PopulationCharacteristic implements Serializable, Characteristic {
	private static final long serialVersionUID = -6603955376389773878L;

	private String d_description;
	private Class<?> d_type;
	
	public static final PopulationCharacteristic MALE = addPGChar("Male subjects", Integer.class);
	public static final PopulationCharacteristic FEMALE = addPGChar("Female subjects", Integer.class);
	public static final PopulationCharacteristic AGE = addPGChar("Age", BasicContinuousMeasurement.class);
	
	protected static List<PopulationCharacteristic> s_allCharacteristics;

	public PopulationCharacteristic(String description, Class<?> type) {
			d_description = description;
			d_type = type;
	}

	public String getDescription() {
		return d_description;
	}

	private static PopulationCharacteristic addPGChar(String name, Class<?> type) {
		if (s_allCharacteristics == null) {
			s_allCharacteristics = new ArrayList<PopulationCharacteristic>();
		}
		
		PopulationCharacteristic c = new PopulationCharacteristic(name, type);
		s_allCharacteristics.add(c);
		return c;
	}

	public Class<?> getValueType() {
		return d_type;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PopulationCharacteristic)) {
			return false;
		}
		Characteristic c = (Characteristic) other;
		return getDescription().equals(c.getDescription());
	}
	
	@Override
	public int hashCode() {
		return getDescription().hashCode();
	}

	public static List<PopulationCharacteristic> values() {
		return s_allCharacteristics;
	}
	
}
