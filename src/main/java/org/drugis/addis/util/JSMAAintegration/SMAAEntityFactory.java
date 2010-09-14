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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.LogGaussian;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.CardinalCriterion;
import fi.smaa.jsmaa.model.CardinalMeasurement;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.LogNormalMeasurement;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.model.ScaleCriterion;

public class SMAAEntityFactory {
	
	private Map<OutcomeMeasure, CardinalCriterion> d_outcomeCriterionMap;
	private Map<Drug, Alternative> d_drugAlternativeMap;
	
	public SMAAEntityFactory() {
		d_outcomeCriterionMap = new HashMap<OutcomeMeasure, CardinalCriterion>();
		d_drugAlternativeMap  = new HashMap<Drug, Alternative>();
	}
	
	public static CardinalMeasurement createCardinalMeasurement(Distribution re) {
		GaussianBase gauss = (GaussianBase)re;
		if (re instanceof LogGaussian) {
			return new LogNormalMeasurement(gauss.getMu(), gauss.getSigma());
		} else if (re instanceof Gaussian) {
			return new GaussianMeasurement(gauss.getMu(), gauss.getSigma());
		} else
			throw new IllegalArgumentException("Unhandled distribution: " + re);
	}
	
	public SMAAModel createSmaaModel(MetaBenefitRiskAnalysis brAnalysis) {
		SMAAModel smaaModel = new SMAAModel(brAnalysis.getName());
				
		Collection<Drug> drugs = brAnalysis.getDrugs();
		for (Drug d: drugs) {
			smaaModel.addAlternative(getAlternative(d));
		}
		
		for (OutcomeMeasure om : brAnalysis.getOutcomeMeasures()) {
			CardinalCriterion crit = getCriterion(om);
			smaaModel.addCriterion(crit);
			
			for (Drug d : drugs) {
				CardinalMeasurement m = createCardinalMeasurement(brAnalysis.getMeasurement(d, om));
				smaaModel.setMeasurement(crit, getAlternative(d), m);
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
	
	Alternative getAlternative(Drug d) {
		if(d_drugAlternativeMap.containsKey(d))
			return d_drugAlternativeMap.get(d);
		Alternative a = new Alternative(d.getName());
		d_drugAlternativeMap.put(d, a);
		return a;
	}
}