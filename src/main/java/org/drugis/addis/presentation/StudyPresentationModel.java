package org.drugis.addis.presentation;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class StudyPresentationModel extends PresentationModel<Study> {
	
	public StudyPresentationModel(Study s) {
		super(s);
	}
	
	public boolean isStudyFinished() {
		Object status = getBean().getCharacteristics().get(StudyCharacteristic.STATUS);
		if (status != null) {
			return status.equals(StudyCharacteristic.Status.FINISHED);
		}
		return false;
	}

}
