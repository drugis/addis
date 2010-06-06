package org.drugis.addis.entities.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.LogGaussian;
import org.drugis.addis.entities.relativeeffect.NetworkRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.common.AlphabeticalComparator;


public class BenefitRiskAnalysis extends AbstractEntity implements Comparable<BenefitRiskAnalysis> {
	
	private String d_name;
	private Indication d_indication;
	//private List<OutcomeMeasure> d_outcomeMeasures;
	//private List<MetaAnalysis> d_metaAnalyses;
	private Map<OutcomeMeasure,MetaAnalysis> d_metaAnalysesMap;
	private List<Drug> d_drugs;
	private Drug d_baseline;
	
	public static String PROPERTY_NAME = "name";
	public static String PROPERTY_INDICATION = "indication";
	public static String PROPERTY_OUTCOMEMEASURES = "outcomeMeasures";
	public static String PROPERTY_DRUGS = "drugs";
	public static String PROPERTY_BASELINE = "baseline";
	public static String PROPERTY_METAANALYSES = "metaAnalyses";
	
	public BenefitRiskAnalysis() {
	}
	
	public BenefitRiskAnalysis(String id, Indication indication, List<OutcomeMeasure> outcomeMeasures,
			List<MetaAnalysis> metaAnalysis, Drug baseline, List<Drug> drugs) {
		super();
		d_indication = indication;
		d_metaAnalysesMap = constructMap(outcomeMeasures, metaAnalysis);
		d_drugs = drugs;
		setBaseline(baseline);
		setName(id);
	}
	
	private Map<OutcomeMeasure,MetaAnalysis> constructMap(Collection<OutcomeMeasure> outcomeMeasures, 	Collection<MetaAnalysis> metaAnalysis ) {
		Map<OutcomeMeasure,MetaAnalysis> metaAnalysisMap = new HashMap<OutcomeMeasure,MetaAnalysis>();
		for(MetaAnalysis ma : metaAnalysis){
			if (!outcomeMeasures.contains(ma.getOutcomeMeasure()))
				throw new IllegalStateException("Outcome " + ma.getOutcomeMeasure() + " not on the outcome-measure list");
			metaAnalysisMap.put(ma.getOutcomeMeasure(), ma);
		}
		return metaAnalysisMap;
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
		List<OutcomeMeasure> sortedList = new ArrayList<OutcomeMeasure>(d_metaAnalysesMap.keySet());
		Collections.sort(sortedList, new AlphabeticalComparator());
		return sortedList;
	}

	public void setOutcomeMeasures(List<OutcomeMeasure> outcomeMeasures) {
		List<OutcomeMeasure> oldValue = new ArrayList<OutcomeMeasure>(d_metaAnalysesMap.keySet());
		d_metaAnalysesMap = constructMap(outcomeMeasures, d_metaAnalysesMap.values());
		firePropertyChange(PROPERTY_OUTCOMEMEASURES, oldValue, outcomeMeasures);
	}

	public Collection<MetaAnalysis> getMetaAnalyses() {
		return d_metaAnalysesMap.values();
	}

	public void setMetaAnalyses(List<MetaAnalysis> metaAnalysis) {
		Collection<MetaAnalysis> oldValue = d_metaAnalysesMap.values();
		d_metaAnalysesMap = constructMap(d_metaAnalysesMap.keySet(), metaAnalysis);
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
		dependencies.addAll(d_metaAnalysesMap.keySet());
		dependencies.addAll(d_drugs);
		dependencies.addAll(d_metaAnalysesMap.values());
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
			MetaAnalysis ma = d_metaAnalysesMap.get(om);
			if(ma.getOutcomeMeasure().equals(om)){
				if (!d.equals(getBaseline())) {
					Class<? extends RelativeEffect<? extends Measurement>> type = (om.getType().equals(Variable.Type.RATE)) ? BasicOddsRatio.class : BasicMeanDifference.class;
					return ma.getRelativeEffect(d_baseline, d, type);
				}
				else {
					return (om.getType().equals(Variable.Type.RATE)) ?  NetworkRelativeEffect.buildOddsRatio(0, .0001) : NetworkRelativeEffect.buildMeanDifference(0, .0001); 
				}
			}
			
		
		throw new IllegalArgumentException("No analyses comparing drug " + d + " and Outcome " + om + " in this Benefit-Risk analysis");
	}
	
	public Distribution getRelativeEffectDistribution(Drug d, OutcomeMeasure om) {
		if (d.equals(getBaseline())) {
			switch (om.getType()) {
			case RATE:
				return new LogGaussian(0, 0);
			case CONTINUOUS:
				return new Gaussian(0, 0);
			}
		}
		return getRelativeEffect(d, om).getDistribution();
	}

	public void runAllConsistencyModels() {
		for (MetaAnalysis ma : getMetaAnalyses() ){
			if (ma instanceof NetworkMetaAnalysis) 
				((NetworkMetaAnalysis) ma).runConsistency();
		}
	}
	
	/*
	 * WIP
	 */
	// How to get the odds from a meta-analysis?
	private double oddsBaseLine(OutcomeMeasure om) {
		return 0;
	}
	
	public Distribution getOddsRatioScale(OutcomeMeasure om) {
		return null;
	}
	
	public Distribution getAbsoluteRiskScale(OutcomeMeasure om) {
		return null;
	}
	
	public Distribution getRiskDifferenceScale(OutcomeMeasure om) {
		return null;
	}
	
	public Distribution getNNT(OutcomeMeasure om) {
		return null;
	}
}
