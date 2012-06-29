package org.drugis.addis.gui.wizard;


import javax.swing.JPanel;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DosedDrugTreatmentOverviewWizardStep extends AbstractDoseTreatmentWizardStep {

	private static final long serialVersionUID = -3991691781012756118L;
	private DecisionTreeNode d_rootNode;

	public DosedDrugTreatmentOverviewWizardStep(DosedDrugTreatmentPresentation pm, Domain domain, AddisWindow mainWindow) {
		super(pm, domain, mainWindow, "Overview","Overview of created treatment.");
		d_rootNode = (DecisionTreeNode) d_pm.getModel(DosedDrugTreatment.PROPERTY_ROOT_NODE).getValue();
	}
	
	@Override
	protected void initialize() { 
		rebuildPanel();
		this.setComplete(true);
	}
	
	protected JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, pref",
				"p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		
		CellConstraints cc = new CellConstraints();
		int row = 1;
		
		builder.addLabel("Overview of" + " " + d_pm.getBean().getLabel(), cc.xy(1, row));

		buildOverview(d_rootNode, 0);
		
		return builder.getPanel();
	}

	private void buildOverview(DecisionTreeNode node, int level) {
		System.out.println("level " + level + " " + node.getName());

		for(int i = 0; i < node.getChildCount(); ++i) {
			DecisionTreeNode child = node.getChildNode(i);
			System.out.println(child.getName());
			if(child.isLeaf()) {
//				System.out.println("leaf " + child.getName());
			} else { 
//				System.out.println(node.getChildLabel(i));
				buildOverview(child, level++);

			}

		}
	}
}
