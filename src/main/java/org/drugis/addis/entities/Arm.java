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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.drugis.addis.util.XMLPropertiesFormat;
import org.drugis.addis.util.XMLPropertiesFormat.PropertyDefinition;

import scala.actors.threadpool.Arrays;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public class Arm extends AbstractEntity implements TypeWithNotes {
	private Integer d_size;
	private Drug d_drug;
	private AbstractDose d_dose;
	private List<Note> d_notes = new ArrayList<Note>();
	
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
		return Collections.<Entity>singleton(d_drug);
	}
	
	@SuppressWarnings("unchecked")
	private List<PropertyDefinition> d_propDefs = Arrays.asList(new PropertyDefinition<?>[]{
		new PropertyDefinition<AbstractDose>(PROPERTY_DOSE, null) {
			public AbstractDose getValue() { return getDose(); }
			public void setValue(Object val) { setDose((AbstractDose) val); }
		},
		new PropertyDefinition<Drug>(PROPERTY_DRUG, Drug.class) {
			public Drug getValue() { return getDrug(); }
			public void setValue(Object val) { setDrug((Drug) val); }
		}
	});
	
	@Override
	public Arm clone() {
		Arm arm = new Arm(getDrug(), getDose().clone(), getSize());
		arm.getNotes().addAll(getNotes());
		return arm;
	}

	public List<Note> getNotes() {
		return d_notes;
	}

	protected static final XMLFormat<Arm> ARM_XML = new XMLFormat<Arm>(Arm.class) {
		@Override
		public Arm newInstance(Class<Arm> cls, InputElement xml) {
			return new Arm();
		}
		
		@Override
		public void read(InputElement ie, Arm a) throws XMLStreamException {
			a.setSize(ie.getAttribute(PROPERTY_SIZE, 0));
			XMLPropertiesFormat.readProperties(ie, a.d_propDefs);
		}

		@Override
		public void write(Arm a, OutputElement oe) throws XMLStreamException {
			oe.setAttribute(PROPERTY_SIZE, a.getSize());
			XMLPropertiesFormat.writeProperties(a.d_propDefs, oe);
		}
	};
}
