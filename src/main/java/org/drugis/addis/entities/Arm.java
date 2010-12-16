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

import java.util.Set;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public class Arm extends AbstractEntity {
	private Integer d_size;
	private Drug d_drug;
	private AbstractDose d_dose;
	
	public static final String PROPERTY_SIZE = "size";
	public static final String PROPERTY_DRUG = "drug";
	public static final String PROPERTY_DOSE = "dose";	
	
	public Arm(Drug drug, AbstractDose dose, int size) {
		d_drug = drug;
		d_dose = dose;
		d_size = size;
		init();
	}

	public Arm() {}

	public Drug getDrug() {
		return d_drug;
	}
	
	public void setDrug(Drug drug) {
		Drug oldVal = d_drug;
		d_drug = drug;
		firePropertyChange(PROPERTY_DRUG, oldVal, d_drug);
	}
	
	public AbstractDose getDose() {
		return d_dose;
	}
	
	public void setDose(AbstractDose dose) {
		AbstractDose oldVal = d_dose;
		d_dose = dose;
		firePropertyChange(PROPERTY_DOSE, oldVal, d_dose);
	}
	
	@Override
	public String toString() {
		return  d_drug + ", " + d_dose + ", size: " + d_size;
	}

	public Integer getSize() {
		return d_size;
	}

	public void setSize(Integer size) {
		Integer oldVal = d_size;
		d_size = size;
		firePropertyChange(PROPERTY_SIZE, oldVal, d_size);
	}
	
	@Override
	public Set<Entity> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Arm clone() {
		return new Arm(getDrug(), getDose().clone(), getSize());
	}
	
	protected static final XMLFormat<Arm> ARM_XML = new XMLFormat<Arm>(Arm.class) {
		@Override
		public Arm newInstance(Class<Arm> cls, InputElement xml) {
			return new Arm();
		}
		
		@Override
		public void read(InputElement ie, Arm a) throws XMLStreamException {
			a.setSize(ie.getAttribute(PROPERTY_SIZE, 0));
			a.setDose((AbstractDose) ie.get(PROPERTY_DOSE));
			a.setDrug((Drug) ie.get(PROPERTY_DRUG, Drug.class));
		}

		@Override
		public void write(Arm a, OutputElement oe) throws XMLStreamException {
			oe.setAttribute(PROPERTY_SIZE, a.getSize());
			oe.add(a.getDose(), PROPERTY_DOSE);
			oe.add(a.getDrug(), PROPERTY_DRUG, Drug.class);
		}
	};
}
