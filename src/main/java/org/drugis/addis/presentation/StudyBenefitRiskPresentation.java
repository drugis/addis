package org.drugis.addis.presentation;

import javax.swing.table.TableModel;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;

@SuppressWarnings("serial")
public class StudyBenefitRiskPresentation extends
		BenefitRiskPresentation<Arm, StudyBenefitRiskAnalysis> {

	public StudyBenefitRiskPresentation(StudyBenefitRiskAnalysis bean, PresentationModelFactory pmf) {
		super(bean, pmf);
	}

	public TableModel getMeasurementTableModel() {
		return new BenefitRiskMeasurementTableModel<Arm>(getBean(), getBean().getAbsoluteMeasurementSource() , d_pmf);
	}

}
