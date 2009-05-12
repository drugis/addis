package nl.rug.escher.addis.gui;

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
	private JPanel d_userPanel;

	protected abstract void commit();

	protected abstract void cancel();
	
	private void construct() {
		setContentPane(createPanel());
		pack();
	}

	private JComponent createPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		
		createUserPanel();
		panel.add(d_userPanel, BorderLayout.CENTER);
		
		JButton okButton = createOkButton();
		JButton cancelButton = createCancelButton();
		ButtonBarBuilder2 builder = new ButtonBarBuilder2();
		builder.addGlue();
		builder.addButton(okButton);
		builder.addButton(cancelButton);
		
		panel.add(builder.getPanel(), BorderLayout.SOUTH);
	
		return panel;
	}

	private void createUserPanel() {
		d_userPanel = new JPanel();
	}
	
	protected JPanel getUserPanel() {
		return d_userPanel;
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
	
	public OkCancelDialog() {
		super();
		construct();
	}

	public OkCancelDialog(Frame owner) {
		super(owner);
		construct();
	}

	public OkCancelDialog(Dialog owner) {
		super(owner);
		construct();
	}

	public OkCancelDialog(Window owner) {
		super(owner);
		construct();
	}

	public OkCancelDialog(Frame owner, boolean modal) {
		super(owner, modal);
		construct();
	}

	public OkCancelDialog(Frame owner, String title) {
		super(owner, title);
		construct();
	}

	public OkCancelDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		construct();
	}

	public OkCancelDialog(Dialog owner, String title) {
		super(owner, title);
		construct();
	}

	public OkCancelDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		construct();
	}

	public OkCancelDialog(Window owner, String title) {
		super(owner, title);
		construct();
	}

	public OkCancelDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		construct();
	}

	public OkCancelDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		construct();
	}

	public OkCancelDialog(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		construct();
	}

	public OkCancelDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		construct();
	}

	public OkCancelDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		construct();
	}

	public OkCancelDialog(Window owner, String title,
			ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		construct();
	}
}