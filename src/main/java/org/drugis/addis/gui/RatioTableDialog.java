package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.drugis.addis.presentation.LabeledPresentationModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;

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
	
	private class RatioTableCellRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object val, boolean isSelected, boolean hasFocus, int row, int col) {
			return BasicComponentFactory.createLabel(((LabeledPresentationModel)val).getLabelModel());
		}
	}
	
	private void initComps() {
		JTable table = new JTable(d_tableModel);
		table.setDefaultRenderer(Object.class, new RatioTableCellRenderer());
		
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
