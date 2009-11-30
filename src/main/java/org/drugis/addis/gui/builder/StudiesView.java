package org.drugis.addis.gui.builder;

import javax.swing.JComponent;
import javax.swing.JLabel;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.DefaultStudyListPresentationModel;
import org.drugis.common.gui.ViewBuilder;

public class StudiesView implements ViewBuilder {

	private Main d_frame;
	private Domain d_domain;
	
	public StudiesView (Main main, Domain d) {
		d_frame = main;
		d_domain = d;
	}
	
	public JComponent buildPanel() {
		return getStudiesComp();
	}
	
	private JComponent getStudiesComp() {
		JComponent studiesComp = null;
		if(d_domain.getStudies().isEmpty()) {
			studiesComp = new JLabel("No studies found.");
		} else {
			StudyTablePanelView d_studyView = new StudyTablePanelView(new DefaultStudyListPresentationModel(d_domain.getStudiesHolder()), d_frame);
			studiesComp = d_studyView.buildPanel();
		}
		return studiesComp;
	}	
}
