package org.drugis.addis.gui.wizard;


import javax.swing.JPanel;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseDecisionTree;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class DosedDrugTreatmentOverviewWizardStep extends AbstractDoseTreatmentWizardStep {

	private static final long serialVersionUID = -3991691781012756118L;

	public DosedDrugTreatmentOverviewWizardStep(DosedDrugTreatmentPresentation pm, Domain domain, AddisWindow mainWindow) {
		super(pm, domain, mainWindow, "Overview","Overview of created treatment.");
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
		row = LayoutUtil.addRow(layout, row);
		JPanel overview = buildOverview(d_pm.getBean().getDecisionTree());
		builder.add(overview, cc.xyw(1, row, 3));
		return builder.getPanel();
	}

	private static JPanel buildOverview(DoseDecisionTree tree) {      
		Layout<DecisionTreeNode, String> layout = new TreeLayout<DecisionTreeNode, String>(tree);
		BasicVisualizationServer<DecisionTreeNode, String> vv = new BasicVisualizationServer<DecisionTreeNode, String>(layout);
		return vv;
	}
}
