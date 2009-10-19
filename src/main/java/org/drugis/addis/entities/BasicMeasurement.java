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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Set;

public abstract class BasicMeasurement extends AbstractEntity implements Measurement {
	private static final long serialVersionUID = 6892934487858770855L;
	private Endpoint d_endpoint;
	private PatientGroup d_patientGroup;
	
	public static final String PROPERTY_PATIENTGROUP = "patientGroup";
	
	public PatientGroup getPatientGroup() {
		return d_patientGroup;
	}

	public BasicMeasurement(Endpoint e, PatientGroup p) {
		d_endpoint = e;
		d_patientGroup = p;
		d_patientGroup.addPropertyChangeListener(new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getPropertyName().equals(PatientGroup.PROPERTY_SIZE)){
					firePropertyChange(PROPERTY_SAMPLESIZE, event.getOldValue(), event.getNewValue());
				}
			}
		});
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
		return d_patientGroup.getSize();
	}
	
	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
}