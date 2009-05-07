package nl.rug.escher.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;

import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.entities.Measurement;
import nl.rug.escher.entities.PatientGroup;
import nl.rug.escher.entities.Study;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class StudyView implements ViewBuilder {
	PresentationModel<Study> d_model;
	Main d_mainWindow;

	public StudyView(PresentationModel<Study> model, Main main) {
		d_model = model;
		d_mainWindow = main;
	}
	
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout( 
				"right:pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		int fullWidth = 3;
		for (int i = 1; i < d_model.getBean().getEndpoints().size(); ++i) {
			layout.appendColumn(ColumnSpec.decode("3dlu"));
			layout.appendColumn(ColumnSpec.decode("pref"));
			fullWidth += 2;
		}
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		buildStudyPart(fullWidth, builder, cc);
		
		int row = buildEndpointsPart(layout, fullWidth, builder, cc);
		
		buildDataPart(layout, fullWidth, builder, cc, row);
		
		return builder.getPanel();
	}

	private void buildDataPart(FormLayout layout, int fullWidth,
			PanelBuilder builder, CellConstraints cc, int row) {
		builder.addSeparator("Data", cc.xyw(1, row, fullWidth));
		row += 2;
		
		int col = 3;
		for (Endpoint e : d_model.getBean().getEndpoints()) {
			builder.add(
					BasicComponentFactory.createLabel(
							new PresentationModel<Endpoint>(e).getModel(Endpoint.PROPERTY_NAME)),
							cc.xy(col, row));
			col += 2;
		}
		row += 2;
		
		for (PatientGroup g : d_model.getBean().getPatientGroups()) {
			layout.appendRow(RowSpec.decode("3dlu"));
			layout.appendRow(RowSpec.decode("p"));
			builder.add(
					BasicComponentFactory.createLabel(
							new PresentationModel<PatientGroup>(g).getModel(PatientGroup.PROPERTY_LABEL)),
					cc.xy(1, row));
			
			col = 3;
			for (Endpoint e : d_model.getBean().getEndpoints()) {
				Measurement m = g.getMeasurement(e);
				builder.add(
						BasicComponentFactory.createLabel(
								new PresentationModel<Measurement>(m).getModel(Measurement.PROPERTY_LABEL)),
						cc.xy(col, row));
				col += 2;
			}
			
			row += 2;
		}
	}

	private int buildEndpointsPart(FormLayout layout, int fullWidth, PanelBuilder builder,
			CellConstraints cc) {
		builder.addSeparator("Endpoints", cc.xyw(1, 5, fullWidth));
		
		int row = 7;
		for (Endpoint e : d_model.getBean().getEndpoints()) {
			layout.appendRow(RowSpec.decode("3dlu"));
			layout.appendRow(RowSpec.decode("p"));
			builder.add(
					BasicComponentFactory.createLabel(
							new PresentationModel<Endpoint>(e).getModel(Endpoint.PROPERTY_NAME)),
					cc.xy(1, row));
			builder.add(
					buildFindStudiesButton(e), cc.xy(3, row));
			row += 2;
		}
		return row;
	}

	private JComponent buildFindStudiesButton(final Endpoint endpoint) {
		JButton button = new JButton("Find Studies");
		button.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				d_mainWindow.endpointSelected(endpoint, d_model.getBean());
			}
		});
		
		ButtonBarBuilder2 builder = new ButtonBarBuilder2();
		builder.addButton(button);
		
		return builder.getPanel();
	}

	private void buildStudyPart(int fullWidth, PanelBuilder builder,
			CellConstraints cc) {
		builder.addSeparator("Study", cc.xyw(1,1,fullWidth));
		builder.addLabel("ID:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(d_model.getModel(Study.PROPERTY_ID)),
				cc.xyw(3, 3, fullWidth - 2));
	}

}
