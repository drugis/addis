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

package org.drugis.addis.lyndobrien;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.addis.entities.relativeeffect.Beta;
import org.drugis.addis.entities.relativeeffect.LogGaussian;
import org.drugis.addis.util.JSMAAintegration.SMAAEntityFactory;
import fi.smaa.jsmaa.model.CardinalMeasurement;

/**
 * Sample relative benefit and risk based on 2x2 absolute effect distributions.
 */
public class BenefitRiskDistributionImpl<Alternative extends Entity> implements BenefitRiskDistribution{
	private CardinalMeasurement d_subjBenefit;
	private CardinalMeasurement d_baseBenefit;
	private CardinalMeasurement d_subjRisk;
	private CardinalMeasurement d_baseRisk;
	private AxisType d_benefitAxisType;
	private AxisType d_riskAxisType;
	private String d_benefitAxisName;
	private String d_riskAxisName;
	private int d_benefitMultiplier;
	private int d_riskMultiplier;

	public BenefitRiskDistributionImpl(BenefitRiskAnalysis<Alternative> a) {
		Alternative alternative = a.getAlternatives().get(0);
		OutcomeMeasure criterion = a.getOutcomeMeasures().get(0);
		d_benefitAxisType = a.getMeasurement(alternative, criterion).getAxisType();
		d_baseBenefit = SMAAEntityFactory.createCardinalMeasurement(a.getMeasurement(alternative, criterion));

		alternative = a.getAlternatives().get(1);
		criterion = a.getOutcomeMeasures().get(0);
		d_subjBenefit = SMAAEntityFactory.createCardinalMeasurement(a.getMeasurement(alternative, criterion));

		alternative = a.getAlternatives().get(0);
		criterion = a.getOutcomeMeasures().get(1);
		d_riskAxisType = a.getMeasurement(alternative, criterion).getAxisType();
		d_baseRisk = SMAAEntityFactory.createCardinalMeasurement(a.getMeasurement(alternative, criterion));

		alternative = a.getAlternatives().get(1);
		criterion = a.getOutcomeMeasures().get(1);
		d_subjRisk = SMAAEntityFactory.createCardinalMeasurement(a.getMeasurement(alternative, criterion));
		
		switch(a.getOutcomeMeasures().get(0).getDirection()) {
		case HIGHER_IS_BETTER:
			d_benefitAxisName = "";
			d_benefitMultiplier = 1;
			break;
		case LOWER_IS_BETTER:
			d_benefitAxisName = "-";
			d_benefitMultiplier = -1;
			break;
		}
		switch(a.getOutcomeMeasures().get(1).getDirection()) {
		case HIGHER_IS_BETTER:
			d_riskAxisName = "-";
			d_riskMultiplier = -1;
			break;
		case LOWER_IS_BETTER:
			d_riskAxisName = "";
			d_riskMultiplier = 1;
			break;
		}
		
		alternative = a.getAlternatives().get(0);
		criterion = a.getOutcomeMeasures().get(0);
		
		if(a.getMeasurement(alternative, criterion) instanceof Beta) {
			d_benefitAxisName += "\u0394 Incidence(" + a.getOutcomeMeasures().get(0).getName() + ")";
		} else if (a.getMeasurement(alternative, criterion) instanceof LogGaussian) {
			d_benefitAxisName += "Log OR (" + a.getOutcomeMeasures().get(0).getName() + ")";
		} else {
			d_benefitAxisName += "\u0394(" + a.getOutcomeMeasures().get(0).getName() + ")";
		}
		
		criterion = a.getOutcomeMeasures().get(1);
		if(a.getMeasurement(alternative, criterion) instanceof Beta) {
			d_riskAxisName += "\u0394 Incidence(" + a.getOutcomeMeasures().get(1).getName() + ")";
		} else if (a.getMeasurement(alternative, criterion) instanceof LogGaussian) {
			d_riskAxisName += "Log OR (" + a.getOutcomeMeasures().get(1).getName() + ")";
		} else {
			d_riskAxisName += "\u0394(" + a.getOutcomeMeasures().get(1).getName() + ")";
		}		
	}

	public AxisType getBenefitAxisType() {
		return d_benefitAxisType;
	}

	public AxisType getRiskAxisType() {
		return d_riskAxisType;
	}

	public String getBenefitAxisName() {
		return d_benefitAxisName;
	}
	public String getRiskAxisName() {
		return d_riskAxisName;
	}

	
	public Sample nextSample() {
		return new Sample(d_benefitMultiplier * (d_subjBenefit.sample() - d_baseBenefit.sample()),
				          d_riskMultiplier * (d_subjRisk.sample() - d_baseRisk.sample()));
	}

}
