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

package nl.rug.escher.addis.entities;

import java.util.List;
import java.util.Set;

import com.jgoodies.binding.beans.Observable;

public interface Study extends Comparable<Study>, Observable {
	public final static String PROPERTY_ID = "id";
	public final static String PROPERTY_ENDPOINTS = "endpoints";
	public final static String PROPERTY_PATIENTGROUPS = "patientGroups";

	public String getId();

	public List<Endpoint> getEndpoints();

	public List<? extends PatientGroup> getPatientGroups();

	public Set<Drug> getDrugs();
}