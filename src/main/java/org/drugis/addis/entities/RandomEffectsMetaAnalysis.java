package org.drugis.addis.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;

public class RandomEffectsMetaAnalysis extends AbstractEntity implements Serializable, Comparable<RandomEffectsMetaAnalysis> {

	private static final long serialVersionUID = -4351415410739040259L;
	private Endpoint d_ep;
	private List<Study> d_studies;
	private Drug d_drug1;
	private Drug d_drug2;	

	private int d_totalSampleSize;
	private double d_thetaDSL;
	private double d_SEThetaDSL;
	private Interval<Double> d_confidenceInterval;
	private double d_qIV;
	private String d_name;
	
	public static final String PROPERTY_NAME = "name";

	/**
	 * 
	 * @param name
	 * @param endpoint
	 * @param studies
	 * @param drug1
	 * @param drug2
	 * @throws IllegalArgumentException if all studies don't measure the same indication OR
	 * if the list of studies is empty
	 */
	public RandomEffectsMetaAnalysis(String name, Endpoint endpoint, List<Study> studies, Drug drug1, Drug drug2) 
		throws IllegalArgumentException {
		if (studies.isEmpty()) {
			throw new IllegalArgumentException("studylist empty");
		}
		checkSameIndication(studies);
		d_studies = studies;
		d_ep = endpoint;
		d_drug1 = drug1;
		d_drug2 = drug2;
		d_name = name;
		
		d_totalSampleSize = 0;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public void setName(String name) {
		String oldName = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldName, d_name);
	}
	
	private void checkSameIndication(List<Study> studies) throws IllegalArgumentException {
		Indication ind = getIndicationFromStudy(studies.get(0));
		for (int i=1;i<studies.size();i++) {
			Indication ind2 = getIndicationFromStudy(studies.get(i));
			if (!ind2.equals(ind)) {
				throw new IllegalArgumentException("different indications in studies");
			}
		}
	}

	private Indication getIndicationFromStudy(Study study) {
		return (Indication) study.getCharacteristic(StudyCharacteristic.INDICATION);
	}

	public Drug getFirstDrug() {
		return d_drug1;
	}
	
	public Drug getSecondDrug() {
		return d_drug2;
	}
	
	public String getName() {
		return d_name;
	}
	
	public List<Study> getStudies() {
		return Collections.unmodifiableList(d_studies);
	}
	
	public Endpoint getEndpoint() {
		return d_ep;
	}
	
	private void compute(Class<? extends RelativeEffect<?>> relEffClass) {
		List<Double> weights = new ArrayList<Double>();
		List<Double> adjweights = new ArrayList<Double>();
		List<RelativeEffect<? extends Measurement>> relEffects = new ArrayList<RelativeEffect<? extends Measurement>>();
		
		for (Study s : d_studies) {
			RelativeEffect<? extends Measurement> re = RelativeEffectFactory.buildRelativeEffect(s, d_ep, d_drug1, d_drug2, relEffClass);
			d_totalSampleSize += re.getSampleSize();
			relEffects.add(re);
		}
		
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

	private double getThetaDL(List<Double> adjweights, List<RelativeEffect<? extends Measurement>> relEffects) {
		double numerator = 0;
		for (int i=0; i < adjweights.size(); ++i) {
			numerator += adjweights.get(i) * relEffects.get(i).getRelativeEffect();
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
	
	private double getQIV(List<Double> weights, List<RelativeEffect<? extends Measurement>> relEffects, double thetaIV) {
		double sum = 0;
		for (int i=0; i < weights.size(); ++i) {
			sum += weights.get(i) * Math.pow(relEffects.get(i).getRelativeEffect() - thetaIV,2);
		}
		return sum;
	}
	
	private double getThetaIV(List<Double> weights, List<RelativeEffect<? extends Measurement>> relEffects) {
		assert(weights.size() == relEffects.size());
		
		// Calculate the sums
		double sumWeightRatio = 0D;
			
		for (int i=0; i < weights.size(); ++i) {
			sumWeightRatio += weights.get(i) * relEffects.get(i).getRelativeEffect();
		}
		
		return sumWeightRatio / computeSum(weights);
	}	
	
	protected double computeSum(List<Double> weights) {
		double weightSum = 0;
		for (int i=0; i < weights.size(); ++i) {
			weightSum += weights.get(i);
		}
		return weightSum;
	}	
		
	public RelativeEffectMetaAnalysis<Measurement> getRelativeEffect(Class<? extends RelativeEffect<?>> type) {
		compute(type);
		return new RandomEffects();
	}
	
	private class RandomEffects implements RelativeEffectMetaAnalysis<Measurement> {
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

		public Double getError() {
			return d_SEThetaDSL;
		}

		public double getHeterogeneity() {
			return d_qIV;
		}		
	}

	@Override
	public Set<Entity> getDependencies() {
		HashSet<Entity> deps = new HashSet<Entity>();
		deps.add(getFirstDrug());
		deps.add(getSecondDrug());
		deps.add(getIndication());
		deps.add(getEndpoint());
		deps.addAll(getStudies());
		return deps;
	}

	public Indication getIndication() {
		return getIndicationFromStudy(d_studies.get(0));
	}

	public int compareTo(RandomEffectsMetaAnalysis o) {
		return getName().compareTo(o.getName());
	}
}

