package org.drugis.addis.entities.analysis.models;

import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.data.MCMCSettings;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.MultivariateNormalSummary;
import org.drugis.mtc.summary.QuantileSummary;
import org.drugis.mtc.summary.RankProbabilitySummary;

import edu.uci.ics.jung.graph.util.Pair;

public class SavedConsistencyModel extends AbstractSavedModel implements ConsistencyWrapper {

	public SavedConsistencyModel(NetworkBuilder<DrugSet> builder,
			MCMCSettings settings,
			Map<Parameter, QuantileSummary> quantileSummaries,
			Map<Parameter, ConvergenceSummary> convergenceSummaries) {
		super(builder, settings, quantileSummaries, convergenceSummaries);
		// TODO Auto-generated constructor stub
	}

	@Override
	public MultivariateNormalSummary getRelativeEffectsSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RankProbabilitySummary getRankProbabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Pair<DrugSet>> getRelativeEffectsList() {
		// TODO Auto-generated method stub
		return null;
	}

}
