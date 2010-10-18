package org.drugis.addis.lyndobrien;

import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.mtc.MCMCModel;

public interface LyndOBrienModel extends MCMCModel {
	/**
	 * Get the i-th BR sample.
	 */
	public abstract BenefitRiskDistribution.Sample getData(int i);
	public String getXAxisName();
	public String getYAxisName();
	
	public AxisType getBenefitAxisType();
	public AxisType getRiskAxisType();

	public double getPValue(double mu);
	
}