package nl.rug.escher.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.text.DefaultFormatter;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.entities.ContinuousMeasurement;
import nl.rug.escher.entities.Measurement;
import nl.rug.escher.entities.PatientGroup;
import nl.rug.escher.entities.Study;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyAddEndpointView implements ViewBuilder {
	private Domain d_domain;
	private Study d_study;
	private PresentationModel<EndpointHolder> d_endpointModel;
	private List<ContinuousMeasurement> d_measurements;
	
	private JComboBox d_endpointSelect;
	private SelectionInList<Endpoint> d_endpointSelectionInList;
	
	public StudyAddEndpointView(Domain domain, Study study,
			PresentationModel<EndpointHolder> endpointModel, List<ContinuousMeasurement> measurements) {
		d_domain = domain;
		d_study = study;
		d_endpointModel = endpointModel;
		d_measurements = measurements;
		initializeMeasurements();
	}

	private void initializeMeasurements() {
		for (PatientGroup g : d_study.getPatientGroups()) {
			ContinuousMeasurement m = new ContinuousMeasurement();
			m.setPatientGroup(g);
			m.setMean(0.0);
			m.setStdDev(0.0);
			d_measurements.add(m);
		}
	}
	
	private void initComponents() {
		d_endpointSelectionInList = new SelectionInList<Endpoint>(getEndpoints(), 
				d_endpointModel.getModel(EndpointHolder.PROPERTY_ENDPOINT));
		d_endpointSelect = BasicComponentFactory.createComboBox(d_endpointSelectionInList);
	}

	private List<Endpoint> getEndpoints() {
		List<Endpoint> list = new ArrayList<Endpoint>(d_domain.getEndpoints());
		list.removeAll(d_study.getEndpoints());
		return list;
	}

	public JComponent buildPanel() {
		initComponents();
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		int fullWidth = 5;
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Study", cc.xyw(1, 1, fullWidth));
		builder.addLabel("ID:", cc.xy(1, 3));
		builder.add(BasicComponentFactory.createLabel(
				new PresentationModel<Study>(d_study).getModel(Study.PROPERTY_ID)
				), cc.xyw(3, 3, fullWidth - 2));
		
		builder.addSeparator("Endpoint", cc.xyw(1, 5, fullWidth));
		builder.addLabel("Endpoint:", cc.xy(1, 7));
		builder.add(d_endpointSelect, cc.xyw(3, 7, fullWidth - 2));
		
		builder.addSeparator("Data", cc.xyw(1, 9, fullWidth));
		builder.addLabel("Mean", cc.xy(3, 11));
		builder.addLabel("StdDev", cc.xy(5, 11));
		
		buildMeasurementsPart(builder, cc, 13, layout);
		
		return builder.getPanel();
	}

	private void buildMeasurementsPart(PanelBuilder builder,
			CellConstraints cc, int row, FormLayout layout) {
		for (ContinuousMeasurement m : d_measurements) {
			LayoutUtil.addRow(layout);
			PresentationModel<ContinuousMeasurement> model = new PresentationModel<ContinuousMeasurement>(m);
			PresentationModel<PatientGroup> gModel = 
				new PresentationModel<PatientGroup>(m.getPatientGroup());
			builder.add(BasicComponentFactory.createLabel(gModel.getModel(PatientGroup.PROPERTY_LABEL)),
					cc.xy(1, row));
			builder.add(BasicComponentFactory.createFormattedTextField(
					model.getModel(Measurement.PROPERTY_MEAN), new DefaultFormatter()),
					cc.xy(3, row));
			builder.add(BasicComponentFactory.createFormattedTextField(
					model.getModel(Measurement.PROPERTY_STDDEV), new DefaultFormatter()),
					cc.xy(5, row));
			row += 2;
		}
	}
}
