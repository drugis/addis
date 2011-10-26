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

package org.drugis.addis.util.JSMAAintegration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.Beta;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.LogGaussian;
import org.drugis.addis.entities.relativeeffect.LogitGaussian;
import org.drugis.addis.entities.relativeeffect.TransformedStudentT;
import org.drugis.addis.entities.relativeeffect.TransformedStudentTBase;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.BaselineGaussianMeasurement;
import fi.smaa.jsmaa.model.BetaMeasurement;
import fi.smaa.jsmaa.model.CardinalCriterion;
import fi.smaa.jsmaa.model.CardinalMeasurement;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.LogNormalMeasurement;
import fi.smaa.jsmaa.model.LogitNormalMeasurement;
import fi.smaa.jsmaa.model.RelativeLogitNormalMeasurement;
import fi.smaa.jsmaa.model.RelativeNormalMeasurement;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.model.ScaleCriterion;

public class SMAAEntityFactory<AltType extends Entity> {
	
	private Map<OutcomeMeasure, CardinalCriterion> d_outcomeCriterionMap;
	private Map<AltType, Alternative> d_entityAlternativeMap;
	
	public SMAAEntityFactory() {
		d_outcomeCriterionMap = new HashMap<OutcomeMeasure, CardinalCriterion>();
		d_entityAlternativeMap  = new HashMap<AltType, Alternative>();
	}
	
	public static CardinalMeasurement createCardinalMeasurement(Distribution re) {
		if (re instanceof GaussianBase) {
			GaussianBase gauss = (GaussianBase)re;
			if (re instanceof LogitGaussian) {
				return new LogitNormalMeasurement(gauss.getMu(), gauss.getSigma());
			} else if (re instanceof LogGaussian) {
				return new LogNormalMeasurement(gauss.getMu(), gauss.getSigma());
			} else if (re instanceof Gaussian) {
				return new GaussianMeasurement(gauss.getMu(), gauss.getSigma());
			} else {
				throw new IllegalArgumentException("Unhandled distribution: " + re);
			}
		} else if (re instanceof TransformedStudentT) {
			TransformedStudentTBase studentt = (TransformedStudentTBase) re;
			return new GaussianMeasurement(studentt.getMu(), studentt.getSigma());
		} else if (re instanceof Beta) {
			Beta beta = (Beta) re;
			return new BetaMeasurement(beta.getAlpha(), beta.getBeta(), 0, 1);
		}
		throw new IllegalArgumentException("Unhandled distribution: " + re);
	}
	
	public SMAAModel createSmaaModel(BenefitRiskAnalysis<AltType> brAnalysis) {
		SMAAModel smaaModel = new SMAAModel(brAnalysis.getName());
				
		for (AltType a : brAnalysis.getAlternatives()) {
			smaaModel.addAlternative(getAlternative(brAnalysis, a));
		}
		
		for (OutcomeMeasure om : brAnalysis.getCriteria()) {
			CardinalCriterion crit = getCriterion(om);
			smaaModel.addCriterion(crit);
			if (brAnalysis instanceof MetaBenefitRiskAnalysis) {
				MetaBenefitRiskAnalysis mbr = (MetaBenefitRiskAnalysis)brAnalysis;
				smaaModel.getImpactMatrix().setBaseline(crit, new BaselineGaussianMeasurement(
						mbr.getBaselineDistribution(om).getMu(), mbr.getBaselineDistribution(om).getSigma()));
			}
			
			for (AltType a : brAnalysis.getAlternatives()) {
				if (brAnalysis instanceof MetaBenefitRiskAnalysis) {
					MetaBenefitRiskAnalysis mbr = (MetaBenefitRiskAnalysis)brAnalysis;
					BaselineGaussianMeasurement baseline = smaaModel.getImpactMatrix().getBaseline(crit);
					GaussianMeasurement relative = new GaussianMeasurement(
							mbr.getRelativeEffectDistribution((DrugSet) a, om).getMu(),
							mbr.getRelativeEffectDistribution((DrugSet) a, om).getSigma());
					CardinalMeasurement m = null; 
					if (om.getVariableType() instanceof RateVariableType) {
						m = new RelativeLogitNormalMeasurement(baseline, relative);
					} else if (om.getVariableType() instanceof ContinuousVariableType) {
						m = new RelativeNormalMeasurement(baseline, relative);
					}
					smaaModel.setMeasurement(crit, getAlternative(brAnalysis, a), m);
				} else {
					CardinalMeasurement m = createCardinalMeasurement(brAnalysis.getMeasurement(om, a));
					smaaModel.setMeasurement(crit, getAlternative(brAnalysis, a), m);
				}
			}
		}
		return smaaModel;
	}

	private Alternative getAlternative(BenefitRiskAnalysis<AltType> brAnalysis,
			AltType a) {
		Alternative alternative;
		if (brAnalysis instanceof StudyBenefitRiskAnalysis) {
			alternative = getAlternative(((StudyBenefitRiskAnalysis)brAnalysis).getStudy(), (Arm)a);
		} else {
			alternative = getAlternative((DrugSet)a);
		}
		return alternative;
	}
	
	CardinalCriterion getCriterion(OutcomeMeasure om) {
		if(d_outcomeCriterionMap.containsKey(om))
			return d_outcomeCriterionMap.get(om);
		ScaleCriterion c = new ScaleCriterion(om.getName());
		c.setAscending(om.getDirection() == Direction.HIGHER_IS_BETTER);
		d_outcomeCriterionMap.put(om, c);
		return c;
	}
	
	public OutcomeMeasure getOutcomeMeasure(CardinalCriterion crit) {
		for (Entry<OutcomeMeasure, CardinalCriterion> entry : d_outcomeCriterionMap.entrySet()) {
			if (entry.getValue().equals(crit)) {
				return entry.getKey();
			}
		}
		return null; 
	}
	
	@SuppressWarnings("unchecked")
	Alternative getAlternative(Study study, Arm arm) {
		if(d_entityAlternativeMap.containsKey(arm))
			return d_entityAlternativeMap.get(arm);
		Alternative a = new Alternative(study.getTreatment(arm).getLabel());
		d_entityAlternativeMap.put((AltType) arm, a);
		return a;
	}
	
	@SuppressWarnings("unchecked")
	Alternative getAlternative(DrugSet drugs) {
		if(d_entityAlternativeMap.containsKey(drugs))
			return d_entityAlternativeMap.get(drugs);

		Alternative a = new Alternative(drugs.getLabel());
		d_entityAlternativeMap.put((AltType)drugs, a);
		return a;
	}
}