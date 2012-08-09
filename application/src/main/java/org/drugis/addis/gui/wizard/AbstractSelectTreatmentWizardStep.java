package org.drugis.addis.gui.wizard;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;

import org.drugis.addis.gui.SelectableStudyGraph;
import org.drugis.addis.presentation.SelectableStudyGraphModel;
import org.drugis.addis.presentation.wizard.AbstractMetaAnalysisWizardPM;
import org.pietschy.wizard.PanelWizardStep;

public class AbstractSelectTreatmentWizardStep extends PanelWizardStep {

	private static final long serialVersionUID = 1746696286888281689L;
	protected SelectableStudyGraph d_studyGraph;
	protected AbstractMetaAnalysisWizardPM<SelectableStudyGraphModel> d_pm;

	public AbstractSelectTreatmentWizardStep() {
		super();
	}

	public AbstractSelectTreatmentWizardStep(String name, String summary) {
		super(name, summary);
	}

	public AbstractSelectTreatmentWizardStep(String name, String summary, Icon icon) {
		super(name, summary, icon);
	}

	protected Component buildStudiesGraph(SelectableStudyGraphModel selectableStudyGraphModel) {
		d_studyGraph = new SelectableStudyGraph(selectableStudyGraphModel);
		d_studyGraph.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		return d_studyGraph;
	}
}