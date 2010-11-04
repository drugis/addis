package org.drugis.addis.entities.relativeeffect;

import org.drugis.addis.entities.RateMeasurement;

public class CorrectedBasicRiskDifference extends BasicRiskDifference implements
		RelativeEffect<RateMeasurement> {

	public CorrectedBasicRiskDifference(RateMeasurement baseline,
			RateMeasurement subject) {
		super(baseline, subject);
	}

	public CorrectedBasicRiskDifference(BasicRiskDifference basicRiskDifference) {
		this(basicRiskDifference.getBaseline(), basicRiskDifference.getSubject());
	}

	@Override
	public String getName() {
		return "Risk Difference (correced for zeroes)";
	}

	
	@Override
	public boolean isDefined() {
		return super.isDefined() &&
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
