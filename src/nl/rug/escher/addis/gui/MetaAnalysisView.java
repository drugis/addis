package nl.rug.escher.addis.gui;

import javax.swing.JComponent;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.Measurement;
import nl.rug.escher.addis.entities.MetaAnalysis;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.common.gui.LayoutUtil;
import nl.rug.escher.common.gui.ViewBuilder;

public class MetaAnalysisView implements ViewBuilder {
	MetaAnalysis d_analysis;
	
	public MetaAnalysisView(MetaAnalysis analysis) {
		d_analysis = analysis;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"pref",
				"p, 3dlu, p, 3dlu, p");
		int fullWidth = 1;
		for (Drug d : d_analysis.getDrugs()) {
			LayoutUtil.addColumn(layout);
			fullWidth += 2;
		}
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Meta Analysis", cc.xyw(1, 1, fullWidth));
		
		builder.addLabel("Endpoint: ", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(
				new PresentationModel<Endpoint>(d_analysis.getEndpoint()).getModel(Endpoint.PROPERTY_NAME)
				), cc.xy(3, 3));
		
		int col = 3;
		for (Drug d : d_analysis.getDrugs()) {
			builder.add(BasicComponentFactory.createLabel(
					new PresentationModel<Drug>(d).getModel(Drug.PROPERTY_NAME)), cc.xy(col, 5));
			col += 2;
		}
		
		int row = 7;
		for (Study s : d_analysis.getStudies()) {
			LayoutUtil.addRow(layout);
			builder.add(BasicComponentFactory.createLabel(
					new PresentationModel<Study>(s).getModel(Study.PROPERTY_ID)), cc.xy(1, row));
			
			col = 3;
			for (Drug d : d_analysis.getDrugs()) {
				builder.add(BasicComponentFactory.createLabel(
						new PresentationModel<Measurement>(
								d_analysis.getMeasurement(s, d)).getModel(Measurement.PROPERTY_LABEL)
								),
						cc.xy(col, row));
				col += 2;
			}
			
			row += 2;
		}
		
		LayoutUtil.addRow(layout);
		builder.addLabel("Combined", cc.xy(1, row));
		col = 3;
		for (Drug d : d_analysis.getDrugs()) {
			builder.add(BasicComponentFactory.createLabel(
					new PresentationModel<Measurement>(d_analysis.getPooledMeasurement(d)).getModel(Measurement.PROPERTY_LABEL)
					),
					cc.xy(col, row));
			col += 2;
		}
	
		return builder.getPanel();
	}
}
