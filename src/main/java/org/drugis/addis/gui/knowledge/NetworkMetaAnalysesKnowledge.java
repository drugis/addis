package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.builder.NetworkMetaAnalysisView;
import org.drugis.addis.gui.wizard.NetworkMetaAnalysisWizard;
import org.drugis.addis.presentation.NetworkMetaAnalysisPresentation;
import org.drugis.addis.presentation.wizard.NetworkMetaAnalysisWizardPM;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardFrameCloser;

import com.jgoodies.binding.value.ValueModel;

public class NetworkMetaAnalysesKnowledge extends CategoryKnowledgeBase {
	public NetworkMetaAnalysesKnowledge() {
		super("Network meta-analysis", "Network meta-analyses", null, NetworkMetaAnalysis.class);
	}
	
	@Override
	public String getIconName() {
		return FileNames.ICON_NETWMETASTUDY;
	}
	
	@Override
	public String getNewIconName() {
		return FileNames.ICON_NETWMETASTUDY_NEW;
	}

	public JDialog getAddDialog(Main main, Domain domain,
			ValueModel selectionModel) {
		JDialog dialog = new JDialog(main, "Create Network meta-analysis", true);
		Wizard wizard = new NetworkMetaAnalysisWizard(main,
				new NetworkMetaAnalysisWizardPM(domain, main.getPresentationModelFactory()));
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
		return new String[] { "name", "type", "indication", "outcomeMeasure",
				"includedDrugs", "studiesIncluded", "sampleSize" };
	}

	@Override
	public ViewBuilder getEntityViewBuilder(Main main, Domain domain,
			Entity entity) {
		return new NetworkMetaAnalysisView(
				(NetworkMetaAnalysisPresentation) main.getPresentationModelFactory().getModel(((NetworkMetaAnalysis) entity)),
				main);
	}
}
