package org.drugis.addis.mocks;

import java.util.List;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.LogGaussian;

public class MockBenefitRiskAnalysis extends BenefitRiskAnalysis {

	public MockBenefitRiskAnalysis(String id, Indication indication,
			List<OutcomeMeasure> outcomeMeasureList,
			List<MetaAnalysis> metaAnalysisList, Drug baseline,
			List<Drug> drugList) {
		super(id,indication,outcomeMeasureList,metaAnalysisList,baseline,drugList);
	}

	/**
	 * Get the assumed distribution for the baseline odds.
	 */
	public GaussianBase getBaselineDistribution(OutcomeMeasure om) {
		switch (om.getType()) {
			case RATE: return new LogGaussian(0.001, 0.0001);
			case CONTINUOUS: return new Gaussian(0.001, 0.0001);
		}
		return null;
	}

}
