package nl.rug.escher.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.Dose;
import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.entities.Measurement;
import nl.rug.escher.entities.PatientGroup;
import nl.rug.escher.entities.Study;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.Model;
import com.jgoodies.forms.builder.ButtonBarBuilder2;

public class AddStudyDialog extends OkCancelDialog {
	public class EndpointHolder extends Model { // TODO: implement as adapter or something?
		private Endpoint d_endpoint;
		public static final String PROPERTY_ENDPOINT = "endpoint";
		public void setEndpoint(Endpoint e) {
			Endpoint oldVal = d_endpoint;
			d_endpoint = e;
			firePropertyChange(PROPERTY_ENDPOINT, oldVal, d_endpoint);
		}
		public Endpoint getEndpoint() {
			return d_endpoint;
		}
		public List<Endpoint> asList() {
			List<Endpoint> list = new ArrayList<Endpoint>();
			if (d_endpoint != null) {
				list.add(d_endpoint);
			}
			return list;
		}
	}
	
	private Domain d_domain;
	private Study d_study;
	private EndpointHolder d_primaryEndpoint;
	private StudyView d_view;
	
	public AddStudyDialog(JFrame frame, Domain domain) {
		super(frame, "Add Endpoint");
		d_domain = domain;
		d_study = new Study();
		d_primaryEndpoint = new EndpointHolder();
		d_view = new StudyView(new PresentationModel<Study>(d_study),
				new PresentationModel<EndpointHolder>(d_primaryEndpoint), domain);
		initUserPanel();
	}

	private void initUserPanel() {
		getUserPanel().removeAll();
		getUserPanel().setLayout(new BorderLayout());
		getUserPanel().add(d_view.buildPanel(), BorderLayout.CENTER);
		getUserPanel().add(buildButtonBar(), BorderLayout.SOUTH);
		pack();
	}

	private JComponent buildButtonBar() {
		ButtonBarBuilder2 builder = new ButtonBarBuilder2();	
		builder.addButton(createAddPatientGroupButton());
		return builder.getPanel();
	}

	private JComponent createAddPatientGroupButton() {
		JButton button = new JButton("Add Patient Group");
		button.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				addPatientGroup();
			}
		});
		return button;
	}

	protected void addPatientGroup() {
		PatientGroup group = initializePatientGroup();
		d_study.addPatientGroup(group);
		initUserPanel();
	}

	private PatientGroup initializePatientGroup() {
		PatientGroup group = new PatientGroup();
		Measurement m = new Measurement();
		m.setMean(0.0);
		m.setStdDev(0.0);
		group.addMeasurement(m);
		Dose d = new Dose();
		d.setQuantity(0.0);
		group.setDose(d);
		return group;
	}

	@Override
	protected void cancel() {
		setVisible(false);
	}

	@Override
	protected void commit() {
		bindEndpoint();
		d_domain.addStudy(d_study);
		setVisible(false);
	}

	private void bindEndpoint() {
		d_study.setEndpoints(d_primaryEndpoint.asList());
		for (PatientGroup g : d_study.getPatientGroups()) {
			g.getMeasurements().get(0).setEndpoint(d_primaryEndpoint.getEndpoint());
		}
	}
}