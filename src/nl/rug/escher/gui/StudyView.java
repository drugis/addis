package nl.rug.escher.gui;

import javax.swing.JComponent;

import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.entities.Measurement;
import nl.rug.escher.entities.PatientGroup;
import nl.rug.escher.entities.Study;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class StudyView implements ViewBuilder {
	PresentationModel<Study> d_model;

	public StudyView(PresentationModel<Study> model) {
		d_model = model;
	}
	
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout( 
				"pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		int fullWidth = 1;
		for (int i = 0; i < d_model.getBean().getEndpoints().size(); ++i) {
			layout.appendColumn(ColumnSpec.decode("3dlu"));
			layout.appendColumn(ColumnSpec.decode("pref"));
			layout.appendRow(RowSpec.decode("3dlu"));
			layout.appendRow(RowSpec.decode("p"));
			fullWidth += 2;
		}
		if (fullWidth == 1) {
			fullWidth = 3;
		}
		
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Study", cc.xyw(1,1,fullWidth));
		builder.addLabel("ID:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(d_model.getModel(Study.PROPERTY_ID)),
				cc.xyw(3, 3, fullWidth - 2));
		
		builder.addSeparator("Endpoints", cc.xyw(1, 5, fullWidth));
		
		int row = 7;
		for (Endpoint e : d_model.getBean().getEndpoints()) {
			builder.add(
					BasicComponentFactory.createLabel(
							new PresentationModel<Endpoint>(e).getModel(Endpoint.PROPERTY_NAME)),
					cc.xy(1, row));
			builder.add(
					BasicComponentFactory.createLabel(
							new PresentationModel<Endpoint>(e).getModel(Endpoint.PROPERTY_DESCRIPTION)),
					cc.xyw(3, row, fullWidth - 2));
			row += 2;
		}
		
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
		
		return builder.getPanel();
	}

}
