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

package org.drugis.addis.util.JSMAAintegration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.Beta;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.LogGaussian;
import org.drugis.addis.entities.relativeeffect.TransformedStudentT;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.BetaMeasurement;
import fi.smaa.jsmaa.model.CardinalCriterion;
import fi.smaa.jsmaa.model.CardinalMeasurement;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.LogNormalMeasurement;
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
			if (re instanceof LogGaussian) {
				return new LogNormalMeasurement(gauss.getMu(), gauss.getSigma());
			} else if (re instanceof Gaussian) {
				return new GaussianMeasurement(gauss.getMu(), gauss.getSigma());
			} else {
				throw new IllegalArgumentException("Unhandled distribution: " + re);
			}
		} else if (re instanceof TransformedStudentT) {
			TransformedStudentT studentt = (TransformedStudentT) re;
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
			smaaModel.addAlternative(getAlternative(a));
		}
		
		for (OutcomeMeasure om : brAnalysis.getOutcomeMeasures()) {
			CardinalCriterion crit = getCriterion(om);
			smaaModel.addCriterion(crit);
			
			for (AltType a : brAnalysis.getAlternatives()) {
				CardinalMeasurement m = createCardinalMeasurement(brAnalysis.getMeasurement(a, om));
				smaaModel.setMeasurement(crit, getAlternative(a), m);
			}
		}
		return smaaModel;
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
	
	Alternative getAlternative(AltType a2) {
		if(d_entityAlternativeMap.containsKey(a2))
			return d_entityAlternativeMap.get(a2);
		Alternative a = null;
		if (a2 instanceof Arm) {
			Arm arm = (Arm) a2;
			a = new Alternative(arm.getDrug() + " " + arm.getDose());
		} else if (a2 instanceof Drug) {
			Drug drug = (Drug) a2;
			a = new Alternative(drug.getName());
		} else {
			a = new Alternative(a2.toString());
		}
		d_entityAlternativeMap.put(a2, a);
		return a;
	}
}