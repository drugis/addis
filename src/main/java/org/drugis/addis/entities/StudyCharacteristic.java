/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudyCharacteristic implements Serializable, Characteristic {
	
	private static final long serialVersionUID = 2363977004179177407L;
	
	private String d_description;
	private Class<?> d_type;
	
	public static final StudyCharacteristic ALLOCATION = addStudyChar("Group allocation", Allocation.class);
	public static final StudyCharacteristic BLINDING = addStudyChar("Blinding", Blinding.class);
	public static final StudyCharacteristic CENTERS = addStudyChar("Number of study centers", Integer.class);
	public static final StudyCharacteristic OBJECTIVE = addStudyChar("Study Objective", String.class);
	public static final StudyCharacteristic INDICATION = addStudyChar("Intended Indication", Indication.class);
	public static final StudyCharacteristic STUDY_START = addStudyChar("Study start date", Date.class);
	public static final StudyCharacteristic STUDY_END = addStudyChar("Study end date", Date.class);
	public static final StudyCharacteristic STATUS = addStudyChar("Study status", Status.class);
	public static final StudyCharacteristic INCLUSION = addStudyChar("Inclusion criteria", String.class);
	public static final StudyCharacteristic EXCLUSION = addStudyChar("Exclusion criteria", String.class);
		
	public enum Allocation {
		RANDOMIZED,
		NONRANDOMIZED
	}
	
	public enum Blinding {
		OPEN,
		SINGLE_BLIND,
		DOUBLE_BLIND,
		TRIPLE_BLIND
	}
	
	public enum Status {
		RECRUITING,
		ONGOING,
		FINISHED
	}
	
	protected StudyCharacteristic(String name, Class<?> type) {
		d_description = name;
		d_type = type;
	}
		
	protected static List<StudyCharacteristic> s_allCharacteristics;
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof StudyCharacteristic)) {
			return false;
		}
		Characteristic c = (Characteristic) other;
		return getDescription().equals(c.getDescription());
	}
	
	@Override
	public int hashCode() {
		return getDescription().hashCode();
	}
	
	public static List<StudyCharacteristic> values() {
		return s_allCharacteristics;
	}
	
	protected static StudyCharacteristic addStudyChar(String name, Class<?> type) {
		if (s_allCharacteristics == null) {
			s_allCharacteristics = new ArrayList<StudyCharacteristic>();
		}
		
		StudyCharacteristic c = new StudyCharacteristic(name, type);
		s_allCharacteristics.add(c);
		return c;
	}
	

	public String getDescription() {
		return d_description;
	}

	public Class<?> getValueType() {
		return d_type;
	}
	
}
