package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import org.drugis.addis.gui.components.EnhancedTableHeader;
import org.drugis.addis.plot.BinnedScale;
import org.drugis.addis.plot.IdentityScale;
import org.drugis.addis.plot.RelativeEffectPlot;
import org.drugis.addis.presentation.LabeledPresentationModel;
import org.drugis.addis.presentation.RelativeEffectRatePresentation;
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
	
	private class RatioTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
		JDialog dialog;
		int d_row;
		int d_column;
		
		public RatioTableCellEditor(Dialog parent) {
			//construct dialog
			dialog = new JDialog(parent, "Relative Effect plot");
		}
		
		public Component getTableCellEditorComponent(JTable arg0, Object arg1,
				boolean arg2, int row, int column) {
		
			JButton button = new JButton();
			d_row = row;
			d_column = column;
			button.addActionListener(this);
			return button;
		}

		public Object getCellEditorValue() {
			return null;
		}

		public void actionPerformed(ActionEvent arg0) {
			// FIXME: THIS DOESN'T do anything at all yet
			Canvas canvas = new Canvas() {
				public void paint (Graphics g) {
					RelativeEffectPlot plot = new RelativeEffectPlot(new BinnedScale(new IdentityScale(), 0, 200), 11, ((RelativeEffectRatePresentation)d_tableModel.getValueAt(d_row, d_column)).getBean());
					plot.paint((Graphics2D) g);
				}
			};

			canvas.setPreferredSize(new Dimension(201, 21));
			dialog.add(canvas);
			dialog.pack();
			dialog.setVisible(true);
			fireEditingStopped();
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
