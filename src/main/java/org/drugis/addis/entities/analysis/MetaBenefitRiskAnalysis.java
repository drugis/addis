/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.NetworkRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.mcmcmodel.AbstractBaselineModel;
import org.drugis.addis.mcmcmodel.BaselineMeanDifferenceModel;
import org.drugis.addis.mcmcmodel.BaselineOddsModel;
import org.drugis.addis.util.threading.ThreadHandler;
import org.drugis.common.AlphabeticalComparator;
import org.drugis.common.OutcomeComparator;
import org.drugis.mtc.ConsistencyModel;

public class MetaBenefitRiskAnalysis extends AbstractEntity implements BenefitRiskAnalysis<Drug> {
	
	private String d_name;
	private Indication d_indication;
	private List<OutcomeMeasure> d_outcomeMeasures;
	private List<MetaAnalysis> d_metaAnalyses;
	private List<Drug> d_drugs;
	private Drug d_baseline;
	private Map<OutcomeMeasure,AbstractBaselineModel<?>> d_baselineModelMap;
	
	public static String PROPERTY_DRUGS = "drugs";
	public static String PROPERTY_BASELINE = "baseline";
	public static String PROPERTY_METAANALYSES = "metaAnalyses";
	
	public MetaBenefitRiskAnalysis() {
		d_baselineModelMap = new HashMap<OutcomeMeasure,AbstractBaselineModel<?>>();
		d_metaAnalyses = new ArrayList<MetaAnalysis>();
	}
	
	public MetaBenefitRiskAnalysis(String id, Indication indication, List<MetaAnalysis> metaAnalysis,
			Drug baseline, List<Drug> drugs) {
		super();
		d_indication = indication;
		d_metaAnalyses = metaAnalysis;
		d_outcomeMeasures = findOutcomeMeasures();
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
		List<OutcomeMeasure> sortedList = findOutcomeMeasures();
		Collections.sort(sortedList, new OutcomeComparator());
		return sortedList;
	}

	private List<OutcomeMeasure> findOutcomeMeasures() {
		List<OutcomeMeasure> list = new ArrayList<OutcomeMeasure>();
		for (MetaAnalysis a : d_metaAnalyses) {
			list.add(a.getOutcomeMeasure());
		}
		return list;
	}

	public List<MetaAnalysis> getMetaAnalyses() {
		ArrayList<MetaAnalysis> analyses = new ArrayList<MetaAnalysis>(d_metaAnalyses);
		Collections.sort(analyses, new AlphabeticalComparator());
		return Collections.unmodifiableList(analyses);
	}

	// FIXME: make private or default
	public void setMetaAnalyses(List<MetaAnalysis> metaAnalysis) {
		List<MetaAnalysis> oldValue = getMetaAnalyses();
		List<OutcomeMeasure> oldOutcomes = getOutcomeMeasures();
		d_metaAnalyses = metaAnalysis;
		firePropertyChange(PROPERTY_METAANALYSES, oldValue, getMetaAnalyses());
		firePropertyChange(PROPERTY_OUTCOMEMEASURES, oldOutcomes, getOutcomeMeasures());
	}
	
	public List<Drug> getAlternatives() {
		return getDrugs();
	}

	public List<Drug> getDrugs() {
		List<Drug> sortedList = new ArrayList<Drug>(d_drugs);
		sortedList.add(getBaseline());
		Collections.sort(sortedList, new AlphabeticalComparator());
		return sortedList;
	}

	// FIXME: make private or default
	public void setDrugs(List<Drug> drugs) {
		List<Drug> oldValue = d_drugs;
		d_drugs = drugs;
		d_drugs.remove(getBaseline());
		firePropertyChange(PROPERTY_DRUGS, oldValue, drugs);
		firePropertyChange(PROPERTY_ALTERNATIVES, oldValue, drugs);
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
		if (!(other instanceof MetaBenefitRiskAnalysis))
			return false;
		return this.getName().equals( ((BenefitRiskAnalysis<?>)other).getName() );
	}

	public int compareTo(BenefitRiskAnalysis<?> other) {
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
	
	private RelativeEffect<? extends Measurement> getRelativeEffect(Drug d, OutcomeMeasure om) {
		for(MetaAnalysis ma : getMetaAnalyses()){
			if(ma.getOutcomeMeasure().equals(om)){
				if (!d.equals(getBaseline())) {
					Class<? extends RelativeEffect<? extends Measurement>> type = (om.getType().equals(Variable.Type.RATE)) ? BasicOddsRatio.class : BasicMeanDifference.class;
					return ma.getRelativeEffect(d_baseline, d, type);
				}
				else {
					return (om.getType().equals(Variable.Type.RATE)) ?  NetworkRelativeEffect.buildOddsRatio(0.0, 0.0) : NetworkRelativeEffect.buildMeanDifference(0.0, 0.0); 
				}
			}
			
		}
		throw new IllegalArgumentException("No analyses comparing drug " + d + " and Outcome " + om + " in this Benefit-Risk analysis");
	}
	
	/**
	 * The effect of d on om relative to the baseline treatment. 
	 */
	public GaussianBase getRelativeEffectDistribution(Drug d, OutcomeMeasure om) {
		return (GaussianBase) getRelativeEffect(d, om).getDistribution();
	}
	
	public Distribution getMeasurement(Drug d, OutcomeMeasure om) {
		return getRelativeEffectDistribution(d, om);
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
			break;
			case CONTINUOUS:
				model = new BaselineMeanDifferenceModel(getBaselineMeasurements(om));
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
		List<Runnable> tasks = new ArrayList<Runnable>();
		for (MetaAnalysis ma : getMetaAnalyses() ){
			if (ma instanceof NetworkMetaAnalysis) {
				ConsistencyModel model = ((NetworkMetaAnalysis) ma).getConsistencyModel();
				if (!model.isReady()) {
					tasks.add(model);
				}			
			}
		}
		ThreadHandler.getInstance().scheduleTasks(tasks);
	}
	
}
