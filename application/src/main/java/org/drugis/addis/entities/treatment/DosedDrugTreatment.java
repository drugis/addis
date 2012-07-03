package org.drugis.addis.entities.treatment;

import java.util.Collections;
import java.util.Set;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.AbstractNamedEntity;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.ScaleModifier;
import org.drugis.addis.util.EntityUtil;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

import edu.uci.ics.jung.graph.DelegateTree;


public class DosedDrugTreatment extends AbstractNamedEntity<DosedDrugTreatment> {
	public static final String PROPERTY_DOSE_UNIT = "doseUnit";
	public static final String PROPERTY_DRUG = "drug";
	public static final String PROPERTY_CATEGORIES = "categories";
	public static final String PROPERTY_ROOT_NODE = "rootNode";
	
	private final ObservableList<CategoryNode> d_categories = new ArrayListModel<CategoryNode>();
	private Drug d_drug;
	private DelegateTree<DecisionTreeNode, String> d_decisionTree = new DelegateTree<DecisionTreeNode, String>(); 
	
	private DoseUnit d_doseUnit;
	
	public DosedDrugTreatment() { 
		this("", null);
	}
	
	public DosedDrugTreatment(String name, Drug drug) {
		this(name, drug, new DoseUnit(Domain.GRAM, ScaleModifier.MILLI, EntityUtil.createDuration("P1D")), new ExcludeNode());
	}
	
	public DosedDrugTreatment(String name, Drug drug, DoseUnit unit, DecisionTreeNode rootNode) {
		super(name);
		d_drug = drug;
		d_doseUnit = unit;
		d_decisionTree.setRoot(rootNode);
	}

	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}

	public Drug getDrug() {
		return d_drug;
	}
	
	public void setDrug(Drug drug) { 
		Drug oldVal = d_drug;
		d_drug = drug;
		firePropertyChange(PROPERTY_DRUG, oldVal, drug);
	}

	public void setDoseUnit(DoseUnit unit) {
		DoseUnit oldVal = d_doseUnit;
		d_doseUnit = unit;
		firePropertyChange(PROPERTY_DOSE_UNIT, oldVal, unit);
	}
	
	public DoseUnit getDoseUnit() {
		return d_doseUnit;
	}
	
	public DecisionTreeNode getRootNode() {
		return d_decisionTree.getRoot();
	}

	public void setRootNode(DecisionTreeNode rootNode) {
		DecisionTreeNode oldVal = d_decisionTree.getRoot();
		d_decisionTree.setRoot(rootNode);
		firePropertyChange(PROPERTY_ROOT_NODE, oldVal, rootNode);
	}
	
	public DecisionTreeNode getCategoryNode(AbstractDose dose) { 
		DecisionTreeNode node = getRootNode();
		while (!node.isLeaf()) {
			node = node.decide(dose);
		}
		return node;
	}

	public void addCategory(CategoryNode categoryNode) {
		d_categories.add(categoryNode);
	}
	
	public ObservableList<CategoryNode> getCategories() {
		return d_categories;
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

}