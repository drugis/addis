package org.drugis.addis.presentation;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.AbstractRatio;
import org.drugis.addis.entities.Study;

@SuppressWarnings("serial")
public class OddsRatioTableModel extends AbstractRatioTableModel {
	public OddsRatioTableModel(Study s, Endpoint e, PresentationModelFactory pmf) {
		super(s, e, pmf);
	}

	@Override
	protected AbstractRatio getRatio(Measurement denominator, Measurement numerator) {
		return new OddsRatio((RateMeasurement)denominator,
		(RateMeasurement)numerator);
	}

	@Override
	public String getTitle() {
		return "Odds-Ratio Table";
	}
}
