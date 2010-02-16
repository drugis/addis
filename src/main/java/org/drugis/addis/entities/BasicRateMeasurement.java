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


public class BasicRateMeasurement extends BasicMeasurement implements RateMeasurement {
	private static final long serialVersionUID = -1004559723622385992L;
	
	private Integer d_rate;
	
	public BasicRateMeasurement(int rate, int size) {
		super(size);
		d_rate = rate;
	}

	public String getLabel() {
		return toString();
	}

	@Override
	public String toString() {
		if (d_rate == null || getSampleSize() == null) {
			return "INCOMPLETE";
		}
		return d_rate.toString() + "/" + getSampleSize().toString();
	}
	
	public void setRate(Integer rate) {
		Integer oldVal = d_rate;
		d_rate = rate;
		firePropertyChange(PROPERTY_RATE, oldVal, d_rate);
	}

	public Integer getRate() {
		return d_rate;
	}
	
	public boolean isOfType(Variable.Type type) {
		return type.equals(Variable.Type.RATE);
	}
}