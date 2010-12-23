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

import java.util.List;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.util.XMLPropertiesFormat;
import org.drugis.addis.util.XMLPropertiesFormat.PropertyDefinition;

import scala.actors.threadpool.Arrays;

public class AdverseEvent extends AbstractVariable implements OutcomeMeasure {

	public AdverseEvent() {
		super("", Type.RATE);
	}
	
	public AdverseEvent(String name, Variable.Type type) {
		super(name, type);
	}

	public Direction getDirection() {
		return Direction.LOWER_IS_BETTER;
	}
	
	public void setDirection(Direction dir) {
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof AdverseEvent) {
			return super.equals(o);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if (d_name != null) {
			return d_name.hashCode() + 7; //magic number ?
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	protected List<PropertyDefinition> d_propDefs = Arrays.asList(new PropertyDefinition<?>[]{
			new PropertyDefinition<Variable.Type>(PROPERTY_TYPE, Variable.Type.class) {
				public Variable.Type getValue() { return AdverseEvent.this.getType(); }
				public void setValue(Object val) { AdverseEvent.this.setType((Variable.Type) val); }
			}
		});
	
	protected static final XMLFormat<AdverseEvent> ADVERSE_EVENT_XML = new XMLFormat<AdverseEvent>(AdverseEvent.class) {
		@Override
		public AdverseEvent newInstance(Class<AdverseEvent> cls, InputElement xml) throws XMLStreamException {
			return new AdverseEvent();
		};

		@Override
		public void read(InputElement ie, AdverseEvent ae) throws XMLStreamException {
			VARIABLE_XML.read(ie, ae);
			XMLPropertiesFormat.readProperties(ie, ae.d_propDefs);
		}

		@Override
		public void write(AdverseEvent ae, OutputElement oe) throws XMLStreamException {
			VARIABLE_XML.write(ae, oe);
			XMLPropertiesFormat.writeProperties(ae.d_propDefs, oe);
		}
	};
	
}
