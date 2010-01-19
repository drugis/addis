package org.drugis.addis.entities.metaanalysis;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.RelativeEffectMetaAnalysis;
import org.drugis.addis.entities.RiskRatio;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;

public class RandomEffectsMetaAnalysis extends AbstractEntity implements Serializable, Comparable<RandomEffectsMetaAnalysis> {

	private static final long serialVersionUID = -4351415410739040259L;
	private OutcomeMeasure d_om;
	private List<? extends Study> d_studies;
	private Drug d_drug1;
	private Drug d_drug2;	
	
	private String d_name;	
	private int d_totalSampleSize;

	transient private double d_thetaDSL;
	transient private double d_SEThetaDSL;
	transient private Interval<Double> d_confidenceInterval;
	transient private double d_qIV;
	private List<StudyArmsEntry> d_studyArms;
	
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_TYPE = "type";
	public static final String PROPERTY_INDICATION = "indication";
	public static final String PROPERTY_OUTCOME_MEASURE = "outcomeMeasure";
	public static final String PROPERTY_FIRST_DRUG = "firstDrug";
	public static final String PROPERTY_SECOND_DRUG = "secondDrug";
	public static final String PROPERTY_SAMPLE_SIZE = "sampleSize";
	public static final String PROPERTY_STUDIES_INCLUDED = "studiesIncluded";
	
	/**
	 * 
	 * @param name
	 * @param om
	 * @param studies
	 * @param drug1
	 * @param drug2
	 * @throws IllegalArgumentException if all studies don't measure the same indication OR
	 * if the list of studies is empty
	 */
	public RandomEffectsMetaAnalysis(String name, OutcomeMeasure om, List<? extends Study> studies,
			Drug drug1, Drug drug2) 
		throws IllegalArgumentException {
		if (studies.isEmpty()) {
			throw new IllegalArgumentException("studylist empty");
		}
//FIXME	if (studies.size() <= 1) {
//			throw new IllegalArgumentException("Cannot calculate Random Effects for just one study");
//		}
		checkSameIndication(studies);
		d_studies = studies;
		d_om = om;
		d_drug1 = drug1;
		d_drug2 = drug2;
		d_name = name;
		d_studyArms = new ArrayList<StudyArmsEntry>();

		for (Study s : d_studies) {
			d_totalSampleSize += s.getSampleSize();
			
			/* Fill the studyArms list for forward compatibility */
			Arm arm1 = RelativeEffectFactory.findFirstArm(s, drug1);
			Arm arm2 = RelativeEffectFactory.findFirstArm(s, drug2);
			d_studyArms.add(new StudyArmsEntry(s, arm1, arm2));
		}
		
		
	}
	
