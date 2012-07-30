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

package org.drugis.addis.gui.wizard;

import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.presentation.DecisionTreeOutEdgesModel;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.ValueModelWrapper;
import org.drugis.addis.presentation.wizard.DosedDrugTreatmentWizardPresentation;
import org.drugis.addis.presentation.wizard.DosedDrugTreatmentWizardPresentation.CategorySpecifiers;
import org.drugis.common.beans.FilteredObservableList;
import org.drugis.common.beans.FilteredObservableList.Filter;
import org.drugis.common.beans.TransformedObservableList;
import org.drugis.common.beans.TransformedObservableList.Transform;
import org.drugis.common.validation.ListMinimumSizeModel;

import com.jgoodies.binding.list.ObservableList;

public class RangeInputPresentation {
	private final DosedDrugTreatmentWizardPresentation d_pm;
	private final DecisionTreeNode d_parent;
	private final String d_nextPropertyName;
	private final DecisionTreeOutEdgesModel d_edges;
	private final ValueHolder<Boolean> d_considerNext;

	public RangeInputPresentation(
			final DosedDrugTreatmentWizardPresentation presentationModel,
			final DecisionTreeNode parent,
			final String nextPropertyName) {
		d_pm = presentationModel;
		d_parent = parent;
		d_nextPropertyName = nextPropertyName;

		d_edges = new DecisionTreeOutEdgesModel(d_pm.getBean().getDecisionTree(), d_parent);
		final TransformedObservableList<DecisionTreeEdge, DecisionTreeNode> selections =
			new TransformedObservableList<DecisionTreeEdge, DecisionTreeNode>(d_edges,
				new Transform<DecisionTreeEdge, DecisionTreeNode>() {
					@Override
					public DecisionTreeNode transform(final DecisionTreeEdge e) {
						final DecisionTree tree = d_pm.getBean().getDecisionTree();
						return tree.containsEdge(e) ? tree.getEdgeTarget(e) : null;
					}});


		final FilteredObservableList<DecisionTreeNode> choiceNodesSelected =
			new FilteredObservableList<DecisionTreeNode>(selections,
				new Filter<DecisionTreeNode>() {
					@Override
					public boolean accept(final DecisionTreeNode obj) {
						return obj != null && obj instanceof ChoiceNode;
					}});

		final ListMinimumSizeModel model = new ListMinimumSizeModel(choiceNodesSelected, 1);

		d_considerNext = new ValueModelWrapper<Boolean>(model);
	}

	public ChoiceNode getParent() {
		return (ChoiceNode)d_parent;
	}

	public ObservableList<DecisionTreeEdge> getRanges() {
		return d_edges;
	}

	public DecisionTreeNode[] getExtraOptions() {
		if (d_nextPropertyName != null) {
			return new ChoiceNode[] { new ChoiceNode(((ChoiceNode) d_parent).getBeanClass(), d_nextPropertyName) };
		}
		return new CategorySpecifiers[] {};
	}

	public DosedDrugTreatmentWizardPresentation getParentPresentation() {
		return d_pm;
	}

	public ValueHolder<Boolean> getConsiderNext() {
		return d_considerNext;
	}
	
	public boolean hasPrevious() { 
		return d_nextPropertyName != null;
	}
}