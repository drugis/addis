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

import org.drugis.addis.entities.Endpoint.Type;

public class BasicRateMeasurement extends BasicMeasurement implements RateMeasurement {
	private static final long serialVersionUID = -1004559723622385992L;
	private Integer d_rate;
	private Integer d_sampleSize;
	
	public BasicRateMeasurement(Endpoint e, int rate, PatientGroup p) {
		super(e, p);
		d_rate = rate;
		d_sampleSize = p.getSize() != null ? p.getSize() : 0;
	}
	
	public BasicRateMeasurement(Endpoint e, PatientGroup p) {
		this(e, 0, p);
	}	

	public String getLabel() {
		return toString();
	}

	@Override
	public String toString() {
		Integer size = getPatientGroup().getSize();
		if (d_rate == null || size == null) {
			return "INCOMPLETE";
		}
		return d_rate.toString() + "/" + size.toString();
	}
	
	public void setRate(Integer rate) {
		Integer oldVal = d_rate;
		d_rate = rate;
		firePropertyChange(PROPERTY_RATE, oldVal, d_rate);
	}

	public Integer getRate() {
		return d_rate;
	}
	
	public void setSampleSize(Integer size) {
		Integer oldVal = d_sampleSize;
		d_sampleSize = size;
		firePropertyChange(PROPERTY_SAMPLESIZE, oldVal, d_sampleSize);
	}
	
	public Integer getSampleSize() {
		return d_sampleSize;
	}

	public boolean isOfType(Type type) {
		return type.equals(Type.RATE);
	}
}