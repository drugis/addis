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

package org.drugis.addis.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.PredefinedActivity;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.EpochDurationPresentation;
import org.stringtemplate.v4.ST;

public class D80TableGenerator {
	private final Study d_study;

	public D80TableGenerator(Study study) {
		d_study = study;
	}

	public String render() {
		Epoch mainPhase = d_study.findTreatmentEpoch();
		Epoch runInPhase = d_study.findEpochWithActivity(PredefinedActivity.WASH_OUT);
		Epoch extensionPhase = d_study.findEpochWithActivity(PredefinedActivity.FOLLOW_UP);
		
		ST processor = new ST(getTemplate(), '$', '$');
		processor.add("studyid", d_study.getName());
		processor.add("mainphase",  getEpochDuration(mainPhase));
		processor.add("runinphase",  getEpochDuration(runInPhase));
		processor.add("extensionphase",  getEpochDuration(extensionPhase));
		processor.add("arms", getArms());
		processor.add("endpoints", getEndpoints());
		processor.add("colspanstatistics", d_study.getEndpoints().size() + 2);
		processor.add("nEndpointRows", getEndpoints().length * 4);

		return processor.render();
	}

	private static String getEpochDuration(Epoch epoch) {
		if (epoch != null && epoch.getDuration() != null) {
			EpochDurationPresentation pm = new EpochDurationPresentation(epoch);
			return pm.getLabel();
		}
		return "&lt;duration&gt;";
	}
	
	@SuppressWarnings("unused")
	private class ArmForTemplate {
		/**
		 * Arm Class used by the template. 
		 * The getters are important, should not be renamed.
		 * $it.name$ in template corresponds to getName(), where $it is the iterator
		 */
		private final Arm d_arm;
		public ArmForTemplate(Arm arm) {
			d_arm = arm;
		}

		public String getName() {
			return d_arm.getName();
		}
		public String getTreatment() {
			return d_study.getTreatment(d_arm).getDescription();
		}
		public String getDuration() {
			return getEpochDuration(d_study.findTreatmentEpoch());
		}
		public String getNrRandomized() {
			return d_arm.getSize().toString();
		}
	}
	
	private ArmForTemplate[] getArms() {
		ArmForTemplate[] ca = new ArmForTemplate[d_study.getArms().size()];
		for (int i = 0; i < ca.length; ++i) {
			ca[i] = new ArmForTemplate(d_study.getArms().get(i));
		}
		return ca;
	}

	@SuppressWarnings("unused")
	private class EndpointForTemplate {		
		private final Endpoint d_endpoint;

		public EndpointForTemplate(Endpoint endpoint) {
			d_endpoint = endpoint;
		}
		
		public String getType() { 
			return d_endpoint.getType().toString();
		}
		public String getName() {
			return d_endpoint.getName();
		}
		public String getDescription() {
			return d_endpoint.getDescription();
		}
		public String[] getMeasurements() {
			List<String> ms = new ArrayList<String>();
			for (Arm a : d_study.getArms()) {
				BasicMeasurement measurement = d_study.getMeasurement(d_endpoint, a);
				ms.add(measurement == null ? "MISSING" : measurement.toString());
			}
			return ms.toArray(new String[0]);
		}
	}
	
	public EndpointForTemplate[] getEndpoints() {
		EndpointForTemplate[] ep = new EndpointForTemplate[d_study.getEndpoints().size()];
		for (int i = 0; i < ep.length; ++i) {
			ep[i] = new EndpointForTemplate(d_study.getEndpoints().get(i).getValue());
		}
		return ep;
	}
	
	public static String getHtml(Study study) {
		return (new D80TableGenerator(study)).render();
	}
	
	public static String getTemplate() {
		String html = "";
		try {
			InputStreamReader fr = new InputStreamReader(D80TableGenerator.class.getResourceAsStream("TemplateD80Report.html"));
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while((line = br.readLine()) != null ) {
				html += line;
			}
			br.close();
		} catch (IOException e) {
			throw new RuntimeException("Could not find / load template file.", e);
		}
		return html;
	}
}