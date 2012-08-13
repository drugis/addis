package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.presentation.wizard.AbstractMetaAnalysisWizardPM;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RefineDrugSelectionWizardStep extends PanelWizardStep {
	private static final long serialVersionUID = -585100940524715529L;
	private AbstractMetaAnalysisWizardPM d_pm;

	public RefineDrugSelectionWizardStep(AbstractMetaAnalysisWizardPM pm) { 
		super("Refine Drugs","Optionally select Treatment Categorizations to use for the selected drugs");
		d_pm = pm;
	}
	
	private void buildPanel() {
		setLayout(new BorderLayout());
		FormLayout layout = new FormLayout(
				"pref, 3dlu, right:pref:grow",				
				"p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int rows = 1;
		for (final Drug drug : d_pm.getSelectedDrugs()) { 
			rows = LayoutUtil.addRow(layout, rows);
			builder.add(new JLabel(drug.getLabel()), cc.xy(1, rows));
			

			JComboBox categorizationSelect = createCategorizationSelect(drug);
			builder.add(categorizationSelect, cc.xy(3, rows));
		}
		add(builder.getPanel());
		setComplete(true);
	}

	private JComboBox createCategorizationSelect(final Drug drug) {
		SelectionInList<TreatmentCategorization> selectionInList = new SelectionInList<TreatmentCategorization>(d_pm.getAvailableCategorizations(drug), d_pm.getCategorizationModel(drug));
		JComboBox categorizationSelect = BasicComponentFactory.createComboBox(selectionInList);
		categorizationSelect.setPreferredSize(new Dimension(220, categorizationSelect.getPreferredSize().height));
		return categorizationSelect;
	}

	public void prepare() { 
		rebuildPanel();
	}

	private void rebuildPanel() {
		setVisible(false);
		removeAll();
		buildPanel();
		setVisible(true);
	}
}