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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.models.ConsistencyWrapper;
import org.drugis.addis.entities.analysis.models.InconsistencyWrapper;
import org.drugis.addis.entities.analysis.models.NodeSplitWrapper;
import org.drugis.addis.entities.analysis.models.SavedInconsistencyModel;
import org.drugis.addis.entities.analysis.models.SimulationConsistencyModel;
import org.drugis.addis.entities.analysis.models.SimulationInconsistencyModel;
import org.drugis.addis.entities.analysis.models.SimulationNodeSplitModel;
import org.drugis.addis.entities.data.MCMCSettings;
import org.drugis.addis.entities.relativeeffect.NetworkRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.util.EntityUtil;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.DefaultModelFactory;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.NodeSplitModel;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Network;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.MultivariateNormalSummary;
import org.drugis.mtc.summary.NodeSplitPValueSummary;
import org.drugis.mtc.summary.ProxyMultivariateNormalSummary;
import org.drugis.mtc.summary.QuantileSummary;

public class NetworkMetaAnalysis extends AbstractMetaAnalysis implements MetaAnalysis {
	
	private static final String ANALYSIS_TYPE = "Markov Chain Monte Carlo Network Meta-Analysis";
	private InconsistencyWrapper d_inconsistencyModel;
	private ConsistencyWrapper d_consistencyModel;
	private NetworkBuilder<DrugSet> d_builder;
	protected Map<Parameter, NodeSplitPValueSummary> d_nodeSplitPValueSummaries = 
		new HashMap<Parameter, NodeSplitPValueSummary>();
	
	private Map<BasicParameter, NodeSplitWrapper> d_nodeSplitModels = new HashMap<BasicParameter, NodeSplitWrapper>();
	private ProxyMultivariateNormalSummary d_relativeEffectsSummary =  new ProxyMultivariateNormalSummary();
	

	public NetworkMetaAnalysis(String name, Indication indication,
			OutcomeMeasure om, List<Study> studies, Collection<DrugSet> drugs,
			Map<Study, Map<DrugSet, Arm>> armMap) throws IllegalArgumentException {
		super(ANALYSIS_TYPE, name, indication, om, studies, sortDrugs(drugs), armMap);
	}
	
	public NetworkMetaAnalysis(String name, Indication indication,
			OutcomeMeasure om, Map<Study, Map<DrugSet, Arm>> armMap) throws IllegalArgumentException {
		super(ANALYSIS_TYPE, name, indication, om, armMap);
	}


	private static List<DrugSet> sortDrugs(Collection<DrugSet> drugs) {
		ArrayList<DrugSet> list = new ArrayList<DrugSet>(drugs);
		Collections.sort(list);
		return list;
	}

	private InconsistencyWrapper createInconsistencyModel() {
		InconsistencyModel inconsistencyModel = (DefaultModelFactory.instance()).getInconsistencyModel(getBuilder().buildNetwork());
		return new SimulationInconsistencyModel(getBuilder(), inconsistencyModel);
	}
	
	private ConsistencyWrapper createConsistencyModel() {
		ConsistencyModel consistencyModel = (DefaultModelFactory.instance()).getConsistencyModel(getBuilder().buildNetwork());
		SimulationConsistencyModel simulationConsistencyModel = new SimulationConsistencyModel(getBuilder(), consistencyModel, getIncludedDrugs());
		d_relativeEffectsSummary.setNested(simulationConsistencyModel.getRelativeEffectsSummary());
		return simulationConsistencyModel;
	}
	
	private NodeSplitWrapper createNodeSplitModel(BasicParameter node) {
		NodeSplitModel nodeSplitModel = (DefaultModelFactory.instance()).getNodeSplitModel(getBuilder().buildNetwork(), node);
		d_nodeSplitPValueSummaries.put(node, new NodeSplitPValueSummary(nodeSplitModel.getResults(), 
				nodeSplitModel.getDirectEffect(), nodeSplitModel.getIndirectEffect()));
		return new SimulationNodeSplitModel(getBuilder(), nodeSplitModel);
	}
	
	private NetworkBuilder<DrugSet> createBuilder(OutcomeMeasure outcomeMeasure, List<Study> studies, List<DrugSet> drugs, Map<Study, Map<DrugSet, Arm>> armMap) {
		return NetworkBuilderFactory.createBuilder(outcomeMeasure, studies, drugs, armMap);
	}

	public synchronized InconsistencyWrapper getInconsistencyModel() {
		if (d_inconsistencyModel == null) {
			d_inconsistencyModel = createInconsistencyModel();
		}
		return d_inconsistencyModel;
	}
	
	public synchronized ConsistencyWrapper getConsistencyModel() {
		if (d_consistencyModel == null) {
			d_consistencyModel = createConsistencyModel();
		}
		return d_consistencyModel;
	}

	public NodeSplitWrapper getNodeSplitModel(BasicParameter p) {
		if (!d_nodeSplitModels.containsKey(p)) {
			d_nodeSplitModels.put(p, createNodeSplitModel(p));
		}
		return d_nodeSplitModels.get(p);
	}

	private void setInconsistencyModel(SavedInconsistencyModel model) {
		d_inconsistencyModel = model;
	}

	public synchronized void loadInconsitencyModel(MCMCSettings settings,
			Map<Parameter, QuantileSummary> quantileSummaries, Map<Parameter, ConvergenceSummary> convergenceSummaries) {
		setInconsistencyModel(new SavedInconsistencyModel(getBuilder(), settings, quantileSummaries, convergenceSummaries));
	}
	
	public NetworkBuilder<DrugSet> getBuilder() {
		if (d_builder == null) {
			d_builder = createBuilder(d_outcome, d_studies, getIncludedDrugs(), d_armMap);
		}
		return d_builder;
	}
	
	public Network getNetwork() {
		return d_builder.buildNetwork();
	}


	public MultivariateNormalSummary getRelativeEffectsSummary() {
		return d_relativeEffectsSummary;
	}
	
	public boolean isContinuous() {
		return NetworkBuilderFactory.isContinuous(d_outcome);
	}
	
	public Treatment getTreatment(DrugSet d) {
		return d_builder.getTreatmentMap().get(d);
	}
	
	public DrugSet getDrugSet(Treatment t) {
		return d_builder.getTreatmentMap().getKey(t);
	}
	
	public List<BasicParameter> getSplitParameters() {
		return DefaultModelFactory.instance().getSplittableNodes(getBuilder().buildNetwork());
	}

	@Deprecated
	public NetworkRelativeEffect<? extends Measurement> getRelativeEffect(DrugSet d1, DrugSet d2, Class<? extends RelativeEffect<?>> type) {		
		if(!getConsistencyModel().isReady())
			return new NetworkRelativeEffect<Measurement>(); // empty relative effect.
		
		ConsistencyWrapper consistencyModel = getConsistencyModel();
		Parameter param = consistencyModel.getRelativeEffect(d1, d2);
		QuantileSummary estimate = consistencyModel.getQuantileSummary(param);
		
		if (isContinuous()) {
			return NetworkRelativeEffect.buildMeanDifference(estimate);
		} else {
			return NetworkRelativeEffect.buildOddsRatio(estimate);
		}
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
