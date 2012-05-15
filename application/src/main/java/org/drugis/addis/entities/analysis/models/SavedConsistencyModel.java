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
