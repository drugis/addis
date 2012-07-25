package org.drugis.addis.entities.treatment;

public class LeafNode extends DecisionTreeNode {
	public static final String NAME_EXCLUDE = "* Exclude";
	private final Category d_category;

	public LeafNode(final Category category) {
		d_category = category;
	}

	public LeafNode() {
		this(null);
	}

	@Override
	public String getName() {
		return d_category == null ? NAME_EXCLUDE : d_category.getName();
	}

	public Category getCategory() {
		return d_category;
	}
}
