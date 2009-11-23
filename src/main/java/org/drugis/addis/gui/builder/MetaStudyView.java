package org.drugis.addis.gui.builder;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.drugis.addis.entities.AbstractStudy;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.CharacteristicHolder;
import org.drugis.addis.presentation.MetaStudyPresentationModel;
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
	private StudyEndpointsView d_epView;

	public MetaStudyView(MetaStudyPresentationModel model, Main main) {
		d_model = model;
		d_studyView = new StudyTablePanelView(model, main);
		d_epView = new StudyEndpointsView(model, main);
	}
	
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout( 
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Meta-analysis", cc.xy(1,1));
		builder.add(GUIFactory.createCollapsiblePanel(buildOverviewPart()), cc.xy(1, 3));
		
		builder.addSeparator("Included Studies", cc.xy(1, 5));
		builder.add(GUIFactory.createCollapsiblePanel(d_studyView.buildPanel()), cc.xy(1, 7));
		
		builder.addSeparator("Endpoints", cc.xy(1, 9));
		builder.add(GUIFactory.createCollapsiblePanel(d_epView.buildPanel()), cc.xy(1, 11));
		
		return builder.getPanel();
	}

	private JPanel buildOverviewPart() {
		FormLayout layout = new FormLayout( 
				"right:pref, 3dlu, pref:grow",
				"p"
				);
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel("ID:", cc.xy(1, 1));
		builder.add(BasicComponentFactory.createLabel(
				d_model.getModel(AbstractStudy.PROPERTY_ID)), cc.xy(3, 1));
		
		int row = 3;
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
		return builder.getPanel();
	}
}
