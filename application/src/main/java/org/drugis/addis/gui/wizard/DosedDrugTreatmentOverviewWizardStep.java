package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
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

	private void buildWizardStep() {
		JPanel dialog = buildPanel();
		d_dialogPanel.setLayout(new BorderLayout());
		d_dialogPanel.setPreferredSize(new Dimension(PANEL_WIDTH, 500));
		d_dialogPanel.add(dialog);
		add(d_dialogPanel, BorderLayout.CENTER);			
	}

	private JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu",
				"p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		
		CellConstraints cc = new CellConstraints();
		int row = 1;
		int colSpan = layout.getColumnCount();
		
		builder.addLabel("Overview of" + " " + d_pm.getBean().getLabel());
		
		return builder.getPanel();
	}
}
