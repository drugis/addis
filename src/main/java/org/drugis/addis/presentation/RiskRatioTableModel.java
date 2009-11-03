package org.drugis.addis.presentation;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.AbstractRatio;
import org.drugis.addis.entities.RiskRatio;
import org.drugis.addis.entities.Study;

@SuppressWarnings("serial")
public class RiskRatioTableModel extends AbstractRatioTableModel {

	public RiskRatioTableModel(Study study, Endpoint endpoint,
			PresentationModelFactory pmf) {
		super(study, endpoint, pmf);
	}

	@Override
	protected AbstractRatio getRatio(Measurement denominator, Measurement numerator) {
		return new RiskRatio((RateMeasurement)denominator, (RateMeasurement)numerator);
	}

	@Override
	public String getTitle() {
		return "Risk-Ratio Table";
	}

}
