package org.drugis.addis.gui.wizard;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SpecifyDoseTypeWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;

	public SpecifyDoseTypeWizardStep(final DosedDrugTreatmentPresentation pm) {
		super(pm, "Specify criteria","Select for the category or criteria for the fixed and flexible dose types.", null);
	}

	@Override
	protected void initialize() {
		rebuildPanel();
	}

	@Override
	protected JPanel buildPanel() {
		final FormLayout layout = new FormLayout(
				"left:pref, 3dlu, pref",
				"p"
				);

		final PanelBuilder builder = new PanelBuilder(layout);
		final CellConstraints cc = new CellConstraints();

		final int colSpan = layout.getColumnCount();
		int row = 1 ;
		builder.addSeparator("Dose type", cc.xyw(1, row, colSpan));

		row = LayoutUtil.addRow(layout, row);

		builder.addLabel("Fixed dose", cc.xy(1, row));
		final JComboBox fixedCategoryComboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(
				d_pm.getModelForFixedDose(),
				d_pm.getCategories(),
				d_pm.getFixedRangeNode());
		builder.add(fixedCategoryComboBox, cc.xy(3, row));

		row = LayoutUtil.addRow(layout, row);

		builder.addLabel("Flexible dose", cc.xy(1, row));
		final JComboBox flexibleCategoryComboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(
				d_pm.getModelForFlexibleDose(),
				d_pm.getCategories(),
				d_pm.getFlexibleLowerRangeNode(),
				d_pm.getFlexibleUpperRangeNode());
		builder.add(flexibleCategoryComboBox, cc.xy(3, row));

		return builder.getPanel();
	}
}
