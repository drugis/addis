package org.drugis.addis.gui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.ListModel;

import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.RangeEdge;
import org.drugis.addis.gui.renderer.CategoryComboboxRenderer;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RangeInputBuilder {
	private final JDialog d_dialog;
	private final RangeInputPresentation d_pm;

	public RangeInputBuilder(final JDialog dialog, final RangeInputPresentation rangeInputPresentation) {
		d_dialog = dialog;
		d_pm = rangeInputPresentation;
	}

	public int addFamilyToPanel(final PanelBuilder builder, int row) {
		final FormLayout layout = builder.getLayout();
//		final CellConstraints cc = new CellConstraints();
//		final DecisionTreeNode parent = d_pm.getParent();
//		if (parent instanceof RangeNode) {
//			row = LayoutUtil.addRow(layout, row);
//			builder.addSeparator(((RangeNode)parent).getLabel(), cc.xyw(1, row, 6));
//		}

		final ObservableList<DecisionTreeEdge> ranges = d_pm.getRanges();
		for (final DecisionTreeEdge edge : ranges) {
			row = rangeRow(layout, builder, row, (RangeEdge) edge);
		}
		return row;
	}

	private int rangeRow(final FormLayout layout,
			final PanelBuilder builder,
			int row,
			final RangeEdge range) {
		final CellConstraints cc = new CellConstraints();
		row = LayoutUtil.addRow(layout, row);

		final JButton splitBtn = new JButton("Split Range");
		splitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final DoseRangeCutOffDialog dialog = new DoseRangeCutOffDialog(d_dialog, d_pm.getParentPresentation(), d_pm.getParent(), range);
				dialog.setVisible(true);
			}
		});

		builder.add(splitBtn, cc.xy(1, row));
		final String variableName = GUIHelper.humanize(d_pm.getParent().getPropertyName());
		builder.add(new JLabel(RangeEdge.format(variableName, range)), cc.xy(3, row));

		final JComboBox comboBox = BasicComponentFactory.createComboBox(
				new SelectionInList<DecisionTreeNode>(
						(ListModel)d_pm.getParentPresentation().getOptionsForEdge(range),
						d_pm.getParentPresentation().getModelForEdge(range)), new CategoryComboboxRenderer(d_pm.hasPrevious()));

		builder.add(comboBox, cc.xy(5, row));
		return row;
	}
}