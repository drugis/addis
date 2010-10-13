package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.lyndobrien.BenefitRiskDistributionAbsolute;
import org.drugis.addis.lyndobrien.LyndOBrienModel;
import org.drugis.addis.lyndobrien.LyndOBrienModelImpl;

public class LyndOBrienPresentation<Alternative extends Entity, AnalysisType extends BenefitRiskAnalysis<Alternative>> {

	AnalysisType d_a;
	StudyBenefitRiskAnalysis sbr;
	private LyndOBrienModelImpl d_model;
	
	public LyndOBrienPresentation(AnalysisType at) {
		d_model = new LyndOBrienModelImpl(new BenefitRiskDistributionAbsolute<Alternative>(at));
	}
	
	public LyndOBrienModel getModel() {
		return d_model;
	}

	public void startLyndOBrien() {
		new Thread(d_model).start();
	}
}
