package org.drugis.addis.entities;

import java.util.ArrayList;
import java.util.List;

public class StudyCharacteristics {
	private static List<Characteristic> s_values;
	
	public static List<Characteristic> values() {
		if (s_values == null) {
			s_values = init();
		}
		return s_values;
	}

	private static List<Characteristic> init() {
		List<Characteristic> values = new ArrayList<Characteristic>();
		for (Characteristic c : BasicStudyCharacteristic.values()) {
			values.add(c);
		}
		for (Characteristic c : DerivedStudyCharacteristic.values()) {
			values.add(c);
		}
		return values;
	}
}
