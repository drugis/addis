package nl.rug.escher.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.Measurement;
import nl.rug.escher.entities.PatientGroup;
import nl.rug.escher.entities.Study;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;

public class AddStudyDialog extends OkCancelDialog {
	private Domain d_domain;
	private Study d_study;
	private StudyView d_view;
	
	public AddStudyDialog(JFrame frame, Domain domain) {
		super(frame, "Add Endpoint");
		d_domain = domain;
		d_study = new Study();
		d_view = new StudyView(new PresentationModel<Study>(d_study), domain);
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
		PatientGroup group = new PatientGroup();
		Measurement m = new Measurement();
		m.setMean(0.0);
		m.setStdDev(0.0);
		group.addMeasurement(m);
		d_study.addPatientGroup(group);
		initUserPanel();
	}

	@Override
	protected void cancel() {
		setVisible(false);
	}

	@Override
	protected void commit() {
		d_domain.addStudy(d_study);
		setVisible(false);
	}
}