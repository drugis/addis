/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import static org.drugis.common.EqualsUtil.equal;

import java.util.List;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.util.XMLPropertiesFormat;
import org.drugis.addis.util.XMLPropertiesFormat.PropertyDefinition;
import org.drugis.common.Interval;

import scala.actors.threadpool.Arrays;

public class FlexibleDose extends AbstractDose {
	private Interval<Double> d_flexDose;
	
	public static final String PROPERTY_FLEXIBLEDOSE = "flexibleDose";
	public static final String PROPERTY_MIN_DOSE = "minDose";
	public static final String PROPERTY_MAX_DOSE = "maxDose";
	
	
	public FlexibleDose(){
		d_flexDose = new Interval<Double>(0.,0.);
	}
	
	public FlexibleDose(Interval<Double> flexDose, SIUnit unit) {
		if (flexDose.getLowerBound() > flexDose.getUpperBound()) {
			throw new IllegalArgumentException("Dose bounds illegal");
		}
		d_flexDose = flexDose;
		d_unit = unit;
	}
	
	public Interval<Double> getFlexibleDose() {
		return d_flexDose;
	}
	
	public Double getMinDose() {
		return d_flexDose.getLowerBound();
	}
	
	public Double getMaxDose() {
		return d_flexDose.getUpperBound();
	}
	
	public void setFlexibleDose(Interval<Double> flexdose) {
		Interval<Double> oldVal = d_flexDose;
		d_flexDose = flexdose;
		firePropertyChange(PROPERTY_FLEXIBLEDOSE, oldVal, d_flexDose);
	}
	
	public void setMinDose(Double d) {
		Interval<Double> oldVal = d_flexDose;
		d_flexDose = new Interval<Double>(d, d > oldVal.getUpperBound() ? d : oldVal.getUpperBound());
		firePropertyChange(PROPERTY_FLEXIBLEDOSE, oldVal, d_flexDose);
		firePropertyChange(PROPERTY_MIN_DOSE, oldVal.getLowerBound(), d_flexDose.getLowerBound());
		firePropertyChange(PROPERTY_MAX_DOSE, oldVal.getUpperBound(), d_flexDose.getUpperBound());
	}
	
	public void setMaxDose(Double d) {
		Interval<Double> oldVal = d_flexDose;
		d_flexDose = new Interval<Double>(d < oldVal.getLowerBound() ? d : oldVal.getLowerBound(), d);
		firePropertyChange(PROPERTY_FLEXIBLEDOSE, oldVal, d_flexDose);
		firePropertyChange(PROPERTY_MIN_DOSE, oldVal.getLowerBound(), d_flexDose.getLowerBound());
		firePropertyChange(PROPERTY_MAX_DOSE, oldVal.getUpperBound(), d_flexDose.getUpperBound());
	}

	@Override
	public String toString() {
		if (d_flexDose == null || d_unit == null) {
			return "INCOMPLETE";
		}
		return d_flexDose.toString() + " " + d_unit.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FlexibleDose) {
			FlexibleDose other = (FlexibleDose)o;
			return equal(other.getFlexibleDose(), getFlexibleDose()) && equal(other.getUnit(), getUnit());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash *= 31; 
		hash += getFlexibleDose().hashCode();
		hash = hash * 31 + getUnit().hashCode();
		return hash;
	}

	@Override
	public AbstractDose clone() {
		return new FlexibleDose(getFlexibleDose(), getUnit());
	}
	
	@SuppressWarnings("unchecked")
	protected List<PropertyDefinition> d_propList = Arrays.asList(new PropertyDefinition<?>[]{
			new PropertyDefinition<SIUnit>(PROPERTY_UNIT, SIUnit.class) {
				public SIUnit getValue() { return getUnit(); }
				public void setValue(Object val) { setUnit((SIUnit) val); }
			}
	});
	
	protected static final XMLFormat<FlexibleDose> FLEXIBLE_DOSE_XML = new XMLFormat<FlexibleDose>(FlexibleDose.class) {

		@Override
		public boolean isReferenceable() {
			return false;
		}
		
		@Override
		public void read(InputElement ie, FlexibleDose fd) throws XMLStreamException {
			fd.setMaxDose(ie.getAttribute(PROPERTY_MAX_DOSE, 0.0));
			fd.setMinDose(ie.getAttribute(PROPERTY_MIN_DOSE, 0.0));
			XMLPropertiesFormat.readProperties(ie, fd.d_propList);
		}

		@Override
		public void write(FlexibleDose fd, OutputElement oe) throws XMLStreamException {
			oe.setAttribute(PROPERTY_MAX_DOSE, fd.getMaxDose());
			oe.setAttribute(PROPERTY_MIN_DOSE, fd.getMinDose());
			XMLPropertiesFormat.writeProperties(fd.d_propList, oe);
		}
	};
}
