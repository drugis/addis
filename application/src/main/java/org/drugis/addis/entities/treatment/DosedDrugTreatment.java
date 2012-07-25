package org.drugis.addis.entities.treatment;

import java.util.Collections;
import java.util.Set;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.AbstractNamedEntity;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

import edu.uci.ics.jung.graph.util.Pair;

public class DosedDrugTreatment extends AbstractNamedEntity<DosedDrugTreatment> {
	public static final String PROPERTY_DOSE_UNIT = "doseUnit";
	public static final String PROPERTY_DRUG = "drug";
	public static final String PROPERTY_CATEGORIES = "categories";

	private final ObservableList<Category> d_categories = new ArrayListModel<Category>();
	private Drug d_drug;
	private final DecisionTree d_decisionTree;

	private DoseUnit d_doseUnit;

	public DosedDrugTreatment() {
		this("", null, ExampleData.MILLIGRAMS_A_DAY);
	}

	public DosedDrugTreatment(final String name, final Drug drug, final DoseUnit unit) {
		super(name);
		d_drug = drug;
		d_doseUnit = unit;
		d_decisionTree = createDefaultTree();
	}

	private static DecisionTree createDefaultTree() {
		final ChoiceNode root = new ChoiceNode(AbstractDose.class, "class");
		final DecisionTree tree = new DecisionTree(root);
		addDefaultEdges(tree);
		return tree;
	}

	private static void addDefaultEdges(final DecisionTree tree) {
		final DecisionTreeNode root = tree.getRoot();
		tree.addEdge(new TypeEdge(UnknownDose.class), root, new LeafNode());
		tree.addEdge(new TypeEdge(FixedDose.class), root, new LeafNode());
		tree.addEdge(new TypeEdge(FlexibleDose.class), root, new LeafNode());
	}

	public void setName(final String name) {
		final String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}

	public Drug getDrug() {
		return d_drug;
	}

	public void setDrug(final Drug drug) {
		final Drug oldVal = d_drug;
		d_drug = drug;
		firePropertyChange(PROPERTY_DRUG, oldVal, drug);
	}

	public void setDoseUnit(final DoseUnit unit) {
		final DoseUnit oldVal = d_doseUnit;
		d_doseUnit = unit;
		firePropertyChange(PROPERTY_DOSE_UNIT, oldVal, unit);
	}

	public DoseUnit getDoseUnit() {
		return d_doseUnit;
	}


	public void addCategory(final Category categoryNode) {
		d_categories.add(categoryNode);
	}

	public ObservableList<Category> getCategories() {
		return d_categories;
	}

	public DecisionTreeNode getCategory(final AbstractDose dose) {
		return d_decisionTree.getCategory(dose);
	}

	@Override
	public String getLabel() {
		return (getDrug() == null ? "" : getDrug().getLabel()) + " " +  getName();
	}

	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.singleton(d_drug);
	}

	public DecisionTree getDecisionTree() {
		return d_decisionTree;
	}

	public DecisionTreeNode getRootNode() {
		return d_decisionTree.getRoot();
	}

	public void resetTree() {
		final DecisionTreeNode root = d_decisionTree.getRoot();
		for (final DecisionTreeNode child : d_decisionTree.getChildren(root)) {
			d_decisionTree.removeChild(child);
		}
		addDefaultEdges(d_decisionTree);
	}


	/**
	 * Add a cut-off value. This splits an existing range in two.
	 * The lower range will always be initialized with the child node of the original range, the higher range will be excluded by default.
	 * @param parent The parent of the set of range nodes to split.
	 * @param value The cut-off value.
	 * @param lowerRangeOpen True if the value should be included in the range
	 */
	public Pair<RangeEdge> splitRange(final ChoiceNode parent, final double value, final boolean lowerRangeOpen) {
		final RangeEdge edge = (RangeEdge) d_decisionTree.findMatchingEdge(parent, value);
		final DecisionTreeNode child = d_decisionTree.getEdgeTarget(edge);

		final Pair<RangeEdge> ranges = splitOnValue(edge, value, lowerRangeOpen);
		d_decisionTree.removeChild(child);
		d_decisionTree.addChild(ranges.getFirst(), parent, child);
		d_decisionTree.addChild(ranges.getSecond(), parent, new LeafNode());
		return ranges;
	}

	private static Pair<RangeEdge> splitOnValue(final RangeEdge range, final double value, final boolean isLowerRangeOpen) {
		final RangeEdge left = new RangeEdge(range.getLowerBound(), range.isLowerBoundOpen(), value, isLowerRangeOpen);
		final RangeEdge right = new RangeEdge(value, !isLowerRangeOpen, range.getUpperBound(), range.isUpperBoundOpen());
		return new Pair<RangeEdge>(left, right);
	}
}