package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.gui.components.EnhancedTableHeader;
import org.drugis.addis.presentation.LabeledPresentationModel;
import org.drugis.addis.presentation.RelativeEffectTableModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;

@SuppressWarnings("serial")
public class RatioTableDialog extends JDialog {
	private RelativeEffectTableModel d_tableModel;

	public RatioTableDialog(JFrame parent, RelativeEffectTableModel model) {
		super(parent, model.getTitle());
		d_tableModel = model;
		initComps();
		setModal(true);
		pack();
	}
	
	private class RatioTableCellRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object val, boolean isSelected, boolean hasFocus, int row, int col) {
			
			JLabel label = BasicComponentFactory.createLabel(((LabeledPresentationModel)val).getLabelModel());
			label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			
			if (((PresentationModel<?>)val).getBean() instanceof PatientGroup) {
				label.setBackground(Color.lightGray);
			} else {
				label.setBackground(Color.white);
			}
			label.setOpaque(true);
			
			if (d_tableModel.getDescriptionAt(row, col) != null) {
				label.setToolTipText(d_tableModel.getDescriptionAt(row, col));
			}
			
			return label;
		}
	}
	
	private void initComps() {
		JTable table = new JTable(d_tableModel);
		table.setDefaultRenderer(Object.class, new RatioTableCellRenderer());
		EnhancedTableHeader.autoSizeColumns(table);
		
		JLabel description = new JLabel(d_tableModel.getDescription());
		
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(table, BorderLayout.CENTER);
		tablePanel.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(7, 7, 7, 7),
						BorderFactory.createMatteBorder(1, 1, 0, 0, Color.gray)));
		
		
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
		
		panel.add(description, BorderLayout.NORTH);
		panel.add(tablePanel, BorderLayout.CENTER);		
		panel.add(closeButton, BorderLayout.SOUTH);		
		
		setContentPane(panel);
	}

}
