package org.drugis.addis.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;

public class RandomEffectsMetaAnalysis implements Serializable {

	private class LogRiskRatioRandomEffects implements RelativeEffectRate {
		public RelativeEffect.AxisType getAxisType() {
			return AxisType.LOGARITHMIC;
		}

		public Interval<Double> getConfidenceInterval() {
			return d_confidenceInterval;
		}

		public Double getRelativeEffect() {
			return d_thetaDSL;
		}
		
		public Integer getSampleSize() {
			return d_totalSampleSize;
		}
		
		public Endpoint getEndpoint() {
			return d_ep;
		}

		public String getName() {
			return "Random Effects";
		}		

		public RateMeasurement getSubject() {
			throw new RuntimeException("Cannot get a Subject Measurement from Random Effects (Meta-Analysis)");
		}
		
		public RateMeasurement getBaseline() {
			throw new RuntimeException("Cannot get a Baseline Measurement from Random Effects (Meta-Analysis)");
		}		
	}
	private static final long serialVersionUID = 4587185353339731347L;
	private Endpoint d_ep;
	private List<Study> d_studies;
	private Drug d_drug1;
	private Drug d_drug2;	

	private int d_totalSampleSize;
	private double d_thetaDSL;
	private double d_SEThetaDSL;
	private Interval<Double> d_confidenceInterval;

	public RandomEffectsMetaAnalysis(List<Study> studies, Endpoint endpoint, Drug drug1, Drug drug2) {
		d_studies = studies;
		d_ep = endpoint;
		d_drug1 = drug1;
		d_drug2 = drug2;
		
		d_totalSampleSize = 0;
		
		compute();
	}
	
	private void compute() {
		List<Double> weights = new ArrayList<Double>();
		List<Double> adjweights = new ArrayList<Double>();
		List<LogRiskRatio> logRiskRatios = new ArrayList<LogRiskRatio>();
		
		// Fill the riskRatios list
		for (Study s : d_studies) {
			LogRiskRatio re = (LogRiskRatio) RelativeEffectFactory.buildRelativeEffect(s, d_ep, d_drug1, d_drug2, LogRiskRatio.class);
			d_totalSampleSize += re.getSampleSize();
			logRiskRatios.add(re);
		}
		
		// Calculate the weights.
		for (LogRiskRatio re : logRiskRatios) {
			weights.add(1D / Math.pow(re.getError(),2));
		}
		
		// Calculate needed variables.
		double thetaIV = getThetaIV(weights, logRiskRatios);
		double QIV = getQIV(weights, logRiskRatios, thetaIV);
		double tauSquared = getTauSquared(QIV, weights);
		
		// Calculated the adjusted Weights.
		for (LogRiskRatio re : logRiskRatios) {
			adjweights.add(1 / (Math.pow(re.getError(),2) + tauSquared) );
		}
		
		d_thetaDSL = getThetaDL(adjweights, logRiskRatios);
		
		d_SEThetaDSL = getSE_ThetaDL(adjweights);
		d_confidenceInterval = getConfidenceInterval();
	}
	
	private Interval<Double> getConfidenceInterval() {	
		double Z95percent = StudentTTable.getT(Integer.MAX_VALUE);
		double lower = d_thetaDSL - Z95percent * d_SEThetaDSL;
		double upper = d_thetaDSL + Z95percent * d_SEThetaDSL;
		return new Interval<Double>(lower, upper);
		
	}
	
	private double getSE_ThetaDL(List<Double> adjweights) {
		return 1.0 / (Math.sqrt(computeSum(adjweights)));
	}

	private double getThetaDL(List<Double> adjweights, List<LogRiskRatio> logRiskRatios) {
		double numerator = 0;
		for (int i=0; i < adjweights.size(); ++i) {
			numerator += adjweights.get(i) * logRiskRatios.get(i).getRelativeEffect();
		}
		
		return numerator / computeSum(adjweights);
	}
	
	private double getTauSquared(double Q, List<Double> weights) {
		double k = weights.size();
		double squaredWeightsSum = 0;
		for (int i=0;i<weights.size();i++) {
			squaredWeightsSum += Math.pow(weights.get(i),2);
		}
		
		double num = Q - (k - 1);
		double denum = computeSum(weights) - (squaredWeightsSum / computeSum(weights));
		return Math.max(num / denum, 0);
	}
	
	private double getQIV(List<Double> weights, List<LogRiskRatio> logRiskRatios, double thetaIV) {
		double sum = 0;
		for (int i=0; i < weights.size(); ++i) {
			sum += weights.get(i) * Math.pow(logRiskRatios.get(i).getRelativeEffect() - thetaIV,2);
		}
		return sum;
	}
	
	private double getThetaIV(List<Double> weights, List<LogRiskRatio> logRiskRatios) {
		assert(weights.size() == logRiskRatios.size());
		
		// Calculate the sums
		double sumWeightRatio = 0D;
			
		for (int i=0; i < weights.size(); ++i) {
			sumWeightRatio += weights.get(i) * logRiskRatios.get(i).getRelativeEffect();
		}
		
		return sumWeightRatio / computeSum(weights);
	}	
	
	public RelativeEffectRate getRiskRatio() {
		return new LogRiskRatioRandomEffects();
    }
	
	public double computeSum(List<Double> weights) {
		double weightSum = 0;
		for (int i=0; i < weights.size(); ++i) {
			weightSum += weights.get(i);
		}
		return weightSum;
	}	
}

