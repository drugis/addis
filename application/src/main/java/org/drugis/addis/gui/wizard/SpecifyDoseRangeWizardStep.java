package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.wizard.AddDosedDrugTreatmentWizardStep.CategorySpecifiers;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SpecifyDoseRangeWizardStep extends PanelWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;
	private static final int PANEL_WIDTH = 600;
	private JPanel d_dialogPanel = new JPanel();
	private final Domain d_domain;
	private final AddisWindow d_mainWindow;
	private final DosedDrugTreatmentPresentation d_pm;

	public SpecifyDoseRangeWizardStep(DosedDrugTreatmentPresentation pm,
			Domain domain, AddisWindow mainWindow) {
		super("Specify criteria","Select for the category or criteria for the fixed and flexible dose types.");
		d_pm = pm;
		d_domain = domain;
		d_mainWindow = mainWindow;
	}

	@Override
	public void prepare() {
		this.setVisible(false);		 
	 	buildWizardStep();
	 	setComplete(true);
	 	this.setVisible(true);
	 	repaint();
	}
	
	public void buildWizardStep() {
		JPanel dialog = buildPanel();
		d_dialogPanel.setLayout(new BorderLayout());
		d_dialogPanel.setPreferredSize(new Dimension(PANEL_WIDTH, 500));
		d_dialogPanel.add(dialog);
		add(d_dialogPanel, BorderLayout.CENTER);	
	}

	private JPanel buildPanel() {
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
		JComboBox fixedCategoryComboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(d_pm.getCategories(), CategorySpecifiers.FIXED_CONSIDER);
		builder.add(fixedCategoryComboBox, cc.xy(3, row));
		
		row = LayoutUtil.addRow(layout, row);

		builder.addLabel("Flexible dose", cc.xy(1, row));
		JComboBox flexibleCategoryComboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(d_pm.getCategories(), CategorySpecifiers.FLEXIBLE_CONSIDER_LOWER, CategorySpecifiers.FLEXIBLE_CONSIDER_UPPER);
		builder.add(flexibleCategoryComboBox, cc.xy(3, row));
		
		return builder.getPanel();
	}
}
