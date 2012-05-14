/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.TypeWithDuration;
import org.drugis.common.beans.AbstractObservable;

public class DurationPresentation<T extends TypeWithDuration> extends AbstractObservable {
	public static class Data {
		public DateUnits units;
		public final int quantity;

		public Data(DateUnits units, int quantity) {
			this.units = units;
			this.quantity = quantity;
		}
		
		public Duration getDuration() {
			return generateDuration(units.asDurationString(quantity));
		}
		
		public DateUnits getDateunits() {
			return units;
		}
		
		@Override
		public String toString() {
			return (quantity == 1 ? units.getSingular() : quantity + " " + units.toString()).toLowerCase();
		}
	}

	public static final String PROPERTY_DEFINED = "defined";
	public static final String PROPERTY_DURATION_QUANTITY = "quantity";
	public static final String PROPERTY_DURATION_UNITS = "units";
	public static final String PROPERTY_LABEL = "label";
	
	private static final Data DEFAULT_DATA = new Data(DateUnits.Weeks, 0);
	
	private final T d_bean;
	private Data d_data = DEFAULT_DATA;

	public static enum DateUnits {
		Months,
		Weeks,
		Days,
		Hours,
		Minutes,
		Seconds;

		/**
		 * Convert a quantity of this unit into a XML Duration string.
		 * @param q The quantity
		 * @return The corresponding duration string.
		 */
		public String asDurationString(int q) {
			switch (this) {
				case Seconds: return "PT" + q + "S";
				case Minutes: return "PT" + q + "M";
				case Hours: return "PT" + q + "H";
				case Days: return "P" + q + "D";
				case Weeks: return "P" + (q * 7) + "D";
				case Months: return "P" + q + "M";
				default: return null;
			}
		}
		
		public String getSingular() {
			return toString().substring(0, this.toString().length() - 1);
		}
	}
	
	/**
	 * Create a presentation model for the duration in epoch.
	 * @param bean
	 */
	public DurationPresentation(T bean) {
		d_bean = bean;
		d_bean.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals(Epoch.PROPERTY_DURATION)) {
					Data oldData = d_data;
					d_data = parseDuration();
					if (oldData != null && d_data == null) {
						firePropertyChange(PROPERTY_DEFINED, true, false);
						firePropertyChange(PROPERTY_DURATION_QUANTITY, oldData.quantity, DEFAULT_DATA.quantity);
						firePropertyChange(PROPERTY_DURATION_UNITS, oldData.units, DEFAULT_DATA.units);
					} else if (oldData != null) {
						if (oldData.quantity != d_data.quantity) {
							firePropertyChange(PROPERTY_DURATION_QUANTITY, oldData.quantity, d_data.quantity);
						}
						if (oldData.units != d_data.units) {
							firePropertyChange(PROPERTY_DURATION_UNITS, oldData.units, d_data.units);
						}
					} else if (d_data != null) {
						firePropertyChange(PROPERTY_DEFINED, false, true);
						firePropertyChange(PROPERTY_DURATION_QUANTITY, DEFAULT_DATA.quantity, d_data.quantity);
						firePropertyChange(PROPERTY_DURATION_UNITS, DEFAULT_DATA.units, d_data.units);
					}
				}
			}
		});
		addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!evt.getPropertyName().equals(PROPERTY_LABEL)) {
					firePropertyChange(PROPERTY_LABEL, null, getLabel());
				}
			}
		});
		d_data = parseDuration();
	}

	public static Data parseDuration(Duration duration, DateUnits prevUnits) {
		if (duration == null) {
			return null;
		} else if (duration.isSet(DatatypeConstants.SECONDS)) {
			return new Data(DateUnits.Seconds, duration.getSeconds());
		} else if (duration.isSet(DatatypeConstants.MINUTES)) {
			return new Data(DateUnits.Minutes, duration.getMinutes());
		} else if (duration.isSet(DatatypeConstants.HOURS)) {
			return new Data(DateUnits.Hours, duration.getHours());
		} else if (duration.isSet(DatatypeConstants.DAYS)) {
			if(duration.getDays() % 7 == 0 && prevUnits != DateUnits.Days) {
				return new Data(DateUnits.Weeks, duration.getDays() / 7);
			} else {
				return new Data(DateUnits.Days, duration.getDays());
			}
		} else if (duration.isSet(DatatypeConstants.MONTHS)) {
			return new Data(DateUnits.Months, duration.getMonths());
		} else {
			throw new RuntimeException("Unhandled Duration: " + duration);
		}
	}
	
	private Data parseDuration() {
		if(d_data == null) { 
			d_data = new Data(DateUnits.Weeks, 0);
		}
		return parseDuration(d_bean.getDuration(), d_data.units);
	}
	
	public DateUnits getUnits() {
		d_data = parseDuration();
		return d_data == null ? DEFAULT_DATA.units : d_data.units;
	}
	
	public void setUnits(DateUnits units) {
		if (getDefined()) {
			DateUnits oldValue = d_data.units;
			d_data = new Data(units, d_data.quantity);
			d_bean.setDuration(d_data.getDuration());
			firePropertyChange(PROPERTY_DURATION_UNITS, oldValue, units);
		}
	}

	public int getQuantity() {
		d_data = parseDuration();
		return d_data == null ? DEFAULT_DATA.quantity : d_data.quantity;
	}
	
	public void setQuantity(int quantity) {
		if (getDefined()) {
			int oldValue = d_data.quantity;
			d_data = new Data(d_data.units, quantity);
			d_bean.setDuration(d_data.getDuration());
			firePropertyChange(PROPERTY_DURATION_QUANTITY, oldValue, quantity);
		}
	}
	
	public boolean getDefined() {
		return d_bean.getDuration() != null;
	}
	
	public void setDefined(boolean defined) {
		if (defined) {
			if (!getDefined()) {
				d_bean.setDuration(DEFAULT_DATA.getDuration());
			}
		} else if (getDefined()) {
			d_bean.setDuration(null);
		}
	}

	public static Duration generateDuration(String duration) {
		try {
			return DatatypeFactory.newInstance().newDuration(duration);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getLabel() {
		return getDefined() ? d_data.quantity + " " + d_data.units : "Undefined";
	}
	
	@Override
	public String toString() {
		return getLabel();
	}
}