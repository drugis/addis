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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.entities.Endpoint.Type;

public class PooledRateMeasurement extends AbstractEntity implements RateMeasurement {
	private static final long serialVersionUID = 5124815300626704289L;
	private List<RateMeasurement> d_measurements;
	private Integer d_rate;
	private Integer d_size;
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		registerListener();
	}
	
	private class ChildListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(RateMeasurement.PROPERTY_RATE)) {
				triggerRateChange((Integer)evt.getNewValue() - (Integer)evt.getOldValue());
			} else if (evt.getPropertyName().equals(RateMeasurement.PROPERTY_SAMPLESIZE)) {
				triggerSampleSizeChange((Integer)evt.getNewValue() - (Integer)evt.getOldValue());
			} else if (evt.getPropertyName().equals(RateMeasurement.PROPERTY_ENDPOINT)) {
				throw new RuntimeException("Endpoint changed for child measurement");
			}
		}
	};
	
	/**
	 * Construct a pooled measurement from the given list of measurements.
	 * @param measurements List of measurements. 
	 * @throws NullPointerException measurements may not be null.
	 * @throws IllegalArgumentException All measurements should measure the same Endpoint. Empty list not allowed.
	 */
	public PooledRateMeasurement(List<RateMeasurement> measurements) 
	throws IllegalArgumentException, NullPointerException {
		if (measurements == null) {
			throw new NullPointerException("measurements null");
		}
		if (measurements.isEmpty()) {
			throw new IllegalArgumentException("measurements empty");
		}
		if (!measureSameEndpoint(measurements)) {
			throw new IllegalArgumentException("measurements not measuring same endpoint");
		}
		d_measurements = measurements;
		d_rate = calcRate();
		d_size = calcSampleSize();
		registerListener();
	}
	
	private void triggerSampleSizeChange(int delta) {
		Integer oldValue = getSampleSize();
		d_size += delta;
		firePropertyChange(RateMeasurement.PROPERTY_SAMPLESIZE, oldValue, getSampleSize());
		firePropertyChange(RateMeasurement.PROPERTY_LABEL, generateLabel(getRate(), oldValue),
				getLabel());
	}

	private void registerListener() {
		ChildListener listener = new ChildListener();
		for (RateMeasurement m : d_measurements) {
			m.addPropertyChangeListener(listener);
		}
	}

	/**
	 * Post-condition: d_rate equals sum of child rates
	 * @param delta
	 */
	private void triggerRateChange(int delta) {
		Integer oldValue = getRate();
		d_rate += delta;
		firePropertyChange(RateMeasurement.PROPERTY_RATE, oldValue, getRate());
		firePropertyChange(RateMeasurement.PROPERTY_LABEL, generateLabel(oldValue, getSampleSize()),
				getLabel());
	}

	public static boolean measureSameEndpoint(List<RateMeasurement> measurements) {
		if (measurements.isEmpty()) {
			return true;
		}
		RateMeasurement rateMeasurement = measurements.get(0);
		Endpoint expected = rateMeasurement.getEndpoint();
		for (RateMeasurement m : measurements) {
			if (!m.getEndpoint().equals(expected)) {
				return false;
			}
		}
		return true;
	}

	public Endpoint getEndpoint() {
		return d_measurements.get(0).getEndpoint();
	}

	public Integer getRate() {
		return d_rate;
	}

	private Integer calcRate() {
		int rate = 0;
		for (RateMeasurement m : d_measurements) {
			rate += m.getRate();
		}
		return rate;
	}
	
	public Integer getSampleSize() {
		return d_size;
	}

	private Integer calcSampleSize() {
		int size = 0;
		for (RateMeasurement m : d_measurements) {
			size += m.getSampleSize();
		}
		return size;
	}

	public String getLabel() {
		return generateLabel(getRate(), getSampleSize());
	}

	private String generateLabel(Integer rate, Integer sampleSize) {
		return rate.toString() + "/" + sampleSize.toString();
	}
	
	@Override
	public String toString() {
		return generateLabel(d_rate, d_size);
	}
	
	public boolean equals(Object o) {
		if (o instanceof PooledRateMeasurement) {
			PooledRateMeasurement other = (PooledRateMeasurement)o;
			return other.d_measurements.size() == d_measurements.size() &&
				other.d_measurements.containsAll(d_measurements);
		}
		return false;
	}
	
	public int hashCode() {
		return new HashSet<RateMeasurement>(d_measurements).hashCode();
	}

	public boolean isOfType(Type type) {
		return type.equals(Type.RATE);
	}

	@Override
	public Set<Entity> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}
}
