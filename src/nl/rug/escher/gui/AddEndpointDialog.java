package nl.rug.escher.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import nl.rug.escher.entities.Endpoint;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.ButtonBarBuilder2;

public class AddEndpointDialog extends JDialog {
	public AddEndpointDialog() {
		EndpointView view = new EndpointView(new PresentationModel<Endpoint>(new Endpoint()));
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
		JOptionPane.showMessageDialog(this, "Adding.");
		setVisible(false);
		// TODO: add to data model
	}
}
