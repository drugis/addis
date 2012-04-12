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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.bidimap.TreeBidiMap;
import org.apache.commons.lang.StringUtils;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.relativeeffect.NetworkRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.ContinuousNetworkBuilder;
import org.drugis.mtc.DefaultModelFactory;
import org.drugis.mtc.DichotomousNetworkBuilder;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.NodeSplitModel;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Network;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.summary.MCMCMultivariateNormalSummary;
import org.drugis.mtc.summary.NodeSplitPValueSummary;
import org.drugis.mtc.summary.NormalSummary;
import org.drugis.mtc.summary.ProxyMultivariateNormalSummary;
import org.drugis.mtc.summary.QuantileSummary;
import org.drugis.mtc.summary.RankProbabilitySummary;
import org.drugis.mtc.summary.MultivariateNormalSummary;

import edu.uci.ics.jung.graph.util.Pair;

public class NetworkMetaAnalysis extends AbstractMetaAnalysis implements MetaAnalysis {
	
	private static final String ANALYSIS_TYPE = "Markov Chain Monte Carlo Network Meta-Analysis";
	private InconsistencyModel d_inconsistencyModel;
	private ConsistencyModel d_consistencyModel;
	private NetworkBuilder<DrugSet> d_builder;
	protected Map<MCMCModel, Map<Parameter, NormalSummary>> d_normalSummaries = 
		new HashMap<MCMCModel, Map<Parameter, NormalSummary>>();
	protected final ProxyMultivariateNormalSummary d_relativeEffectsSummary = new ProxyMultivariateNormalSummary();
	protected Map<MCMCModel, Map<Parameter, QuantileSummary>> d_quantileSummaries = 
		new HashMap<MCMCModel, Map<Parameter, QuantileSummary>>();
	protected Map<Parameter, NodeSplitPValueSummary> d_nodeSplitPValueSummaries = 
		new HashMap<Parameter, NodeSplitPValueSummary>();
	
	private RankProbabilitySummary d_rankProbabilitySummary;
	private Map<BasicParameter, NodeSplitModel> d_nodeSplitModels = new HashMap<BasicParameter, NodeSplitModel>();
	private static final Transformer<DrugSet, String> s_transform = new Transformer<DrugSet, String>() {
		private final BidiMap<Drug, String> nameLookup = new TreeBidiMap<Drug, String>();  
		@Override
		public String transform(DrugSet input) {
			List<String> names = new ArrayList<String>();
			for (Drug drug : input.getContents()) {
				names.add(getCleanName(drug));
			}
			return StringUtils.join(names, "_");
		}

		private String getCleanName(Drug drug) {
			if (!nameLookup.containsKey(drug)) {
				insertUniqueName(drug);
			}
			return nameLookup.get(drug);
		}

		private void insertUniqueName(Drug drug) {
			String sanitized = sanitize(drug.getName());
			String name = sanitized;
			int i = 1;
			while (nameLookup.containsValue(name)) {
				name = sanitized + ++i;
			}
			nameLookup.put(drug, name);
		}
		
		private String sanitize(String dirtyString) {
			return dirtyString.replaceAll("[^a-zA-Z0-9]", "");
		}
	};
	

	public NetworkMetaAnalysis(String name, Indication indication,
			OutcomeMeasure om, List<Study> studies, Collection<DrugSet> drugs,
			Map<Study, Map<DrugSet, Arm>> armMap) throws IllegalArgumentException {
		super(ANALYSIS_TYPE, name, indication, om, studies, sortDrugs(drugs), armMap);
	}

	private static List<DrugSet> sortDrugs(Collection<DrugSet> drugs) {
		ArrayList<DrugSet> list = new ArrayList<DrugSet>(drugs);
		Collections.sort(list);
		return list;
	}

	public NetworkMetaAnalysis(String name, Indication indication,
			OutcomeMeasure om, Map<Study, Map<DrugSet, Arm>> armMap) throws IllegalArgumentException {
		super(ANALYSIS_TYPE, name, indication, om, armMap);
	}

	private InconsistencyModel createInconsistencyModel() {
		InconsistencyModel inconsistencyModel = (DefaultModelFactory.instance()).getInconsistencyModel(getBuilder().buildNetwork());
		d_normalSummaries.put(inconsistencyModel, new HashMap<Parameter, NormalSummary>());
		d_quantileSummaries.put(inconsistencyModel, new HashMap<Parameter, QuantileSummary>());
		return inconsistencyModel;
	}
	
