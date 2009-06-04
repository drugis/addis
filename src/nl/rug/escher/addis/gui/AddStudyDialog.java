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
import nl.rug.escher.addis.entities.BasicMeasurement;
import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.SIUnit;
import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.common.gui.OkCancelDialog;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;

@SuppressWarnings("serial")
public class AddStudyDialog extends OkCancelDialog {
	private Domain d_domain;
	private BasicStudy d_study;
	private EndpointHolder d_primaryEndpoint;
	private AddStudyView d_view;
	
	public AddStudyDialog(JFrame frame, Domain domain) {
		super(frame, "Add Study");
		d_domain = domain;
		d_study = new BasicStudy("X");
		d_primaryEndpoint = new EndpointHolder();
		d_primaryEndpoint.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				buildMeasurements();
				initUserPanel();
			}
		});
		d_view = new AddStudyView(new PresentationModel<BasicStudy>(d_study),
				new PresentationModel<EndpointHolder>(d_primaryEndpoint), domain);
		initUserPanel();
	}

	protected void buildMeasurements() {
		for (BasicPatientGroup g : d_study.getPatientGroups()) {
			g.setMeasurements(new ArrayList<BasicMeasurement>());
			BasicMeasurement m = d_primaryEndpoint.getEndpoint().buildMeasurement();
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
		BasicPatientGroup group = initializePatientGroup();
		d_study.addPatientGroup(group);
		initUserPanel();
	}

	private BasicPatientGroup initializePatientGroup() {
		BasicPatientGroup group = new BasicPatientGroup(d_study, null,
				new Dose(0.0, SIUnit.MILLIGRAMS_A_DAY), 0);
		if (d_primaryEndpoint.getEndpoint() != null) {
			BasicMeasurement m = d_primaryEndpoint.getEndpoint().buildMeasurement();
			group.addMeasurement(m);
		}
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
		for (BasicPatientGroup g : d_study.getPatientGroups()) {
			g.getMeasurements().get(0).setEndpoint(d_primaryEndpoint.getEndpoint());
		}
	}
}