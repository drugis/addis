package org.drugis.addis.presentation;

import org.drugis.addis.entities.AbstractRelativeEffect;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.Study;

@SuppressWarnings("serial")
public class OddsRatioTableModel extends AbstractRelativeEffectTableModel {
	public OddsRatioTableModel(Study s, Endpoint e, PresentationModelFactory pmf) {
		super(s, e, pmf);
	}

	@Override
	protected AbstractRelativeEffect<RateMeasurement> getRelativeEffect(Measurement denominator, Measurement numerator) {
		return new OddsRatio((RateMeasurement)denominator,
		(RateMeasurement)numerator);
	}

	@Override
	public String getTitle() {
		return "Odds-Ratio Table";
	}

	@Override
	protected Class<? extends RelativeEffect<?>> getRelativeEffectType() {
		return OddsRatio.class;
	}
}
