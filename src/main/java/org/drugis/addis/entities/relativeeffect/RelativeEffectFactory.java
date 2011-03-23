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

package org.drugis.addis.entities.relativeeffect;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.Variable;

public class RelativeEffectFactory {
	public static <T extends RelativeEffect<?>> RelativeEffect<?> buildRelativeEffect(
			Study s, OutcomeMeasure om, Drug baseDrug, Drug subjDrug, Class<T> type, boolean isCorrected) {
		
		Arm base = findFirstArm(s, baseDrug);
		Arm subj = findFirstArm(s, subjDrug);
		
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
			Study s, OutcomeMeasure om, Drug baseDrug, Drug subjDrug, Class<T> type) {
		
		Arm base = findFirstArm(s, baseDrug);
		Arm subj = findFirstArm(s, subjDrug);
		
		if (type.equals(BasicStandardisedMeanDifference.class)) {
			return buildStandardisedMeanDifference(s, om, base, subj);
		}
		if (type.equals(BasicMeanDifference.class)) {
			return buildMeanDifference(s, om, base, subj);
		}
		if (type.equals(BasicOddsRatio.class)) {
			return buildOddsRatio(s, om, base, subj);
		}
		if (type.equals(BasicRiskRatio.class)) {
			return buildRiskRatio(s, om, base, subj);
		}
		if (type.equals(BasicRiskDifference.class)) {
			return buildRiskDifference(s, om, base, subj);
		}
		
		return null;
	}

	public static Arm findFirstArm(Study s, Drug d) {
		for (Arm a : s.getArms()) {
			if (a.getDrug().equals(d))
				return a;
		}
		throw new IllegalArgumentException("Drug " + d.toString() + " not used in study " + s.toString());
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

	
	private static RelativeEffect<?> buildRiskDifference(Study s, OutcomeMeasure om,
			Arm base, Arm subj) {
		return new BasicRiskDifference(
				findRateMeasurement(s, om, base),
				findRateMeasurement(s, om, subj));
	}

	private static RelativeEffect<?> buildRiskRatio(Study s, OutcomeMeasure om,
			Arm base, Arm subj) {
		return new BasicRiskRatio(
				findRateMeasurement(s, om, base),
				findRateMeasurement(s, om, subj));
	}
	
	private static RelativeEffect<?> buildOddsRatio(Study s, OutcomeMeasure om,
			Arm base, Arm subj) {
		return new BasicOddsRatio(
				findRateMeasurement(s, om, base),
				findRateMeasurement(s, om, subj));
	}

	private static RelativeEffect<?> buildMeanDifference(Study s, OutcomeMeasure om,
			Arm base, Arm subj) {
		return new BasicMeanDifference(
				findContinuousMeasurement(s, om, base),
				findContinuousMeasurement(s, om, subj));
	}

	private static RelativeEffect<?> buildStandardisedMeanDifference(Study s,
			OutcomeMeasure e, Arm base, Arm subj) {
		return new BasicStandardisedMeanDifference(
				findContinuousMeasurement(s, e, base),
				findContinuousMeasurement(s, e, subj));
	}
	
	private static ContinuousMeasurement findContinuousMeasurement(Study s, OutcomeMeasure om, Arm arm) {
		if (!om.getType().equals(Variable.Type.CONTINUOUS)) {
			throw new IllegalArgumentException("OutcomeMeasure should be Continuous");
		}
		return (ContinuousMeasurement)s.getMeasurement(om, arm);
	}
	
	private static RateMeasurement findRateMeasurement(Study s, OutcomeMeasure om, Arm arm) {
		if (!om.getType().equals(Variable.Type.RATE)) {
			throw new IllegalArgumentException("OutcomeMeasure should be Rate");
		}
		return (RateMeasurement)s.getMeasurement(om, arm);
	}


}
