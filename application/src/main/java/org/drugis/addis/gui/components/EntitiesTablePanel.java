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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.presentation.EntityTableModel;
import org.drugis.addis.presentation.PresentationModelFactory;

import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class EntitiesTablePanel extends TablePanel {
	private final ObservableList<? extends Entity> d_entities;

	public EntitiesTablePanel(List<String> formatter, ObservableList<? extends Entity> observableList, final AddisWindow parent, PresentationModelFactory pmf) {
		super(createTable(formatter, observableList, pmf));
		d_entities = observableList;
				
		if (parent != null)
			getTable().addKeyListener(new EntityTableDeleteListener(parent));
		
		getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int row = ((EnhancedTable)e.getComponent()).rowAtPoint(e.getPoint());
					Entity entity = d_entities.get(row);
					parent.leftTreeFocus(entity);
				}
			}
		});
	}

	private static EnhancedTable createTable(List<String> formatter, ObservableList<? extends Entity> observableList, PresentationModelFactory pmf) {
		EnhancedTable table = EnhancedTable.createWithSorter(new EntityTableModel(observableList, formatter, pmf));
		EnhancedTable.insertEntityRenderer(table);
		table.autoSizeColumns();
		return table;
	}
}
