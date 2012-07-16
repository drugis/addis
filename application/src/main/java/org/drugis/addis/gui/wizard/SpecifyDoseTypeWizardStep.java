package org.drugis.addis.gui.wizard;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.treatment.TypeNode;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.knowledge.DosedDrugTreatmentKnowledge;
import org.drugis.addis.gui.knowledge.DosedDrugTreatmentKnowledge.CategorySpecifiers;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.EqualsUtil;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SpecifyDoseTypeWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;
	private final TypeNode d_fixedDoseNode = new TypeNode(FixedDose.class);
	private final TypeNode d_flexibleDoseNode = new TypeNode(FlexibleDose.class);

	JPanel d_dialogPanel = new JPanel();
	private JComboBox d_flexibleCategoryComboBox;
	private JComboBox d_fixedCategoryComboBox;
	private ValueHolder<Boolean> d_considerFlexibleLower = new ModifiableHolder<Boolean>();
	private ValueHolder<Boolean> d_considerFlexibleUpper = new ModifiableHolder<Boolean>();
	private ValueHolder<Boolean> d_considerFlexibleBoth = new ModifiableHolder<Boolean>();
	private ValueHolder<Boolean> d_considerFixed = new ModifiableHolder<Boolean>();

	public SpecifyDoseTypeWizardStep(DosedDrugTreatmentPresentation pm,
			Domain domain, AddisWindow mainWindow) {
		super(pm, domain, mainWindow, "Specify criteria","Select for the category or criteria for the fixed and flexible dose types.");
	}
	
	@Override
	protected void initialize() {
//		rebuildPanel();
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
		d_fixedCategoryComboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(
				d_pm.getCategories(), 
				DosedDrugTreatmentKnowledge.CategorySpecifiers.FIXED_CONSIDER);
		d_fixedCategoryComboBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) { 
					d_pm.setSelected(d_fixedDoseNode, d_fixedCategoryComboBox.getSelectedItem());
					setConsiderFixed();
				}
			}
		});
		builder.add(d_fixedCategoryComboBox, cc.xy(3, row));
		
		row = LayoutUtil.addRow(layout, row);

		builder.addLabel("Flexible dose", cc.xy(1, row));
	
		d_flexibleCategoryComboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(
				d_pm.getCategories(),
				DosedDrugTreatmentKnowledge.CategorySpecifiers.FLEXIBLE_CONSIDER_BOTH,
				DosedDrugTreatmentKnowledge.CategorySpecifiers.FLEXIBLE_CONSIDER_LOWER,
				DosedDrugTreatmentKnowledge.CategorySpecifiers.FLEXIBLE_CONSIDER_UPPER);
		d_flexibleCategoryComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					d_pm.setSelected(d_flexibleDoseNode, d_flexibleCategoryComboBox.getSelectedItem());
					setConsiderFlexible();
				}
			}
		});

		builder.add(d_flexibleCategoryComboBox, cc.xy(3, row));
		
		return builder.getPanel();
	}	
	
	public void setConsiderFlexible() { 
		setCategorySelection(
				d_flexibleCategoryComboBox.getSelectedItem(),
				d_considerFlexibleLower, 
				CategorySpecifiers.FLEXIBLE_CONSIDER_LOWER.getName());
		setCategorySelection(
				d_flexibleCategoryComboBox.getSelectedItem(),
				d_considerFlexibleUpper, 
				CategorySpecifiers.FLEXIBLE_CONSIDER_UPPER.getName());
		setCategorySelection(
				d_flexibleCategoryComboBox.getSelectedItem(),
				d_considerFlexibleBoth, 
				CategorySpecifiers.FLEXIBLE_CONSIDER_BOTH.getName());
	}
	
	public ValueHolder<Boolean> getConsiderFlexibleBoth() { 
		return d_considerFlexibleBoth;
	}
	
	public ValueHolder<Boolean> getConsiderFlexibleUpper() { 
		return d_considerFlexibleUpper;
	}
	
	public ValueHolder<Boolean> getConsiderFlexibleLower() { 
		return d_considerFlexibleLower;
	}
	
	public void setConsiderFixed() { 
		setCategorySelection(
				d_fixedCategoryComboBox.getSelectedItem(),
				d_considerFixed, 
				CategorySpecifiers.FIXED_CONSIDER.getName());
	}
	
	public ValueHolder<Boolean> getConsiderFixed() { 
		return d_considerFixed;
	}
	
	private void setCategorySelection(Object selection, ValueHolder<Boolean> holder, String desired) {
		if(EqualsUtil.equal(desired, selection.toString())) {
			holder.setValue(true);
		} else {
			holder.setValue(false);
		}
	}
}
