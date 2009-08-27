package org.drugis.addis.gui;

import java.util.Map;

import javax.swing.JFrame;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.common.gui.OkCancelDialog;

import fi.smaa.common.ImageLoader;

@SuppressWarnings("serial")
public class StudyAddPatientGroupDialog extends OkCancelDialog {

	private Domain d_domain;
	private BasicStudy d_study;
	private StudyAddPatientGroupView d_view;

	public StudyAddPatientGroupDialog(ImageLoader loader, JFrame frame, Domain domain, BasicStudy study) {
		super(frame, "Add Patient Group to Study");
		this.setModal(true);
		d_domain = domain;
		d_study = study;
		d_view = new StudyAddPatientGroupView(loader, d_domain, d_study, d_okButton);
		getUserPanel().removeAll();
		getUserPanel().add(d_view.buildPanel());		
		pack();
	}

	@Override
	protected void cancel() {
		setVisible(false);
	}

	@Override
	protected void commit() {
		BasicPatientGroup pg = d_view.getPatientGroup();
		d_study.addPatientGroup(pg);
		for (Map.Entry<Endpoint, Measurement> entry : d_view.getMeasurements().entrySet()) {
			d_study.setMeasurement(entry.getKey(), pg, entry.getValue());
		}
		setVisible(false);
	}	
}
