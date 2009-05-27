package nl.rug.escher.addis.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.BasicMeasurement;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.common.gui.LayoutUtil;
import nl.rug.escher.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;

public class StudyAddEndpointView implements ViewBuilder {
	private Domain d_domain;
	private Study d_study;
	private PresentationModel<EndpointHolder> d_endpointModel;
	private List<BasicMeasurement> d_measurements;
	
	private JComboBox d_endpointSelect;
	private SelectionInList<Endpoint> d_endpointSelectionInList;
	
	public StudyAddEndpointView(Domain domain, Study study,
			PresentationModel<EndpointHolder> endpointModel, List<BasicMeasurement> measurements) {
		d_domain = domain;
		d_study = study;
		d_endpointModel = endpointModel;
		d_measurements = measurements;
	}

	private void initializeMeasurements() {
		for (PatientGroup g : d_study.getPatientGroups()) {
			if (getEndpoint() != null) {
				BasicMeasurement m = getEndpoint().buildMeasurement();
				m.setPatientGroup(g);
				d_measurements.add(m);
			}
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
		initializeMeasurements();
		initComponents();
		
		String colSpec = "right:pref, 3dlu, " +
			(getNumComponents() > 0 ? "pref" : "pref:grow");
		FormLayout layout = new FormLayout(colSpec,
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		int fullWidth = 3;
		for (int i = 1; i < getNumComponents(); ++i) {
			layout.appendColumn(ColumnSpec.decode("3dlu"));
			if (i < getNumComponents() - 1) {
				layout.appendColumn(ColumnSpec.decode("pref"));				
			} else {
				layout.appendColumn(ColumnSpec.decode("pref:grow"));				
			}
			fullWidth += 2;
		}
		
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
		
		if (getEndpoint() != null) {
			int col = 3;
			for (String header : MeasurementInputHelper.getHeaders(getEndpoint())) {
				builder.addLabel(header, cc.xy(col, 11));
				col += 2;
			}
		}
		
		buildMeasurementsPart(builder, cc, 13, layout);
		
		return builder.getPanel();
	}

	private int getNumComponents() {
		Endpoint e = getEndpoint();
		if (e == null) {
			return 0;
		}
		return MeasurementInputHelper.numComponents(e);
	}

	private Endpoint getEndpoint() {
		return d_endpointModel.getBean().getEndpoint();
	}

	private void buildMeasurementsPart(PanelBuilder builder,
			CellConstraints cc, int row, FormLayout layout) {
		for (BasicMeasurement m : d_measurements) {
			LayoutUtil.addRow(layout);
			PresentationModel<PatientGroup> gModel = 
				new PresentationModel<PatientGroup>(m.getPatientGroup());
			builder.add(BasicComponentFactory.createLabel(gModel.getModel(PatientGroup.PROPERTY_LABEL)),
					cc.xy(1, row));
			int col = 3;
			for (JComponent component : MeasurementInputHelper.getComponents(m)) {
				builder.add(component, cc.xy(col, row));
				col += 2;
			}
			row += 2;
		}
	}
}
