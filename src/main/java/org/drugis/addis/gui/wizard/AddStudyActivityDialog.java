package org.drugis.addis.gui.wizard;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.PredefinedActivity;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.presentation.wizard.AddStudyWizardPresentation;
import org.drugis.common.gui.OkCancelDialog;

public class AddStudyActivityDialog extends OkCancelDialog {
	private static final long serialVersionUID = 325928004747685827L;
	private final AddStudyWizardPresentation d_pm;
	private String d_activityType;
	private String d_drugName;
	private String d_atcCode;
	private String d_name;
	
	public AddStudyActivityDialog(AddStudyWizardPresentation pm) {
		d_pm = pm;
	}

	@Override
	protected void cancel() {
		setVisible(false);
	}

	@Override
	protected void commit() {
		StudyActivity newActivity;
		if (d_activityType.equals("Treatment")) {
			Drug drug = new Drug(d_drugName, d_atcCode);
			AbstractDose dose = new FixedDose(12.5, SIUnit.MILLIGRAMS_A_DAY);
			TreatmentActivity treatmentActivity = new TreatmentActivity(drug, dose);
			newActivity = new StudyActivity(d_name, treatmentActivity);
		} else {
			newActivity = new StudyActivity(d_name, PredefinedActivity.RANDOMIZATION);
		}
				
		d_pm.getNewStudyPM().getBean().getStudyActivities().add(newActivity);
	}

}
