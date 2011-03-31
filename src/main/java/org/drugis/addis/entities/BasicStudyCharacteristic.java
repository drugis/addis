/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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


import org.drugis.addis.util.EnumXMLFormat;

public enum BasicStudyCharacteristic implements Serializable, Characteristic {

	TITLE("Title", String.class, true, null),
	ALLOCATION("Group allocation", Allocation.class, true, Allocation.UNKNOWN),
	BLINDING("Blinding", Blinding.class, true, Blinding.UNKNOWN),
	CENTERS("Number of study centers", Integer.class, false, null),
	OBJECTIVE("Study Objective", String.class, false, null),
	STUDY_START("Study start date", Date.class, false, null),
	STUDY_END("Study end date", Date.class, false, null),
	STATUS("Study status", Status.class, false, Status.UNKNOWN),
	INCLUSION("Inclusion criteria", String.class, false, null),
	EXCLUSION("Exclusion criteria", String.class, false, null),
	PUBMED("Pubmed ID", PubMedIdList.class, false, new PubMedIdList()),
	SOURCE("Source of the data", Source.class, false, Source.MANUAL),
	CREATION_DATE("Creation/extraction date", Date.class, false, null);

	private String d_description;
	private Class<?> d_type;
	private boolean d_defaultVisible;
	private Object d_defaultValue;
	static EnumXMLFormat<BasicStudyCharacteristic> XML = new EnumXMLFormat<BasicStudyCharacteristic>(BasicStudyCharacteristic.class);
	
	public enum Allocation {
		RANDOMIZED("Randomized"),
		NONRANDOMIZED("Non-randomized"),
		UNKNOWN("Unknown");
		
		private String d_title;
		static EnumXMLFormat<Allocation> XML = new EnumXMLFormat<Allocation>(Allocation.class);

		Allocation(String title) {
			d_title = title;
		}
		
		@Override
		public String toString() {
			return d_title;
		}
	}
	
	public enum Blinding {
		OPEN("Open"),
		SINGLE_BLIND("Single blind"),
		DOUBLE_BLIND("Double blind"),
		TRIPLE_BLIND("Triple blind"),
		UNKNOWN("Unknown");
		
		Blinding(String title) {
			d_title = title;
		}
		
		private String d_title;
		static EnumXMLFormat<Blinding> XML = new EnumXMLFormat<Blinding>(Blinding.class);
		
		@Override
		public String toString() {
			return d_title;
		}
	}
	
	public enum Status {
		NOT_YET_RECRUITING("Not yet recruiting"),
		RECRUITING("Recruiting"),
		ENROLLING("Enrolling"),
		ACTIVE("Active"),
		COMPLETED("Completed"),
		SUSPENDED("Suspended"),
		TERMINATED("Terminated"),
		WITHDRAWN("Withdrawn"),
		UNKNOWN("Unknown");
		
		private String d_title;
		static EnumXMLFormat<Status> XML = new EnumXMLFormat<Status>(Status.class);

		Status(String title) {
			d_title = title;
		}
		
		@Override
		public String toString() {
			return d_title;
		}		
	}
	
	BasicStudyCharacteristic(String name, Class<?> type, boolean defaultVisible, Object defaultValue) {
		d_description = name;
		d_type = type;
		d_defaultVisible = defaultVisible;
		d_defaultValue = defaultValue;
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
	
	public Object getDefaultValue() {
		return d_defaultValue;
	}
}
