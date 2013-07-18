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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.relativeeffect.AbstractBasicRelativeEffect;
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.addis.entities.relativeeffect.BasicRiskRatio;
import org.drugis.addis.entities.relativeeffect.ConfidenceInterval;
import org.drugis.addis.util.D80TableGenerator.StatisticType;

public class EndpointForTemplate {
	private final Study d_study;
	private final Endpoint d_endpoint;
	private final Boolean d_isPrimary;

	public EndpointForTemplate(Study study, Endpoint endpoint, Boolean isPrimary) {
		d_study = study;
		d_endpoint = endpoint;
		d_isPrimary = isPrimary;
	}

	public String getType() {
		return d_endpoint.getVariableType().getType();
	}

	public String getPrimary() {
		return d_isPrimary ? "Primary" : "Secondary";
	}

	public String getName() {
		return d_endpoint.getName();
	}
	public String getDescription() {
		return d_endpoint.getLabel();
	}
	public String[] getMeasurements() {
		List<String> ms = new ArrayList<String>();
		for (Arm a : d_study.getArms()) {
			BasicMeasurement measurement = d_study.getMeasurement(d_endpoint, a);
			ms.add(measurement == null ? "MISSING" : measurement.toString());
		}
		return ms.toArray(new String[0]);
	}

	// These three are not used in Java but called by the template
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

	private String getStatistic(StatisticType type, BasicMeasurement baseline, BasicMeasurement subject) {
		if (baseline == null || subject == null) return "MISSING";
		DecimalFormat df = new DecimalFormat("###0.00");
		switch(type) {
		case CONFIDENCE_INTERVAL :
			return formatConfidenceInterval(baseline, subject, df);
		case POINT_ESTIMATE :
			return formatPointEstimate(baseline, subject, df);
		case P_VALUE :
			return formatPValue(baseline, subject, df);
		default:
			throw new RuntimeException("D80 table generator: unknown statistic type.");
		}
	}


	private String formatPValue(BasicMeasurement baseline, BasicMeasurement subject, DecimalFormat df) {
		AbstractBasicRelativeEffect<? extends Measurement> relEffect = getRelativeEffect(baseline, subject);
		if (relEffect.getTwoSidedPValue() >= 0.01) {
			return df.format(relEffect.getTwoSidedPValue());
		} else {
			return "&lt;0.01";
		}
	}

	private String formatConfidenceInterval(BasicMeasurement baseline, BasicMeasurement subject, DecimalFormat df) {
		ConfidenceInterval ci = (getRelativeEffect(baseline, subject)).getConfidenceInterval();
		return 	"(" + df.format(ci.getLowerBound()) + ", " + df.format(ci.getUpperBound()) + ")";
	}

	private String formatPointEstimate(BasicMeasurement baseline, BasicMeasurement subject, DecimalFormat df) {
		ConfidenceInterval ci = (getRelativeEffect(baseline, subject)).getConfidenceInterval();
		return df.format(ci.getPointEstimate());
	}

	private AbstractBasicRelativeEffect<? extends Measurement> getRelativeEffect(BasicMeasurement baseline, BasicMeasurement subject) {
		return (d_endpoint.getVariableType() instanceof ContinuousVariableType ?
				new BasicMeanDifference((ContinuousMeasurement)baseline, (ContinuousMeasurement)subject) :
				new BasicRiskRatio((RateMeasurement) baseline, (RateMeasurement) subject));
	}

	public String getTestStatisticType() {
		return d_endpoint.getVariableType() instanceof ContinuousVariableType ? "Mean Difference" : "Risk Ratio";
	}

}
