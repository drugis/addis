package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.gui.components.EnhancedTableHeader;
import org.drugis.addis.presentation.LabeledPresentationModel;
import org.drugis.addis.presentation.RelativeEffectTableModel;
import org.drugis.common.gui.GUIHelper;

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
	
	private class RatioTableCellEditor extends AbstractCellEditor implements TableCellEditor {
		private RelativeEffectPlotDialog d_dialog;
		private JDialog d_parent;
		
		public RatioTableCellEditor(JDialog parent) {
			d_parent = parent;
		}
		
		public Component getTableCellEditorComponent(JTable arg0, Object arg1,
				boolean arg2, final int row, final int column) {
		
			JButton button = new JButton();
			button.addActionListener(new ActionListener(){

				@SuppressWarnings("unchecked")
				public void actionPerformed(ActionEvent e) {
					List<RelativeEffect<?>> effectList = new ArrayList<RelativeEffect<?>>();
					effectList.add(((PresentationModel<RelativeEffect<?>>) d_tableModel.getValueAt(row, column)).getBean());
					effectList.add(((PresentationModel<RelativeEffect<?>>) d_tableModel.getValueAt(1,0)).getBean());

					d_dialog = new RelativeEffectPlotDialog(d_parent,
															effectList,
															"Relative Effect plot");
					GUIHelper.centerWindow(d_dialog, d_parent);					
					d_dialog.setVisible(true);
					fireEditingStopped();
				}
			});

			return button;
		}

		public Object getCellEditorValue() {
			return null;
		}

	}
	
	private class EditableTableModel extends AbstractTableModel {
		private TableModel d_nested;
		
		public EditableTableModel(TableModel nested) {
			d_nested = nested;
		}

		public int getColumnCount() {
			return d_nested.getColumnCount();
		}

		public int getRowCount() {
			return d_nested.getRowCount();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return d_nested.getValueAt(rowIndex, columnIndex);
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return rowIndex != columnIndex;
		}
	}
	
	private void initComps() {
		JTable table = new JTable(new EditableTableModel(d_tableModel));
		table.setDefaultRenderer(Object.class, new RatioTableCellRenderer());
		table.setDefaultEditor(Object.class, new RatioTableCellEditor(this));
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
