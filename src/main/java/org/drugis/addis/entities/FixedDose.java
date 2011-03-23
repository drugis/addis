/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import java.util.List;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.util.XMLPropertiesFormat;
import org.drugis.addis.util.XMLPropertiesFormat.PropertyDefinition;
import org.drugis.common.EqualsUtil;

import scala.actors.threadpool.Arrays;


public class FixedDose extends AbstractDose {

	public static final String PROPERTY_QUANTITY = "quantity";

	private Double d_quantity;
	
	public FixedDose(){
	}
	
	public FixedDose(double quantity, SIUnit unit) {
		d_quantity = quantity;
		d_unit = unit;
	}
	
	public Double getQuantity() {
		return d_quantity;
	}

	public void setQuantity(Double quantity) {
		Double oldVal = d_quantity;
		d_quantity = quantity;
		firePropertyChange(PROPERTY_QUANTITY, oldVal, d_quantity);
	}
	
	@Override
	public String toString() {
		if (d_quantity == null || d_unit == null) {
			return "INCOMPLETE";
		}
		return d_quantity.toString() + " " + d_unit.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FixedDose) {
			FixedDose other = (FixedDose)o;
			return EqualsUtil.equal(other.getQuantity(), getQuantity()) &&
				EqualsUtil.equal(other.getUnit(), getUnit());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash *= 31; 
		hash += getQuantity().hashCode();
		hash = hash * 31 + getUnit().hashCode();
		return hash;
	}

	@Override
	public AbstractDose clone() {
		return new FixedDose(getQuantity(), getUnit());
	}
	
	@SuppressWarnings("unchecked")
	protected List<PropertyDefinition> d_propList = Arrays.asList(new PropertyDefinition<?>[]{
			new PropertyDefinition<SIUnit>(PROPERTY_UNIT, SIUnit.class) {
				public SIUnit getValue() { return getUnit(); }
				public void setValue(Object val) { setUnit((SIUnit) val); }
			}
	});
	
	protected static final XMLFormat<FixedDose> FIXED_DOSE_XML = new XMLFormat<FixedDose>(FixedDose.class) {

		@Override
		public boolean isReferenceable() {
			return false;
		}
		
		@Override
		public void read(InputElement ie, FixedDose fd) throws XMLStreamException {
			fd.setQuantity(ie.getAttribute(PROPERTY_QUANTITY, 0.0));
			XMLPropertiesFormat.readProperties(ie, fd.d_propList);
		}

		@Override
		public void write(FixedDose fd, OutputElement oe) throws XMLStreamException {
			oe.setAttribute(PROPERTY_QUANTITY, fd.getQuantity());
			XMLPropertiesFormat.writeProperties(fd.d_propList, oe);
		}
	};
}
