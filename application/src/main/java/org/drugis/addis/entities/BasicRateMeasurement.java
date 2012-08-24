/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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

import org.drugis.common.EqualsUtil;



public class BasicRateMeasurement extends BasicMeasurement implements RateMeasurement {
	
	private Integer d_rate;
	
	public BasicRateMeasurement() {
		super(0);
	}
	
	public BasicRateMeasurement(Integer rate, Integer size) {
		super(size);
		d_rate = rate;
	}

	public String getLabel() {
		return toString();
	}

	@Override
	public String toString() {
		return (d_rate == null ? "N/A" : d_rate.toString()) + " / " + (getSampleSize() == null ? "N/A" : getSampleSize().toString());
	}
	
	public void setRate(Integer rate) {
		Integer oldVal = d_rate;
		d_rate = rate;
		firePropertyChange(PROPERTY_RATE, oldVal, d_rate);
	}

	public Integer getRate() {
		return d_rate;
	}
	
	public boolean isOfType(VariableType type) {
		return type instanceof RateVariableType;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof BasicRateMeasurement) {
			BasicRateMeasurement other = (BasicRateMeasurement) o;
			return EqualsUtil.equal(d_sampleSize, other.d_sampleSize) &&
				EqualsUtil.equal(d_rate, other.d_rate);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 31 * hash(d_sampleSize) + hash(d_rate);
	}

	private int hash(Object x) {
		return x == null ? 0 : x.hashCode();
	}
	@Override
	public BasicMeasurement clone() {
		return new BasicRateMeasurement(d_rate, d_sampleSize);
	}
	
	@Override
	public boolean isComplete() {
		return super.isComplete() && d_rate != null && d_rate > -1 && d_rate <= d_sampleSize;
	}
	
}