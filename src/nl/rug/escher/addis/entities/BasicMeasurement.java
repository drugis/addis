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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import nl.rug.escher.common.EqualsUtil;

import com.jgoodies.binding.beans.Model;

public abstract class BasicMeasurement extends Model implements Measurement {
	private static final long serialVersionUID = 6892934487858770855L;
	private BasicPatientGroup d_patientGroup;
	private Endpoint d_endpoint;
	private PatientGroupListener d_listener = new PatientGroupListener();

	public static final String PROPERTY_PATIENTGROUP = "patientGroup";
	
	protected BasicMeasurement() {
	}
	
	public BasicMeasurement(Endpoint e) {
		d_endpoint = e;
	}

	public BasicPatientGroup getPatientGroup() {
		return d_patientGroup;
	}

	private class PatientGroupListener implements PropertyChangeListener, Serializable {
		private static final long serialVersionUID = -443447362262064055L;

		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals(BasicPatientGroup.PROPERTY_SIZE)) {
				Integer oldSize = (Integer)event.getOldValue();
				Integer newSize = (Integer)event.getNewValue();
				firePropertyChange(PROPERTY_SAMPLESIZE, oldSize, newSize);
			}
		}
	}
	
	public void setPatientGroup(BasicPatientGroup g) {
		if (getPatientGroup() != null) {
			getPatientGroup().removePropertyChangeListener(BasicPatientGroup.PROPERTY_SIZE, d_listener);
		}
		Integer oldSize = getSampleSize();
		g.addPropertyChangeListener(BasicPatientGroup.PROPERTY_SIZE, d_listener);
		BasicPatientGroup oldVal = d_patientGroup;
		d_patientGroup = g;
		firePropertyChange(PROPERTY_PATIENTGROUP, oldVal, d_patientGroup);
		Integer newSize = getSampleSize();
		if ((oldSize == null && newSize != null) || (oldSize != null && !oldSize.equals(newSize))) {
			firePropertyChange(PROPERTY_SAMPLESIZE, oldSize, newSize);
		}
	}

	public Endpoint getEndpoint() {
		return d_endpoint;
	}

	public void setEndpoint(Endpoint endpoint) {
		Endpoint oldVal = d_endpoint;
		d_endpoint = endpoint;
		firePropertyChange(PROPERTY_ENDPOINT, oldVal, d_endpoint);
	}

	public Integer getSampleSize() {
		if (getPatientGroup() == null) {
			return null;
		}
		return getPatientGroup().getSize();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof BasicMeasurement) {
			BasicMeasurement other = (BasicMeasurement)o;
			return EqualsUtil.equal(other.getEndpoint(), getEndpoint()) &&
				EqualsUtil.equal(other.getPatientGroup(), getPatientGroup());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + getEndpoint().hashCode();
		hash = hash * 31 + getPatientGroup().hashCode();
		return hash;
	}
}