package org.drugis.addis.entities.treatment;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.Drug;


public class DoseRestrictedTreatment {
	public static final String EXCLUDE = "<EXCLUDE>";
	
	private final Drug d_drug;
	private DecisionTreeNode d_rootNode;

	public DoseRestrictedTreatment(Drug drug, DecisionTreeNode rootNode) {
		d_drug = drug;
		setRootNode(rootNode);
	}

	public DoseRestrictedTreatment(Drug drug) {
		this(drug, new ExcludeNode());
	}

	public Drug getDrug() {
		return d_drug;
	}
	
	public DecisionTreeNode getRootNode() {
		return d_rootNode;
	}

	public void setRootNode(DecisionTreeNode rootNode) {
		d_rootNode = rootNode;
	}
	
	public String getCategory(AbstractDose dose) {
		DecisionTreeNode node = getRootNode();
		while (!node.isLeaf()) {
			node = node.decide(dose);
		}
		
		if (node instanceof CategoryNode) {
			return ((CategoryNode)node).getName();
		} else if (node instanceof ExcludeNode) {
			return EXCLUDE;
		}
		
		return null;
	}
}