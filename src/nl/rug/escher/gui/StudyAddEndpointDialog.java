package nl.rug.escher.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.Measurement;
import nl.rug.escher.entities.Study;

import com.jgoodies.binding.PresentationModel;

public class StudyAddEndpointDialog extends OkCancelDialog {
	private Domain d_domain;
	private Study d_study;
	private EndpointHolder d_newEndpoint;
	private List<Measurement> d_measurements;
	
	public StudyAddEndpointDialog(JFrame frame, Domain domain, Study study) {
		super(frame, "Add Endpoint to Study");
		d_domain = domain;
		d_study = study;
		d_measurements = new ArrayList<Measurement>();
		d_newEndpoint = new EndpointHolder();
		final StudyAddEndpointView view = new StudyAddEndpointView(d_domain, d_study,
				new PresentationModel<EndpointHolder>(d_newEndpoint), d_measurements);
		
		
		d_newEndpoint.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				initPanel(view);
			}
		});
		initPanel(view);
	}

	private void initPanel(StudyAddEndpointView view) {
		d_measurements.clear();
		getUserPanel().removeAll();
		getUserPanel().add(view.buildPanel());
		pack();
	}

	protected void cancel() {
		setVisible(false);
	}

	protected void commit() {
		setVisible(false);
		
		addEndpointToMeasurements();
		addMeasurementsToPatientGroups();
		addEndpointToStudy();
	}

	private void addMeasurementsToPatientGroups() {
		for (Measurement m : d_measurements) {
			m.getPatientGroup().addMeasurement(m);
		}
	}

	private void addEndpointToStudy() {
		d_study.addEndpoint(d_newEndpoint.getEndpoint());
	}

	private void addEndpointToMeasurements() {
		for (Measurement m : d_measurements) {
			m.setEndpoint(d_newEndpoint.getEndpoint());
		}
	}
}
