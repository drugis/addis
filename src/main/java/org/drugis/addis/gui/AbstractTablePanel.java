/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.drugis.addis.gui.components.EnhancedTableHeader;
import org.drugis.addis.presentation.TableModelWithDescription;

@SuppressWarnings("serial")
public class AbstractTablePanel extends JPanel {

	protected TableModel d_tableModel;
	protected JTable d_table;
	private JScrollPane d_scroll;
	private int d_maxWidth;

	public AbstractTablePanel(TableModel tableModel) {
		super();
		d_tableModel = tableModel;
		d_table = new JTable(d_tableModel);
		d_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setRenderer();
		initComps();
	}
	
	public void setRenderer() {
		d_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer());
		EnhancedTableHeader.autoSizeColumns(d_table);
	}
	
	public void setMaxWidth(int width) {
		d_maxWidth = width;
	}
	
	protected void initComps() {
		JPanel d_rootPanel = new JPanel();
		d_rootPanel.setLayout(new BorderLayout());
		
		if (d_tableModel instanceof TableModelWithDescription)
			d_rootPanel.add(new JLabel(((TableModelWithDescription) d_tableModel).getDescription()), BorderLayout.NORTH);
		
		EnhancedTableHeader.autoSizeColumns(d_table);
	
		d_scroll = new JScrollPane(d_table);
		d_scroll.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(7, 7, 7, 7),
						BorderFactory.createMatteBorder(1, 1, 0, 0, Color.gray)));
		d_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		d_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		d_table.setPreferredScrollableViewportSize(new Dimension(500, d_table.getPreferredSize().height ));
		
		d_table.setBackground(Color.WHITE);
		
		d_rootPanel.add(d_scroll, BorderLayout.CENTER);			
		this.add(d_rootPanel, BorderLayout.NORTH); 
		//this.add(d_scroll, BorderLayout.NORTH);
	}

	class DefaultTableCellRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object val, boolean isSelected, boolean hasFocus, int row, int col) {
		
			JLabel label = new JLabel(val.toString());
			label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			
			return label;
		}
	}
		
	public void doLayout() {
		super.doLayout();
		EnhancedTableHeader.autoSizeColumns(d_table, 350);
		d_table.setPreferredScrollableViewportSize(new Dimension(d_table.getPreferredSize().width, d_table.getPreferredSize().height ));
		if (d_maxWidth != 0 && d_table.getPreferredSize().width > d_maxWidth)
			d_scroll.setPreferredSize(new Dimension(Math.min(d_table.getPreferredSize().width, d_maxWidth), d_table.getPreferredSize().height + 35));
	}
}