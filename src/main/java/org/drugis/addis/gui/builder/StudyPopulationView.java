package org.drugis.addis.gui.builder;

import java.util.List;
import java.util.TreeSet;

import javax.swing.JComponent;

import org.drugis.addis.entities.Variable;
import org.drugis.addis.presentation.BasicArmPresentation;
import org.drugis.addis.presentation.LabeledPresentationModel;
import org.drugis.addis.presentation.StudyPresentationModel;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyPopulationView implements ViewBuilder {
	private StudyPresentationModel d_pm;

	public StudyPopulationView(StudyPresentationModel model) {
		d_pm = model;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout("left:pref", "p");
		int columns = 2 + d_pm.getArmCount();
		for (int c = 1; c < columns; ++c) {
			LayoutUtil.addColumn(layout);
		}
		int rows = 1 + d_pm.getPopulationCharacteristicCount();
		for (int r = 1; r < rows; ++r) {
			LayoutUtil.addRow(layout);
		}
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		List<BasicArmPresentation> arms = d_pm.getArms();
		int row = 1;
		int col = 3;
		for (BasicArmPresentation arm : arms) {
			builder.add(BasicComponentFactory.createLabel(arm.getLabelModel()), cc.xy(col, row));
			col += 2;
		}
		builder.addLabel("Total", cc.xy(col, row));
		
		row += 2;
		for (Variable v : new TreeSet<Variable>(d_pm.getPopulationCharacteristics())) {
			builder.addLabel(v.getName(), cc.xy(1, row));
			col = 3;
			for (BasicArmPresentation arm : arms) {
				LabeledPresentationModel model = arm.getCharacteristicModel(v);
				if (model != null) {
					builder.add(BasicComponentFactory.createLabel(model.getLabelModel()),
							cc.xy(col, row));
				} else {
					builder.addLabel("UNKNOWN", cc.xy(col, row));
				}
				col += 2;
			}
			row += 2;
		}
		
		return builder.getPanel();
	}
}
