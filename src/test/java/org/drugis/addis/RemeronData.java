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

package org.drugis.addis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.addis.util.AtcParser;
import org.drugis.addis.util.AtcParser.AtcDescription;

public class RemeronData {
	private BufferedReader d_reader;
	
	private static final int COLUMN_ID = 0;
	private static final int COLUMN_DRUG = 1;
	private static final int COLUMN_ITT = 2;
	private static final int COLUMN_RESPONSE = 3;
	private static final int COLUMN_AST = 4;
	private static final int COLUMN_ADE = 5;
	
	private List<AdverseEvent> d_adverseEvents = new ArrayList<AdverseEvent>();
	private Endpoint d_endpointResponders = new Endpoint("Responders", Type.RATE, Direction.HIGHER_IS_BETTER);
	private Indication d_indication = ExampleData.buildIndicationDepression();
	private Map<String, Study> d_studies = new HashMap<String, Study>();
	private Map<String, Drug> d_drugs = new HashMap<String, Drug>();

	private DomainImpl d_domain;
	
	private RemeronData(String fileName) throws FileNotFoundException {
		d_reader = new BufferedReader(new FileReader(fileName));
		d_domain = new DomainImpl();
	}
	
	private void parse() throws IOException {
		String line = d_reader.readLine();
		parseHeaders(line);
		while ((line = d_reader.readLine()) != null) {
			parseLine(line);
		}
		d_reader.close();
	}
	
	private void parseHeaders(String line) {
		String[] parts = chop(line);
		d_domain.addEndpoint(d_endpointResponders);
		d_domain.addIndication(d_indication);
		for (int i = COLUMN_ADE; i < parts.length; ++i) {
			
			d_adverseEvents.add(new AdverseEvent(unquote(parts[i]), Type.RATE));
			d_domain.addAdverseEvent(d_adverseEvents.get(i - COLUMN_ADE));
		}
	}

	private String[] chop(String line) {
		return line.split(",");
	}
	
	private String unquote(String str) {
		if (str.charAt(0) == '"') {
			return str.substring(1, str.length() - 1);
		} else {
			return str;
		}
	}
	
	private int getInt(String str) {
		return Integer.parseInt(str);
	}

	private void parseLine(String line) {
		String[] parts = chop(line);
		String id = unquote(parts[COLUMN_ID]);
		Study study = getOrCreateStudy(id);
		
		String drugId = unquote(parts[COLUMN_DRUG]);
		Arm arm = new Arm(getOrCreateDrug(drugId), new UnknownDose(), getInt(parts[COLUMN_ITT]));
		study.addArm(arm);
		addEndpointMeasurement(study, arm, parts);
		addAdverseEventMeasurements(study, arm, parts);
	}

	private Drug getOrCreateDrug(String drugId) {
		Drug drug = d_drugs.get(drugId);
		if (drug == null) {
			AtcParser parser = new AtcParser();
			String atc = drugId;
			try {
				AtcDescription desc = parser.getAtcCode(drugId);
				if (desc.getCode() != null) {
					atc = desc.getCode();
				}
			} catch (IOException e) {
			}
			drug = new Drug(drugId, atc);
			d_drugs.put(drugId, drug);
			d_domain.addDrug(drug);
		}
		return drug;
	}

	private void addAdverseEventMeasurements(Study study, Arm a, String[] parts) {
		int sampleSize = getInt(parts[COLUMN_AST]);
		for (int i = 0; i < d_adverseEvents.size(); ++i) {
			int rate = getInt(parts[COLUMN_ADE + i]);
			study.setMeasurement(d_adverseEvents.get(i), a, new BasicRateMeasurement(rate, sampleSize));
		}
	}

	private void addEndpointMeasurement(Study study, Arm a, String[] parts) {
		int sampleSize = getInt(parts[COLUMN_ITT]);
		int responders = getInt(parts[COLUMN_RESPONSE]);
		study.setMeasurement(d_endpointResponders, a, new BasicRateMeasurement(responders, sampleSize));
	}

	private Study getOrCreateStudy(String id) {
		Study study = d_studies.get(id);
		if (study == null) {
			study = new Study(id, d_indication);
			d_studies.put(id, study);
			d_domain.addStudy(study);
			study.setAdverseEvents(d_adverseEvents);
			study.addEndpoint(d_endpointResponders);
		}
		return study;
	}

	private DomainImpl getDomain() {
		return d_domain;
	}
	
	public static void main(String[] args) throws IOException {
		RemeronData parser = new RemeronData(args[0]);
		parser.parse();
		parser.getDomain().saveXMLDomainData(System.out);
	}
}
