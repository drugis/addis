package org.drugis.addis.mcmcmodel;

import gov.lanl.yadas.ArgumentMaker;


public class InverseLogitArgumentMaker implements ArgumentMaker{

	private final int d_idx;

	public InverseLogitArgumentMaker(int idx) {
		d_idx = idx;
		
	}
	
	public double[] getArgument(double[][] params) {
		double[] result = new double[params[d_idx].length];
		for (int i = 0; i < result.length; ++i) {
			result[i] =  MathUtil.ilogit(params[d_idx][i]);
		}
		return result;
	}

}
