package org.drugis.addis.entities.relativeeffect;

import org.drugis.addis.entities.RateMeasurement;

public class CorrectedBasicOddsRatio extends BasicOddsRatio implements
		RelativeEffect<RateMeasurement> {

	public CorrectedBasicOddsRatio(RateMeasurement baseline,
			RateMeasurement subject) {
		super(baseline, subject);
	}

	public CorrectedBasicOddsRatio(BasicOddsRatio bor) {
		this(bor.getBaseline(), bor.getSubject());
	}

	@Override
	public boolean isDefined() {
		return (getDegreesOfFreedom() > 0) &&
			(getA() != 0.5 || getC() != 0.5) && 
			(getB() != 0.5 || getD() != 0.5);
	}
	
	@Override
	protected double getA() {
		return super.getA() + 0.5;
	}
	
	@Override
	protected double getB() {
		return getSubject().getSampleSize() - getSubject().getRate() + 0.5;
	}

	@Override
	protected double getC() {
		return getBaseline().getRate() + 0.5;
	}

	@Override
	protected double getD() {
		return getBaseline().getSampleSize() - getBaseline().getRate() + 0.5;
	}
}
