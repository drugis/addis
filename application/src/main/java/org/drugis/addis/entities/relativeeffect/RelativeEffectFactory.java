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

package org.drugis.addis.entities.relativeeffect;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.treatment.TreatmentDefinition;

public class RelativeEffectFactory {
	public static <T extends RelativeEffect<?>> RelativeEffect<?> buildRelativeEffect(
			Study s, OutcomeMeasure om, TreatmentDefinition baseline, TreatmentDefinition subject, Class<T> type, boolean isCorrected) {
		
		Arm base = findFirstArm(s, baseline);
		Arm subj = findFirstArm(s, subject);
		
		if (type.equals(BasicStandardisedMeanDifference.class)) {
			return buildStandardisedMeanDifference(s, om, base, subj);
		}
		if (type.equals(BasicMeanDifference.class)) {
			return buildMeanDifference(s, om, base, subj);
		}
		if (type.equals(BasicOddsRatio.class)) {
			return isCorrected ? ((BasicOddsRatio) buildOddsRatio(s, om, base, subj)).getCorrected() : buildOddsRatio(s, om, base, subj);
		}
		if (type.equals(BasicRiskRatio.class)) {
			return isCorrected ? ((BasicRiskRatio) buildRiskRatio(s, om, base, subj)).getCorrected() : buildRiskRatio(s, om, base, subj);
		}
		if (type.equals(BasicRiskDifference.class)) {
			return isCorrected ? ((BasicRiskDifference) buildRiskDifference(s, om, base, subj)).getCorrected() : buildRiskDifference(s, om, base, subj);
		}
		
		return null;
	}
	
	public static <T extends RelativeEffect<?>> RelativeEffect<?> buildRelativeEffect(
			Study s, OutcomeMeasure om, TreatmentDefinition baseline, TreatmentDefinition subject, Class<T> type) {
		return buildRelativeEffect(s, om, baseline, subject, type, false);
	}

	public static Arm findFirstArm(Study s, TreatmentDefinition d) {
		for (Arm a : s.getArms()) {
			if (d.match(s, a)) {
				return a;
			}
		}
		throw new IllegalArgumentException("Treatment definition " + d.toString() + " not used in study " + s.toString());
	}
	

	public static RelativeEffect<? extends Measurement> buildRelativeEffect(
			StudyArmsEntry studyArmsEntry, OutcomeMeasure om,
			Class<? extends RelativeEffect<? extends Measurement>> type, boolean isCorrected) {
		
		Study s = studyArmsEntry.getStudy();
		Arm base = studyArmsEntry.getBase();
		Arm subj = studyArmsEntry.getSubject();
		
		if (type.equals(BasicStandardisedMeanDifference.class)) {
			return buildStandardisedMeanDifference(s, om, base, subj);
		}
		if (type.equals(BasicMeanDifference.class)) {
			return buildMeanDifference(s, om, base, subj);
		}
		if (type.equals(BasicOddsRatio.class)) {
			return isCorrected ? ((BasicOddsRatio) buildOddsRatio(s, om, base, subj)).getCorrected() : buildOddsRatio(s, om, base, subj);
		}
		if (type.equals(BasicRiskRatio.class)) {
			return isCorrected ? ((BasicRiskRatio) buildRiskRatio(s, om, base, subj)).getCorrected() : buildRiskRatio(s, om, base, subj);
		}
		if (type.equals(BasicRiskDifference.class)) {
			return isCorrected ? ((BasicRiskDifference) buildRiskDifference(s, om, base, subj)).getCorrected() : buildRiskDifference(s, om, base, subj);
		}
		
		return null;
	}

	
	private static RelativeEffect<?> buildRiskDifference(Study s, OutcomeMeasure om, Arm base, Arm subj) {
		return new BasicRiskDifference(
				findRateMeasurement(s, om, base),
				findRateMeasurement(s, om, subj));
	}

	private static RelativeEffect<?> buildRiskRatio(Study s, OutcomeMeasure om, Arm base, Arm subj) {
		return new BasicRiskRatio(
				findRateMeasurement(s, om, base),
				findRateMeasurement(s, om, subj));
	}
	
	private static RelativeEffect<?> buildOddsRatio(Study s, OutcomeMeasure om, Arm base, Arm subj) {
		return new BasicOddsRatio(
				findRateMeasurement(s, om, base),
				findRateMeasurement(s, om, subj));
	}

	private static RelativeEffect<?> buildMeanDifference(Study s, OutcomeMeasure om, Arm base, Arm subj) {
		return new BasicMeanDifference(
				findContinuousMeasurement(s, om, base),
				findContinuousMeasurement(s, om, subj));
	}

	private static RelativeEffect<?> buildStandardisedMeanDifference(Study s, OutcomeMeasure e, Arm base, Arm subj) {
		return new BasicStandardisedMeanDifference(
				findContinuousMeasurement(s, e, base),
				findContinuousMeasurement(s, e, subj));
	}
	
	private static ContinuousMeasurement findContinuousMeasurement(Study s, OutcomeMeasure om, Arm arm) {
		if (!(om.getVariableType() instanceof ContinuousVariableType)) {
			throw new IllegalArgumentException("OutcomeMeasure should be Continuous");
		}
		checkMeasurementPresent(s, om, arm);
		return (ContinuousMeasurement)s.getMeasurement(om, arm);
	}
	
	private static RateMeasurement findRateMeasurement(Study s, OutcomeMeasure om, Arm arm) {
		if (!(om.getVariableType() instanceof RateVariableType)) {
			throw new IllegalArgumentException("OutcomeMeasure should be Rate");
		}
		checkMeasurementPresent(s, om, arm);
		return (RateMeasurement)s.getMeasurement(om, arm);
	}

	private static void checkMeasurementPresent(Study s, OutcomeMeasure om, Arm arm) {
		if (s.getMeasurement(om, arm) == null) {
			throw new IllegalStateException("No measurement found: study=" + s + ", outcomeMeasure=" + om + ", arm=" + arm);
		}
	}


}
