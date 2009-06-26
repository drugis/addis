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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.jgoodies.binding.beans.Model;

public abstract class AbstractStudy extends Model implements Study {
	private static final long serialVersionUID = -845477477003790845L;
	
	private String d_id;
	protected Map<MeasurementKey, Measurement> d_measurements
		= new HashMap<MeasurementKey, Measurement>();	

	public AbstractStudy(String id) {
		d_id = id;
	}	

	public String getId() {
		return d_id;
	}

	public void setId(String id) {
		String oldVal = d_id;
		d_id = id;
		firePropertyChange(PROPERTY_ID, oldVal, d_id);
	}

	@Override
	public String toString() {
		return getId();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Study) {
			Study other = (Study)o;
			if (other.getId() == null) {
				return getId() == null;
			}
			return other.getId().equals(getId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}

	public int compareTo(Study other) {
		return getId().compareTo(other.getId());
	}
	
	public Measurement getMeasurement(Endpoint e, PatientGroup g) {
		forceLegalArguments(e, g);
		Measurement measurement = d_measurements.get(new MeasurementKey(e, g));
		if (measurement == null) {
			throw new IllegalStateException("measurement null - shouldn't be!");
		}
		return measurement;
	}
	
	protected void forceLegalArguments(Endpoint e, PatientGroup g) {
		if (!getPatientGroups().contains(g)) {
			throw new IllegalArgumentException("PatientGroup " + g + " not part of this study.");
		}
		if (!getEndpoints().contains(e)) {
			throw new IllegalArgumentException("Endpoint " + e + " not measured by this study.");
		}
	}	

	protected static class MeasurementKey implements Serializable {
		private static final long serialVersionUID = 6310789667384578005L;
		private Endpoint d_endpoint;
		private PatientGroup d_patientGroup;
		
		public MeasurementKey(Endpoint e, PatientGroup g) {
			d_endpoint = e;
			d_patientGroup = g;
		}
		
		public boolean equals(Object o) {
			if (o instanceof MeasurementKey) { 
				MeasurementKey other = (MeasurementKey)o;
				return d_endpoint.equals(other.d_endpoint) && d_patientGroup.equals(other.d_patientGroup);
			}
			return false;
		}
		
		public int hashCode() {
			int code = 1;
			code = code * 31 + d_endpoint.hashCode();
			code = code * 31 + d_patientGroup.hashCode();
			return code;
		}
	}
}