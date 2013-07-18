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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.util.JSMAAintegration;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.Beta;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.TransformedStudentT;
import org.drugis.addis.entities.relativeeffect.TransformedStudentTBase;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.BetaMeasurement;
import fi.smaa.jsmaa.model.CardinalMeasurement;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.IndependentMeasurements;
import fi.smaa.jsmaa.model.SMAAModel;

public class StudyBenefitRiskSMAAFactory extends AbstractBenefitRiskSMAAFactory<Arm> {
	private final StudyBenefitRiskAnalysis d_brAnalysis;

	public StudyBenefitRiskSMAAFactory(StudyBenefitRiskAnalysis brAnalysis) {
		d_brAnalysis = brAnalysis;
	}

	public SMAAModel createSMAAModel() {
		SMAAModel smaaModel = new SMAAModel(d_brAnalysis.getName());
		addCriteriaAndAlternatives(smaaModel, d_brAnalysis);

		IndependentMeasurements measurements = (IndependentMeasurements) smaaModel.getMeasurements();

		for (OutcomeMeasure om : d_brAnalysis.getCriteria()) {
			for (Arm a : d_brAnalysis.getAlternatives()) {
				CardinalMeasurement m = createMeasurement(d_brAnalysis.getMeasurement(om, a));
				measurements.setMeasurement(getCriterion(om), getAlternative(a), m);
			}
		}

		return smaaModel;
	}

	@Override
	protected Alternative createAlternative(Arm arm) {
		return new Alternative(d_brAnalysis.getStudy().getTreatment(arm).getLabel());
	}

	public static CardinalMeasurement createMeasurement(Distribution re) {
		if (re instanceof TransformedStudentT) {
			TransformedStudentTBase studentt = (TransformedStudentTBase) re;
			return new GaussianMeasurement(studentt.getMu(), studentt.getSigma());
		} else if (re instanceof Beta) {
			Beta beta = (Beta) re;
			return new BetaMeasurement(beta.getAlpha(), beta.getBeta(), 0, 1);
		}
		throw new IllegalArgumentException("Unhandled distribution: " + re);
	}
}
