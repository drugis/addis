package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.builder.BenefitRiskView;
import org.drugis.addis.gui.wizard.BenefitRiskWizard;
import org.drugis.addis.presentation.BenefitRiskPresentation;
import org.drugis.addis.presentation.wizard.BenefitRiskWizardPM;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardFrameCloser;

import com.jgoodies.binding.value.ValueModel;

public class BenefitRiskAnalysesKnowledge extends CategoryKnowledgeBase {
	public BenefitRiskAnalysesKnowledge() {
		super("benefit-risk analysis", "Benefit-risk analyses", FileNames.ICON_BENEFITRISK, BenefitRiskAnalysis.class);
	}
	
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
	
	@Override
	public boolean isToolbarCategory() {
		return true;
	}
	
	@Override
	protected String[] getShownProperties() {
		return new String[] { "name", "indication", "outcomeMeasures",
				"metaAnalyses", "baseline", "drugs" };
	}

	@Override
	public ViewBuilder getEntityViewBuilder(Main main, Domain domain,
			Entity entity) {
		BenefitRiskPresentation model = (BenefitRiskPresentation) main.getPresentationModelFactory().getModel((BenefitRiskAnalysis) entity);
		return new BenefitRiskView(model, main);
	}
	
	@Override
	public String getNewIconName() {
		return FileNames.ICON_BENEFITRISK_NEW;
	}
}
