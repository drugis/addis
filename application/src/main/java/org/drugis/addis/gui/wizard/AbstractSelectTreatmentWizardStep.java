package org.drugis.addis.gui.wizard;

import javax.swing.BorderFactory;
import javax.swing.Icon;

import org.drugis.addis.gui.SelectableStudyGraph;
import org.drugis.addis.presentation.SelectableTreatmentDefinitionsGraphModel;
import org.drugis.addis.presentation.wizard.AbstractMetaAnalysisWizardPM;
import org.pietschy.wizard.PanelWizardStep;

public class AbstractSelectTreatmentWizardStep extends PanelWizardStep {

	private static final long serialVersionUID = 1746696286888281689L;
	protected SelectableStudyGraph d_studyGraph;
	protected AbstractMetaAnalysisWizardPM<SelectableTreatmentDefinitionsGraphModel> d_pm;

	public AbstractSelectTreatmentWizardStep(String name, String summary, SelectableTreatmentDefinitionsGraphModel selectableStudyGraphModel) {
		super(name, summary);
		d_studyGraph = buildStudiesGraph(selectableStudyGraphModel);
	}

	public AbstractSelectTreatmentWizardStep(String name, String summary, Icon icon, SelectableTreatmentDefinitionsGraphModel selectableStudyGraphModel) {
		super(name, summary, icon);
		d_studyGraph = buildStudiesGraph(selectableStudyGraphModel);
	}

	private SelectableStudyGraph buildStudiesGraph(SelectableTreatmentDefinitionsGraphModel selectableStudyGraphModel) {
		SelectableStudyGraph studyGraph = new SelectableStudyGraph(selectableStudyGraphModel);
		studyGraph.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		return studyGraph;
	}
}