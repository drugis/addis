package org.drugis.addis.presentation;

import org.drugis.addis.entities.AbstractRelativeEffect;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.LogOddsRatio;

@SuppressWarnings("serial")
public class LogOddsRatioTableModel extends OddsRatioTableModel {
	
	public LogOddsRatioTableModel(Study s, OutcomeMeasure om, PresentationModelFactory pmf) {
		super(s, om, pmf);
	}

	@Override
	protected AbstractRelativeEffect<RateMeasurement> getRelativeEffect(Measurement denominator, Measurement numerator) {
		return new LogOddsRatio((RateMeasurement)denominator,
		(RateMeasurement)numerator);
	}

	@Override
	public String getTitle() {
		return "log Odds-Ratio Table";
	}

	@Override
	protected Class<? extends RelativeEffect<?>> getRelativeEffectType() {
		return LogOddsRatio.class;
	}
}
