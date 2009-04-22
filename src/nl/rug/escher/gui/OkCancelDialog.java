package nl.rug.escher.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.ButtonBarBuilder2;

public abstract class OkCancelDialog extends JDialog {

	public OkCancelDialog() {
		super();
	}

	public OkCancelDialog(Frame owner) {
		super(owner);
	}

	public OkCancelDialog(Dialog owner) {
		super(owner);
	}

	public OkCancelDialog(Window owner) {
		super(owner);
	}

	public OkCancelDialog(Frame owner, boolean modal) {
		super(owner, modal);
	}

	public OkCancelDialog(Frame owner, String title) {
		super(owner, title);
	}

	public OkCancelDialog(Dialog owner, boolean modal) {
		super(owner, modal);
	}

	public OkCancelDialog(Dialog owner, String title) {
		super(owner, title);
	}

	public OkCancelDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
	}

	public OkCancelDialog(Window owner, String title) {
		super(owner, title);
	}

	public OkCancelDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	public OkCancelDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	public OkCancelDialog(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
	}

	public OkCancelDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
	}

	public OkCancelDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
	}

	public OkCancelDialog(Window owner, String title,
			ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
	}

	protected abstract void commit();

	protected abstract void cancel();

	protected JComponent createPanel(ViewBuilder view) {
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

	private JButton createOkButton() {
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				commit();
			}
		});
		return okButton;
	}

}