/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.entities;

import java.util.ArrayList;
import java.util.List;

public class StudyArmsEntry {

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
