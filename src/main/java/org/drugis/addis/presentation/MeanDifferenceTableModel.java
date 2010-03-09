package org.drugis.addis.presentation;

import java.util.List;

import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.MeanDifference;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.Study;

@SuppressWarnings("serial")
public class MeanDifferenceTableModel extends AbstractRelativeEffectTableModel{

	public MeanDifferenceTableModel(Study study, OutcomeMeasure om,
			PresentationModelFactory pmf) {
		super(study, om, pmf);
	}

	public MeanDifferenceTableModel(List<Drug> drugList,
			OutcomeMeasure outcomeMeasure,
			PresentationModelFactory presentationModelFactory) {
		super(drugList, outcomeMeasure, presentationModelFactory);
	}

	@Override
	protected RelativeEffect<ContinuousMeasurement> getRelativeEffect(Measurement baseline,
			Measurement subject) {
		return new MeanDifference((ContinuousMeasurement) baseline, (ContinuousMeasurement) subject);
	}

	@Override
	public String getTitle() {
		return "Mean-Difference Table";
	}

	@Override
	protected Class<? extends RelativeEffect<?>> getRelativeEffectType() {
		return MeanDifference.class;
	}

}
