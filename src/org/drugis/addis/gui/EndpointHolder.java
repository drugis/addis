/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package org.drugis.addis.gui;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Endpoint;


import com.jgoodies.binding.beans.Model;

@SuppressWarnings("serial")
public class EndpointHolder extends Model { // TODO: implement as adapter or something?
	private Endpoint d_endpoint;
	public static final String PROPERTY_ENDPOINT = "endpoint";
	public void setEndpoint(Endpoint e) {
		Endpoint oldVal = d_endpoint;
		d_endpoint = e;
		firePropertyChange(PROPERTY_ENDPOINT, oldVal, d_endpoint);
	}
	public Endpoint getEndpoint() {
		return d_endpoint;
	}
	public List<Endpoint> asList() {
		List<Endpoint> list = new ArrayList<Endpoint>();
		if (d_endpoint != null) {
			list.add(d_endpoint);
		}
		return list;
	}
}