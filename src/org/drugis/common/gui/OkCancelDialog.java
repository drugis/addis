package org.drugis.common.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.ButtonBarBuilder2;

@SuppressWarnings("serial")
public abstract class OkCancelDialog extends JDialog {
	private JPanel d_userPanel;
	
	protected JButton d_okButton;
	protected JButton d_cancelButton;

	protected abstract void commit();

	protected abstract void cancel();
	
	private void construct() {
		setContentPane(createPanel());
		pack();
	}

	private JComponent createPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		
		d_userPanel = new JPanel();
		panel.add(d_userPanel, BorderLayout.CENTER);
		
		d_okButton = createOkButton();
		d_cancelButton = createCancelButton();
		ButtonBarBuilder2 builder = new ButtonBarBuilder2();
		builder.addGlue();
		builder.addButton(d_okButton);
		builder.addButton(d_cancelButton);
		
		panel.add(builder.getPanel(), BorderLayout.SOUTH);
	
		return panel;
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

	public OkCancelDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		construct();
	}

	public OkCancelDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		construct();
	}
}