/**
 * 
 */
package org.drugis.addis.gui.builder.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.StudyTable;
import org.drugis.addis.presentation.SelectableStudyCharTableModel;
import org.drugis.addis.presentation.wizard.AbstractMetaAnalysisWizardPM;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class SelectStudiesWizardStep extends PanelWizardStep {

	public SelectStudiesWizardStep(AbstractMetaAnalysisWizardPM<?> pm, Main frame) {
		super("Select Studies","Select the studies to be used for meta analysis. At least one study must be selected to continue.");

		setLayout(new BorderLayout());
		JComponent studiesComp;			

		StudyTable table = new StudyTable(new SelectableStudyCharTableModel(pm.getStudyListModel(), frame.getPresentationModelFactory()));

		JScrollPane sPane = new JScrollPane(table);
		sPane.getVerticalScrollBar().setUnitIncrement(16);			
		sPane.setPreferredSize(new Dimension(700,300));

		studiesComp = sPane;

		FormLayout layout = new FormLayout(
				"center:pref:grow",
				"p, 3dlu, p"
		);	

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();

		builder.add(BasicComponentFactory.createLabel(pm.getStudiesMeasuringLabelModel()),
				cc.xy(1, 1));
		builder.add(studiesComp, cc.xy(1, 3));
		JScrollPane sp = new JScrollPane(builder.getPanel());
		sp.getVerticalScrollBar().setUnitIncrement(16);
		add(sp);
	}
}