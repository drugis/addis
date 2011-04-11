/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.LogitGaussian;
import org.drugis.addis.entities.relativeeffect.NetworkRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.mcmcmodel.AbstractBaselineModel;
import org.drugis.addis.mcmcmodel.BaselineMeanDifferenceModel;
import org.drugis.addis.mcmcmodel.BaselineOddsModel;
import org.drugis.addis.util.comparator.AlphabeticalComparator;
import org.drugis.addis.util.comparator.OutcomeComparator;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.mtc.BasicParameter;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.Treatment;
import org.drugis.mtc.summary.Summary;

public class MetaBenefitRiskAnalysis extends AbstractEntity implements BenefitRiskAnalysis<Drug> {
	private final class MetaMeasurementSource extends AbstractMeasurementSource<Drug> {
		public MetaMeasurementSource() {
			PropertyChangeListener l = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					notifyListeners();
				}
			};
			for (Summary s : getEffectSummaries()) {
				s.addPropertyChangeListener(l);
			}
		}
	}

	private String d_name;
	private Indication d_indication;
	private List<MetaAnalysis> d_metaAnalyses;
	private List<Drug> d_drugs;
	private Drug d_baseline;
	private Map<OutcomeMeasure, AbstractBaselineModel<?>> d_baselineModelMap;
	private AnalysisType d_analysisType;
	
	public static String PROPERTY_DRUGS = "drugs";
	public static String PROPERTY_BASELINE = "baseline";
	public static String PROPERTY_METAANALYSES = "metaAnalyses";

	
	public MetaBenefitRiskAnalysis(String id, Indication indication, List<MetaAnalysis> metaAnalysis,
			Drug baseline, List<Drug> drugs, AnalysisType analysisType) {
		super();
		d_indication = indication;
		d_metaAnalyses = metaAnalysis;
		d_drugs = drugs;
		d_baselineModelMap = new HashMap<OutcomeMeasure, AbstractBaselineModel<?>>();
		d_analysisType = analysisType;
		if(d_analysisType == AnalysisType.LyndOBrien && (d_metaAnalyses.size() != 2 || d_drugs.size() != 1) ) {
			throw new IllegalArgumentException("Attempt to create Lynd & O'Brien analysis with not exactly 2 criteria and 2 alternatives");
		}
		
		setBaseline(baseline);
		setName(id);
	}

	public Indication getIndication() {
		return d_indication;
	}

	public List<OutcomeMeasure> getCriteria() {
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

	void setMetaAnalyses(List<MetaAnalysis> metaAnalysis) {
		d_metaAnalyses = metaAnalysis;
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

	void setDrugs(List<Drug> drugs) {
		d_drugs = drugs;
		d_drugs.remove(getBaseline());
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		HashSet<Entity> dependencies = new HashSet<Entity>();
		dependencies.add(d_indication);
		dependencies.addAll(d_drugs);
		dependencies.addAll(d_metaAnalyses);
		return dependencies;
	}

	void setName(String name) {
		d_name = name;
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

	private void setBaseline(Drug baseline) {
		d_baseline = baseline;
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
	
	/**
	 * Get the measurement to be used in the BenefitRisk simulation.
	 */
	public Distribution getMeasurement(Drug d, OutcomeMeasure om) {
		if (om.getType() == Type.RATE) {
			GaussianBase logOdds = getAbsoluteEffectDistribution(d, om);
			return logOdds == null ? null : new LogitGaussian(logOdds.getMu(), logOdds.getSigma());
		}
		return getAbsoluteEffectDistribution(d, om);
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
	
	private AbstractBaselineModel<?> createBaselineModel(OutcomeMeasure om) {
		AbstractBaselineModel<?> model = null;
			switch (om.getType()) {
			case RATE:
				model = new BaselineOddsModel(getBaselineMeasurements(om, RateMeasurement.class));
			break;
			case CONTINUOUS:
				model = new BaselineMeanDifferenceModel(getBaselineMeasurements(om, ContinuousMeasurement.class));
			break;
			}
		return model;
	}
	
	@SuppressWarnings("unchecked")
	private <M extends Measurement> List<M> getBaselineMeasurements(OutcomeMeasure om, Class<M> cls) {
		List<M> result = new ArrayList<M>(); 
		for (MetaAnalysis ma : getMetaAnalyses())
			if (ma.getOutcomeMeasure().equals(om))
				for (Study s : ma.getIncludedStudies())
					for (Arm a : s.getArms())
						if (s.getDrug(a).equals(getBaseline()))
							result.add((M)s.getMeasurement(om,a));
		
		return result;
	}
	
	/**
	 * The absolute effect of d on om given the assumed odds of the baseline treatment. 
	 */
	private GaussianBase getAbsoluteEffectDistribution(Drug d, OutcomeMeasure om) {
		GaussianBase baseline = getBaselineDistribution(om);
		GaussianBase relative = getRelativeEffectDistribution(d, om);
		if (baseline == null || relative == null) return null;
		return baseline.plus(relative);
	}

	public void runAllConsistencyModels() {
		List<Task> tasks = getNetworkTasks();
		ThreadHandler.getInstance().scheduleTasks(tasks);
	}

	public List<Task> getNetworkTasks() {
		List<Task> tasks = new ArrayList<Task>();
		for (MetaAnalysis ma : getMetaAnalyses() ){
			if (ma instanceof NetworkMetaAnalysis) {
				ConsistencyModel model = ((NetworkMetaAnalysis) ma).getConsistencyModel();
				tasks.add((Task) model.getActivityTask());
			}
		}
		return tasks;
	}
		
	public AnalysisType getAnalysisType() {
		return d_analysisType;
	}

	public List<Summary> getRelativeEffectSummaries() {
		List<Summary> summaryList = new ArrayList<Summary>();
		for (MetaAnalysis ma : getMetaAnalyses()) {
			if (ma instanceof NetworkMetaAnalysis) {
				for(Drug d: getNonBaselineAlternatives()) {
					Parameter p = new BasicParameter(new Treatment(getBaseline().getName()), new Treatment(d.getName()));
					NetworkMetaAnalysis nma = (NetworkMetaAnalysis)ma;
					summaryList.add(nma.getNormalSummary(nma.getConsistencyModel(), p));
				}
			}
		}
		return summaryList;
	}

	public List<Summary> getAbsoluteEffectSummaries() {
		List<Summary> summaryList = new ArrayList<Summary>();
		for (OutcomeMeasure om : getCriteria()) {
			summaryList.add(getBaselineModel(om).getSummary());
		}
		return summaryList;
	}

	public List<Summary> getEffectSummaries() {
		List<Summary> summaryList = getAbsoluteEffectSummaries();
		summaryList.addAll(getRelativeEffectSummaries());
		return summaryList;
	}

	public List<Drug> getNonBaselineAlternatives() {
		List<Drug> alternatives = getDrugs();
		alternatives.remove(getBaseline());
		return alternatives;
	}

	public MeasurementSource<Drug> getMeasurementSource() {
		return new MetaMeasurementSource();
	}
	
}
