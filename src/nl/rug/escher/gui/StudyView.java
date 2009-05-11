package nl.rug.escher.gui;

import java.awt.event.ActionEvent;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.DefaultFormatter;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.entities.Measurement;
import nl.rug.escher.entities.PatientGroup;
import nl.rug.escher.entities.Study;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyView implements ViewBuilder {
	PresentationModel<Study> d_model;
	Domain d_domain;
	Main d_mainWindow;

	public StudyView(PresentationModel<Study> model, Domain domain, Main main) {
		d_model = model;
		d_mainWindow = main;
		d_domain = domain;
	}
	
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout( 
				"right:pref, 3dlu, pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		int fullWidth = 5;
		for (int i = 1; i < d_model.getBean().getEndpoints().size(); ++i) {
			LayoutUtil.addColumn(layout);
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
		
		builder.addLabel("Size", cc.xy(3, row));		
		int col = 5;
		for (Endpoint e : d_model.getBean().getEndpoints()) {
			builder.add(
					BasicComponentFactory.createLabel(
							new PresentationModel<Endpoint>(e).getModel(Endpoint.PROPERTY_NAME)),
							cc.xy(col, row));
			col += 2;
		}
		row += 2;
		
		for (PatientGroup g : d_model.getBean().getPatientGroups()) {
			LayoutUtil.addRow(layout);
			builder.add(
					BasicComponentFactory.createLabel(
							new PresentationModel<PatientGroup>(g).getModel(PatientGroup.PROPERTY_LABEL)),
					cc.xy(1, row));
			
			builder.add(
					BasicComponentFactory.createLabel(
							new PresentationModel<PatientGroup>(g).getModel(PatientGroup.PROPERTY_SIZE),
							NumberFormat.getInstance()),
							cc.xy(3, row));
			
			col = 5;
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
			LayoutUtil.addRow(layout);
			builder.add(
					BasicComponentFactory.createLabel(
							new PresentationModel<Endpoint>(e).getModel(Endpoint.PROPERTY_NAME)),
					cc.xy(1, row));
			builder.add(
					buildFindStudiesButton(e), cc.xy(3, row));
			row += 2;
		}
		LayoutUtil.addRow(layout);
		builder.add(buildAddEndpointButton(), cc.xy(1, row));
		
		row += 2;
		
		return row;
	}

	private JPanel buildAddEndpointButton() {
		JButton button = new JButton("Add Endpoint");
		button.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				addEndpointClicked();
			}			
		});
		ButtonBarBuilder2 bbarBuilder = new ButtonBarBuilder2();
		bbarBuilder.addGlue();
		bbarBuilder.addButton(button);
		
		if (studyHasAllEndpoints()) {
			button.setEnabled(false);
		}
		
		JPanel panel = bbarBuilder.getPanel();
		return panel;
	}

	private boolean studyHasAllEndpoints() {
		return d_model.getBean().getEndpoints().containsAll(d_domain.getEndpoints());
	}

	private void addEndpointClicked() {
		d_mainWindow.showStudyAddEndpointDialog(d_model.getBean());
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
