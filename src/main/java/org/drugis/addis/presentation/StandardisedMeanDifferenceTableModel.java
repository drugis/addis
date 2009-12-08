package org.drugis.addis.presentation;

import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.StandardisedMeanDifference;
import org.drugis.addis.entities.Study;

@SuppressWarnings("serial")
public class StandardisedMeanDifferenceTableModel extends AbstractRelativeEffectTableModel{
	public StandardisedMeanDifferenceTableModel(Study study, Endpoint endpoint,
			PresentationModelFactory pmf) {
		super(study, endpoint, pmf);
	}

	@Override
	protected RelativeEffect<ContinuousMeasurement> getRelativeEffect(Measurement baseline,
			Measurement subject) {
		return new StandardisedMeanDifference((ContinuousMeasurement) subject, (ContinuousMeasurement) baseline);
	}

	@Override
	public String getTitle() {
		return "Standardised Mean Difference Table";
	}

	@Override
	protected Class<? extends RelativeEffect<?>> getRelativeEffectType() {
		return StandardisedMeanDifference.class;
	}

}
