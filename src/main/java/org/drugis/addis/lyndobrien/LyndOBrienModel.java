package org.drugis.addis.lyndobrien;

import org.drugis.mtc.MCMCModel;

public interface LyndOBrienModel extends MCMCModel {
	/**
	 * Get the i-th BR sample.
	 */
	public abstract BenefitRiskDistribution.Sample getData(int i);
}