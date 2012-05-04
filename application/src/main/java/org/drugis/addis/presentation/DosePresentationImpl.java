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

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.ScaleModifier;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.Interval;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;

class DosePresentationImpl implements DosePresentation {
	private static final DoseUnit MILLIGRAMS_A_DAY = new DoseUnit(Domain.GRAM, ScaleModifier.MILLI, EntityUtil.createDuration("P1D"));
	private DrugTreatment d_activity;
	private ValueHolder d_min;
	private ValueHolder d_max;
	
	public DosePresentationImpl(DrugTreatmentPresentation treatmentActivityPresentation) {
		d_activity = treatmentActivityPresentation.getBean();
		if (d_activity.getDose() == null) {
			d_activity.setDose(new FixedDose(0.0, MILLIGRAMS_A_DAY));
		}
		d_min = new ValueHolder(getMinDose(d_activity));
		d_max = new ValueHolder(getMaxDose(d_activity));
		d_min.addPropertyChangeListener(new DoseChangeListener());
		d_max.addPropertyChangeListener(new DoseChangeListener());
	}

	private double getMaxDose(DrugTreatment pg) {
		if (d_activity.getDose() instanceof FlexibleDose) {
			return ((FlexibleDose)d_activity.getDose()).getFlexibleDose().getUpperBound();
		} else if (d_activity.getDose() instanceof FixedDose) {
			return ((FixedDose)d_activity.getDose()).getQuantity();
		}
		return 0.0;
	}

	private double getMinDose(DrugTreatment pg) {
		if (d_activity.getDose() instanceof FlexibleDose) {
			return ((FlexibleDose)d_activity.getDose()).getFlexibleDose().getLowerBound();
		} else if (d_activity.getDose() instanceof FixedDose) {
			return ((FixedDose)d_activity.getDose()).getQuantity();
		}
		return 0.0;
	}

	public AbstractValueModel getMaxModel() {
		return d_max;
	}

	public AbstractValueModel getMinModel() {
		return d_min;
	}

	public DoseUnitPresentation getDoseUnitPresentation() {
		return new DoseUnitPresentation(d_activity.getDose().getDoseUnit());
	}
	
	private class DoseChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == d_min) {
				double newMin = (Double)evt.getNewValue();
				if (newMin > d_max.doubleValue()) {
					d_max.setValue(newMin);
					return;
				}
			}
			if (evt.getSource() == d_max) {
				double newMax = (Double)evt.getNewValue();
				if (newMax < d_min.doubleValue()) {
					d_min.setValue(newMax);
					return;
				}
			}
			if (d_min.doubleValue() == d_max.doubleValue()) {
				d_activity.setDose(new FixedDose(d_min.doubleValue(), d_activity.getDose().getDoseUnit()));
			} else if (d_min.doubleValue() < d_max.doubleValue()) {
				Interval<Double> interval = new Interval<Double>(d_min.doubleValue(), d_max.doubleValue());
				d_activity.setDose(new FlexibleDose(interval, d_activity.getDose().getDoseUnit()));
			} else {
				throw new RuntimeException("Should not be reached");
			}
		}
	}
}