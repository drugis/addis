package org.drugis.addis.gui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RangeInputBuilder {
	private final JDialog d_dialog;
	private final RangeInputPresentation d_pm;
	
	public RangeInputBuilder(JDialog dialog, RangeInputPresentation rangeInputPresentation) {
		d_dialog = dialog;
		d_pm = rangeInputPresentation;
	}

	public int addFamilyToPanel(PanelBuilder builder, int row) {
		d_pm.determineSelections();
		
		FormLayout layout = builder.getLayout();
		CellConstraints cc = new CellConstraints();
		DecisionTreeNode parent = d_pm.getParent();
		if (parent instanceof RangeNode) {
			row = LayoutUtil.addRow(layout, row);
			builder.addSeparator(((RangeNode)parent).getLabel(), cc.xyw(1, row, 6));
		}
		
		ObservableList<DecisionTreeNode> children = d_pm.getChildren();
		for (int i = 0; i < children.size(); ++i) {
			row = rangeRow(layout, builder, row, i);
		}
		return row;
	}

	private int rangeRow(FormLayout layout,
			PanelBuilder builder, 
			int row, 
			final int index) {
		
		if (!(d_pm.getChildren().get(index) instanceof RangeNode)) {
			return row;
		}
		
		CellConstraints cc = new CellConstraints();
		row = LayoutUtil.addRow(layout, row);
		
		JButton splitBtn = new JButton("Split Range");
		splitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DoseRangeCutOffDialog dialog;
				if (d_pm.getBeanProperty() != null) {
					dialog = new DoseRangeCutOffDialog(d_dialog, d_pm.getParentPresentation(), index, d_pm.getFamily(), GUIHelper.humanize(d_pm.getBeanProperty().getValue()), false);
				} else {
					dialog = new DoseRangeCutOffDialog(d_dialog, d_pm.getParentPresentation(), index, d_pm.getFamily(), "quantity", true);
				}
				dialog.setVisible(true);
			}

		});
		builder.add(splitBtn, cc.xy(1, row));
		final RangeNode rangeNode = d_pm.getChild(index);
		builder.add(new JLabel(rangeNode.getLabel()), cc.xy(3, row));
		final JComboBox comboBox = AddDosedDrugTreatmentWizardStep.createCategoryComboBox(d_pm.getParentPresentation().getCategories(), d_pm.getExtraOptions());
		comboBox.setSelectedItem(d_pm.getSelected(rangeNode));
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Object selected = comboBox.getSelectedItem();
					d_pm.setSelected(rangeNode, selected);
				}
			}
		});
		builder.add(comboBox, cc.xy(5, row));
		return row;
	}
}