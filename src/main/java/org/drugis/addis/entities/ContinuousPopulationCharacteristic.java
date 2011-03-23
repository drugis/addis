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

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;


public class ContinuousPopulationCharacteristic extends AbstractVariable implements PopulationCharacteristic {

	public ContinuousPopulationCharacteristic() {
		super("", Type.CONTINUOUS);
	}
	
	public ContinuousPopulationCharacteristic(String name) {
		super(name, Type.CONTINUOUS);
	}
	
	protected static final XMLFormat<ContinuousPopulationCharacteristic> CONT_PC_XML = 
		new XMLFormat<ContinuousPopulationCharacteristic>(ContinuousPopulationCharacteristic.class) {

		@Override
		public void read(InputElement ie, ContinuousPopulationCharacteristic cpc) throws XMLStreamException {
			cpc.setDescription(ie.getAttribute(PROPERTY_DESCRIPTION, null));
			cpc.setName(ie.getAttribute(PROPERTY_NAME, null));
			// read unused unit of measurement attribute for legacy xml
			cpc.setUnitOfMeasurement(ie.getAttribute(PROPERTY_UNIT_OF_MEASUREMENT, null));
			if (ie.hasNext()) { ie.get("type", String.class); }
		}

		@Override
		public void write(ContinuousPopulationCharacteristic cpc, OutputElement oe) throws XMLStreamException {
			oe.setAttribute(PROPERTY_DESCRIPTION, cpc.getDescription());
			oe.setAttribute(PROPERTY_NAME, cpc.getName());
			oe.setAttribute(PROPERTY_UNIT_OF_MEASUREMENT, cpc.getUnitOfMeasurement());
		}
	};
}
