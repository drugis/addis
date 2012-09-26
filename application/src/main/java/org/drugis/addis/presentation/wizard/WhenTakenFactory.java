package org.drugis.addis.presentation.wizard;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.WhenTaken;

public class WhenTakenFactory {
	public Study study;

	public WhenTakenFactory(Study study) {
		this.study = study;
	}

	public WhenTaken buildDefault() {
		WhenTaken whenTaken = study.defaultMeasurementMoment();
		if (whenTaken == null) {
			return null;
		}
		whenTaken.commit();
		return whenTaken;
	}
}