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

package org.drugis.addis;

import java.io.FileInputStream;
import java.io.InputStream;

import org.drugis.addis.NetworkData.IdGenerator;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.Variable.Type;

public class GonzaloData {
	
	public static Indication buildIndicationDiabetes() {
		return new Indication(73211009L, "Diabetes mellitus");
	}
	
	public static Endpoint buildEndpointMACE() {
		Endpoint endpoint = new Endpoint("MACE", Type.RATE);
		endpoint.setDirection(Direction.LOWER_IS_BETTER);
		endpoint.setDescription("Major Adverse Cardiovascular Events (Cardiac and Cerebrovascular)");
		return endpoint;
	}
	
	public static void main(String[] args) throws Exception {
		String basename = "/home/gert/escher/papers/gonzalo/";
		
		DomainImpl d = new DomainImpl();
		Indication diabetes = buildIndicationDiabetes();
		d.addIndication(diabetes);
		
		IdGenerator gen = new IdGenerator() {
			public String studyId(String id) {
				return id;
			}
		};
		
		Endpoint mace = buildEndpointMACE();
		d.addOutcomeMeasure(mace);
		InputStream xml = new FileInputStream(basename + "mace.xml");
		NetworkData.addData(d, xml, diabetes, mace, gen);
		xml.close();
		
		d.saveXMLDomainData(System.out);
	}
}
