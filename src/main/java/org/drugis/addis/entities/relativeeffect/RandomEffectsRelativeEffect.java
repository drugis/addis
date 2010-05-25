package org.drugis.addis.entities.relativeeffect;

import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Measurement;
import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;

public class RandomEffectsRelativeEffect implements RandomEffectMetaAnalysisRelativeEffect {
	
	private class ComputeRandomEffects {
		
		transient  double d_thetaDSL;
		transient  double d_SEThetaDSL;
		transient  double d_qIV;
		
		public ComputeRandomEffects(List<BasicRelativeEffect<? extends Measurement>> relEffects, boolean drugsSwapped) {
			List<Double> weights = new ArrayList<Double>();
			List<Double> adjweights = new ArrayList<Double>();
			
			if (relEffects.size() < 1)
				throw new IllegalStateException("Cannot calculate RandomEffectMetaAnalysis without any relative effects.");
		
			d_axisType = relEffects.get(0).getAxisType();
			
			// FIXME: How are we going to get mu & sigma in a consistent way, without using large if-else trains. Or, if preferably, no if-else.
			
			// Calculate the weights.
			for (RelativeEffect<? extends Measurement> re : relEffects) {
				weights.add(1D / Math.pow(re.getError(),2));
			}
			
			// Calculate needed variables.
			double thetaIV = getThetaIV(weights, relEffects);
			d_qIV = getQIV(weights, relEffects, thetaIV);
			double tauSquared = getTauSquared(d_qIV, weights);
			
			// Calculated the adjusted Weights.
			for (RelativeEffect<? extends Measurement> re : relEffects) {
				adjweights.add(1 / (Math.pow(re.getError(),2) + tauSquared) );
			}
			
			d_thetaDSL = getThetaDL(adjweights, relEffects);
			d_SEThetaDSL = getSE_ThetaDL(adjweights);
		}
		
		private double getSE_ThetaDL(List<Double> adjweights) {
			return 1.0 / (Math.sqrt(computeSum(adjweights)));
		}

		
		
		private double getThetaDL(List<Double> adjweights, List<BasicRelativeEffect<? extends Measurement>> relEffects) {
			double numerator = 0;
			for (int i=0; i < adjweights.size(); ++i) {
				BasicRelativeEffect<? extends Measurement> re = relEffects.get(i);
				numerator += adjweights.get(i) * getMu(re);
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
			if (denum == 0) // FIXME ask.
				return 0;
			return Math.max(num / denum, 0);
		}
		
		private double getQIV(List<Double> weights, List<BasicRelativeEffect<? extends Measurement>> relEffects, double thetaIV) {
			double sum = 0;
			for (int i=0; i < weights.size(); ++i) {
				BasicRelativeEffect<? extends Measurement> re = relEffects.get(i);
				sum += weights.get(i) * Math.pow(getMu(re) - thetaIV,2);
			}
			return sum;
		}
		
		private double getThetaIV(List<Double> weights, List<BasicRelativeEffect<? extends Measurement>> relEffects) {
			assert(weights.size() == relEffects.size());
			
			// Calculate the sums
			double sumWeightRatio = 0D;
				
			for (int i=0; i < weights.size(); ++i) {
				BasicRelativeEffect<? extends Measurement> re = relEffects.get(i);
				sumWeightRatio += weights.get(i) * getMu(re);
			}
			
			return sumWeightRatio / computeSum(weights);
		}	
		
		protected double getMu(BasicRelativeEffect<? extends Measurement> re) { // FIXME - shouldn't be necessary 
			double val;
			Distribution distribution = re.getDistribution();
			if (distribution instanceof TransformedStudentT)
				val = ((TransformedStudentT) distribution).getMu();
			else if (distribution instanceof TransformedLogStudentT)
				val = ((TransformedLogStudentT) distribution).getMu();
			else
				throw new RuntimeException("Unexpected Distribution type");
			return val;
		}
		
		protected double computeSum(List<Double> weights) {
			double weightSum = 0;
			for (int i=0; i < weights.size(); ++i) {
				weightSum += weights.get(i);
			}
			return weightSum;
		}
	}
	
	private List<BasicRelativeEffect<? extends Measurement>> d_componentEffects;
	private Distribution d_distribution;
	private ComputeRandomEffects d_results;
	private int d_numRelativeEffects;
	private AxisType d_axisType; // FIXME
	private int d_totalSampleSize;

	public RandomEffectsRelativeEffect(List<BasicRelativeEffect<? extends Measurement>> componentEffects, boolean drugsSwapped, int totalSampleSize) {
		d_componentEffects = componentEffects;
		d_numRelativeEffects = componentEffects.size();
		d_totalSampleSize = totalSampleSize;
		d_results = new ComputeRandomEffects(d_componentEffects, drugsSwapped);
		d_distribution = createDistribution(d_results.d_thetaDSL, d_results.d_SEThetaDSL);
	}

	public AxisType getAxisType() {
		return d_distribution.getAxisType();
	}

	public Measurement getBaseline() {
		// TODO Auto-generated method stub
		return null;
	}

	public Interval<Double> getConfidenceInterval() {
		return new Interval<Double>(d_distribution.getQuantile(0.025),d_distribution.getQuantile(0.975));
	}

	public Distribution getDistribution() {
		return d_distribution;
	}

	public Integer getSampleSize() {
		return d_totalSampleSize;
	}

	public Measurement getSubject() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDefined() {
		// TODO Auto-generated method stub
		return false;
	}

	public Set<? extends Entity> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getXmlExclusions() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub
	}
	
	public double getHeterogeneity() {
		return d_results.d_qIV;
	}
	
	public double getHeterogeneityI2() {
		return Math.max(0, 100* ((getHeterogeneity() - (d_numRelativeEffects-1)) / getHeterogeneity() ) );
	}
	
	public String toString() { // FIXME
		DecimalFormat decimalFormatter = new DecimalFormat("##0.000");
		return "" + decimalFormatter.format(d_distribution.getQuantile(0.5)) + " (" +
					decimalFormatter.format(d_distribution.getQuantile(0.025)) + ", " + 
					decimalFormatter.format(d_distribution.getQuantile(0.975)) + ")";
	}
	
	// FIXME: Should be abstract, including the class. See FIXME in RandomEffectMetaAnalysis
	//protected abstract Distribution createDistribution(double mu, double sigma);
	//public abstract String getName();
	
	public String getName() {
		return "Random Effects relative Effect";
	}
	
	public Distribution createDistribution(double mu, double sigma) { // FIXME subclass
		if (d_axisType == AxisType.LOGARITHMIC) // FIXME
			//return new TransformedLogStudentT(mu, sigma, d_totalSampleSize-2); 
			return new LogGaussian(mu, sigma); // FIXME
		else if (d_axisType == AxisType.LINEAR) // FIXME
			//return new TransformedStudentT(mu, sigma, d_totalSampleSize-2);
			return new Gaussian(mu, sigma); // FIXME
		else
			throw new IllegalStateException("Axistype unknown");
	}

	public Double getError() { // FIXME subclass
			return d_results.d_SEThetaDSL;
	}

	public Double getRelativeEffect() { // FIXME subclass
		if (d_axisType == AxisType.LOGARITHMIC) // FIXME
			return Math.exp(d_results.d_thetaDSL);
		else if (d_axisType == AxisType.LINEAR) // FIXME
			return d_results.d_thetaDSL;
		else
			throw new IllegalStateException("Axistype unknown");
	}

}
