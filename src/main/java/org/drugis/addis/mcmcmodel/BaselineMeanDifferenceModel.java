package org.drugis.addis.mcmcmodel;

import gov.lanl.yadas.ArgumentMaker;
import gov.lanl.yadas.BasicMCMCBond;
import gov.lanl.yadas.ConstantArgument;
import gov.lanl.yadas.IdentityArgument;
import gov.lanl.yadas.MCMCParameter;

import java.util.List;

import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.relativeeffect.Gaussian;

public class BaselineMeanDifferenceModel extends AbstractBaselineModel<ContinuousMeasurement> {
	public BaselineMeanDifferenceModel(List<ContinuousMeasurement> measurements) {
		super(measurements);
	}

	@Override
	protected void createDataBond(MCMCParameter studyMu) {
		new BasicMCMCBond(new MCMCParameter[] {studyMu},
				new ArgumentMaker[] {
					new ConstantArgument(meanArray()),
					new IdentityArgument(0),
					new ConstantArgument(standardErrorArray())  
				}, new gov.lanl.yadas.Gaussian());
	}

	private double[] standardErrorArray() {
		double[] arr = new double[d_measurements.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = d_measurements.get(i).getStdDev() / Math.sqrt(d_measurements.get(i).getSampleSize());
		}
		return arr;
	}

	private double[] meanArray() {
		double[] arr = new double[d_measurements.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = d_measurements.get(i).getMean();
		}
		return arr;
	}

	@Override
	public Gaussian getResult() {
		return new Gaussian(getMean(), getStdDev());
	}

	@Override
	protected double getStdDevPrior() {
		return 20.0; // FIXME
	}
}
