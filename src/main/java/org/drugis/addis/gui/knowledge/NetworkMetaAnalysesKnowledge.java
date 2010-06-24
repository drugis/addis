package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.wizard.NetworkMetaAnalysisWizard;
import org.drugis.addis.presentation.wizard.NetworkMetaAnalysisWizardPM;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardFrameCloser;

import com.jgoodies.binding.value.ValueModel;

public class NetworkMetaAnalysesKnowledge extends CategoryKnowledgeBase {
	public NetworkMetaAnalysesKnowledge() {
		super("Network meta-analysis", "Network meta-analyses", null);
	}
	
	@Override
	public String getIconName() {
		return FileNames.ICON_NETWMETASTUDY;
	}
	
	@Override
	public String getNewIconName() {
		return FileNames.ICON_NETWMETASTUDY_NEW;
	}

	@Override
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
}
