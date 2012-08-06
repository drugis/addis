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

package org.drugis.addis.entities.mtcwrapper;

import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.NodeSplitModel;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.summary.NodeSplitPValueSummary;

public class SimulationNodeSplitWrapper extends AbstractSimulationWrapper<NodeSplitModel> implements NodeSplitWrapper {
	private NodeSplitPValueSummary d_pValueSummary;

	public SimulationNodeSplitWrapper(NetworkBuilder<TreatmentDefinition> builder, NodeSplitModel model) {
		super(builder, model, "Node Split on " + model.getSplitNode().getName());
	}

	@Override
	public Parameter getDirectEffect() {
		return d_nested.getDirectEffect();
	}

	@Override
	public Parameter getIndirectEffect() {
		return d_nested.getIndirectEffect();
	}

	@Override
	public BasicParameter getSplitNode() {
		return d_nested.getSplitNode();
	}

	@Override
	public NodeSplitPValueSummary getNodeSplitPValueSummary() {
		if(d_pValueSummary == null) {
			d_pValueSummary = new NodeSplitPValueSummary(d_nested.getResults(), getDirectEffect(), getIndirectEffect());
		}
		return d_pValueSummary;
	}
}
