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

package org.drugis.addis.lyndobrien;

import java.util.Arrays;
import java.util.List;

import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.util.JSMAAintegration.AbstractBenefitRiskSMAAFactory;
import org.drugis.addis.util.JSMAAintegration.SMAAEntityFactory;

import fi.smaa.common.RandomUtil;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.FullJointMeasurements;
import fi.smaa.jsmaa.model.SMAAModel;

/**
 * Sample relative benefit and risk based on 2x2 absolute effect distributions.
 */
public class BenefitRiskDistributionImpl<AltType extends Entity> implements BenefitRiskDistribution {
	private FullJointMeasurements d_measurements;
	private String d_benefitAxisName;
	private String d_riskAxisName;
	private int d_benefitMultiplier;
	private int d_riskMultiplier;

	public BenefitRiskDistributionImpl(BenefitRiskAnalysis<AltType> a) {
		initRiskBenefits(a);
		initAxisLabelsAndMultipliers(a);
	}

	private void initAxisLabelsAndMultipliers(BenefitRiskAnalysis<AltType> a) {
		switch(a.getCriteria().get(0).getDirection()) {
		case HIGHER_IS_BETTER:
			d_benefitAxisName = "";
			d_benefitMultiplier = 1;
			break;
		case LOWER_IS_BETTER:
			d_benefitAxisName = "-";
			d_benefitMultiplier = -1;
			break;
		}
		switch(a.getCriteria().get(1).getDirection()) {
		case HIGHER_IS_BETTER:
			d_riskAxisName = "-";
			d_riskMultiplier = -1;
			break;
		case LOWER_IS_BETTER:
			d_riskAxisName = "";
			d_riskMultiplier = 1;
			break;
		}
		d_benefitAxisName += getAxisLabel(a.getCriteria().get(0));
		d_riskAxisName += getAxisLabel(a.getCriteria().get(1));
	}

	private String getAxisLabel(OutcomeMeasure om) {
		if (om.getVariableType() instanceof RateVariableType) {
			return "\u0394 Incidence(" + om.getName() + ")";
		} else if (om.getVariableType() instanceof ContinuousVariableType) {
			return "\u0394(" + om.getName() + ")";
		} else {
			throw new IllegalArgumentException("OutcomeMeasure " + om.getName() + " is of unknown type " + om.getVariableType());
		}
	}

	private void initRiskBenefits(BenefitRiskAnalysis<AltType> brAnalysis) {
		AbstractBenefitRiskSMAAFactory<AltType> smaaFactory = SMAAEntityFactory.createFactory(brAnalysis);
		List<Alternative> alts = Arrays.asList(
				smaaFactory.getAlternative(brAnalysis.getBaseline()),
				smaaFactory.getAlternative(brAnalysis.getNonBaselineAlternatives().get(0)));
		SMAAModel smaaModel = smaaFactory.createSMAAModel();
		smaaModel.reorderAlternatives(alts);
		d_measurements = smaaModel.getMeasurements();
	}

	public String getBenefitAxisName() {
		return d_benefitAxisName;
	}
	public String getRiskAxisName() {
		return d_riskAxisName;
	}
	
	private static final int BENEFIT = 0;
	private static final int RISK = 1;
	private static final int BASELINE = 0;
	private static final int SUBJECT = 1;
	
	public Sample nextSample(RandomUtil random) {
		double[][] sample = new double[2][2];
		d_measurements.sample(random, sample);
		return new Sample(d_benefitMultiplier * (sample[BENEFIT][SUBJECT] - sample[BENEFIT][BASELINE]),
				          d_riskMultiplier * (sample[RISK][SUBJECT] - sample[RISK][BASELINE]));
	}

}
