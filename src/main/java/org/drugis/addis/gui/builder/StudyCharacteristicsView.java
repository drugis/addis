package org.drugis.addis.gui.builder;


import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.StudyCharacteristics;
import org.drugis.addis.presentation.StudyPresentationModel;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.GUIHelper;
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
		JLabel idLabel = BasicComponentFactory.createLabel(d_model.getModel(Study.PROPERTY_ID));
		idLabel.setToolTipText(GUIHelper.createToolTip(d_model.getNote(Study.PROPERTY_ID)));
		builder.add(idLabel,
				cc.xyw(3, 1, fullWidth - 2));
		
		int row = 3;
		for (Characteristic c : StudyCharacteristics.values()) {
			if (isCharacteristicShown(c)) {
				LayoutUtil.addRow(layout);
				builder.addLabel(c.getDescription() + ":", cc.xy(1, row));

				JComponent charView = 
					AuxComponentFactory.createCharacteristicView(d_model.getCharacteristicModel(c));
				if (charView instanceof JScrollPane) {
					JScrollPane pane = (JScrollPane)charView;
					((JComponent)pane.getViewport().getView()).setToolTipText(
							GUIHelper.createToolTip(d_model.getNote(c)));
				} else {
					charView.setToolTipText(GUIHelper.createToolTip(d_model.getNote(c)));
				}
				builder.add(charView,
						cc.xyw(3, row, fullWidth - 2));

				row += 2;
			}
		}
		return builder.getPanel();
	}

	private boolean isCharacteristicShown(Characteristic c) {
		if (c.equals(BasicStudyCharacteristic.STUDY_END)) {
			return (d_model.isStudyFinished());
		}
		return true;
	}
}