	private ConsistencyModel createConsistencyModel() {
		ConsistencyModel consistencyModel = (DefaultModelFactory.instance()).getConsistencyModel(getBuilder().buildNetwork());
		d_normalSummaries.put(consistencyModel, new HashMap<Parameter, NormalSummary>());
		d_quantileSummaries.put(consistencyModel, new HashMap<Parameter, QuantileSummary>());
		List<Pair<DrugSet>> relEffects = getRelativeEffectsList();
		Parameter[] parameters = new Parameter[relEffects.size()]; 
		for (int i = 0; i < relEffects.size(); ++i) {
			Pair<DrugSet> relEffect = relEffects.get(i);
			parameters[i] = consistencyModel.getRelativeEffect(getTreatment(relEffect.getFirst()), getTreatment(relEffect.getSecond()));
		}
		d_relativeEffectsSummary.setNested(new MCMCMultivariateNormalSummary(consistencyModel.getResults(), parameters));
		return consistencyModel;
	}
	
	private NodeSplitModel createNodeSplitModel(BasicParameter node) {
		NodeSplitModel nodeSplitModel = (DefaultModelFactory.instance()).getNodeSplitModel(getBuilder().buildNetwork(), node);
		d_normalSummaries.put(nodeSplitModel, new HashMap<Parameter, NormalSummary>());
		d_quantileSummaries.put(nodeSplitModel, new HashMap<Parameter, QuantileSummary>());
		d_nodeSplitPValueSummaries.put(node, new NodeSplitPValueSummary(nodeSplitModel.getResults(), 
				nodeSplitModel.getDirectEffect(), nodeSplitModel.getIndirectEffect()));
		return nodeSplitModel;
	}
	
	private NetworkBuilder<DrugSet> createBuilder(List<Study> studies, List<DrugSet> drugs, Map<Study, Map<DrugSet, Arm>> armMap) {
		if (isContinuous()) {
			return createContinuousBuilder(studies, drugs, armMap);
		} else {
			return createRateBuilder(studies, drugs, armMap);
		}
	}
	
	private NetworkBuilder<DrugSet> createContinuousBuilder(List<Study> studies, List<DrugSet> drugs, Map<Study, Map<DrugSet, Arm>> armMap) {
		ContinuousNetworkBuilder<DrugSet> builder = new ContinuousNetworkBuilder<DrugSet>(s_transform);
		for(Study s : studies){
			for (DrugSet d : drugs) {
				if (armMap.get(s).containsKey(d)) {
					BasicContinuousMeasurement cm = (BasicContinuousMeasurement) s.getMeasurement(d_outcome, armMap.get(s).get(d));
					builder.add(s.getName(), s.getDrugs(armMap.get(s).get(d)), cm.getMean(), cm.getStdDev(), cm.getSampleSize());
				}
        	}
        }
		return builder;
	}

	private NetworkBuilder<DrugSet> createRateBuilder(List<Study> studies, List<DrugSet> drugs, Map<Study, Map<DrugSet, Arm>> armMap) {
		DichotomousNetworkBuilder<DrugSet> builder = new DichotomousNetworkBuilder<DrugSet>(s_transform);
		for(Study s : studies){
			for (DrugSet d : drugs) {
				if (armMap.get(s).containsKey(d)) {
					BasicRateMeasurement brm = (BasicRateMeasurement) s.getMeasurement(d_outcome, armMap.get(s).get(d));
					builder.add(s.getName(), s.getDrugs(armMap.get(s).get(d)), brm.getRate(), brm.getSampleSize());
				}
        	}
        }
		return builder;
	}

	public synchronized InconsistencyModel getInconsistencyModel() {
		if (d_inconsistencyModel == null) {
			d_inconsistencyModel = createInconsistencyModel();
		}
		return d_inconsistencyModel;
	}
	
	public synchronized ConsistencyModel getConsistencyModel() {
		if (d_consistencyModel == null) {
			d_consistencyModel = createConsistencyModel();
		}
		return d_consistencyModel;
	}

	public NetworkBuilder<DrugSet> getBuilder() {
		if (d_builder == null) {
			d_builder = createBuilder(d_studies, getIncludedDrugs(), d_armMap);
		}
		return d_builder;
	}
	
	public Network getNetwork() {
		return d_builder.buildNetwork();
	}
	
	public void run() {
		List<Task> tasks = new ArrayList<Task>();
		if (!getConsistencyModel().isReady()) {
			tasks.add(getConsistencyModel().getActivityTask());
		}
		if (!getInconsistencyModel().isReady()) {
			tasks.add(getInconsistencyModel().getActivityTask());
		}
		ThreadHandler.getInstance().scheduleTasks(tasks);
	}


	public List<Parameter> getInconsistencyFactors(){
		return getInconsistencyModel().getInconsistencyFactors();
	}
	
	public QuantileSummary getQuantileSummary(MixedTreatmentComparison networkModel, Parameter ip) {
		QuantileSummary summary = d_quantileSummaries.get(networkModel).get(ip);
		if (summary == null) {
			summary = new QuantileSummary(networkModel.getResults(), ip);
			d_quantileSummaries.get(networkModel).put(ip, summary);
		}
		return summary;
	}
	
