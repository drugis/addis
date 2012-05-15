package org.drugis.addis.entities.analysis.models;

import java.util.List;

import org.drugis.addis.entities.DrugSet;
import org.drugis.mtc.summary.MultivariateNormalSummary;
import org.drugis.mtc.summary.RankProbabilitySummary;

import edu.uci.ics.jung.graph.util.Pair;

public interface ConsistencyWrapper extends MTCModelWrapper {

	/**
	 * Return a multivariate summary of the effects for all treatments relative to the baseline. 
	 * The order in which the relative effects are given is based on the natural ordering of the
	 * treatments. The first treatment is used as the baseline.  
	 * 
	 * @see getRelativeEffectsList()
	 * @return A multivariate summary of all the relative effects. 
	 */
	public MultivariateNormalSummary getRelativeEffectsSummary();
	
	public RankProbabilitySummary getRankProbabilities();
	
	/**
	 * @return A list of all <baseline, subject> pairs, where the subjects are given in their natural order  
	 */	
	public List<Pair<DrugSet>> getRelativeEffectsList();

}
