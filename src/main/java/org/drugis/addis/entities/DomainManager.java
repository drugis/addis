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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



public class DomainManager {
	private DomainImpl d_domain = new DomainImpl();
	
	public Domain getDomain() {
		return d_domain;
	}
	
	public void resetDomain() {
		d_domain = new DomainImpl();
	}
	
	/**
	 * Replace the Domain by a new instance loaded from a XML stream.
	 * @param is Stream to read objects from.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void loadXMLDomain(InputStream is)
	throws IOException, ClassNotFoundException {
		d_domain.loadXMLDomainData(is);
	}
	
	/**
	 * Save the Domain by a new instance loaded from a XML stream.
	 * @param os Stream to write objects to.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void saveXMLDomain(OutputStream os)
	throws IOException {
		d_domain.saveXMLDomainData(os);
	}
}
