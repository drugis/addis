package org.drugis.addis.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drugis.common.Interval;

public class RandomEffectsMetaAnalysis implements Serializable {
	private static final long serialVersionUID = 4587185353339731347L;
	private Endpoint d_ep;
	private List<Study> d_studies;
	private Drug d_drug1;
	private Drug d_drug2;	
	private List<RiskRatio> d_riskRatios;
	private List<Double> d_adjweights;
	private double d_thetaDL;
	private Interval<Double> d_confidenceInterval;

	public RandomEffectsMetaAnalysis(Endpoint endpoint, List<Study> studies, Drug drug1, Drug drug2) {
		d_studies = studies;
		d_ep = endpoint;
		d_drug1 = drug1;
		d_drug2 = drug2;
		
		compute();
	}
	
	private void compute() {
		List<Double> weights = new ArrayList<Double>();
		d_adjweights = new ArrayList<Double>();
		d_riskRatios = new ArrayList<RiskRatio>();
		
		// Fill the riskRatios list
		for (Study s : d_studies) {
			d_riskRatios.add((RiskRatio) RelativeEffectFactory.buildRelativeEffect(s, d_ep, d_drug1, d_drug2, RiskRatio.class));
		}
		
		// Calculate the weights.
		for (RiskRatio re : d_riskRatios) {
			weights.add(1D / Math.pow(re.getError(),2));
		}
		
		// Calculate needed variables.
		double thetaIV = getThetaIV(weights, d_riskRatios);
		double QIV = getQIV(weights, d_riskRatios, thetaIV);
		double tauSquared = getTauSquared(QIV, weights);
		
		// Calculated the adjusted Weights.
		for (RiskRatio re : d_riskRatios) {
			d_adjweights.add(1 / (Math.pow(re.getError(),2) + tauSquared) );
		}
		
		d_thetaDL = getThetaDL(d_adjweights, d_riskRatios);
		
		double SE_ThetaDL = getSE_ThetaDL(d_adjweights);
		
		
		//d_confidenceInterval = getConfidenceInterval(SE_ThetaDL, 
	}
	
	private Interval<Double> getConfidenceInterval() {
		return null;
		
	}
	
	private double getSE_ThetaDL(List<Double> adjweights) {
		return 1.0 / (Math.sqrt(computeSum(adjweights)));
	}

	private double getThetaDL(List<Double> adjweights, List<RiskRatio> riskRatios) {
		double numerator = 0;
		for (int i=0; i < adjweights.size(); ++i) {
			numerator += adjweights.get(i) * riskRatios.get(i).getRelativeEffect();
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
	
	private double getQIV(List<Double> weights, List<RiskRatio> riskRatios, double thetaIV) {
		double sum = 0;
		for (int i=0; i < weights.size(); ++i) {
			sum += weights.get(i) * Math.pow(riskRatios.get(i).getRelativeEffect() - thetaIV,2);
		}
		return sum;
	}
	
	private double getThetaIV(List<Double> weights, List<RiskRatio> riskRatios) {
		assert(weights.size() == riskRatios.size());
		
		// Calculate the sums
		double sumWeightRatio = 0D;
			
		for (int i=0; i < weights.size(); ++i) {
			sumWeightRatio += weights.get(i) * riskRatios.get(i).getRelativeEffect();
		}
		
		return sumWeightRatio / computeSum(weights);
	}	

	private class RiskRatioRandomEffects implements RelativeEffectRate{
		
		public org.drugis.addis.entities.RelativeEffect.AxisType getAxisType() {
			return AxisType.LOGARITHMIC;
		}

		public Interval<Double> getConfidenceInterval() {
			return d_confidenceInterval;
		}

		public Double getRelativeEffect() {
			return d_thetaDL;
		}
		
		
		public Integer getSampleSize() {
			// TODO Auto-generated method stub
			return null;
		}
		
		public Endpoint getEndpoint() {
			return d_ep;
		}

		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}		

		public RateMeasurement getSubject() {
			// TODO Auto-generated method stub
			return null;
		}
		
		public RateMeasurement getBaseline() {
			// TODO Auto-generated method stub
			return null;
		}		
	}
	
	public RelativeEffectRate getRiskRatio() {
		return new RiskRatioRandomEffects();
    }
	
	public static double computeSum(List<Double> weights) {
		double weightSum = 0;
		for (int i=0; i < weights.size(); ++i) {
			weightSum += weights.get(i);
		}
		return weightSum;
	}	
}

