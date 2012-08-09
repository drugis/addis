package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.presentation.SelectableStudyGraphModel;
import org.drugis.addis.presentation.wizard.AbstractMetaAnalysisWizardPM;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RefineDrugSelectionWizardStep extends PanelWizardStep {
	private static final long serialVersionUID = -585100940524715529L;
	private AbstractMetaAnalysisWizardPM<SelectableStudyGraphModel> d_pm;
	private AddisWindow d_main;

	public RefineDrugSelectionWizardStep(AbstractMetaAnalysisWizardPM<SelectableStudyGraphModel> pm, AddisWindow main) { 
		super("Refine Drugs","Optionally select Treatment Categorizations to use for the selected drugs");
		d_pm = pm;
		d_main = main;
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
		List<TreatmentCategorization> categorizations = d_main.getDomain().getCategorizations(drug);
		categorizations.add(0, TreatmentCategorization.createTrivial(drug));
		
		JComboBox categorizationSelect = new JComboBox(categorizations.toArray());
		categorizationSelect.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) { 
					ValueModel categorizationModel = d_pm.getCategorizationModel(drug);
					categorizationModel.setValue(e.getItem());
				}
			}
		});
		categorizationSelect.setPreferredSize(new Dimension(220, categorizationSelect.getPreferredSize().height));
		categorizationSelect.setSelectedItem(d_pm.getCategorizationModel(drug).getValue());
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