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
