package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.builder.StudiesNodeView;
import org.drugis.addis.gui.builder.wizard.AddStudyWizard;
import org.drugis.addis.gui.components.StudiesTablePanel;
import org.drugis.addis.presentation.DefaultStudyListPresentationModel;
import org.drugis.addis.presentation.wizard.AddStudyWizardPresentation;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardFrameCloser;

import com.jgoodies.binding.value.ValueModel;

public class StudiesKnowledge extends CategoryKnowledgeBase {
	public StudiesKnowledge() {
		super("Study", "Studies", FileNames.ICON_STUDY, Study.class);
	}
	
	@Override
	public String getNewIconName() {
		return FileNames.ICON_STUDY_NEW;
	}
	
	@Override
	public JDialog getAddDialog(Main main, Domain domain,
			ValueModel selectionModel) { // TODO: AddStudyWizard should implement Wizard to simplify code below.
		JDialog dialog = new JDialog(main, "Add Study", true);
		AddStudyWizard wizardBuilder = new AddStudyWizard(
				new AddStudyWizardPresentation(domain,
						main.getPresentationModelFactory(), main), main, dialog);
		Wizard wizard = wizardBuilder.buildPanel();
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
	public ViewBuilder getCategoryViewBuilder(Main main, Domain domain) {
		DefaultStudyListPresentationModel studyListPM = new DefaultStudyListPresentationModel(
				domain.getStudiesHolder());
		StudiesNodeView view = new StudiesNodeView(new StudiesTablePanel(studyListPM, main));
		return view;
	}
}
