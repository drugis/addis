package org.drugis.addis.entities.treatment;

import java.util.Collections;
import java.util.Set;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.AbstractNamedEntity;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;


public class DosedDrugTreatment extends AbstractNamedEntity<DosedDrugTreatment> {
	public static final String EXCLUDE = "<EXCLUDE>";
	
	private Drug d_drug;
	private DecisionTreeNode d_rootNode;

	public DosedDrugTreatment() { 
		super("");
	}
	
	public DosedDrugTreatment(Drug drug, DecisionTreeNode rootNode) {
		super(drug.getName());
		d_drug = drug;
		setRootNode(rootNode);
	}

	public DosedDrugTreatment(Drug drug) {
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
	
	public DecisionTreeNode getCategoryNode(AbstractDose dose) { 
		DecisionTreeNode node = getRootNode();
		while (!node.isLeaf()) {
			node = node.decide(dose);
		}
		return node;
	}
	
	public String getCategoryName(AbstractDose dose) {
		DecisionTreeNode node = getCategoryNode(dose);
		if (node instanceof CategoryNode) {
			return ((CategoryNode)node).getName();
		} else if (node instanceof ExcludeNode) {
			return EXCLUDE;
		}
		return null;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.singleton(d_drug);
	}
}