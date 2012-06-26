package org.drugis.addis.gui.wizard;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.DefaultFormatter;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.treatment.DoseRangeNode;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DoseRangeWizardStep extends AbstractDoseTreatmentWizardStep {
	private static final long serialVersionUID = 3313939584326101804L;
	private final RangeNode d_node;
	private final Class<? extends AbstractDose> d_beanClass;

	/**
	 * NOTE: this class abuses {@link #setPositivePrefix(String)} and {@link #setPositiveSuffix(String)}
	 * from {@link DecimalFormat}, so this class is only usable for positive numbers.
	 */
	@SuppressWarnings("serial")
	private final class AffixableFormat extends DecimalFormat {
	
		public void setPrefix(String prefixText) {
			setPositivePrefix(prefixText);
		}
		
		public void setSuffix(String suffixText) {
			setPositiveSuffix(suffixText);
		}
	}
	
	private final class AddCutOffDialog extends OkCancelDialog {
		@SuppressWarnings("serial")
		public final class RangeValidator extends AbstractValueModel implements ValueHolder<Boolean> {
			private static final String PROPERTY_VALID = "value";
			private final ValueHolder<Double> d_range;
			private final double d_maximum;
			private final double d_minimum;
			private boolean d_valid = false;
			public RangeValidator(ValueHolder<Double> range, double minimum, double maximum) {
				d_range = range;
				d_maximum = maximum;
				d_minimum = minimum;
				d_range.addValueChangeListener(new PropertyChangeListener() {		
					public void propertyChange(PropertyChangeEvent evt) {
						validate();
					}
				});
				validate();
			}
			
			@Override
			public Boolean getValue() {
				return d_valid;
			}

			@Override
			public void setValue(Object newValue) {
				throw new UnsupportedOperationException("Cannot set value on validators");
			}
			
			public void validate() { 
				boolean oldValue = d_valid;
				d_valid =  d_range.getValue() <= d_maximum && d_range.getValue() >= d_minimum;
				firePropertyChange(PROPERTY_VALID, oldValue, d_valid);
			}
		}

		private static final long serialVersionUID = -7519390341921875264L;
		private final int d_rangeIndex;
		private final ValueHolder<Double> d_cutOff = new ModifiableHolder<Double>(0.0d);
		private final ValueHolder<Boolean> d_upperOpen = new ModifiableHolder<Boolean>(false);
		private String d_boundName;
		private ValueHolder<Boolean> d_validator;

		public AddCutOffDialog(int rangeIndex, String boundName) {
			super(d_mainWindow, "Split range", true);
			d_rangeIndex = rangeIndex;
			d_boundName = boundName;
			d_validator = new RangeValidator(d_cutOff, d_node.getRangeLowerBound(rangeIndex), d_node.getRangeUpperBound(rangeIndex));
			getUserPanel().add(buildPanel());
			pack(); 
		}
		
		protected JPanel buildPanel() { 
			Bindings.bind(d_okButton, "enabled", d_validator);

			FormLayout layout = new FormLayout(
					"pref, 3dlu, pref, 3dlu, fill:pref:grow",
					"p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			final int colSpan = builder.getColumnCount();
			int row = 1;
			
			builder.addSeparator("Original range: " + d_node.getRangeLabel(d_rangeIndex), cc.xyw(1, row, colSpan));
			
			row = LayoutUtil.addRow(layout, row);
			builder.addLabel("Split range at:", cc.xy(1, row));
			JFormattedTextField cutOffField = BasicComponentFactory.createFormattedTextField(d_cutOff, new DefaultFormatter());
			cutOffField.setColumns(5);
			builder.add(cutOffField, cc.xy(3, row));
			String unitText = d_pm.getDoseUnitPresentation().getBean().toString();
			builder.addLabel(unitText, cc.xy(5, row));
			
			row = LayoutUtil.addRow(layout, row);
			builder.addLabel("Bound is open for:", cc.xyw(1, row, colSpan));
			
			AffixableFormat formatLower = new AffixableFormat();
			StringBuilder prefix = new StringBuilder()
				.append(d_node.getRangeLowerBound(d_rangeIndex))
				.append((d_node.isRangeLowerBoundOpen(d_rangeIndex) ? " < " : " <= "))
				.append(d_boundName + " < ");
			formatLower.setPrefix(prefix.toString());
			formatLower.setSuffix(" " + unitText);
			JLabel cutOffLower = BasicComponentFactory.createLabel(d_cutOff, formatLower);
			
			row = LayoutUtil.addRow(layout, row);
			builder.add(BasicComponentFactory.createRadioButton(d_upperOpen, false, ""), cc.xy(1, row));
			builder.add(cutOffLower, cc.xy(3, row));
			
			AffixableFormat formatUpper = new AffixableFormat();
			if ( d_rangeIndex < d_node.getChildCount() - 1) {
				StringBuilder suffix = new StringBuilder()
					.append(" <= " + d_boundName)
					.append((d_node.isRangeUpperBoundOpen(d_rangeIndex) ? " < " : " <= "))
					.append(d_node.getRangeUpperBound(d_rangeIndex));
				formatLower.setSuffix(" " + unitText);
				formatUpper.setSuffix(suffix.toString());
			} else {
				formatUpper.setPrefix(d_boundName + " >= ");
				formatLower.setSuffix(" " + unitText);
			}
			JLabel cutOffUpper = BasicComponentFactory.createLabel(d_cutOff, formatUpper);
			
			row = LayoutUtil.addRow(layout, row);
			builder.add(BasicComponentFactory.createRadioButton(d_upperOpen, true, ""), cc.xy(1, row));
			builder.add(cutOffUpper, cc.xy(3, row));

			return builder.getPanel();
		}
		
		@Override
		protected void commit() {
			d_node.addCutOff(d_cutOff.getValue(), ! d_upperOpen.getValue());
			setVisible(false);
		}

		@Override
		protected void cancel() {
			setVisible(false);			
		} 
		
	}
	
	public static DoseRangeWizardStep createOnBeanProperty(
			DosedDrugTreatmentPresentation pm, 
			Domain domain, 
			AddisWindow mainWindow,
			Class<? extends AbstractDose> beanClass, 
			String propertyName) {
	
		return new DoseRangeWizardStep(pm, domain, mainWindow, beanClass, new DoseRangeNode(beanClass, propertyName, pm.getDoseUnit()));
	}
	
	private DoseRangeWizardStep(DosedDrugTreatmentPresentation presentationModel, 
			Domain domain, 
			AddisWindow mainWindow,
			Class<? extends AbstractDose> beanClass, 
			DoseRangeNode node) {
		super(presentationModel, domain, mainWindow);
		d_beanClass = beanClass;
		d_node = node;
		d_node.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				rebuildPanel();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override 
	public void initialize() { 
		d_pm.setDoseCategory(d_node, d_beanClass);
	}
	
	protected JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, fill:pref:grow, 3dlu, pref, 3dlu",
				"p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		int row = 1;
		for (int i = 0; i < d_node.getChildCount(); ++i) {
			row = rangeRow(layout, builder, cc, row, i);
		}
		
		return builder.getPanel();
	}

	private int rangeRow(FormLayout layout, PanelBuilder builder,
			CellConstraints cc, int row, final int index) {
		row = LayoutUtil.addRow(layout, row);
		JButton splitBtn = new JButton("Split Range");
		splitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddCutOffDialog dialog = new AddCutOffDialog(index, "bound");
				GUIHelper.centerWindow(dialog, d_mainWindow);
				dialog.setVisible(true);
			}

		});
		builder.add(splitBtn, cc.xy(1, row));
		String rangeText = d_node.getRangeLabel(index);
		builder.add(new JLabel(rangeText), cc.xy(3, row));
		
		JComboBox comboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(d_pm.getCategories());
		builder.add(comboBox, cc.xy(5, row));
		return row;
	}


}
