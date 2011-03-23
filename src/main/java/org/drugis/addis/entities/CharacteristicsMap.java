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

import java.util.Collections;
import java.util.Set;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public class CharacteristicsMap extends MapBean<Characteristic, ObjectWithNotes<?>> {
	
	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	protected static XMLFormat<CharacteristicsMap> XMLMap = new XMLFormat<CharacteristicsMap>(CharacteristicsMap.class) {
		
		@Override
		public boolean isReferenceable() {
			return false;
		}
		
		@Override
		public void read(InputElement xml,	CharacteristicsMap obj) throws XMLStreamException {
			for(BasicStudyCharacteristic c : BasicStudyCharacteristic.values()){
				Object value = xml.get(c.toString());
				if(value != null) obj.put(c, new ObjectWithNotes<Object>(value));	
			}
		}

		@Override
		public void write(CharacteristicsMap map, OutputElement xml) throws XMLStreamException {	
			for(BasicStudyCharacteristic c : BasicStudyCharacteristic.values())
					xml.add(map.get(c) == null ? null : map.get(c).getValue(), c.toString());
		}
	};	
}
