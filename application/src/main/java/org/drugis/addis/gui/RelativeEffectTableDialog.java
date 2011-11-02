/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.gui.components.EnhancedTableHeader;
import org.drugis.addis.presentation.LabeledPresentation;
import org.drugis.addis.presentation.RelativeEffectPresentation;
import org.drugis.addis.presentation.RelativeEffectTableModel;
import org.drugis.common.gui.GUIHelper;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class RelativeEffectTableDialog extends JDialog {
	private RelativeEffectTableModel d_tableModel;
	private RelativeEffectPlotDialog d_dialog;
	private JDialog d_parentDialog;

	public RelativeEffectTableDialog(Window mainWindow, RelativeEffectTableModel model) {
		super(mainWindow, model.getTitle());
		d_parentDialog = this;
		d_tableModel = model;
		
		initComps();
		setModal(true);
		setResizable(false);
		pack();
	}
	
	
	private class RatioTableCellRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object val, boolean isSelected, boolean hasFocus, int row, int col) {
			
			if (row < 0) {
				return new JLabel("");
			}
			
			JComponent label = null;
			if (val == null) {
				label = new JLabel("MISSING");
			} else if (((PresentationModel<?>)val).getBean() instanceof Arm) {
				label = new JLabel(((PresentationModel<?>)val).getBean().toString());
				label.setBackground(Color.lightGray);
			} else {
				LabeledPresentation labeled = (LabeledPresentation)val;
				AbstractValueModel labelModel = labeled.getLabelModel();
				if(labelModel.getValue() == null) {
					System.out.println("FOO");
				}
				label = BasicComponentFactory.createLabel(labelModel);
				label.setBackground(Color.white);
			}
			label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			label.setOpaque(true);
			
			if (d_tableModel.getDescriptionAt(row, col) != null) {
				label.setToolTipText(d_tableModel.getDescriptionAt(row, col));
			}
			
			return label;
		}
	}
	
	
	private class CellClickedMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			int row = ((JTable)e.getComponent()).rowAtPoint(e.getPoint());
			int col = ((JTable)e.getComponent()).columnAtPoint(e.getPoint());
			
			if (row == col || d_tableModel.getValueAt(row, col) == null ||
					!((RelativeEffectPresentation) d_tableModel.getValueAt(row, col)).getBean().isDefined()) {
				return;
			}
			
			d_dialog = new RelativeEffectPlotDialog(d_parentDialog,
					d_tableModel.getPlotPresentation(row, col),
					"Relative Effect plot");
			GUIHelper.centerWindow(d_dialog, d_parentDialog);					
			d_dialog.setVisible(true);	
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

		JLabel clickLegend = new JLabel("Click on any white cell to display its forest plot.");
		
		JButton closeButton = new JButton("Close");
		closeButton.setMnemonic('c');
		closeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				dispose();
			}
		});
		
		table.addMouseListener(new CellClickedMouseListener());

		JPanel bottomPanel = new JPanel(new BorderLayout());
			
		panel.add(description, BorderLayout.NORTH);
		panel.add(tablePanel, BorderLayout.CENTER);
		bottomPanel.add(clickLegend, BorderLayout.CENTER);
		bottomPanel.add(closeButton, BorderLayout.SOUTH);		
		panel.add(bottomPanel, BorderLayout.SOUTH);
		
		setContentPane(panel);
	}
}
