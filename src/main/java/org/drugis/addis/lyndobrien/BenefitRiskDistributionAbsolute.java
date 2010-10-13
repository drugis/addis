package org.drugis.addis.lyndobrien;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.addis.util.JSMAAintegration.SMAAEntityFactory;

import fi.smaa.jsmaa.model.CardinalMeasurement;

/**
 * Sample relative benefit and risk based on 2x2 absolute effect distributions.
 */
public class BenefitRiskDistributionAbsolute<Alternative extends Entity> implements BenefitRiskDistribution {
	private CardinalMeasurement d_subjBenefit;
	private CardinalMeasurement d_baseBenefit;
	private CardinalMeasurement d_subjRisk;
	private CardinalMeasurement d_baseRisk;
	private AxisType d_benefitAxisType;
	private AxisType d_riskAxisType;

	public BenefitRiskDistributionAbsolute(BenefitRiskAnalysis<Alternative> a) {
		Alternative alternative = a.getAlternatives().get(0);
		OutcomeMeasure benefit = a.getOutcomeMeasures().get(0);
		d_benefitAxisType = a.getMeasurement(alternative, benefit).getAxisType();
		d_baseBenefit = SMAAEntityFactory.createCardinalMeasurement(a.getMeasurement(alternative, benefit));

		alternative = a.getAlternatives().get(1);
		benefit = a.getOutcomeMeasures().get(0);
		d_subjBenefit = SMAAEntityFactory.createCardinalMeasurement(a.getMeasurement(alternative, benefit));

		alternative = a.getAlternatives().get(0);
		benefit = a.getOutcomeMeasures().get(1);
		d_riskAxisType = a.getMeasurement(alternative, benefit).getAxisType();
		d_baseRisk = SMAAEntityFactory.createCardinalMeasurement(a.getMeasurement(alternative, benefit));

		alternative = a.getAlternatives().get(1);
		benefit = a.getOutcomeMeasures().get(1);
		d_subjRisk = SMAAEntityFactory.createCardinalMeasurement(a.getMeasurement(alternative, benefit));
	}

	public AxisType getBenefitAxisType() {
		return d_benefitAxisType;
	}

	public AxisType getRiskAxisType() {
		return d_riskAxisType;
	}

	public Sample nextSample() {
		return new Sample(d_subjBenefit.sample() - d_baseBenefit.sample(), d_subjRisk.sample() - d_baseRisk.sample());
	}

}