	public NormalSummary getNormalSummary(MixedTreatmentComparison networkModel, Parameter ip) {
		NormalSummary summary = d_normalSummaries.get(networkModel).get(ip);
		if (summary == null) {
			summary = new NormalSummary(networkModel.getResults(), ip);
			d_normalSummaries.get(networkModel).put(ip, summary);
		}
		return summary;
	}
	
	/**
	 * Return a multivariate summary of the effects for all treatments relative to the baseline. 
	 * The order in which the relative effects are given is based on the natural ordering of the
	 * treatments. The first treatment is used as the baseline.  
	 * 
	 * @see getRelativeEffectsList()
	 * @return A multivariate summary of all the relative effects. 
	 */
	public MultivariateNormalSummary getRelativeEffectsSummary() {
		return d_relativeEffectsSummary;
	}
	
	/**
	 * @return A list of all <baseline, subject> pairs, where the subjects are given in their natural order  
	 */
	public List<Pair<DrugSet>> getRelativeEffectsList() {
		List<Pair<DrugSet>> list = new ArrayList<Pair<DrugSet>>(getIncludedDrugs().size() - 1); // first DrugSet is baseline-> excluded
		for (int i = 0; i < getIncludedDrugs().size() - 1; ++i) {
			Pair<DrugSet> relEffect = new Pair<DrugSet>(getIncludedDrugs().get(0), getIncludedDrugs().get(i + 1));
			list.add(relEffect);
		}
		return list;
	}

	public NodeSplitPValueSummary getNodesNodeSplitPValueSummary(Parameter p) {
		NodeSplitPValueSummary summary = d_nodeSplitPValueSummaries.get(p);
		if(summary == null) {
			NodeSplitModel m = getNodeSplitModel((BasicParameter) p);
			Parameter dir = m.getDirectEffect();
			Parameter indir = m.getIndirectEffect();
			summary = new NodeSplitPValueSummary(m.getResults(), dir, indir);
			d_nodeSplitPValueSummaries.put(p, summary);
		}
		return summary;
	}
	
	public RankProbabilitySummary getRankProbabilities() {
		if (d_rankProbabilitySummary == null) {
			d_rankProbabilitySummary = new RankProbabilitySummary(d_consistencyModel.getResults(), getTreatments());
		}
		return d_rankProbabilitySummary;
	}

	public boolean isContinuous() {
		if (d_outcome.getVariableType() instanceof RateVariableType) {
			return false;
		} else if (d_outcome.getVariableType() instanceof ContinuousVariableType) {
			return true;
		} else {
			throw new IllegalStateException("Unexpected VariableType: " + d_outcome.getVariableType());
		}
	}

	public NetworkRelativeEffect<? extends Measurement> getRelativeEffect(DrugSet d1, DrugSet d2, Class<? extends RelativeEffect<?>> type) {
		
		if(!getConsistencyModel().isReady())
			return new NetworkRelativeEffect<Measurement>(); // empty relative effect.
		
		ConsistencyModel consistencyModel = getConsistencyModel();
		Parameter param = consistencyModel.getRelativeEffect(getTreatment(d1), getTreatment(d2));
		NormalSummary estimate = getNormalSummary(consistencyModel, param);
		
		if (isContinuous()) {
			return NetworkRelativeEffect.buildMeanDifference(estimate.getMean(), estimate.getStandardDeviation());
		} else {
			return NetworkRelativeEffect.buildOddsRatio(estimate.getMean(), estimate.getStandardDeviation());
		}
	}
	
	public List<Treatment> getTreatments() {
		List<Treatment> treatments = new ArrayList<Treatment>();
		for (DrugSet d : d_drugs) {
			treatments.add(getTreatment(d));
		}
		return treatments;
	}

	public Treatment getTreatment(DrugSet d) {
		return getBuilder().getTreatmentMap().get(d);
	}

	public List<BasicParameter> getSplitParameters() {
		return DefaultModelFactory.instance().getSplittableNodes(getBuilder().buildNetwork());
	}

	public NodeSplitModel getNodeSplitModel(BasicParameter p) {
		if (!d_nodeSplitModels.containsKey(p)) {
			d_nodeSplitModels.put(p, createNodeSplitModel(p));
		}
		return d_nodeSplitModels.get(p);
	}
	
	@Override
	public boolean deepEquals(Entity other) {
		if (!super.deepEquals(other)) {
			return false;
		}
		NetworkMetaAnalysis o = (NetworkMetaAnalysis) other;
		for (DrugSet d : o.getIncludedDrugs()) {
			for (Study s : o.getIncludedStudies()) {
				if (!EntityUtil.deepEqual(getArm(s, d), o.getArm(s, d))) {
					return false;
				}
			}
		}
		return true;
	}
	
}
