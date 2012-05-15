package org.drugis.addis.entities.analysis.models;

import java.util.ArrayList;
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

	private final MultivariateNormalSummary d_relativeEffectsSummary;
	private final RankProbabilitySummary d_rankProbabilitySummary;
	private List<DrugSet> d_drugs;

	public SavedConsistencyModel(NetworkBuilder<DrugSet> builder,
			MCMCSettings settings,
			Map<Parameter, QuantileSummary> quantileSummaries,
			Map<Parameter, ConvergenceSummary> convergenceSummaries, 
			MultivariateNormalSummary relativeEffectsSummary, 
			RankProbabilitySummary rankProbabilitySummary, 
			List<DrugSet> drugs) {
		super(builder, settings, quantileSummaries, convergenceSummaries);
		d_relativeEffectsSummary = relativeEffectsSummary;
		d_rankProbabilitySummary = rankProbabilitySummary;
		
		d_drugs = drugs;
		List<Pair<DrugSet>> relEffects = getRelativeEffectsList();
		Parameter[] parameters = new Parameter[relEffects.size()]; 
		for (int i = 0; i < relEffects.size(); ++i) {
			Pair<DrugSet> relEffect = relEffects.get(i);
			parameters[i] = getRelativeEffect(relEffect.getFirst(), relEffect.getSecond());
		}
	}

	@Override
	public MultivariateNormalSummary getRelativeEffectsSummary() {
		return d_relativeEffectsSummary;
	}

	@Override
	public RankProbabilitySummary getRankProbabilities() {
		return d_rankProbabilitySummary;
	}

	@Override
	public List<Pair<DrugSet>> getRelativeEffectsList() {
		List<Pair<DrugSet>> list = new ArrayList<Pair<DrugSet>>(d_drugs.size() - 1); // first DrugSet is baseline-> excluded
		for (int i = 0; i < d_drugs.size() - 1; ++i) {
			Pair<DrugSet> relEffect = new Pair<DrugSet>(d_drugs.get(0), d_drugs.get(i + 1));
			list.add(relEffect);
		}
		return list;
	}

}
