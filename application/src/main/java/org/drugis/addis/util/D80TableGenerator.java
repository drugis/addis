/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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
import java.util.HashMap;
import java.util.Map;

import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.PredefinedActivity;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyOutcomeMeasure;
import org.drugis.addis.presentation.DurationPresentation;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

public class D80TableGenerator {
	public enum StatisticType {
		CONFIDENCE_INTERVAL, POINT_ESTIMATE, P_VALUE
	}

	private final Study d_study;

	public D80TableGenerator(Study study) {
		d_study = study;
	}

	public String render() {
		Epoch mainPhase = d_study.findTreatmentEpoch();
		Epoch runInPhase = d_study.findEpochWithActivity(PredefinedActivity.WASH_OUT);
		Epoch extensionPhase = d_study.findEpochWithActivity(PredefinedActivity.FOLLOW_UP);
		
		CompiledTemplate template = TemplateCompiler.compileTemplate(getTemplate());
		Map<String, Object> propMap = new HashMap<String, Object>();
		
		propMap.put("title", d_study.getCharacteristic(BasicStudyCharacteristic.TITLE));
		propMap.put("studyid", d_study.getName());
		propMap.put("mainphase",  getEpochDuration(mainPhase));
		propMap.put("runinphase",  getEpochDuration(runInPhase));
		propMap.put("extensionphase",  getEpochDuration(extensionPhase));
		propMap.put("arms", getArms());
		propMap.put("endpoints", getEndpoints());
		propMap.put("rowspanstatistics", d_study.getEndpoints().size() + 2);
		propMap.put("nEndpointRows", getEndpoints().length * 4);
		propMap.put("colspan", getArms().length + 1);
		propMap.put("fullcolspan", getArms().length + 2);
		propMap.put("smallercolspan", getArms().length);

		return (String)TemplateRuntime.execute( template, propMap );
	}

	private static String getEpochDuration(Epoch epoch) {
		if (epoch != null && epoch.getDuration() != null) {
			DurationPresentation<Epoch> pm = new DurationPresentation<Epoch>(epoch);
			return pm.getLabel();
		}
		return "&lt;duration&gt;";
	}
		
	private ArmForTemplate[] getArms() {
		ArmForTemplate[] ca = new ArmForTemplate[d_study.getArms().size()];
		for (int i = 0; i < ca.length; ++i) {
			ca[i] = new ArmForTemplate(d_study, d_study.getArms().get(i));
		}
		return ca;
	}
	
	public EndpointForTemplate[] getEndpoints() {
		EndpointForTemplate[] ep = new EndpointForTemplate[d_study.getEndpoints().size()];
		for (int i = 0; i < ep.length; ++i) {
			StudyOutcomeMeasure<Endpoint> endpoint = d_study.getEndpoints().get(i);
			ep[i] = new EndpointForTemplate(d_study, endpoint.getValue(), endpoint.getIsPrimary());
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