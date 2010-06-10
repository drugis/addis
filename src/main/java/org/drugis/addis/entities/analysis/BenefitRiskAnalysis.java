package org.drugis.addis.entities.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.LogGaussian;
import org.drugis.addis.entities.relativeeffect.NetworkRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.mcmcmodel.AbstractBaselineModel;
import org.drugis.addis.mcmcmodel.BaselineMeanDifferenceModel;
import org.drugis.addis.mcmcmodel.BaselineOddsModel;
import org.drugis.common.AlphabeticalComparator;

public class BenefitRiskAnalysis extends AbstractEntity implements Comparable<BenefitRiskAnalysis> {
	
	private String d_name;
	private Indication d_indication;
	private List<OutcomeMeasure> d_outcomeMeasures;
	private List<MetaAnalysis> d_metaAnalyses;
	private List<Drug> d_drugs;
	private Drug d_baseline;
	private Map<OutcomeMeasure,AbstractBaselineModel<?>> d_baselineModelMap;
	
	public static String PROPERTY_NAME = "name";
	public static String PROPERTY_INDICATION = "indication";
	public static String PROPERTY_OUTCOMEMEASURES = "outcomeMeasures";
	public static String PROPERTY_DRUGS = "drugs";
	public static String PROPERTY_BASELINE = "baseline";
	public static String PROPERTY_METAANALYSES = "metaAnalyses";
	
	public BenefitRiskAnalysis() {
		d_baselineModelMap = new HashMap<OutcomeMeasure,AbstractBaselineModel<?>>();
	}
	
	public BenefitRiskAnalysis(String id, Indication indication, List<OutcomeMeasure> outcomeMeasures,
			List<MetaAnalysis> metaAnalysis, Drug baseline, List<Drug> drugs) {
		super();
		d_indication = indication;
		d_outcomeMeasures = outcomeMeasures;
		d_metaAnalyses = metaAnalysis;
		d_drugs = drugs;
		d_baselineModelMap = new HashMap<OutcomeMeasure,AbstractBaselineModel<?>>();
		
		setBaseline(baseline);
		setName(id);
	}

	public Indication getIndication() {
		return d_indication;
	}

	public void setIndication(Indication indication) {
		Indication oldValue = d_indication;
		d_indication = indication;
		firePropertyChange(PROPERTY_INDICATION, oldValue, indication);
	}

	public List<OutcomeMeasure> getOutcomeMeasures() {
		List<OutcomeMeasure> sortedList = new ArrayList<OutcomeMeasure>(d_outcomeMeasures);
		Collections.sort(sortedList, new AlphabeticalComparator());
		return sortedList;
	}

	public void setOutcomeMeasures(List<OutcomeMeasure> outcomeMeasures) {
		List<OutcomeMeasure> oldValue = d_outcomeMeasures;
		d_outcomeMeasures = outcomeMeasures;
		firePropertyChange(PROPERTY_OUTCOMEMEASURES, oldValue, outcomeMeasures);
	}

	public List<MetaAnalysis> getMetaAnalyses() {
		return d_metaAnalyses;
	}

	public void setMetaAnalyses(List<MetaAnalysis> metaAnalysis) {
		List<MetaAnalysis> oldValue = d_metaAnalyses;
		d_metaAnalyses = metaAnalysis;
		firePropertyChange(PROPERTY_METAANALYSES, oldValue, metaAnalysis);
	}

	public List<Drug> getDrugs() {
		List<Drug> sortedList = new ArrayList<Drug>(d_drugs);
		sortedList.add(getBaseline());
		Collections.sort(sortedList, new AlphabeticalComparator());
		return sortedList;
	}

	public void setDrugs(List<Drug> drugs) {
		List<Drug> oldValue = d_drugs;
		d_drugs = drugs;
		d_drugs.remove(getBaseline());
		firePropertyChange(PROPERTY_DRUGS, oldValue, drugs);
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		HashSet<Entity> dependencies = new HashSet<Entity>();
		dependencies.add(d_indication);
		dependencies.addAll(d_outcomeMeasures);
		dependencies.addAll(d_drugs);
		dependencies.addAll(d_metaAnalyses);
		return dependencies;
	}

