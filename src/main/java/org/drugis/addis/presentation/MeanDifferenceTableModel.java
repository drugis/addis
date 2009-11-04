package org.drugis.addis.presentation;

import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.MeanDifference;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.Study;

@SuppressWarnings("serial")
public class MeanDifferenceTableModel extends AbstractRatioTableModel{

	public MeanDifferenceTableModel(Study study, Endpoint endpoint,
			PresentationModelFactory pmf) {
		super(study, endpoint, pmf);
	}

	@Override
	protected RelativeEffect<ContinuousMeasurement> getRatio(Measurement baseline,
			Measurement subject) {
		return new MeanDifference((ContinuousMeasurement) baseline, (ContinuousMeasurement) subject);
	}

	@Override
	public String getTitle() {
		return "Mean-Difference Table";
	}

}
