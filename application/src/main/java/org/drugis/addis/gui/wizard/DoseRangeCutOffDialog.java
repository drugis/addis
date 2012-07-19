package org.drugis.addis.gui.wizard;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.DefaultFormatter;

import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.wizard.DoseRangeWizardStep.Family;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.RangeValidator;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.util.AffixableFormat;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

final class DoseRangeCutOffDialog extends OkCancelDialog {
	private final DosedDrugTreatmentPresentation d_pm;
	private static final long serialVersionUID = -7519390341921875264L;
	private final int d_rangeIndex;
	private final ValueHolder<Double> d_cutOff = new ModifiableHolder<Double>(0.0d);
	private final ValueHolder<Boolean> d_upperOpen = new ModifiableHolder<Boolean>(false);
	private String d_boundName;
	private final RangeValidator d_validator;
	private final RangeNode d_childToSplit;
	private Family d_family;
	private boolean d_onKnownDoses;

	public DoseRangeCutOffDialog(
			DosedDrugTreatmentPresentation model, 
			int rangeIndex, 
			Family family,
			String boundName,
			boolean onKnownDoses) {
		super(Main.getMainWindow(), "Split range", true);
		d_pm = model;
		d_rangeIndex = rangeIndex;
		d_boundName = boundName;
		d_family =  family;
		d_onKnownDoses = onKnownDoses;
		d_childToSplit = d_family.children.get(rangeIndex);
		d_validator = 
				new RangeValidator(d_cutOff, d_childToSplit.getRangeLowerBound(), d_childToSplit.getRangeUpperBound());
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
		
		boolean nodeIsLast = (d_rangeIndex == d_family.children.size() - 1);
		builder.addSeparator("Original range: " + d_childToSplit.getLabel(nodeIsLast), cc.xyw(1, row, colSpan));
		
		row = LayoutUtil.addRow(layout, row);
		builder.addLabel("Split range at:", cc.xy(1, row));
		final JFormattedTextField cutOffField = BasicComponentFactory.createFormattedTextField(d_cutOff, new DefaultFormatter());
		cutOffField.setColumns(5);
		cutOffField.addCaretListener(new CaretListener() {		
			public void caretUpdate(CaretEvent e) {
				try {
					cutOffField.commitEdit();
					pack();
				} catch (ParseException exp) {
					return; // we don't care
				}
			}
		});
		
		builder.add(cutOffField, cc.xy(3, row));
		String unitText = d_pm.getDoseUnitPresentation().getBean().toString();
		builder.addLabel(unitText, cc.xy(5, row));
		
		row = LayoutUtil.addRow(layout, row);
		builder.addLabel("Bound is open for:", cc.xyw(1, row, colSpan));
		
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(3);
		
		AffixableFormat formatLower = new AffixableFormat();
		formatLower.setMaximumFractionDigits(3);
		StringBuilder prefix = new StringBuilder()
			.append(decimalFormat.format(d_childToSplit.getRangeLowerBound()))
			.append((d_childToSplit.isRangeLowerBoundOpen() ? " < " : " <= "))
			.append(d_boundName + " < ");
		formatLower.setPrefix(prefix.toString());
		formatLower.setSuffix(" " + unitText);
		JLabel cutOffLower = BasicComponentFactory.createLabel(d_cutOff, formatLower);
		
		row = LayoutUtil.addRow(layout, row);
		builder.add(BasicComponentFactory.createRadioButton(d_upperOpen, false, ""), cc.xy(1, row));
		builder.add(cutOffLower, cc.xy(3, row));
		
		AffixableFormat formatUpper = new AffixableFormat();
		if (d_rangeIndex < d_pm.getBean().getDecisionTree().getChildCount(d_family.parent) - 1) { 
			StringBuilder suffix = new StringBuilder()
				.append(" <= " + d_boundName)
				.append((d_childToSplit.isRangeUpperBoundOpen() ? " < " : " <= "))
				.append(decimalFormat.format(d_childToSplit.getRangeUpperBound()));
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
		d_family.children.remove(d_rangeIndex);
		List<RangeNode> splitRange;
		if (!d_onKnownDoses) {
			splitRange = d_pm.splitRange(d_childToSplit, d_cutOff.getValue(), !d_upperOpen.getValue());
		} else {
			splitRange = d_pm.splitKnowDoseRanges(d_cutOff.getValue(), !d_upperOpen.getValue());
		}
		d_family.children.addAll(d_rangeIndex, splitRange);
		setVisible(false);
	}

	@Override
	protected void cancel() {
		setVisible(false);			
	} 
}