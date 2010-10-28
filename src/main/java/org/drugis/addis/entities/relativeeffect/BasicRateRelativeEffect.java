package org.drugis.addis.entities.relativeeffect;

import org.drugis.addis.entities.RateMeasurement;

public interface BasicRateRelativeEffect {
	public RelativeEffect<RateMeasurement> getCorrected();
}
