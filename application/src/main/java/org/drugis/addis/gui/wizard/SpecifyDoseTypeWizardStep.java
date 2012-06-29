package org.drugis.addis.gui.wizard;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.knowledge.DosedDrugTreatmentKnowledge;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SpecifyDoseTypeWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;
	JPanel d_dialogPanel = new JPanel();
	public SpecifyDoseTypeWizardStep(DosedDrugTreatmentPresentation pm,
			Domain domain, AddisWindow mainWindow) {
		super(pm, domain, mainWindow, "Specify criteria","Select for the category or criteria for the fixed and flexible dose types.");
	}

	protected JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, pref",
				"p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		int colSpan = layout.getColumnCount();
		int row = 1 ;
		builder.addSeparator("Dose type", cc.xyw(1, row, colSpan));
		
		row = LayoutUtil.addRow(layout, row);
		
		builder.addLabel("Fixed dose", cc.xy(1, row));
		final JComboBox fixedCategoryComboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(d_pm.getCategories(), DosedDrugTreatmentKnowledge.CategorySpecifiers.FIXED_CONSIDER);
		fixedCategoryComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				d_pm.setChildNode(FixedDose.class, fixedCategoryComboBox.getSelectedItem());
			}
		});
		builder.add(fixedCategoryComboBox, cc.xy(3, row));
		
		row = LayoutUtil.addRow(layout, row);

		builder.addLabel("Flexible dose", cc.xy(1, row));
	
		final JComboBox flexibleCategoryComboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(d_pm.getCategories(), DosedDrugTreatmentKnowledge.CategorySpecifiers.FLEXIBLE_CONSIDER_LOWER, DosedDrugTreatmentKnowledge.CategorySpecifiers.FLEXIBLE_CONSIDER_UPPER);
		flexibleCategoryComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				d_pm.setChildNode(FlexibleDose.class, flexibleCategoryComboBox.getSelectedItem());
			}
		});
		builder.add(flexibleCategoryComboBox, cc.xy(3, row));
		
		return builder.getPanel();
	}
	
	@Override
	protected void initialize() {
		rebuildPanel();
	}
}
