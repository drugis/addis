package org.drugis.addis.gui.wizard;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.DefaultFormatter;

import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.RangeEdge;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.RangeValidator;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

final class DoseRangeCutOffDialog extends OkCancelDialog {
	private final class FormatRange extends Format {
		private static final long serialVersionUID = -8624949198891358088L;

		private final String d_variableName;
		private final boolean d_isUpperBoundOpen;

		private FormatRange(final String variableName, final boolean isUpperBoundOpen) {
			d_variableName = variableName;
			d_isUpperBoundOpen = isUpperBoundOpen;
		}

		@Override
		public StringBuffer format(final Object toFormat, final StringBuffer toAppendTo, final FieldPosition pos) {
			final double cutOff = (Double) toFormat;
			toAppendTo.append(RangeEdge.format(d_variableName,
					d_rangeToSplit.getLowerBound(), d_rangeToSplit.isLowerBoundOpen(),
					cutOff, d_isUpperBoundOpen));
			return toAppendTo;
		}

		@Override
		public Object parseObject(final String arg0, final ParsePosition arg1) {
			return null;
		}
	}

	private final DosedDrugTreatmentPresentation d_pm;
	private static final long serialVersionUID = -7519390341921875264L;
	private final ValueHolder<Double> d_cutOff = new ModifiableHolder<Double>(0.0d);
	private final ValueHolder<Boolean> d_lowerOpen = new ModifiableHolder<Boolean>(false);
	private final RangeValidator d_validator;
	private final RangeEdge d_rangeToSplit;
	private final ChoiceNode d_choice;

	public DoseRangeCutOffDialog(
			final JDialog parent,
			final DosedDrugTreatmentPresentation model,
			final ChoiceNode choice,
			final RangeEdge rangeToSplit) {
		super(parent, "Split range", true);
		setLocationByPlatform(true);
		d_pm = model;
		d_choice = choice;
		d_rangeToSplit = rangeToSplit;
		d_validator =
				new RangeValidator(d_cutOff, d_rangeToSplit.getLowerBound(), d_rangeToSplit.getUpperBound());
		getUserPanel().add(buildPanel());
		pack();
	}

	protected JPanel buildPanel() {
		Bindings.bind(d_okButton, "enabled", d_validator);

		final FormLayout layout = new FormLayout(
				"pref, 3dlu, pref, 3dlu, fill:pref:grow",
				"p"
				);

		final PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		final CellConstraints cc = new CellConstraints();
		final int colSpan = builder.getColumnCount();
		int row = 1;

		final String variableName = GUIHelper.humanize(d_choice.getPropertyName());

		builder.addSeparator("Original range: " + RangeEdge.format(variableName, d_rangeToSplit), cc.xyw(1, row, colSpan));

		row = LayoutUtil.addRow(layout, row);
		builder.addLabel("Split range at:", cc.xy(1, row));
		final JFormattedTextField cutOffField = BasicComponentFactory.createFormattedTextField(d_cutOff, new DefaultFormatter());
		cutOffField.setColumns(5);
		cutOffField.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(final CaretEvent e) {
				try {
					cutOffField.commitEdit();
					pack();
				} catch (final ParseException exp) {
					return; // we don't care
				}
			}
		});

		builder.add(cutOffField, cc.xy(3, row));
		final String unitText = d_pm.getDoseUnitPresentation().getBean().toString();
		builder.addLabel(unitText, cc.xy(5, row));

		row = LayoutUtil.addRow(layout, row);
		builder.addLabel("Bound is inclusive/exclusive for lower range:", cc.xyw(1, row, colSpan));

		row = LayoutUtil.addRow(layout, row);
		builder.add(BasicComponentFactory.createRadioButton(d_lowerOpen, false, ""), cc.xy(1, row));
		builder.add(BasicComponentFactory.createLabel(d_cutOff, new FormatRange(variableName, false)), cc.xy(3, row));
		row = LayoutUtil.addRow(layout, row);
		builder.add(BasicComponentFactory.createRadioButton(d_lowerOpen, true, ""), cc.xy(1, row));
		builder.add(BasicComponentFactory.createLabel(d_cutOff, new FormatRange(variableName, true)), cc.xy(3, row));

		return builder.getPanel();
	}

	@Override
	protected void commit() {
		d_pm.getBean().splitRange(d_choice, d_cutOff.getValue(), d_lowerOpen.getValue());
		dispose();
	}

	@Override
	protected void cancel() {
		dispose();
	}
}