/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import javax.swing.SwingUtilities;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.LogGaussian;
import org.drugis.addis.entities.relativeeffect.LogitGaussian;
import org.drugis.addis.mcmcmodel.AbstractBaselineModel;
import org.drugis.addis.mcmcmodel.BaselineMeanDifferenceModel;
import org.drugis.addis.mcmcmodel.BaselineOddsModel;
import org.drugis.addis.util.EntityUtil;
import org.drugis.addis.util.comparator.AlphabeticalComparator;
import org.drugis.common.beans.SortedSetModel;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.summary.MultivariateNormalSummary;
import org.drugis.mtc.summary.NormalSummary;
import org.drugis.mtc.summary.Summary;
import org.drugis.mtc.summary.TransformedMultivariateNormalSummary;

import com.jgoodies.binding.list.ObservableList;

public class MetaBenefitRiskAnalysis extends BenefitRiskAnalysis<DrugSet> {
	private final class MetaMeasurementSource extends AbstractMeasurementSource<DrugSet> {
		public MetaMeasurementSource() {
			PropertyChangeListener l = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							notifyListeners();							
						}
					});
				}
			};
			for (Summary s : getEffectSummaries()) {
				s.addPropertyChangeListener(l);
			}
		}
		
		private List<Summary> getEffectSummaries() {
			List<Summary> summaryList = getAbsoluteEffectSummaries();
			summaryList.addAll(d_relativeEffects.values());
			return summaryList;
		}
	}

	private Indication d_indication;
	private List<MetaAnalysis> d_metaAnalyses;
	private ObservableList<DrugSet> d_drugs;
	private DrugSet d_baseline;
	private Map<OutcomeMeasure, AbstractBaselineModel<?>> d_baselineModelMap;
	private AnalysisType d_analysisType;
	private DecisionContext d_decisionContext;
	private Map<MetaAnalysis, TransformedMultivariateNormalSummary> d_relativeEffects =
		new HashMap<MetaAnalysis, TransformedMultivariateNormalSummary>();
	
	public static String PROPERTY_DRUGS = "drugs";
	public static String PROPERTY_BASELINE = "baseline";
	public static String PROPERTY_METAANALYSES = "metaAnalyses";

	
	public MetaBenefitRiskAnalysis(String name, Indication indication, List<MetaAnalysis> metaAnalysis,
			DrugSet baseline, List<DrugSet> drugs, AnalysisType analysisType) {
		this(name, indication, metaAnalysis, baseline, drugs, analysisType, null); 
	}

	public MetaBenefitRiskAnalysis(String name, Indication indication, List<MetaAnalysis> metaAnalysis,
			DrugSet baseline, List<DrugSet> drugs, AnalysisType analysisType, DecisionContext context) {
		super(name);
		d_indication = indication;
		d_metaAnalyses = metaAnalysis;
		d_drugs = new SortedSetModel<DrugSet>(drugs);
		d_baseline = baseline;
		d_drugs.add(baseline);

		d_baselineModelMap = new HashMap<OutcomeMeasure, AbstractBaselineModel<?>>();
		d_analysisType = analysisType;
		if(d_analysisType == AnalysisType.LyndOBrien && (d_metaAnalyses.size() != 2 || d_drugs.size() != 2) ) {
			throw new IllegalArgumentException("Attempt to create Lynd & O'Brien analysis with not exactly 2 criteria and 2 alternatives");
		}
		d_decisionContext = context;
		for (MetaAnalysis ma : d_metaAnalyses) {
			double[][] transformation = createTransform(ma);
			d_relativeEffects.put(ma, new TransformedMultivariateNormalSummary(ma.getRelativeEffectsSummary(), transformation));
		}
	}

	/** 
	 * Fill in a transformation matrix to change the baseline of the relative effects.
	 * For the algorithm see docs/transform.pdf in the repository.
	 */
	private double[][] createTransform(MetaAnalysis ma) {
		final List<DrugSet> rowAlternatives = getNonBaselineAlternatives();
		final List<DrugSet> columnAlternatives = new ArrayList<DrugSet>(ma.getIncludedDrugs());

		final DrugSet rowBaseline = d_baseline;
		final DrugSet columnBaseline = columnAlternatives.remove(0); // first drugSet in metaAnalysis is baseline by definition
		
		final int nRows = rowAlternatives.size();
		final int nCols = columnAlternatives.size();

		double[][] transformation = new double[nRows][nCols];
		
		// Change of baseline (from x to y): d_{y,z} = -d_{x,y} + d_{x,z}

		// fill the column where the rowBaseline occurs with -1 (if baselines differ)
		if (!rowBaseline.equals(columnBaseline)) {
			int minusColumn = columnAlternatives.indexOf(rowBaseline);
			for (int i = 0; i < nRows; ++i) {
				transformation[i][minusColumn] = -1;
			}
		}
		
		// +1 where row- and column-drugsets match
		for (int i = 0; i < nRows; ++i) {
			int oneColumn = columnAlternatives.indexOf(rowAlternatives.get(i));
			if (oneColumn >= 0) {
				transformation[i][oneColumn] = 1;
			}
		}
		
		return transformation;
	}

	public Indication getIndication() {
		return d_indication;
	}

	public List<OutcomeMeasure> getCriteria() {
		List<OutcomeMeasure> sortedList = findOutcomeMeasures();
		Collections.sort(sortedList);
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
	
	public ObservableList<DrugSet> getAlternatives() {
		return getDrugs();
	}

	public ObservableList<DrugSet> getDrugs() {
		return d_drugs;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		HashSet<Entity> dependencies = new HashSet<Entity>();
		dependencies.add(d_indication);
		dependencies.addAll(EntityUtil.flatten(d_drugs));
		dependencies.addAll(d_metaAnalyses);
		return dependencies;
	}

	@Deprecated
	void setName(String name) {
		d_name = name;
	}

	@Override
	public boolean equals(Object other){
		if (other == null)
			return false;
		if (!(other instanceof MetaBenefitRiskAnalysis))
			return false;
		return this.getName().equals( ((BenefitRiskAnalysis<?>)other).getName() );
	}
	
	@Override
	public boolean deepEquals(Entity other) {
		if (!equals(other)) {
			return false;
		}
		MetaBenefitRiskAnalysis o = (MetaBenefitRiskAnalysis) other;
		return EntityUtil.deepEqual(getBaseline(), o.getBaseline()) &&
			EntityUtil.deepEqual(getIndication(), o.getIndication()) &&
			EntityUtil.deepEqual(getMetaAnalyses(), o.getMetaAnalyses()) &&
			EntityUtil.deepEqual(getDrugs(), o.getDrugs()) &&
			EntityUtil.deepEqual(getDecisionContext(), o.getDecisionContext());
	}

	@Override
	public String toString() {
		return getName();
	}

	public DrugSet getBaseline() {
		return d_baseline;
	}

	private MetaAnalysis findMetaAnalysis(OutcomeMeasure om) {
		for(MetaAnalysis ma : getMetaAnalyses()){
			if(ma.getOutcomeMeasure().equals(om)) {
				return ma;
			}
		}
		return null;
	}
	
	public GaussianBase getRelativeEffectDistribution(OutcomeMeasure om, DrugSet subject) {
		if (subject.equals(d_baseline)) {
			return createDistribution(om, 0.0, 0.0); 
		}
		MetaAnalysis ma = findMetaAnalysis(om);
		if (ma == null) {
			throw new IllegalArgumentException("No meta-analysis for outcome " + om);
		}
		MultivariateNormalSummary summary = d_relativeEffects.get(ma);
		if (summary.getDefined()) {
			int index = getNonBaselineAlternatives().indexOf(subject);
			return createDistribution(om, summary.getMeanVector()[index], Math.sqrt(summary.getCovarianceMatrix()[index][index]));
		} else {
			return null;
		}
	}
	
	/**
	 * Get a summary of the effects on the given criterion of the non-baseline alternatives relative to the baseline.
	 * @param om The criterion to get the summary for.
	 * @return A MultivariateNormalSummary (mean and covariance) of the relative effects.
	 * @see MetaBenefitRiskAnalysis#getNonBaselineAlternatives() The non-baseline alternatives.
	 * @see MetaBenefitRiskAnalysis#getBaseline() The baseline.
	 */
	public MultivariateNormalSummary getRelativeEffectsSummary(OutcomeMeasure om) {
		return d_relativeEffects.get(findMetaAnalysis(om));
	}

	private GaussianBase createDistribution(OutcomeMeasure om, double mu, double sigma) {
		return (om.getVariableType() instanceof RateVariableType) ? new LogGaussian(mu, sigma) : new Gaussian(mu, sigma);
	}
	
	/**
	 * Get the measurement to be used in the BenefitRisk simulation.
	 */
	public Distribution getMeasurement(OutcomeMeasure om, DrugSet d) {
		if (om.getVariableType() instanceof RateVariableType) {
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
		NormalSummary summary = model.getSummary();
		if (!summary.getDefined()) {
			return null;
		}
		return createDistribution(om, summary.getMean(), summary.getStandardDeviation());
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
		return (AbstractBaselineModel<?>) ((om.getVariableType() instanceof RateVariableType) ? 
				new BaselineOddsModel(getBaselineMeasurements(om, RateMeasurement.class)) : 
				new BaselineMeanDifferenceModel(getBaselineMeasurements(om, ContinuousMeasurement.class)));
	}
	
	@SuppressWarnings("unchecked")
	private <M extends Measurement> List<M> getBaselineMeasurements(OutcomeMeasure om, Class<M> cls) {
		List<M> result = new ArrayList<M>(); 
		for (MetaAnalysis ma : getMetaAnalyses())
			if (ma.getOutcomeMeasure().equals(om))
				for (Study s : ma.getIncludedStudies())
					for (Arm a : s.getArms())
						if (s.getDrugs(a).equals(getBaseline()))
							result.add((M)s.getMeasurement(om, a));
		
		return result;
	}
	
	/**
	 * The absolute effect of d on om given the assumed odds of the baseline treatment. 
	 */
	private GaussianBase getAbsoluteEffectDistribution(DrugSet d, OutcomeMeasure om) {
		GaussianBase baseline = getBaselineDistribution(om);
		GaussianBase relative = getRelativeEffectDistribution(om, d);
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
				for(DrugSet d: getNonBaselineAlternatives()) {
					NetworkMetaAnalysis nma = (NetworkMetaAnalysis)ma;
					Parameter p = new BasicParameter(nma.getTreatment(getBaseline()), nma.getTreatment(d));
					summaryList.add(nma.getQuantileSummary(nma.getConsistencyModel(), p));
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

	public List<DrugSet> getNonBaselineAlternatives() {
		List<DrugSet> alternatives = new ArrayList<DrugSet>(getDrugs());
		alternatives.remove(getBaseline());
		return alternatives;
	}

	public MeasurementSource<DrugSet> getMeasurementSource() {
		return new MetaMeasurementSource();
	}

	public DecisionContext getDecisionContext() {
		return d_decisionContext;
	}
}
