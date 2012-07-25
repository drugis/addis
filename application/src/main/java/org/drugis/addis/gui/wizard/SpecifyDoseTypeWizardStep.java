package org.drugis.addis.gui.wizard;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.ValueModelWrapper;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.binding.value.AbstractConverter;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SpecifyDoseTypeWizardStep extends AbstractDoseTreatmentWizardStep {
	private final class ChildIsChoiceNodeModel extends AbstractConverter {
		private static final long serialVersionUID = -6921782548274093924L;
		private final Class<?> d_beanClass;
		private final String d_propertyName;

		private ChildIsChoiceNodeModel(final ValueModel subject, final Class<?> beanClass, final String propertyName) {
			super(subject);
			d_beanClass = beanClass;
			d_propertyName = propertyName;
		}

		@Override
		public void setValue(final Object newValue) {
		}

		@Override
		public Object convertFromSubject(final Object subjectValue) {
			if (subjectValue instanceof ChoiceNode) {
				final ChoiceNode choice = (ChoiceNode) subjectValue;
				return choice.getBeanClass().equals(d_beanClass) &&
						choice.getPropertyName().equals(d_propertyName);
			}
			return false;
		}
	}

	private static final long serialVersionUID = 3313939584326101804L;

	private final ValueHolder<Boolean> d_considerFixed;
	private final ValueHolder<Boolean> d_considerFlexibleLower;
	private final ValueHolder<Boolean> d_considerFlexibleUpper;

	public SpecifyDoseTypeWizardStep(final DosedDrugTreatmentPresentation pm) {
		super(pm, "Specify criteria","Select for the category or criteria for the fixed and flexible dose types.", null);

		d_considerFixed = new ValueModelWrapper<Boolean>(
				new ChildIsChoiceNodeModel(d_pm.getChoiceModelForType(FixedDose.class), FixedDose.class, FixedDose.PROPERTY_QUANTITY));

		d_considerFlexibleLower = new ValueModelWrapper<Boolean>(
				new ChildIsChoiceNodeModel(d_pm.getChoiceModelForType(FlexibleDose.class), FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE));
		d_considerFlexibleUpper = new ValueModelWrapper<Boolean>(
				new ChildIsChoiceNodeModel(d_pm.getChoiceModelForType(FlexibleDose.class), FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE));
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
				d_pm.getChoiceModelForType(FixedDose.class),
				d_pm.getCategories(),
				new ChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY));
//		if (d_previousFixed != null) {
//			fixedCategoryComboBox.setSelectedItem(d_previousFixed);
//		}
		builder.add(fixedCategoryComboBox, cc.xy(3, row));

		row = LayoutUtil.addRow(layout, row);

		builder.addLabel("Flexible dose", cc.xy(1, row));
		final JComboBox flexibleCategoryComboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(
				d_pm.getChoiceModelForType(FlexibleDose.class),
				d_pm.getCategories(),
				new ChoiceNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE),
				new ChoiceNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE));
//		if(d_previousFlexible != null) {
//			flexibleCategoryComboBox.setSelectedItem(d_previousFlexible);
//		}
		builder.add(flexibleCategoryComboBox, cc.xy(3, row));

		return builder.getPanel();
	}

	public ValueHolder<Boolean> getConsiderFlexibleUpper() {
		return d_considerFlexibleUpper;
	}

	public ValueHolder<Boolean> getConsiderFlexibleLower() {
		return d_considerFlexibleLower;
	}

	public ValueHolder<Boolean> getConsiderFixed() {
		return d_considerFixed;
	}
}
