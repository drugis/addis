package org.drugis.addis.gui.builder;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.drugis.addis.entities.AbstractStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.gui.CharacteristicHolder;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.MetaStudyPresentationModel;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.OneWayObjectFormat;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;

public class MetaStudyView implements ViewBuilder {
	MetaStudyPresentationModel d_model;
	Domain d_domain;
	Main d_mainWindow;
	private ImageLoader d_loader;

	public MetaStudyView(MetaStudyPresentationModel model, Domain domain, Main main, ImageLoader loader) {
		d_loader = loader;
		d_model = model;
		d_mainWindow = main;
		d_domain = domain;
	}
	
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout( 
				"left:pref, 3dlu, pref:grow, 3dlu, center:pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		int fullWidth = 5;
		int[] colGroup = new int[d_model.getBean().getEndpoints().size()];
		colGroup[0] = 5;	
		for (int i = 1; i < d_model.getBean().getEndpoints().size(); ++i) {			
			colGroup[i] = 5 + (i*2);
			layout.appendColumn(ColumnSpec.decode("3dlu"));
			layout.appendColumn(ColumnSpec.decode("center:pref"));			
			fullWidth += 2;
		}
		
		layout.setColumnGroups(new int[][]{new int[]{3}, colGroup});
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		int row = buildStudyPart(fullWidth, builder, cc, layout);
		
		row = buildStudiesPart(layout, fullWidth, builder, cc, row);
		
		row += 2;
		
		builder.addSeparator("Data", cc.xyw(1, row, fullWidth));
		
		row +=2 ;
		builder.add(new StudyDataView(d_model, d_loader, d_mainWindow.getPresentationModelManager()).buildPanel(), cc.xyw(1, row, fullWidth));		
		
		return builder.getPanel();
	}

	private int buildStudiesPart(FormLayout layout, int fullWidth,
			PanelBuilder builder, CellConstraints cc, int row) {
		LayoutUtil.addRow(layout);
		LayoutUtil.addRow(layout);
		LayoutUtil.addRow(layout);
		
		builder.addSeparator("Included Studies", cc.xyw(1, row, fullWidth));
		row += 2;
		
		JPanel panel = createStudyTablePanel();
		
		builder.add(panel, cc.xyw(1, row, fullWidth));
		row += 2;
		
		return row;
	}

	private JPanel createStudyTablePanel() {
		return new StudyTablePanelView(d_model, d_mainWindow).buildPanel();
	}


	private int buildStudyPart(int fullWidth, PanelBuilder builder,
			CellConstraints cc, FormLayout layout) {
		String studyLabel = getStudyLabel();
		builder.addSeparator(studyLabel, cc.xyw(1,1,fullWidth));
		builder.addLabel("ID:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(d_model.getModel(AbstractStudy.PROPERTY_ID)),
				cc.xyw(3, 3, fullWidth - 2));
		
		int row = 5;
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			ValueModel model = new CharacteristicHolder(d_model.getBean(), c);
			if (model.getValue() != null) {
				LayoutUtil.addRow(layout);
				builder.addLabel(c.getDescription() + ":", cc.xy(1, row));			
				builder.add(BasicComponentFactory.createLabel(model, new OneWayObjectFormat()),
						cc.xyw(3, row, fullWidth - 2));
				row += 2;				
			}
		}
		
		return row;
	}

	private String getStudyLabel() {
		return "Meta-analysis";			
	}

}
