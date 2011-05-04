package org.drugis.addis.gui;

import org.drugis.addis.entities.PredefinedActivity;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.presentation.ModifiableHolder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;

public class AddStudyActivityPresentationModel extends PresentationModel<StudyActivity> {
	private static final long serialVersionUID = -2471695041884415688L;

	private class StudyActivityValueHolder extends ModifiableHolder<StudyActivity> {
		private static final long serialVersionUID = -1601203195395456237L;

		public StudyActivityValueHolder(StudyActivity t) {
			super(t);
		}
		
		@Override
		public void setValue(Object newValue) {
			StudyActivity bean = getBean();
			if (bean.getActivity() instanceof TreatmentActivity) {
				// do one thing
			} else if (bean.getActivity() instanceof PredefinedActivity) {
				// do another
			}
		}
	}
	
	public AddStudyActivityPresentationModel(StudyActivity bean) {
		super(bean);
	}
	
	ValueModel getActivityTypeModel() {
		return new StudyActivityValueHolder(getBean());
	}
}
