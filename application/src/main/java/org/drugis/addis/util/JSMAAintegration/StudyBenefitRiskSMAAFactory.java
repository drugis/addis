package org.drugis.addis.util.JSMAAintegration;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
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
	public SMAAModel createStudyBenefitRiskModel(StudyBenefitRiskAnalysis brAnalysis) {
		SMAAModel smaaModel = new SMAAModel(brAnalysis.getName());
		addCriteriaAndAlternatives(smaaModel, brAnalysis);

		IndependentMeasurements measurements = (IndependentMeasurements) smaaModel.getMeasurements();
		
		for (OutcomeMeasure om : brAnalysis.getCriteria()) {
			for (Arm a : brAnalysis.getAlternatives()) {
				CardinalMeasurement m = createMeasurement(brAnalysis.getMeasurement(om, a));
				measurements.setMeasurement(getCriterion(om), getAlternative(brAnalysis, a), m);
			}
		}
		
		return smaaModel;
	}
	
	@Override
	protected Alternative createAlternative(BenefitRiskAnalysis<Arm> brAnalysis, Arm arm) {
		StudyBenefitRiskAnalysis studyBr = (StudyBenefitRiskAnalysis) brAnalysis;
		return new Alternative(studyBr.getStudy().getTreatment(arm).getLabel());
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
