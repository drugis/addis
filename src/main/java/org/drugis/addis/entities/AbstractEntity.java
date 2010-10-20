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

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import javolution.xml.XMLFormat;

import org.drugis.addis.util.EntityXMLFormat;
import org.drugis.addis.util.EntryXMLFormat;
import org.drugis.addis.util.HashMapXMLFormat;
import org.drugis.common.beans.AbstractObservable;

@SuppressWarnings("unchecked")
public abstract class AbstractEntity extends AbstractObservable implements Entity {
	protected static final XMLFormat<HashMap> mapXML = new HashMapXMLFormat();	
	protected static final XMLFormat<Entry> entryXML = new EntryXMLFormat();

	public abstract Set<? extends Entity> getDependencies();
	
	public String[] getXmlExclusions() {
		return null;
	}
	
	protected static final XMLFormat<Entity> XML = new EntityXMLFormat();
}
