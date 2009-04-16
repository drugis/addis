package nl.rug.escher.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.Endpoint;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;

public class AddEndpointDialog extends JDialog {
	private Domain d_domain;
	private Endpoint d_endpoint;
	
	public AddEndpointDialog(JFrame frame, Domain domain) {
		super(frame, "Add Endpoint");
		d_domain = domain;
		d_endpoint = new Endpoint();
		EndpointView view = new EndpointView(new PresentationModel<Endpoint>(d_endpoint));
		setContentPane(createPanel(view));
		pack();
	}

	private JComponent createPanel(EndpointView view) {
		JPanel panel = new JPanel(new BorderLayout());
		
		JComponent viewPanel = view.buildPanel();
		panel.add(viewPanel, BorderLayout.CENTER);
		
		JButton okButton = createOkButton();
		JButton cancelButton = createCancelButton();
		ButtonBarBuilder2 builder = new ButtonBarBuilder2();
		builder.addGlue();
		builder.addButton(okButton);
		builder.addButton(cancelButton);
		
		panel.add(builder.getPanel(), BorderLayout.SOUTH);
	
		return panel;
	}

	private JButton createCancelButton() {
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				cancel();
			}
		});
		return cancelButton;
	}
	
	private void cancel() {
		setVisible(false);
	}

	private JButton createOkButton() {
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				commit();
			}
		});
		return okButton;
	}
	
	private void commit() {
		d_domain.addEndpoint(d_endpoint);
		System.out.println("Endpoints: " + d_domain.getEndpoints());
		setVisible(false);
	}
}
