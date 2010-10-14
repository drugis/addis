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
public class BenefitRiskDistributionImpl<Alternative extends Entity> implements BenefitRiskDistribution {
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
//		if(d_baseBenefit instanceof Beta) {}
		d_benefitAxisName += "\u0394(" + a.getOutcomeMeasures().get(0).getName() + ")";
		d_riskAxisName += "\u0394(" + a.getOutcomeMeasures().get(1).getName() + ")";
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
