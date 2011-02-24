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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public class PubMedId {
	private String d_id;

	private PubMedId() {
	}
	
	public PubMedId(String id) {
		setId(id);
	}

	public String getId() {
		return d_id;
	}
	
	@Override
	public String toString() {
		return getId();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PubMedId) {
			return ((PubMedId)o).getId().equals(this.getId());
		}
		return false;
	}
	
	private static final Pattern s_onlyDigits = Pattern.compile("^[1-9][0-9]*$");
	
	private void setId(String id) {
		if (id == null || id.length() == 0) { // FIXME: more validation should be done
			throw new IllegalArgumentException("PubMedId may not be null or empty.");
		}
		Matcher matcher = s_onlyDigits.matcher(id);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Only digits are valid in a PubMedId, and it may not start with 0.");
		}
		d_id = id;
	}

	protected static final XMLFormat<PubMedId> XML = new XMLFormat<PubMedId>(PubMedId.class) {
		@Override
		public PubMedId newInstance(java.lang.Class<PubMedId> cls, XMLFormat.InputElement ie) throws XMLStreamException {
			return new PubMedId();
		};

		@Override
		public void read(javolution.xml.XMLFormat.InputElement ie,
				PubMedId obj) throws XMLStreamException {
			obj.setId(ie.getAttribute("value").toString());
		}

		@Override
		public void write(PubMedId obj,
				javolution.xml.XMLFormat.OutputElement oe)
				throws XMLStreamException {
			oe.setAttribute("value", obj.getId());				
		}

		@Override
		public boolean isReferenceable() {
			return false;
		}
	};
}