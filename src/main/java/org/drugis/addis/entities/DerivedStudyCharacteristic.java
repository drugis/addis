package org.drugis.addis.entities;


public enum DerivedStudyCharacteristic implements Characteristic {

	STUDYSIZE("Study size", Integer.class, true),
	INDICATION("Intended Indication", Indication.class, true),
	DRUGS("Investigational drugs", Object.class, true),
	DOSING("Dosing", Dosing.class, true),
	ARMS("Study Arms", Integer.class, false);

	private Class<?> d_type;
	private String d_description;
	private boolean d_defaultVisible;

	DerivedStudyCharacteristic(String description, Class<?> type, boolean defaultVisible) {
		d_description = description;
		d_type = type;
		d_defaultVisible = defaultVisible;
	}
	
	public enum Dosing {
		FIXED("Fixed"),
		FLEXIBLE("Flexible"),
		MIXED("Mixed");
		
		private String d_title;

		Dosing(String title) {
			d_title = title;
		}
		
		@Override
		public String toString() {
			return d_title;
		}
	}

	public String getDescription() {
		return d_description;
	}

	public Class<?> getValueType() {
		return d_type;
	}	
	
	public boolean getDefaultVisible() {
		return d_defaultVisible;
	}
}