	public void setName(String name) {
		String oldValue = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldValue, name);
	}

	public String getName() {
		return d_name;
	}

	@Override
	public boolean equals(Object other){
		if (other == null)
			return false;
		if (!(other instanceof BenefitRiskAnalysis))
			return false;
		return this.getName().equals( ((BenefitRiskAnalysis)other).getName() );
	}

	public int compareTo(BenefitRiskAnalysis other) {
		if (other == null) {
			return 1;
		}
		return getName().compareTo(other.getName());
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public void setBaseline(Drug baseline) {
		Drug oldValue = d_baseline;
		d_baseline = baseline;
		firePropertyChange(PROPERTY_BASELINE, oldValue, baseline);
	}

	public Drug getBaseline() {
		return d_baseline;
	}

	public RelativeEffect<? extends Measurement> getRelativeEffect(Drug d, OutcomeMeasure om) {
		for(MetaAnalysis ma : getMetaAnalyses()){
			if(ma.getOutcomeMeasure().equals(om)){
				if (!d.equals(getBaseline())) {
					Class<? extends RelativeEffect<? extends Measurement>> type = (om.getType().equals(Variable.Type.RATE)) ? BasicOddsRatio.class : BasicMeanDifference.class;
					return ma.getRelativeEffect(d_baseline, d, type);
				}
				else {
					return (om.getType().equals(Variable.Type.RATE)) ?  NetworkRelativeEffect.buildOddsRatio(0, .0001) : NetworkRelativeEffect.buildMeanDifference(0, .0001); 
				}
			}
			
		}
		throw new IllegalArgumentException("No analyses comparing drug " + d + " and Outcome " + om + " in this Benefit-Risk analysis");
	}
	
	/**
	 * The effect of d on om relative to the baseline treatment. 
	 */
	public GaussianBase getRelativeEffectDistribution(Drug d, OutcomeMeasure om) {
		if (d.equals(getBaseline())) {
			switch (om.getType()) {
			case RATE:
				return new LogGaussian(0, 0);
			case CONTINUOUS:
				return new Gaussian(0, 0);
			}
		}
		return (GaussianBase) getRelativeEffect(d, om).getDistribution();
	}
	
	
	/**
	 * Get the assumed distribution for the baseline odds.
	 */
	public GaussianBase getBaselineDistribution(OutcomeMeasure om) {
		AbstractBaselineModel<?> model = getBaselineModel(om);
		if (!model.isReady()) {
			return null;
		}
		return (GaussianBase) model.getResult();
	}
	
	public AbstractBaselineModel<?> getBaselineModel(OutcomeMeasure om) {
		AbstractBaselineModel<?> model = d_baselineModelMap.get(om);
		if (model == null) {
			model = createBaselineModel(om);
			d_baselineModelMap.put(om,model);
		}
		return model;
	}
	
	@SuppressWarnings("unchecked")
	private AbstractBaselineModel<?> createBaselineModel(OutcomeMeasure om) {
		AbstractBaselineModel<?> model = null;
			switch (om.getType()) {
			case RATE:
				model = new BaselineOddsModel(getBaselineMeasurements(om));
				Thread t1 = new Thread(model);
				t1.start();
			break;
			case CONTINUOUS:
				model = new BaselineMeanDifferenceModel(getBaselineMeasurements(om));
				Thread t2 = new Thread(model);
				t2.start();
			break;
			}
		return model;
	}
	
	// FIXME: type safety.
	@SuppressWarnings("unchecked")
	private List getBaselineMeasurements(OutcomeMeasure om) {
		List<Measurement> result = new ArrayList<Measurement>(); 
		for (MetaAnalysis ma : getMetaAnalyses())
			if (ma.getOutcomeMeasure().equals(om))
				for (Study s : ma.getIncludedStudies())
					for (Arm a : s.getArms())
						if (a.getDrug().equals(getBaseline()))
							result.add(s.getMeasurement(om,a));
		
		return result;
	}
	
	/**
	 * The absolute effect of d on om given the assumed odds of the baseline treatment. 
	 */
	public GaussianBase getAbsoluteEffectDistribution(Drug d, OutcomeMeasure om) {
		GaussianBase baseline = getBaselineDistribution(om);
		GaussianBase relative = getRelativeEffectDistribution(d, om);
		if (baseline == null || relative == null) return null;
		return baseline.plus(relative);
	}

	public void runAllConsistencyModels() {
		for (MetaAnalysis ma : getMetaAnalyses() ){
			if (ma instanceof NetworkMetaAnalysis) 
				((NetworkMetaAnalysis) ma).runConsistency();
		}
	}
	
}
