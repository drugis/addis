package org.drugis.addis.gui.builder.wizard;

import java.awt.Dimension;

import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.wizard.NetworkMetaAnalysisWizardPM;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.models.StaticModel;

public class NetworkMetaAnalysisWizard implements ViewBuilder {
	
	private NetworkMetaAnalysisWizardPM d_pm;
	private Main d_frame;

	public NetworkMetaAnalysisWizard(Main parent, NetworkMetaAnalysisWizardPM model) {
		this.d_pm = model;
		this.d_frame = parent;
	}

	public Wizard buildPanel() {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new SelectIndicationWizardStep(d_pm));
		wizardModel.add(new SelectEndpointWizardStep(d_pm));
		
		Wizard wizard = new Wizard(wizardModel);		
		wizard.setDefaultExitMode(Wizard.EXIT_ON_FINISH);
		wizard.setPreferredSize(new Dimension(950, 650));
		return wizard;
	}
}
