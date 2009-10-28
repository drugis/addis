package org.drugis.addis.gui.builder;

import javax.swing.JComponent;

import org.drugis.addis.entities.AbstractStudy;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.CharacteristicHolder;
import org.drugis.addis.presentation.MetaStudyPresentationModel;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.OneWayObjectFormat;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class MetaStudyView implements ViewBuilder {
	private MetaStudyPresentationModel d_model;
	private StudyTablePanelView d_studyView;
	private StudyDataView d_dataView;

	public MetaStudyView(MetaStudyPresentationModel model, Main main, ImageLoader loader) {
		d_model = model;
		d_studyView = new StudyTablePanelView(model, main);
		d_dataView = new StudyDataView(model, loader, main.getPresentationModelManager());
	}
	
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout( 
				"right:pref, 3dlu, left:pref:grow",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		int fullWidth = 3;
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Meta-analysis", cc.xyw(1,1,fullWidth));
		builder.addLabel("ID:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(
				d_model.getModel(AbstractStudy.PROPERTY_ID)), cc.xy(3, 3));
		
		int row = 5;
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			ValueModel model = new CharacteristicHolder(d_model.getBean(), c);
			if (model.getValue() != null) {
				LayoutUtil.addRow(layout);
				builder.addLabel(c.getDescription() + ":", cc.xy(1, row));			
				builder.add(BasicComponentFactory.createLabel(model, new OneWayObjectFormat()),
						cc.xy(3, row));
				row += 2;				
			}
		}
		
		builder.addSeparator("Included Studies", cc.xyw(1, row, fullWidth));
		row += 2;
		builder.add(d_studyView.buildPanel(), cc.xyw(1, row, fullWidth));
		row += 2;
		builder.addSeparator("Data", cc.xyw(1, row, fullWidth));
		row += 2;
		builder.add(d_dataView.buildPanel(), cc.xyw(1, row, fullWidth));		
		
		return builder.getPanel();
	}
}
