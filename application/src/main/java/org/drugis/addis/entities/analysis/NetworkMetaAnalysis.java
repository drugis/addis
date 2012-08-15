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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.presentation.mcmc.MCMCResultsAvailableModel;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.threading.status.TaskTerminatedModel;
import org.drugis.common.validation.BooleanAndModel;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.DefaultModelFactory;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.MCMCSettingsCache;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.NodeSplitModel;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Network;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.parameterization.ParameterComparator;
import org.drugis.mtc.presentation.ConsistencyWrapper;
import org.drugis.mtc.presentation.InconsistencyWrapper;
import org.drugis.mtc.presentation.MCMCModelWrapper;
import org.drugis.mtc.presentation.NodeSplitWrapper;
import org.drugis.mtc.presentation.SavedConsistencyWrapper;
import org.drugis.mtc.presentation.SavedInconsistencyWrapper;
import org.drugis.mtc.presentation.SavedNodeSplitWrapper;
import org.drugis.mtc.presentation.SimulationConsistencyWrapper;
import org.drugis.mtc.presentation.SimulationInconsistencyWrapper;
import org.drugis.mtc.presentation.SimulationNodeSplitWrapper;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.MultivariateNormalSummary;
import org.drugis.mtc.summary.NodeSplitPValueSummary;
import org.drugis.mtc.summary.ProxyMultivariateNormalSummary;
import org.drugis.mtc.summary.QuantileSummary;
import org.drugis.mtc.summary.RankProbabilitySummary;

import com.jgoodies.binding.value.ValueModel;

public class NetworkMetaAnalysis extends AbstractMetaAnalysis implements MetaAnalysis {

	private static final String PROPERTY_MCMC_RESULTS = "MCMCResults";
	private static final String ANALYSIS_TYPE = "Markov Chain Monte Carlo Network Meta-Analysis";
	private InconsistencyWrapper<TreatmentDefinition> d_inconsistencyModel;
	private ConsistencyWrapper<TreatmentDefinition> d_consistencyModel;
	protected NetworkBuilder<TreatmentDefinition> d_builder;
	protected Map<Parameter, NodeSplitPValueSummary> d_nodeSplitPValueSummaries =
		new HashMap<Parameter, NodeSplitPValueSummary>();

	private final Map<BasicParameter, NodeSplitWrapper<TreatmentDefinition>> d_nodeSplitModels = new TreeMap<BasicParameter, NodeSplitWrapper<TreatmentDefinition>>(new ParameterComparator());
	private final ProxyMultivariateNormalSummary d_relativeEffectsSummary =  new ProxyMultivariateNormalSummary();


	public NetworkMetaAnalysis(final String name, final Indication indication,
			final OutcomeMeasure om, final List<Study> studies, final Collection<TreatmentDefinition> alternatives,
			final Map<Study, Map<TreatmentDefinition, Arm>> armMap) throws IllegalArgumentException {
		super(ANALYSIS_TYPE, name, indication, om, studies, sortAlternatives(alternatives), armMap);
	}

	public NetworkMetaAnalysis(final String name, final Indication indication,
			final OutcomeMeasure om, final Map<Study, Map<TreatmentDefinition, Arm>> armMap) throws IllegalArgumentException {
		super(ANALYSIS_TYPE, name, indication, om, armMap);
	}


	private static List<TreatmentDefinition> sortAlternatives(final Collection<TreatmentDefinition> alternatives) {
		final ArrayList<TreatmentDefinition> list = new ArrayList<TreatmentDefinition>(alternatives);
		Collections.sort(list);
		return list;
	}

	private InconsistencyWrapper<TreatmentDefinition> createInconsistencyModel() {
		final InconsistencyModel inconsistencyModel = (DefaultModelFactory.instance()).getInconsistencyModel(getBuilder().buildNetwork());
		attachModelSavableListener(inconsistencyModel);
		return new SimulationInconsistencyWrapper<TreatmentDefinition>(inconsistencyModel, getBuilder().getTreatmentMap());
	}

	private ConsistencyWrapper<TreatmentDefinition> createConsistencyModel() {
		final ConsistencyModel consistencyModel = (DefaultModelFactory.instance()).getConsistencyModel(getBuilder().buildNetwork());
		final SimulationConsistencyWrapper<TreatmentDefinition> model = new SimulationConsistencyWrapper<TreatmentDefinition>(consistencyModel, getAlternatives(), getBuilder().getTreatmentMap());
		d_relativeEffectsSummary.setNested(model.getRelativeEffectsSummary());
		attachModelSavableListener(consistencyModel);
		return model;
	}

	private NodeSplitWrapper<TreatmentDefinition> createNodeSplitModel(final BasicParameter node) {
		final NodeSplitModel nodeSplitModel = (DefaultModelFactory.instance()).getNodeSplitModel(getBuilder().buildNetwork(), node);
		d_nodeSplitPValueSummaries.put(node, new NodeSplitPValueSummary(nodeSplitModel.getResults(),
				nodeSplitModel.getDirectEffect(), nodeSplitModel.getIndirectEffect()));
		attachModelSavableListener(nodeSplitModel);
		return new SimulationNodeSplitWrapper<TreatmentDefinition>(nodeSplitModel, getBuilder().getTreatmentMap());
	}

	private NetworkBuilder<TreatmentDefinition> createBuilder(final OutcomeMeasure outcomeMeasure, final List<Study> studies, final List<TreatmentDefinition> alternatives, final Map<Study, Map<TreatmentDefinition, Arm>> armMap) {
		return NetworkBuilderFactory.createBuilder(outcomeMeasure, studies, alternatives, armMap);
	}

