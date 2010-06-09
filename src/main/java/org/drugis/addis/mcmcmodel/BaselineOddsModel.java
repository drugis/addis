package org.drugis.addis.mcmcmodel;

import gov.lanl.yadas.ArgumentMaker;
import gov.lanl.yadas.BasicMCMCBond;
import gov.lanl.yadas.Binomial;
import gov.lanl.yadas.ConstantArgument;
import gov.lanl.yadas.MCMCParameter;

import java.util.List;

import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.relativeeffect.LogGaussian;

//FIXME: allow reuse of ProgressObservable from MTC
public class BaselineOddsModel extends AbstractBaselineModel<RateMeasurement> {
	public BaselineOddsModel(List<RateMeasurement> measurements) {
		super(measurements);
	}

	@Override
	protected void createDataBond(MCMCParameter studyMu) {
		new BasicMCMCBond(new MCMCParameter[] {studyMu},
				new ArgumentMaker[] {
					new ConstantArgument(rateArray()),
					new ConstantArgument(sampleSizeArray()),  
					new InverseLogitArgumentMaker(0)
				}, new Binomial());
	}

	@Override
	protected double getStdDevPrior() {
		return 2.0;
	}

	@Override
	public LogGaussian getResult() {
		return new LogGaussian(getMean(), getStdDev());
	}

	private double[] sampleSizeArray() {
		double[] arr = new double[d_measurements.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = d_measurements.get(i).getSampleSize();
		}
		return arr;
	}

	private double[] rateArray() {
		double[] arr = new double[d_measurements.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = d_measurements.get(i).getRate();
		}
		return arr;
	}
}
