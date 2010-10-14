package org.drugis.addis.lyndobrien;

import org.drugis.addis.entities.relativeeffect.AxisType;

public interface BenefitRiskDistribution {
	public class Sample {
		public final double risk;
		public final double benefit;
		
		public Sample(double benefit, double risk) {
			this.benefit = benefit;
			this.risk = risk;
		}
	}
	
	public Sample nextSample();
	public AxisType getBenefitAxisType();
	public AxisType getRiskAxisType();
	public String getBenefitAxisName();
	public String getRiskAxisName();
}