	public RandomEffectsMetaAnalysis(String name, OutcomeMeasure om, List<StudyArmsEntry> studyArms) throws IllegalArgumentException {
		if(studyArms.isEmpty()){
			throw new IllegalArgumentException("studylist empty");
		}
		d_studies = StudyArmsEntry.getStudyList(studyArms);
		checkSameIndication(d_studies);
		
		d_name = name;
		d_om = om;
		d_studyArms = studyArms;
		d_drug1 = d_studyArms.get(0).getBase().getDrug();
		d_drug2 = d_studyArms.get(0).getSubject().getDrug();
		
		for (StudyArmsEntry s : studyArms){
			if(!s.getBase().getDrug().equals(d_drug1)){
				throw new IllegalArgumentException("Left drug not consistent over all studies");
			}
			if(!s.getSubject().getDrug().equals(d_drug2)){
				throw new IllegalArgumentException("Right drug not consistent over all studies");
			}
		}
		
		for (Study s : d_studies) {
			d_totalSampleSize += s.getSampleSize();
		}
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
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
	
	private void checkSameIndication(List<? extends Study> studies) throws IllegalArgumentException {
		Indication ind = getIndicationFromStudy(studies.get(0));
		for (int i=1;i<studies.size();i++) {
			Indication ind2 = getIndicationFromStudy(studies.get(i));
			if (!ind2.equals(ind)) {
				throw new IllegalArgumentException("different indications in studies");
			}
		}
	}

	private Indication getIndicationFromStudy(Study study) {
		return study.getIndication();
	}

	public Drug getFirstDrug() {
		return d_drug1;
	}
	
	public Drug getSecondDrug() {
		return d_drug2;
	}
	
	public List<StudyArmsEntry> getStudyArms() {
		return d_studyArms;
	}
	
	public String getName() {
		return d_name;
	}
	
	public String getType() {
		return "DerSimonian-Laird Random Effects";
	}
	
	public int getSampleSize() {
		return d_totalSampleSize;
	}
	
	public List<Study> getStudies() {
		return Collections.unmodifiableList(d_studies);
	}
	
	public OutcomeMeasure getOutcomeMeasure() {
		return d_om;
	}
	
	public int getStudiesIncluded() {
		return d_studies.size();
	}
	
	private void compute(Class<? extends RelativeEffect<?>> relEffClass) {
		
		Class<? extends RelativeEffect<? extends Measurement>> type = relEffClass; 
		if (relEffClass == RiskRatio.class)
			type = LogRiskRatio.class;
		if (relEffClass == OddsRatio.class)
			type = LogOddsRatio.class;
		
		List<Double> weights = new ArrayList<Double>();
		List<Double> adjweights = new ArrayList<Double>();
		List<RelativeEffect<? extends Measurement>> relEffects = new ArrayList<RelativeEffect<? extends Measurement>>();
			
		for (int i=0; i<d_studies.size(); ++i ){
			RelativeEffect<? extends Measurement> re;
			re = RelativeEffectFactory.buildRelativeEffect(d_studyArms.get(i), d_om, type);
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
		
		if ((type == LogRiskRatio.class) || (type == LogOddsRatio.class)) {
			d_thetaDSL = Math.exp(d_thetaDSL);
			d_confidenceInterval = new Interval<Double>(Math.exp(d_confidenceInterval.getLowerBound()),Math.exp(d_confidenceInterval.getUpperBound()));
		}
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
		return new RandomEffects(d_confidenceInterval, d_thetaDSL, d_totalSampleSize, d_SEThetaDSL, d_qIV);		
	}
	
	private class RandomEffects extends AbstractEntity implements RelativeEffectMetaAnalysis<Measurement> {
		private static final long serialVersionUID = 6195228866106906214L;
		
		private Interval<Double> t_confidenceInterval;
		private double t_thetaDSL;
		private int t_totalSampleSize;
		private double t_qIV;
		private Double t_SEThetaDSL;

		public RandomEffects(Interval<Double> confidenceInterval, double thetaDSL, 
				int totalSampleSize, double SEThetaDSL, double qIV) {
			t_confidenceInterval = confidenceInterval;
			t_thetaDSL = thetaDSL;
			t_totalSampleSize = totalSampleSize;
			t_SEThetaDSL = SEThetaDSL;
			t_qIV = qIV;
		}
		public RelativeEffect.AxisType getAxisType() {
			return AxisType.LOGARITHMIC;
		}

		public Interval<Double> getConfidenceInterval() {
			return t_confidenceInterval;
		}

		public Double getRelativeEffect() {
			return t_thetaDSL;
		}
		
		public Integer getSampleSize() {
			return t_totalSampleSize;
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
			return t_SEThetaDSL;
		}

		public double getHeterogeneity() {
			return t_qIV;
		}
		
		public double getHeterogeneityI2() {
			int k = getStudies().size();
			return Math.max(0, 100* ((t_qIV - (k-1)) / t_qIV ) );
		}
		@Override
		public Set<Entity> getDependencies() {
			return Collections.emptySet();
		}	
	}
	
	@Override
	public Set<Entity> getDependencies() {
		HashSet<Entity> deps = new HashSet<Entity>();
		deps.add(getFirstDrug());
		deps.add(getSecondDrug());
		deps.add(getIndication());
		deps.add(getOutcomeMeasure());
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

