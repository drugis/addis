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

public class Endpoint extends AbstractVariable implements OutcomeMeasure {
	
	private Direction d_direction;
	
	public Endpoint() {
		super("", Type.RATE);
	}
	
	public Endpoint(String name, Variable.Type type, Direction direction) {
		super(name, type);
		d_direction = direction;
	}
	
	public Endpoint(String string, Variable.Type type) {
		this(string, type, Direction.HIGHER_IS_BETTER);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Endpoint) {
			return super.equals(o);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if (d_name != null) {
			return d_name.hashCode();
		}
		return 0;
	}
	
	public void setDirection(Direction dir) {
		Direction oldVal = d_direction;
		d_direction = dir;
		firePropertyChange(PROPERTY_DIRECTION, oldVal, d_direction);
	}
	
	public Direction getDirection() {
		return d_direction;
	}
	
	@SuppressWarnings("unchecked")
	protected List<PropertyDefinition> d_propDefs = Arrays.asList(new PropertyDefinition<?>[]{
			new PropertyDefinition<Direction>(PROPERTY_DIRECTION, Direction.class) {
				public Direction getValue() { return getDirection(); }
				public void setValue(Object val) { setDirection((Direction) val); }
			},
			new PropertyDefinition<Variable.Type>(PROPERTY_TYPE, Variable.Type.class) {
				public Variable.Type getValue() { return Endpoint.this.getType(); }
				public void setValue(Object val) { Endpoint.this.setType((Variable.Type) val); }
			}
		});
	
	protected static final XMLFormat<Endpoint> ENDPOINT_XML = new XMLFormat<Endpoint>(Endpoint.class) {
		@Override
		public Endpoint newInstance(Class<Endpoint> cls, InputElement xml) throws XMLStreamException {
			return new Endpoint();
		};

		@Override
		public void read(InputElement ie, Endpoint e) throws XMLStreamException {
			VARIABLE_XML.read(ie, e);
			XMLPropertiesFormat.readProperties(ie, e.d_propDefs);
		}

		@Override
		public void write(Endpoint e, OutputElement oe) throws XMLStreamException {
			VARIABLE_XML.write(e, oe);
			XMLPropertiesFormat.writeProperties(e.d_propDefs, oe);
		}
	};
}
