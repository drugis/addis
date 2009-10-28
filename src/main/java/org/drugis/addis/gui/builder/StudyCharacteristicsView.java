package org.drugis.addis.gui.builder;


import javax.swing.JComponent;

import org.drugis.addis.entities.AbstractStudy;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.presentation.CharacteristicHolder;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyCharacteristicsView implements ViewBuilder {
	
	private PresentationModel<? extends Study> d_model;

	public StudyCharacteristicsView(PresentationModel<? extends Study> model) {
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
		builder.add(BasicComponentFactory.createLabel(d_model.getModel(AbstractStudy.PROPERTY_ID)),
				cc.xyw(3, 1, fullWidth - 2));
		
		int row = 3;
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			LayoutUtil.addRow(layout);
			builder.addLabel(c.getDescription() + ":", cc.xy(1, row));
			
			// FIXME: should get CharacteristicHolder from d_model.getCharacteristic(c)
			CharacteristicHolder model = new CharacteristicHolder(d_model.getBean(), c);
			builder.add(AuxComponentFactory.createCharacteristicView(model),
					cc.xyw(3, row, fullWidth - 2));
			
			row += 2;
		}
		return builder.getPanel();
	}
}
