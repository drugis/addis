package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.analysis.PairWiseMetaAnalysis;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.wizard.MetaAnalysisWizard;
import org.drugis.addis.presentation.wizard.MetaAnalysisWizardPresentation;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardFrameCloser;

import com.jgoodies.binding.value.ValueModel;

public class PairWiseMetaAnalysesKnowledge extends CategoryKnowledgeBase {
	public PairWiseMetaAnalysesKnowledge() {
		super("Pair-wise meta-analysis", "Pair-wise meta-analyses", null, PairWiseMetaAnalysis.class);
	}
	
	@Override
	public String getIconName() {
		return FileNames.ICON_METASTUDY;
	}
	
	@Override
	public char getMnemonic() {
		return 'm';
	}
	
	@Override
	public String getNewIconName() {
		return FileNames.ICON_METASTUDY_NEW;
	}
	
	public JDialog getAddDialog(Main main, Domain domain,
			ValueModel selectionModel) {
		JDialog dialog = new JDialog(main, "Create DerSimonian-Laird random effects meta-analysis", true);
		Wizard wizard = new MetaAnalysisWizard(main,
				new MetaAnalysisWizardPresentation(domain, main.getPresentationModelFactory()));
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
}
