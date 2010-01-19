package org.drugis.addis.entities;


public enum DerivedStudyCharacteristic implements Characteristic {

	DOSING("Dosing", Dosing.class),
	DRUGS("Investigational drugs", Object.class),
	STUDYSIZE("Study size", Integer.class),
	ARMS("Study Arms", Integer.class),
	INDICATION("Intended Indication", Indication.class);

	private Class<?> d_type;
	private String d_description;

	DerivedStudyCharacteristic(String description, Class<?> type) {
		d_description = description;
		d_type = type;
	}
	
	public enum Dosing {
		FIXED,
		FLEXIBLE,
		MIXED
	}

	public String getDescription() {
		return d_description;
	}

	public Class<?> getValueType() {
		return d_type;
	}	
}
