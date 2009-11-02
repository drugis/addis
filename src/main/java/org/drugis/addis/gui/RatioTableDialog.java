package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class RatioTableDialog extends JDialog {
	private TableModel d_tableModel;

	public RatioTableDialog(JFrame parent, TableModel model) {
		super(parent);
		d_tableModel = model;
		initComps();
		setModal(true);
		pack();
	}
	
	private void initComps() {
		JTable table = new JTable(d_tableModel);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JButton closeButton = new JButton("Close");
		closeButton.setMnemonic('c');
		closeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				dispose();
			}
		});
		
		panel.add(table, BorderLayout.CENTER);		
		panel.add(closeButton, BorderLayout.SOUTH);		
		
		setContentPane(panel);
	}

}
