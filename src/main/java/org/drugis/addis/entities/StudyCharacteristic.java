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

import java.util.Date;
import java.util.SortedSet;

public enum StudyCharacteristic {
		ARMS("Study Arms", ValueType.POSITIVE_INTEGER),
		STUDYSIZE("Total number of subjects", ValueType.POSITIVE_INTEGER),
		DRUGS("Investigational Drugs", ValueType.DRUGS),
		FLEXIBLE_DOSE("Flexible Dose", ValueType.FLEXIBLE_DOSE),
		ALLOCATION("Group allocation", ValueType.ALLOCATION),
		BLINDING("Blinding", ValueType.BLINDING),
		CENTERS("Number of study centers", ValueType.POSITIVE_INTEGER),
		OBJECTIVE("Study Objective", ValueType.TEXT),
		INDICATION("Intended Indication", ValueType.INDICATION),
		STUDY_START("Study start date", ValueType.DATE),
		STUDY_END("Study end date", ValueType.DATE),
		STATUS("Study status", ValueType.STATUS),
		INCLUSION("Inclusion criteria", ValueType.TEXT),
		EXCLUSION("Exclusion criteria", ValueType.TEXT);
		
		public enum ValueType {
			TEXT(String.class),
			POSITIVE_INTEGER(Integer.class),
			DATE(Date.class),
			DRUGS(SortedSet.class),
			FLEXIBLE_DOSE(Boolean.class),
			INDICATION(Indication.class),
			ALLOCATION(Allocation.class),
			BLINDING(Blinding.class),
			STATUS(Status.class);
			
			public final Class<?> valueClass;
			
			ValueType(Class<?> vclass) {
				valueClass = vclass;
			}
			
			public boolean validate(Object value) {
				if (!valueClass.isInstance(value)) {
					return false;
				}
				if (this.equals(POSITIVE_INTEGER)) {
					Integer i = (Integer) value;
					if (i < 1) {
						return false;
					}
				}
				return true;
			}
		}
		
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
		
		private String d_description;
		private ValueType d_valueType;
		
		StudyCharacteristic(String description, ValueType valueType) { 
			d_description = description;
			d_valueType = valueType;
		}
		
		public String getDescription() {
			return d_description;
		}
		
		public ValueType getValueType() {
			return d_valueType;
		}
}
