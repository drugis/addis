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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.PredefinedActivity;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.addis.entities.relativeeffect.AbstractBasicRelativeEffect;
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.addis.entities.relativeeffect.BasicRiskRatio;
import org.drugis.addis.entities.relativeeffect.ConfidenceInterval;
import org.drugis.addis.presentation.EpochDurationPresentation;
import org.stringtemplate.v4.ST;

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
		
		public String[] getTestStatistics() {
			return getStatistics(StatisticType.POINT_ESTIMATE);		
		}

		public String[] getVariabilityStatistics() {
			return getStatistics(StatisticType.CONFIDENCE_INTERVAL);
		}
		
		public String[] getPValueStatistics() {
			return getStatistics(StatisticType.P_VALUE);
		}
		
		public String[] getStatistics(StatisticType type) {
			List<String> statistics = new ArrayList<String>();
			Arm base = d_study.getArms().get(0);
			BasicMeasurement baseline = d_study.getMeasurement(d_endpoint, base);
			for (Arm a : d_study.getArms().subList(1, d_study.getArms().size())) {
				BasicMeasurement subject = d_study.getMeasurement(d_endpoint, a);
				statistics.add(getStatistic(type, baseline, subject));
			}
			return statistics.toArray(new String[0]);
		}

		private String getStatistic(StatisticType type,
				BasicMeasurement baseline, BasicMeasurement subject) {
			switch(type) {
			case CONFIDENCE_INTERVAL :
				return formatConfidenceInterval(baseline, subject);
			case POINT_ESTIMATE :
				return formatPointEstimate(baseline, subject);
			case P_VALUE :
				return formatPValue(baseline, subject);
			}
			return type == StatisticType.CONFIDENCE_INTERVAL ? 
											formatConfidenceInterval(baseline, subject) : 
											formatPointEstimate(baseline, subject);
		}

		
		private String formatPValue(BasicMeasurement baseline, BasicMeasurement subject) {
			DecimalFormat df = new DecimalFormat("###0.00");
			AbstractBasicRelativeEffect<? extends Measurement> relEffect = getRelativeEffect(baseline, subject);
			return 	df.format(relEffect.getTwoSidedPValue());
		}

		private String formatConfidenceInterval(BasicMeasurement baseline, BasicMeasurement subject) {
			DecimalFormat df = new DecimalFormat("###0.00");
			ConfidenceInterval ci = getconfidenceInterval(baseline, subject);
			return 	"(" + df.format(ci.getLowerBound()) + ", " + df.format(ci.getUpperBound()) + ")";
		}

		private String formatPointEstimate(BasicMeasurement baseline, BasicMeasurement subject) {
			DecimalFormat df = new DecimalFormat("###0.00");
			ConfidenceInterval ci = getconfidenceInterval(baseline, subject);
			return df.format(ci.getPointEstimate());
		}

		private ConfidenceInterval getconfidenceInterval(BasicMeasurement baseline, BasicMeasurement subject) {
			return (getRelativeEffect(baseline, subject)).getConfidenceInterval();
		}

		private AbstractBasicRelativeEffect<? extends Measurement> getRelativeEffect(BasicMeasurement baseline, BasicMeasurement subject) {
			return (d_endpoint.getType() == Type.CONTINUOUS ? 
					new BasicMeanDifference((ContinuousMeasurement)baseline, (ContinuousMeasurement)subject) : 
					new BasicRiskRatio((RateMeasurement) baseline, (RateMeasurement) subject));
		}
		
		public String getTestStatisticType() {
			return d_endpoint.getType() == Type.CONTINUOUS ? "Mean Difference" : "Risk Ratio";
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