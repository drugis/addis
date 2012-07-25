package org.drugis.addis.entities.treatment;

import static org.apache.commons.collections15.CollectionUtils.forAllDo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Predicate;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.addis.util.BoundedInterval;

import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.FixedObservableGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;

public class DoseDecisionTree extends DelegateTree<DecisionTreeNode, String> {

	private final class DosePredicate implements Predicate<DecisionTreeNode> {
		private AbstractDose d_dose;
		public DosePredicate(AbstractDose dose) {
			d_dose = dose;
		}

		public boolean evaluate(DecisionTreeNode object) {
			return object.decide(d_dose);
		}
	}

	private static final long serialVersionUID = 5924217742805415944L;	
	
	public static DoseDecisionTree createDefaultTree() {
		EmptyNode rootNode = new EmptyNode();
		DoseDecisionTree tree = new DoseDecisionTree(rootNode);
		createDefaultTypes(tree);
		return tree;
	}

	private static void createDefaultTypes(DoseDecisionTree tree) {
		DecisionTreeNode rootNode = tree.getRoot();
		TypeNode unknownDoseNode = new TypeNode(UnknownDose.class);
		TypeNode fixedDoseNode = new TypeNode(FixedDose.class);
		TypeNode flexibleDoseNode = new TypeNode(FlexibleDose.class);
		
		tree.addChild(rootNode, unknownDoseNode);
		tree.addChild(rootNode, fixedDoseNode);
		tree.addChild(rootNode, flexibleDoseNode);
		
		tree.addChild(unknownDoseNode, new ExcludeNode());
		tree.addChild(fixedDoseNode, new ExcludeNode());
		tree.addChild(flexibleDoseNode, new ExcludeNode());
	}
	
	public static class ObservableDirectedGraph<V, E> extends FixedObservableGraph<V, E> implements DirectedGraph<V, E> {
		private static final long serialVersionUID = 442135818546886998L;
		public ObservableDirectedGraph(Graph<V, E> delegate) {
			super(delegate);
		}
	}
	
	public DoseDecisionTree(DecisionTreeNode rootNode) {
		super(new ObservableDirectedGraph<DecisionTreeNode, String>(new DirectedSparseGraph<DecisionTreeNode, String>()));
		setRoot(rootNode);
	}
	
	public ObservableGraph<DecisionTreeNode, String> getObservableGraph() {
		return (ObservableGraph<DecisionTreeNode, String>) delegate;
	}
	
	public DecisionTreeNode getCategory(AbstractDose dose) {
		DecisionTreeNode node = searchNode(dose, getRoot());
		return (node != null) ? node : null;
	}

	/**
	 * Add a cut-off value. This splits an existing range in two.
	 * The lower range will always be initialized with the child node of the original range.
	 * @param parent The parent of the set of range nodes to split.
	 * @param value The cut-off value.
	 * @param includeInRightSide True if the value should be included in the range
	 */
	public List<RangeNode> splitChildRange(DecisionTreeNode parent, double value, boolean includeInRightSide) {
		RangeNode rangeToSplit = findByValue(parent, value);

		if (rangeToSplit != null) {
			List<RangeNode> ranges = splitOnValue(rangeToSplit, value, includeInRightSide);
			setChild(parent, ranges.get(0));
			setChild(parent, ranges.get(1));
			removeChild(rangeToSplit);
			return ranges;
		}
		return Collections.emptyList();
	}
	

	private static List<RangeNode> splitOnValue(RangeNode range, double value, boolean includeInRightSide) {
		BoundedInterval interval = range.getInterval();
		BoundedInterval left = new BoundedInterval(interval.getRange().getMinimumDouble(), interval.isLowerBoundOpen(), value, includeInRightSide);
		BoundedInterval right = new BoundedInterval(value, !includeInRightSide, interval.getRange().getMaximumDouble(), interval.isUpperBoundOpen());

		RangeNode leftNode 	= new RangeNode(range.getBeanClass(), range.getPropertyName(), left);
		RangeNode rightNode	= new RangeNode(range.getBeanClass(), range.getPropertyName(), right);
		
		return Arrays.asList(leftNode, rightNode);
	}
	
	/**
	 * Sets the child of a parent node in the tree. Removes all current children.
	 * @param parent
	 * @param child
	 */
	public void setChild(final DecisionTreeNode parent, final DecisionTreeNode child) {
		forAllDo(getChildren(parent), new Closure<DecisionTreeNode>() {
			public void execute(DecisionTreeNode child) {
				if(child instanceof LeafNode) {
					removeChild(child);
				}
			}
		});
		addChild(parent, child);
		if(isLeaf(child) && !(child instanceof LeafNode)) { 
			addChild(child, new ExcludeNode());
		}
	}
	
	private void addChild(DecisionTreeNode parent, DecisionTreeNode child) { 
		if(!containsVertex(parent)) {
			throw new IllegalArgumentException("Parent node " + parent + " does not exist, cannot set child " + child);
		}
		if(!containsVertex(child)) {
			addChild(Integer.toString(parent.hashCode() + 31 * child.hashCode()), parent, child);
		}
	}
	
	public RangeNode findByValue(DecisionTreeNode parent, double value) {
		for(DecisionTreeNode child : getChildren(parent)) {
			if(child instanceof RangeNode) {
				if(((RangeNode) child).getInterval().getRange().containsDouble(value)) return (RangeNode) child;
			}
		}
		throw new IllegalArgumentException("No range matches " + value + " in " + parent.getName() );
	}
	
	
	private DecisionTreeNode searchNode(AbstractDose dose, DecisionTreeNode parent) {
		return searchLeafNode(new DosePredicate(dose), parent);
	}
	
	/** 
	 * @param predicate a predicate functor which returns true if a node should match
	 * @param parent the start of the recursive search
	 * @return the leaf node for which all elements on the path return true for the predicate, null if no such node exists
	 */
	private DecisionTreeNode searchLeafNode(Predicate<DecisionTreeNode> predicate, DecisionTreeNode parent) {
		if (getChildCount(parent) == 0
				&& predicate.evaluate(parent)) {
			return parent;
		}
		for (DecisionTreeNode child : getChildren(parent)) {
			DecisionTreeNode match = null;
			if (predicate.evaluate(child)) {
				match = searchLeafNode(predicate, child);
				if (match != null) {
					return match;
				}
			}
		}
		return null;
	}
	
	public void resetToDefault() { 
		for(DecisionTreeNode child : new ArrayList<DecisionTreeNode>(getChildren(getRoot()))) {
			resetChildren(child);
		}
	}

	public void resetChildren(DecisionTreeNode node) {
		for(DecisionTreeNode child : new ArrayList<DecisionTreeNode>(getChildren(node))) {
			removeVertex(child);
		}
		addChild(node, new ExcludeNode());
	}
}
