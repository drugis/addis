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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.drugis.addis.util.EntityUtil;
import org.drugis.common.EqualsUtil;

public class Arm extends AbstractEntity implements TypeWithNotes {
	public static Arm createArm(Study study, String name, Integer size,
			Drug drug, AbstractDose dose) {
		Arm arm = new Arm(name, size);
		study.addArm(arm);
		StudyActivity studyActivity = new StudyActivity(name + " treatment", new TreatmentActivity(drug, dose));
		study.getStudyActivities().add(studyActivity);
		if (study.getEpochs().isEmpty()) {
			study.getEpochs().add(new Epoch("Main phase", null));
		}
		Epoch epoch = study.getEpochs().get(0);
		study.setStudyActivityAt(arm, epoch, studyActivity);
		return arm;
	}

	private String d_name;
	private Integer d_size;
	private List<Note> d_notes = new ArrayList<Note>();
	private TreatmentActivity d_activity = new TreatmentActivity(null, null);
	
	public static final String PROPERTY_SIZE = "size";
	public static final String PROPERTY_NAME = "name";

	public Arm(String name, int size) {
		d_name = name;
		d_size = size;
	}

	@Deprecated
	public TreatmentActivity getTreatmentActivity() {
		return d_activity;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public Integer getSize() {
		return d_size;
	}

	public void setSize(Integer size) {
		Integer oldVal = d_size;
		d_size = size;
		firePropertyChange(PROPERTY_SIZE, oldVal, d_size);
	}
	
	public String getName() {
		return d_name;
	}

	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}
	
	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	@Override
	public Arm clone() {
		Arm arm = new Arm(getName(), getSize());
		arm.getNotes().addAll(getNotes());
		arm.d_activity = d_activity.clone();
		return arm;
	}

	public List<Note> getNotes() {
		return d_notes;
	}

	@Override
	public boolean deepEquals(Entity obj) {
		if (!equals(obj)) return false;
		Arm other = (Arm) obj;
		return EqualsUtil.equal(other.getSize(), getSize()) && EntityUtil.deepEqual(other.getNotes(), getNotes());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof Arm) {
			Arm other = (Arm) obj;
			return EqualsUtil.equal(other.getName(), getName());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (getName() != null ? getName().hashCode() : 0);
	}
}
