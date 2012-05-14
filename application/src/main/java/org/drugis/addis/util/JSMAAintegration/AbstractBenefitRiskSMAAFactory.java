/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.CardinalCriterion;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.model.ScaleCriterion;

public abstract class AbstractBenefitRiskSMAAFactory<AltType extends Entity> {
	private Map<OutcomeMeasure, ScaleCriterion> d_outcomeCriterionMap = new HashMap<OutcomeMeasure, ScaleCriterion>();
	private Map<AltType, Alternative> d_entityAlternativeMap = new HashMap<AltType, Alternative>();

	public abstract SMAAModel createSMAAModel();

	protected void addCriteriaAndAlternatives(SMAAModel smaaModel, BenefitRiskAnalysis<AltType> brAnalysis) {
		for (AltType a : brAnalysis.getAlternatives()) {
			smaaModel.addAlternative(getAlternative(a));
		}
		for (OutcomeMeasure om : brAnalysis.getCriteria()) {
			CardinalCriterion crit = getCriterion(om);
			smaaModel.addCriterion(crit);
		}
	}

	public Alternative getAlternative(AltType a) {
		if(d_entityAlternativeMap.containsKey(a)) {
			return d_entityAlternativeMap.get(a);
		}
		Alternative alt = createAlternative(a);
		d_entityAlternativeMap.put(a, alt);
		return alt;
	}

	protected abstract Alternative createAlternative(AltType a);

	public ScaleCriterion getCriterion(OutcomeMeasure om) {
		if(d_outcomeCriterionMap.containsKey(om)) {
			return d_outcomeCriterionMap.get(om);
		}
		ScaleCriterion c = new ScaleCriterion(om.getName());
		c.setAscending(Direction.HIGHER_IS_BETTER.equals(om.getDirection()));
		d_outcomeCriterionMap.put(om, c);
		return c;
	}

	public OutcomeMeasure getOutcomeMeasure(CardinalCriterion crit) {
		for (Entry<OutcomeMeasure, ScaleCriterion> entry : d_outcomeCriterionMap.entrySet()) {
			if (entry.getValue().equals(crit)) {
				return entry.getKey();
			}
		}
		return null;
	}
}
