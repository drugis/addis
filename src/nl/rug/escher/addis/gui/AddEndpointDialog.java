package nl.rug.escher.addis.gui;


import javax.swing.JFrame;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.common.gui.OkCancelDialog;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
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
		setVisible(false);
	}
}
