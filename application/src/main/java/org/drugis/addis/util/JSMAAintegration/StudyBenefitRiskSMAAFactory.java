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
