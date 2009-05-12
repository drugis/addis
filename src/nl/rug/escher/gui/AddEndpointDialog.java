package nl.rug.escher.gui;


import javax.swing.JFrame;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.Endpoint;

import com.jgoodies.binding.PresentationModel;

public class AddEndpointDialog extends OkCancelDialog {
	private Domain d_domain;
	private Endpoint d_endpoint;
	
	public AddEndpointDialog(JFrame frame, Domain domain) {
		super(frame, "Add Endpoint");
		d_domain = domain;
		d_endpoint = new Endpoint();
		EndpointView view = new EndpointView(new PresentationModel<Endpoint>(d_endpoint));
		getUserPanel().add(view.buildPanel());
		pack();
	}

	protected void cancel() {
		setVisible(false);
	}

	protected void commit() {
		d_domain.addEndpoint(d_endpoint);
		System.out.println("Endpoints: " + d_domain.getEndpoints());
		System.out.println("Study " + d_domain.getStudies().get(0) + " has " +
				d_domain.getStudies().get(0).getEndpoints());
		setVisible(false);
	}
}
