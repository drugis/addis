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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nl.rug.escher.addis.entities.Endpoint.Type;

public class BasicRateMeasurement extends BasicMeasurement implements RateMeasurement {
	private static final long serialVersionUID = -1004559723622385992L;
	private Integer d_rate;
	private transient SampleSizeListener d_listener = new SampleSizeListener();
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		d_listener = new SampleSizeListener();
		addPropertyChangeListener(PROPERTY_SAMPLESIZE, d_listener);
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}
	
	public BasicRateMeasurement() {
		addPropertyChangeListener(PROPERTY_SAMPLESIZE, d_listener);
	}
	
	public BasicRateMeasurement(Endpoint e) {
		this(e, 0);
	}
	
	public BasicRateMeasurement(Endpoint e, int rate) {
		super(e);
		addPropertyChangeListener(PROPERTY_SAMPLESIZE, d_listener);
		d_rate = rate;
	}

	public String getLabel() {
		return toString();
	}

	@Override
	public String toString() {
		return generateLabel(getSampleSize());
	}
	
	private String generateLabel(Integer size) {
		if (d_rate == null || size == null) {
			return "INCOMPLETE";
		}
		return d_rate.toString() + "/" + size.toString();
	}

	public void setRate(Integer rate) {
		String oldLabel = getLabel();
		Integer oldVal = d_rate;
		d_rate = rate;
		firePropertyChange(PROPERTY_RATE, oldVal, d_rate);
		firePropertyChange(PROPERTY_LABEL, oldLabel, getLabel());
	}

	public Integer getRate() {
		return d_rate;
	}
	
	private class SampleSizeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals(Measurement.PROPERTY_SAMPLESIZE)) {
				Integer oldSize = (Integer)event.getOldValue();
				Integer newSize = (Integer)event.getNewValue();
				firePropertyChange(Measurement.PROPERTY_LABEL, 
						generateLabel(oldSize), generateLabel(newSize));
			}
		}	
	}

	public boolean isOfType(Type type) {
		return type.equals(Type.RATE);
	}
}