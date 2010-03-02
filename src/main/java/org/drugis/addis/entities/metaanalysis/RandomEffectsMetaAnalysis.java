package org.drugis.addis.entities.metaanalysis;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

public class RandomEffectsMetaAnalysis extends AbstractMetaAnalysis {

	private static final long serialVersionUID = -4351415410739040259L;
	transient private double d_thetaDSL;
	transient private double d_SEThetaDSL;
	transient private Interval<Double> d_confidenceInterval;
	transient private double d_qIV;
	private List<StudyArmsEntry> d_studyArms;

	public static final String PROPERTY_FIRST_DRUG = "firstDrug";
	public static final String PROPERTY_SECOND_DRUG = "secondDrug";
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
		super(name, studies.get(0).getIndication(), om, studies, Arrays.asList(new Drug[] {drug1, drug2}));

		d_studyArms = new ArrayList<StudyArmsEntry>();

		for (Study s : d_studies) {
			Arm arm1 = RelativeEffectFactory.findFirstArm(s, drug1);
			Arm arm2 = RelativeEffectFactory.findFirstArm(s, drug2);
			d_studyArms.add(new StudyArmsEntry(s, arm1, arm2));
		}
	}
	
	public RandomEffectsMetaAnalysis(String name, OutcomeMeasure om, List<StudyArmsEntry> studyArms)
	throws IllegalArgumentException {
		super(name, getIndication(studyArms), om, getStudies(studyArms), getDrugs(studyArms));

		d_studyArms = studyArms;
		
		for (StudyArmsEntry s : studyArms){
			if(!s.getBase().getDrug().equals(getFirstDrug())){
				throw new IllegalArgumentException("Left drug not consistent over all studies");
			}
			if(!s.getSubject().getDrug().equals(getSecondDrug())){
				throw new IllegalArgumentException("Right drug not consistent over all studies");
			}
		}
	}

	private static List<Drug> getDrugs(List<StudyArmsEntry> studyArms) {
		return Arrays.asList(new Drug[]{getFirstDrug(studyArms), getSecondDrug(studyArms)});
	}

	private static Drug getSecondDrug(List<StudyArmsEntry> studyArms) {
		return studyArms.get(0).getSubject().getDrug();
	}

	private static Drug getFirstDrug(List<StudyArmsEntry> studyArms) {
		return studyArms.get(0).getBase().getDrug();
	}

	private static List<? extends Study> getStudies(
			List<StudyArmsEntry> studyArms) {
		return StudyArmsEntry.getStudyList(studyArms);
	}

	private static Indication getIndication(List<StudyArmsEntry> studyArms) {
		return getStudies(studyArms).get(0).getIndication();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
	}	
	
	public Drug getFirstDrug() {
		return d_drugs.get(0);
	}
	
	public Drug getSecondDrug() {
		return d_drugs.get(1);
	}
	
	public List<StudyArmsEntry> getStudyArms() {
		return d_studyArms;
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
			re = RelativeEffectFactory.buildRelativeEffect(d_studyArms.get(i), d_outcome, type);
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
		
		public boolean isDefined() {
			return true;
		}
	}
}

