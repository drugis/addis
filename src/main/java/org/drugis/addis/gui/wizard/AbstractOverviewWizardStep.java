package org.drugis.addis.gui.wizard;

import javax.swing.JOptionPane;

import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.wizard.AbstractMetaAnalysisWizardPM;
import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;

@SuppressWarnings("serial")
public abstract class AbstractOverviewWizardStep<G extends StudyGraphModel> extends PanelWizardStep {
	protected final AbstractMetaAnalysisWizardPM<G> d_pm;
	protected final Main d_main;

	public AbstractOverviewWizardStep(AbstractMetaAnalysisWizardPM<G> pm, Main main) {
		super("Overview","Overview of selected Meta-analysis.");
		d_pm = pm;
		d_main = main;
	}


	public void applyState()
	throws InvalidStateException {
		saveAsAnalysis();
	}

	private void saveAsAnalysis() throws InvalidStateException {
		String res = JOptionPane.showInputDialog(this.getTopLevelAncestor(),
				"Input name for new analysis", 
				"Save meta-analysis", JOptionPane.QUESTION_MESSAGE);
		if (res != null) {
			try {
				d_main.leftTreeFocus(d_pm.saveMetaAnalysis(res));
			} catch (EntityIdExistsException e) {
				JOptionPane.showMessageDialog(this.getTopLevelAncestor(), 
						"There already exists a meta-analysis with the given name, input another name",
						"Unable to save meta-analysis", JOptionPane.ERROR_MESSAGE);
				saveAsAnalysis();
			}
		} else {
			throw new InvalidStateException();
		}
	}
}
