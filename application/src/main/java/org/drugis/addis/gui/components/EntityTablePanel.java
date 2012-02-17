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

package org.drugis.addis.gui.components;

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableModel;


import org.drugis.addis.entities.Entity;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.presentation.EntityTableModel;
import org.drugis.addis.presentation.PresentationModelFactory;

import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class EntityTablePanel extends TablePanel {
	public EntityTablePanel(Class<? extends Entity> entityType, ObservableList<? extends Entity> observableList, List<String> formatter, final AddisWindow parent, PresentationModelFactory pmf) {
		super(createTable(parent, new EntityTableModel(entityType, observableList, formatter, pmf)));
	}

	public static EnhancedTable createTable(final AddisWindow main, final TableModel model) {
		EnhancedTable table = EnhancedTable.createWithSorter(model);
		EnhancedTable.insertEntityRenderer(table);
		table.autoSizeColumns();

		if (main != null) {
			table.addKeyListener(new EntityTableDeleteListener(main));
			table.addMouseListener(new EntityTableDoubleClickListener(main));
		}
		
		return table;
	}
	
	public static Entity getEntityAt(JTable table, int row) {
		return (Entity) table.getModel().getValueAt(row, findEntityColumn(table));
	}

	private static int findEntityColumn(JTable table) {
		for (int i = 0; i < table.getColumnCount(); ++i) {
			if (table.getColumnName(i).equals("Name") || table.getColumnName(i).equals("Study ID")) {
				return i;
			}
		}
		return -1;
	}
}
