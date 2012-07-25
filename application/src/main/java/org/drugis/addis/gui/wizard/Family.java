package org.drugis.addis.gui.wizard;

import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;

import com.jgoodies.binding.list.ObservableList;

public class Family { 
	public final DecisionTreeNode parent;
	private final ObservableList<DecisionTreeNode> children;
	
	public Family(DosedDrugTreatmentPresentation pm, DecisionTreeNode parent) {
		this.parent = parent;
		this.children = pm.getChildNodes(parent);
	}
	
	/**
	 * Families equal on their parent.
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Family) { 
			Family other = (Family) obj;
			return parent.similar(other.parent);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getChildren().hashCode() + 31 * parent.hashCode();
	}
	
	@Override
	public String toString() {
		String s = "";
		s = s + parent.toString() + "\n";
		for(DecisionTreeNode c : getChildren()) { 
			s = s + "\t\t child: " +  c + "\n";
		}
		return s;
	}

	public ObservableList<DecisionTreeNode> getChildren() {
		return children;
	}
}