	private void attachModelSavableListener(final MixedTreatmentComparison model) {
		final MCMCResultsAvailableModel resultsAvailableModel = new MCMCResultsAvailableModel(model.getResults());
		final TaskTerminatedModel modelTerminated = new TaskTerminatedModel(model.getActivityTask());
		final BooleanAndModel modelFinishedAndResults = new BooleanAndModel(Arrays.<ValueModel>asList(modelTerminated, resultsAvailableModel));
		modelFinishedAndResults.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				firePropertyChange(PROPERTY_MCMC_RESULTS, false, true);
			}
		});
	}

	public synchronized InconsistencyWrapper<TreatmentDefinition> getInconsistencyModel() {
		if (d_inconsistencyModel == null || d_inconsistencyModel.getDestroyed()) {
			d_inconsistencyModel = createInconsistencyModel();
		}
		return d_inconsistencyModel;
	}

	public synchronized ConsistencyWrapper<TreatmentDefinition> getConsistencyModel() {
		if (d_consistencyModel == null || d_consistencyModel.getDestroyed()) {
			d_consistencyModel = createConsistencyModel();
		}
		return d_consistencyModel;
	}

	public synchronized NodeSplitWrapper<TreatmentDefinition> getNodeSplitModel(final BasicParameter p) {
		if (!d_nodeSplitModels.containsKey(p) || d_nodeSplitModels.get(p).getDestroyed()) {
			d_nodeSplitModels.put(p, createNodeSplitModel(p));
		}
		return d_nodeSplitModels.get(p);
	}

	public synchronized void loadInconsistencyModel(final MCMCSettingsCache settings,
			final Map<Parameter, QuantileSummary> quantileSummaries, final Map<Parameter, ConvergenceSummary> convergenceSummaries) {
		d_inconsistencyModel = new SavedInconsistencyWrapper<TreatmentDefinition>(settings, quantileSummaries, convergenceSummaries, d_builder.getTreatmentMap());

	}

	public synchronized void loadConsistencyModel(final MCMCSettingsCache mcmcSettingsCache,
			final HashMap<Parameter, QuantileSummary> quantileSummaries,
			final HashMap<Parameter, ConvergenceSummary> convergenceSummaries,
			final MultivariateNormalSummary relativeEffectsSummary,
			final RankProbabilitySummary rankProbabilitySummary) {
		d_consistencyModel = new SavedConsistencyWrapper<TreatmentDefinition>(mcmcSettingsCache,
				quantileSummaries,
				convergenceSummaries,
				relativeEffectsSummary,
				rankProbabilitySummary,
				getAlternatives(),
				getBuilder().getTreatmentMap());
		d_relativeEffectsSummary.setNested(d_consistencyModel.getRelativeEffectsSummary());
	}

	public void loadNodeSplitModel(final BasicParameter splitParameter,
			final MCMCSettingsCache settings,
			final HashMap<Parameter, QuantileSummary> quantileSummaries,
			final HashMap<Parameter, ConvergenceSummary> convergenceSummaries,
			final NodeSplitPValueSummary nodeSplitPValueSummary) {
		final SavedNodeSplitWrapper<TreatmentDefinition> nodeSplitModel = new SavedNodeSplitWrapper<TreatmentDefinition>(settings,
				quantileSummaries,
				convergenceSummaries,
				splitParameter,
				nodeSplitPValueSummary,
				d_builder.getTreatmentMap());
		d_nodeSplitModels.put(splitParameter, nodeSplitModel);
	}

	public void resetNodeSplitModels() {
		d_nodeSplitModels.clear();
		for(final BasicParameter p : getSplitParameters()) {
			getNodeSplitModel(p);
		}
	}

	public NetworkBuilder<TreatmentDefinition> getBuilder() {
		if (d_builder == null) {
			d_builder = createBuilder(d_outcome, d_studies, getAlternatives(), d_armMap);
		}
		return d_builder;
	}

	public Network getNetwork() {
		return getBuilder().buildNetwork();
	}


	@Override
	public MultivariateNormalSummary getRelativeEffectsSummary() {
		return d_relativeEffectsSummary;
	}

	public boolean isContinuous() {
		return NetworkBuilderFactory.isContinuous(d_outcome);
	}

	public Treatment getTreatment(final TreatmentDefinition d) {
		return getBuilder().getTreatmentMap().get(d);
	}

	public TreatmentDefinition getTreatmentDefinition(final Treatment t) {
		return getBuilder().getTreatmentMap().getKey(t);
	}

	public List<BasicParameter> getSplitParameters() {
		return DefaultModelFactory.instance().getSplittableNodes(getBuilder().buildNetwork());
	}

	public Collection<NodeSplitWrapper<TreatmentDefinition>> getNodeSplitModels() {
		return d_nodeSplitModels.values();
	}

	@Override
	public boolean deepEquals(final Entity other) {
		if (!super.deepEquals(other)) {
			return false;
		}
		final NetworkMetaAnalysis o = (NetworkMetaAnalysis) other;
		for (final TreatmentDefinition d : o.getAlternatives()) {
			for (final Study s : o.getIncludedStudies()) {
				if (!EntityUtil.deepEqual(getArm(s, d), o.getArm(s, d))) {
					return false;
				}
			}
		}
		return true;
	}

	public void reset(final MCMCModelWrapper m) {
		m.selfDestruct();
		firePropertyChange(PROPERTY_MCMC_RESULTS, true, false);
	}
}
