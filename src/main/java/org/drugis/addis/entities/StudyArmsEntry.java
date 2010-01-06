package org.drugis.addis.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StudyArmsEntry implements Serializable {
	private static final long serialVersionUID = -8054742152791933411L;

	private Study d_study;
	private Arm d_base;
	private Arm d_subject;

	public StudyArmsEntry(Study study, Arm base, Arm subject){
		d_study   = study;
		d_base    = base;
		d_subject = subject;
		checkArms();
	}
	
	private void checkArms() throws IllegalArgumentException {
		if (!d_study.getArms().contains(d_base)) {
			throw new IllegalArgumentException("first arm not associated with study.");
		}
		if (!d_study.getArms().contains(d_subject)) {
			throw new IllegalArgumentException("second arm not associated with study.");
		}
	}

	public Study getStudy() {
		return d_study;
	}

	public Arm getBase() {
		return d_base;
	}

	public Arm getSubject() {
		return d_subject;
	}
	
	public static List<Study> getStudyList(List<StudyArmsEntry> studyArms) {
		ArrayList<Study> studyList = new ArrayList<Study>();
		for (StudyArmsEntry curStudyArmEntry : studyArms) {
			studyList.add(curStudyArmEntry.getStudy());
		}
		
		return studyList;
	}
}
