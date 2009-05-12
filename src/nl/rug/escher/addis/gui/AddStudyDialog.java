package nl.rug.escher.addis.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.Measurement;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.Study;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;

public class AddStudyDialog extends OkCancelDialog {
	private Domain d_domain;
	private Study d_study;
	private EndpointHolder d_primaryEndpoint;
	private AddStudyView d_view;
	
	public AddStudyDialog(JFrame frame, Domain domain) {
		super(frame, "Add Study");
		d_domain = domain;
		d_study = new Study();
		d_primaryEndpoint = new EndpointHolder();
		d_primaryEndpoint.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				buildMeasurements();
				initUserPanel();
			}
		});
		d_view = new AddStudyView(new PresentationModel<Study>(d_study),
				new PresentationModel<EndpointHolder>(d_primaryEndpoint), domain);
		initUserPanel();
	}

	protected void buildMeasurements() {
		for (PatientGroup g : d_study.getPatientGroups()) {
			g.setMeasurements(new ArrayList<Measurement>());
			Measurement m = d_primaryEndpoint.getEndpoint().buildMeasurement();
			g.addMeasurement(m);
		}
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
		if (d_primaryEndpoint.getEndpoint() != null) {
			Measurement m = d_primaryEndpoint.getEndpoint().buildMeasurement();
			group.addMeasurement(m);
		}
		Dose d = new Dose();
		d.setQuantity(0.0);
		group.setDose(d);
		group.setSize(0);
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