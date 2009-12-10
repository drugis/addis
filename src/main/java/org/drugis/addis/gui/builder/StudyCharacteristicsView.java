package org.drugis.addis.gui.builder;


import javax.swing.JComponent;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.presentation.CharacteristicHolder;
import org.drugis.addis.presentation.StudyPresentationModel;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyCharacteristicsView implements ViewBuilder {
	
	private StudyPresentationModel d_model;

	public StudyCharacteristicsView(StudyPresentationModel model) {
		d_model = model;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, left:pref:grow",
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		int fullWidth = 3;
		
		builder.addLabel("ID:", cc.xy(1, 1));
		builder.add(BasicComponentFactory.createLabel(d_model.getModel(Study.PROPERTY_ID)),
				cc.xyw(3, 1, fullWidth - 2));
		
		int row = 3;
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			if (isCharacteristicShown(c)) {
				LayoutUtil.addRow(layout);
				builder.addLabel(c.getDescription() + ":", cc.xy(1, row));

				CharacteristicHolder model = d_model.getCharacteristicModel(c);
				builder.add(AuxComponentFactory.createCharacteristicView(model),
						cc.xyw(3, row, fullWidth - 2));

				row += 2;
			}
		}
		return builder.getPanel();
	}

	private boolean isCharacteristicShown(StudyCharacteristic c) {
		if (c.equals(StudyCharacteristic.STUDY_END)) {
			return (d_model.isStudyFinished());
		}
		return true;
	}
}
