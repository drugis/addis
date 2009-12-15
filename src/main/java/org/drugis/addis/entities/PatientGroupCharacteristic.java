package org.drugis.addis.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PatientGroupCharacteristic implements Serializable, Characteristic {
	private static final long serialVersionUID = -6603955376389773878L;

	private String d_description;
	private Class<?> d_type;
	
	public static final PatientGroupCharacteristic MALE = addPGChar("Male subjects", Integer.class);
	public static final PatientGroupCharacteristic FEMALE = addPGChar("Female subjects", Integer.class);
	public static final PatientGroupCharacteristic AGE = addPGChar("Age", BasicContinuousMeasurement.class);
	
	protected static List<PatientGroupCharacteristic> s_allCharacteristics;

	public PatientGroupCharacteristic(String description, Class<?> type) {
			d_description = description;
			d_type = type;
	}

	public String getDescription() {
		return d_description;
	}

	private static PatientGroupCharacteristic addPGChar(String name, Class<?> type) {
		if (s_allCharacteristics == null) {
			s_allCharacteristics = new ArrayList<PatientGroupCharacteristic>();
		}
		
		PatientGroupCharacteristic c = new PatientGroupCharacteristic(name, type);
		s_allCharacteristics.add(c);
		return c;
	}

	public Class<?> getValueType() {
		return d_type;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PatientGroupCharacteristic)) {
			return false;
		}
		Characteristic c = (Characteristic) other;
		return getDescription().equals(c.getDescription());
	}
	
	@Override
	public int hashCode() {
		return getDescription().hashCode();
	}

	public static List<PatientGroupCharacteristic> values() {
		return s_allCharacteristics;
	}
	
}
