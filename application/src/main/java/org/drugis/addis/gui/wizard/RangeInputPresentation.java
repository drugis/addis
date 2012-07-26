package org.drugis.addis.gui.wizard;

import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.gui.knowledge.DosedDrugTreatmentKnowledge.CategorySpecifiers;
import org.drugis.addis.presentation.DecisionTreeOutEdgesModel;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.ValueModelWrapper;
import org.drugis.common.beans.FilteredObservableList;
import org.drugis.common.beans.FilteredObservableList.Filter;
import org.drugis.common.beans.TransformedObservableList;
import org.drugis.common.beans.TransformedObservableList.Transform;
import org.drugis.common.validation.ListMinimumSizeModel;

import com.jgoodies.binding.list.ObservableList;

public class RangeInputPresentation {
	private final DosedDrugTreatmentPresentation d_pm;
	private final ChoiceNode d_parent;
	private final String d_nextPropertyName;
	private final DecisionTreeOutEdgesModel d_edges;
	private final ValueHolder<Boolean> d_considerNext;

	public RangeInputPresentation(
			final DosedDrugTreatmentPresentation presentationModel,
			final ChoiceNode parent,
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
		return d_parent;
	}

	public ObservableList<DecisionTreeEdge> getRanges() {
		return d_edges;
	}

	public DecisionTreeNode[] getExtraOptions() {
		if (d_nextPropertyName != null) {
			return new ChoiceNode[] { new ChoiceNode(d_parent.getBeanClass(), d_nextPropertyName) };
		}
		return new CategorySpecifiers[] {};
	}

	public DosedDrugTreatmentPresentation getParentPresentation() {
		return d_pm;
	}

	public ValueHolder<Boolean> getConsiderNext() {
		return d_considerNext;
	}
}