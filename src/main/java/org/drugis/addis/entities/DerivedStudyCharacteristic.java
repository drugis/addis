package org.drugis.addis.entities;


public class DerivedStudyCharacteristic extends StudyCharacteristic {

	private static final long serialVersionUID = 4902739766450418848L;
	
	protected DerivedStudyCharacteristic(String name, Class<?> type) {
		super(name, type);
	}
	
	protected static DerivedStudyCharacteristic addDerivedStudyChar(String name, Class<?> type) {
		DerivedStudyCharacteristic c = new DerivedStudyCharacteristic(name, type);
		s_allCharacteristics.add(c);
		return c;
	}
	
	public enum Dosing {
		FIXED,
		FLEXIBLE,
		MIXED
	}
	
	public static final DerivedStudyCharacteristic DOSING = addDerivedStudyChar("Dosing", Dosing.class);
	public static final DerivedStudyCharacteristic DRUGS = addDerivedStudyChar("Investigational drugs", Object.class);
	public static final DerivedStudyCharacteristic STUDYSIZE = addDerivedStudyChar("Study size", Integer.class);
}
