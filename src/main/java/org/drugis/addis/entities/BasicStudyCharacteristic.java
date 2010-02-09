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
import java.util.Date;

public enum BasicStudyCharacteristic implements Serializable, Characteristic {

	TITLE("Title", String.class),
	ALLOCATION("Group allocation", Allocation.class),
	BLINDING("Blinding", Blinding.class),
	CENTERS("Number of study centers", Integer.class),
	OBJECTIVE("Study Objective", String.class),
	STUDY_START("Study start date", Date.class),
	STUDY_END("Study end date", Date.class),
	STATUS("Study status", Status.class),
	INCLUSION("Inclusion criteria", String.class),
	EXCLUSION("Exclusion criteria", String.class),
	SOURCE("Source of the data", Source.class),
	CREATION_DATE("Date the study was created or extracted", Date.class);

	private String d_description;
	private Class<?> d_type;
	
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
	
	BasicStudyCharacteristic(String name, Class<?> type) {
		d_description = name;
		d_type = type;
	}	

	public String getDescription() {
		return d_description;
	}

	public Class<?> getValueType() {
		return d_type;
	}
	
}
