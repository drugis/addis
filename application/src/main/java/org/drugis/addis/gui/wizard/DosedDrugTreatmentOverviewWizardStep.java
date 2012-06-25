package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.TypeNode;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DosedDrugTreatmentOverviewWizardStep extends PanelWizardStep {

	private static final long serialVersionUID = -3991691781012756118L;
	private static final int PANEL_WIDTH = 600;
	private final DosedDrugTreatmentPresentation d_pm;
	private JPanel d_dialogPanel = new JPanel();

	public DosedDrugTreatmentOverviewWizardStep(DosedDrugTreatmentPresentation pm, Domain domain, AddisWindow mainWindow) {
		super("Overview","Overview of created treatment.");
		d_pm = pm;
		setComplete(true);
	}
	
	@Override
	public void prepare() { 
		this.setVisible(false);		 
	 	buildWizardStep();
	 	setComplete(true);
	 	this.setVisible(true);
	 	repaint();
	}
	
	private void rebuildPanel() {
		d_dialogPanel.setVisible(false);
		d_dialogPanel.removeAll();
		d_dialogPanel.add(buildPanel());
		d_dialogPanel.setVisible(true);
	}
	
	private void buildWizardStep() {
		JPanel dialog = buildPanel();
		rebuildPanel(); // always rebuild the panel to ensure up-to date information
		d_dialogPanel.setLayout(new BorderLayout());
		d_dialogPanel.setPreferredSize(new Dimension(PANEL_WIDTH, 500));
		d_dialogPanel.add(dialog);
		add(d_dialogPanel, BorderLayout.CENTER);			
	}

	private JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, pref",
				"p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		
		CellConstraints cc = new CellConstraints();
		int row = 1;
		
		builder.addLabel("Overview of" + " " + d_pm.getBean().getLabel(), cc.xy(1, row));
	
		TypeNode types = (TypeNode)d_pm.getBean().getRootNode();
		for(Class<? extends AbstractDose> type : types.getTypeMap().keySet()) {
			row = LayoutUtil.addRow(layout, row);
			builder.addLabel(GUIHelper.humanize(type.getSimpleName()), cc.xy(1, row));
			DecisionTreeNode typeNode = types.getTypeMap().get(type);
			builder.addLabel((typeNode != null) ? typeNode.toString() : "", cc.xy(3, row));		
		}
		
		return builder.getPanel();
	}
}
