package org.drugis.addis.gui.builder;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.DefaultStudyListPresentationModel;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.layout.FormLayout;

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
			StudyTablePanelView d_studyView = new StudyTablePanelView(new DefaultStudyListPresentationModel(new StudyListHolder(d_domain)), d_frame);
			studiesComp = d_studyView.buildPanel();
		}
		return studiesComp;
	}	

	@SuppressWarnings("serial")
	private class StudyListHolder extends AbstractListHolder<Study> implements DomainListener {
		
		Domain d_domain;
		private List<Study> d_holderStudies;

		StudyListHolder(Domain d) {
			d_domain = d;
			updateList();
			d.addListener(this);				
		}

		private void updateList() {
			List<Study> oldStudies = d_holderStudies;
			d_holderStudies = new ArrayList<Study>();
			d_holderStudies.addAll(d_domain.getStudies());
			firePropertyChange("value", oldStudies, d_holderStudies);
		}
		
		@Override
		public List<Study> getValue() {
			return d_holderStudies;
		}

		public void analysesChanged() {
			updateList();
		}

		public void drugsChanged() {
			updateList();
		}

		public void endpointsChanged() {
			updateList();
		}

		public void indicationsChanged() {
			updateList();	
		}

		public void studiesChanged() {
			updateList();	
		}
		
	}
}
