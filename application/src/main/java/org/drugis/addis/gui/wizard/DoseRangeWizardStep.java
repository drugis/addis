package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class DoseRangeWizardStep extends PanelWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;
	private static final int PANEL_WIDTH = 600;
	private JPanel d_dialogPanel = new JPanel();
	private final Domain d_domain;
	private final AddisWindow d_mainWindow;
	private final DosedDrugTreatmentPresentation d_pm;

	public DoseRangeWizardStep(DosedDrugTreatmentPresentation pm,
			Domain domain, AddisWindow mainWindow) {
				d_pm = pm;
				d_domain = domain;
				d_mainWindow = mainWindow;
	}

	@Override
	public void prepare() {
		this.setVisible(false);		 
	 	buildWizardStep();
	 	setComplete(true);
	 	this.setVisible(true);
	 	repaint();
	}
	
	public void buildWizardStep() {
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
		
		builder.addLabel("Of flexible of fixed upper, of fixed lower afhankelijk van wat je hartje begeert (vorige scherm gekozen)");
		
		return builder.getPanel();
	}
}
