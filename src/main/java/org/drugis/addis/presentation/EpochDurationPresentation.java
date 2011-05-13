package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.drugis.addis.entities.Epoch;
import org.drugis.common.beans.AbstractObservable;

public class EpochDurationPresentation extends AbstractObservable {
	private static class Data {
		public final DateUnits units;
		public final int quantity;

		public Data(DateUnits units, int quantity) {
			this.units = units;
			this.quantity = quantity;
		}
		
		public Duration getDuration() {
			return generateDuration(units.asDurationString(quantity));
		}
	}

	public static final String PROPERTY_DEFINED = "defined";
	public static final String PROPERTY_UNITS = "units";
	public static final String PROPERTY_QUANTITY = "quantity";
	public static final String PROPERTY_LABEL = "label";
	
	private static final Data DEFAULT_DATA = new Data(DateUnits.Weeks, 0);
	
	private final Epoch d_epoch;
	private Data d_data = DEFAULT_DATA;

	public static enum DateUnits { // FIXME: does this belong here ?
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
	}
	
	/**
	 * Create a presentation model for the duration in epoch.
	 * @param epoch
	 */
	public EpochDurationPresentation(Epoch epoch) {
		d_epoch = epoch;
		d_epoch.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals(Epoch.PROPERTY_DURATION)) {
					Data oldData = d_data;
					d_data = parseDuration();
					if (oldData != null && d_data == null) {
						firePropertyChange(PROPERTY_DEFINED, true, false);
						firePropertyChange(PROPERTY_QUANTITY, oldData.quantity, DEFAULT_DATA.quantity);
						firePropertyChange(PROPERTY_UNITS, oldData.units, DEFAULT_DATA.units);
					} else if (oldData != null) {
						if (oldData.quantity != d_data.quantity) {
							firePropertyChange(PROPERTY_QUANTITY, oldData.quantity, d_data.quantity);
						}
						if (oldData.units != d_data.units) {
							firePropertyChange(PROPERTY_UNITS, oldData.units, d_data.units);
						}
					} else if (d_data != null) {
						firePropertyChange(PROPERTY_DEFINED, false, true);
						firePropertyChange(PROPERTY_QUANTITY, DEFAULT_DATA.quantity, d_data.quantity);
						firePropertyChange(PROPERTY_UNITS, DEFAULT_DATA.units, d_data.units);
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

	private Data parseDuration() {
		Duration duration = d_epoch.getDuration();
		if (duration == null) {
			return null;
		} else if (duration.isSet(DatatypeConstants.SECONDS)) {
			return new Data(DateUnits.Seconds, duration.getSeconds());
		} else if (duration.isSet(DatatypeConstants.MINUTES)) {
			return new Data(DateUnits.Minutes, duration.getMinutes());
		} else if (duration.isSet(DatatypeConstants.HOURS)) {
			return new Data(DateUnits.Hours, duration.getHours());
		} else if (duration.isSet(DatatypeConstants.DAYS)) {
			if(duration.getDays() % 7 == 0 && (d_data == null || d_data.units != DateUnits.Days)) {
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
	
	public DateUnits getUnits() {
		d_data = parseDuration();
		return d_data == null ? DEFAULT_DATA.units : d_data.units;
	}
	
	public void setUnits(DateUnits units) {
		if (getDefined()) {
			DateUnits oldValue = d_data.units;
			d_data = new Data(units, d_data.quantity);
			d_epoch.setDuration(d_data.getDuration());
			firePropertyChange(PROPERTY_UNITS, oldValue, units);
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
			d_epoch.setDuration(d_data.getDuration());
			firePropertyChange(PROPERTY_QUANTITY, oldValue, quantity);
		}
	}
	
	public boolean getDefined() {
		return d_epoch.getDuration() != null;
	}
	
	public void setDefined(boolean defined) {
		if (defined) {
			if (!getDefined()) {
				d_epoch.setDuration(DEFAULT_DATA.getDuration());
			}
		} else if (getDefined()) {
			d_epoch.setDuration(null);
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
}