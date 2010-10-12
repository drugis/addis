package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.Distribution;

public class LyndOBrienPresentation<Alternative extends Entity, AnalysisType extends BenefitRiskAnalysis<Alternative>> {

	AnalysisType d_a;
	StudyBenefitRiskAnalysis sbr;
	
	public LyndOBrienPresentation(AnalysisType at) {
		d_a = at;
		List<Distribution> dists = new ArrayList<Distribution>();
		for(Alternative a: d_a.getAlternatives()) {
			for(OutcomeMeasure om: d_a.getOutcomeMeasures()) {
				dists.add(d_a.getMeasurement(a, om));
			}
		}
		
	}

	public void startLyndOBrien() {
		
	}
}
