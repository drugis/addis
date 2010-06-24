package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.wizard.BenefitRiskWizard;
import org.drugis.addis.presentation.wizard.BenefitRiskWizardPM;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardFrameCloser;

import com.jgoodies.binding.value.ValueModel;

public class BenefitRiskAnalysesKnowledge extends CategoryKnowledgeBase {
	public BenefitRiskAnalysesKnowledge() {
		super("Benefit-risk analysis", "Benefit-risk analyses", FileNames.ICON_BENEFITRISK);
	}
	

	@Override
	public JDialog getAddDialog(Main main, Domain domain,
			ValueModel selectionModel) {
		JDialog dialog = new JDialog(main, "Create Benefit-risk analysis", true);
		Wizard wizard = new BenefitRiskWizard(main,
				new BenefitRiskWizardPM(domain));
		dialog.getContentPane().add(wizard);
		dialog.pack();
		WizardFrameCloser.bind(wizard, dialog);
		return dialog;
	}
}
