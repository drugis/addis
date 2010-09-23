package org.drugis.addis.entities.analysis;

import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;

public class MockStudyBenefitRiskAnalysis extends StudyBenefitRiskAnalysis {
	public MockStudyBenefitRiskAnalysis(String name, Indication indication, Study study, 
			List<OutcomeMeasure> criteria, List<Arm> alternatives) {
		super(name,indication, study, criteria,alternatives);
	}
}
