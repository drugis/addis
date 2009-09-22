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

import java.util.Set;

import com.jgoodies.binding.beans.Model;

public class PooledPatientGroup extends Model implements PatientGroup {
	
	private static final long serialVersionUID = 7548091994878904366L;
	
	private MetaStudy d_study;
	private Drug d_drug;

	public PooledPatientGroup(MetaStudy study, Drug drug) {
		d_study = study;
		d_drug = drug;
	}

	public Dose getDose() {
		return new UnknownDose();
	}

	public Drug getDrug() {
		return d_drug;
	}

	public String getLabel() {
		return "META " + d_drug.toString();
	}

	public Integer getSize() {
		return d_study.getAnalysis().getPooledMeasurement(d_drug).getSampleSize();
	}

	public Study getStudy() {
		return d_study;
	}

	public Set<Entity> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

}
