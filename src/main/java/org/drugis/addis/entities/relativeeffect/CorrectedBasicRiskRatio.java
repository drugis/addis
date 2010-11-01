package org.drugis.addis.entities.relativeeffect;

import org.drugis.addis.entities.RateMeasurement;

public class CorrectedBasicRiskRatio extends BasicRiskRatio implements
		RelativeEffect<RateMeasurement> {

	public CorrectedBasicRiskRatio(RateMeasurement baseline,
			RateMeasurement subject) {
		super(baseline, subject);
	}

	public CorrectedBasicRiskRatio(BasicRiskRatio brr) {
		this(brr.getBaseline(), brr.getSubject());
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
