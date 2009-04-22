package nl.rug.escher.gui;


import javax.swing.JFrame;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.Study;

import com.jgoodies.binding.PresentationModel;

public class AddStudyDialog extends OkCancelDialog {
	private Domain d_domain;
	private Study d_study;
	
	public AddStudyDialog(JFrame frame, Domain domain) {
		super(frame, "Add Endpoint");
		d_domain = domain;
		d_study = new Study();
		StudyView view = new StudyView(new PresentationModel<Study>(d_study));
		setContentPane(createPanel(view));
		pack();
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