package org.drugis.addis.presentation;

import java.util.Arrays;
import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.relativeeffect.BasicRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffectFactory;

public class StudyForestPlotPresentation extends AbstractForestPlotPresentation {
	private Arm d_baseline;
	private Arm d_subject;

	public StudyForestPlotPresentation(OutcomeMeasure om, StudyArmsEntry studyArmsEntry, 
			Class<? extends RelativeEffect<?>> type) {
		super(om, Arrays.asList(studyArmsEntry.getStudy()),
				createRelativeEffect(om, studyArmsEntry, type), null);
		d_baseline = studyArmsEntry.getBase();
		d_subject = studyArmsEntry.getSubject();
	}

	private static List<BasicRelativeEffect<?>> createRelativeEffect(
			OutcomeMeasure om, StudyArmsEntry studyArmsEntry,
			Class<? extends RelativeEffect<?>> type) {
		final BasicRelativeEffect<?> re = (BasicRelativeEffect<?>)RelativeEffectFactory.buildRelativeEffect(studyArmsEntry, om, type, false);
		return Arrays.<BasicRelativeEffect<?>>asList(re);
	}

	protected String getBaselineLabel() {
		return d_baseline.getLabel();
	}

	protected String getSubjectLabel() {
		return d_subject.getLabel();
	}
}
