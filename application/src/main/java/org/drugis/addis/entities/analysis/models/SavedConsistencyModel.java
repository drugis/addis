package org.drugis.addis.entities.analysis.models;

import java.util.List;

import org.drugis.addis.entities.DrugSet;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.MultivariateNormalSummary;
import org.drugis.mtc.summary.RankProbabilitySummary;

import edu.uci.ics.jung.graph.util.Pair;


public class SavedConsistencyModel extends AbstractSavedModel implements ConsistencyWrapper {


	public SavedConsistencyModel(NetworkBuilder<DrugSet> builder) {
		super(builder);
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
