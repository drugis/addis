package org.drugis.addis.gui.builder;

import java.text.NumberFormat;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.drugis.addis.entities.AbstractStudy;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.gui.CharacteristicHolder;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.StudyTablePanel;
import org.drugis.addis.presentation.MetaStudyPresentationModel;
import org.drugis.addis.presentation.StudyListPresentationModel;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.OneWayObjectFormat;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.AbstractValueModel;
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
		
		row = buildDataPart(layout, fullWidth, builder, cc, row);
		
		return builder.getPanel();
	}

	private int buildStudiesPart(FormLayout layout, int fullWidth,
			PanelBuilder builder, CellConstraints cc, int row) {
		LayoutUtil.addRow(layout);
		LayoutUtil.addRow(layout);
		LayoutUtil.addRow(layout);
		
		builder.addSeparator("Included Studies", cc.xyw(1, row, fullWidth));
		row += 2;
		
		JPanel panel = createStudyTablePanel(d_model, d_mainWindow);
		
		builder.add(panel, cc.xyw(1, row, fullWidth));
		row += 2;
		
		
		//builder.add(customizeButton, cc.xy(3, row));
		//row += 2;
		
		return row;
	}

	private static JPanel createStudyTablePanel(final StudyListPresentationModel metamodel, final Main mainWindow) {
		return StudyTablePanel.createStudyTablePanel(metamodel, mainWindow);
	}

	private int buildDataPart(FormLayout layout, int fullWidth,
			PanelBuilder builder, CellConstraints cc, int row) {
		builder.addSeparator("Data", cc.xyw(1, row, fullWidth));
		row += 2;
		
		builder.addLabel("Size", cc.xy(3, row, "center, center"));		
		int col = 5;
		for (Endpoint e : d_model.getBean().getEndpoints()) {
			builder.add(
					GUIFactory.createEndpointLabelWithIcon(d_loader, d_model.getBean(), e),
							cc.xy(col, row));
			col += 2;
		}
		row += 2;

		for (PatientGroup g : d_model.getBean().getPatientGroups()) {
			row = buildPatientGroup(layout, builder, cc, row, g);
		}
			
		return row;
	}

	private int buildPatientGroup(FormLayout layout, PanelBuilder builder,
			CellConstraints cc, int row, PatientGroup g) {
		int col;
		LayoutUtil.addRow(layout);
		builder.add(
				BasicComponentFactory.createLabel(getLabelModel(g)),
				cc.xy(1, row));
		
		builder.add(
				BasicComponentFactory.createLabel(
						new PresentationModel<PatientGroup>(g).getModel(BasicPatientGroup.PROPERTY_SIZE),
						NumberFormat.getInstance()),
						cc.xy(3, row, "center, center"));
		
		col = 5;
		for (Endpoint e : d_model.getBean().getEndpoints()) {
			Measurement m = d_model.getBean().getMeasurement(e, g);
			if (m != null) {
				builder.add(
						BasicComponentFactory.createLabel(getLabelModel(m)),
						cc.xy(col, row));
			}
			col += 2;
		}
		
		row += 2;
		return row;
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

	private AbstractValueModel getLabelModel(Object model) {
		return d_mainWindow.getPresentationModelManager().getLabeledModel(model).getLabelModel();
	}

	private String getStudyLabel() {
		return "Meta-analysis";			
	}

}
