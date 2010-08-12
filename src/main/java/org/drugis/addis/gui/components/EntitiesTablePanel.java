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

package org.drugis.addis.gui.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.EntityTableModel;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.PresentationModelFactory;

@SuppressWarnings("serial")
public class EntitiesTablePanel extends TablePanel {
	private final ListHolder<? extends Entity> d_entities;

	public EntitiesTablePanel(List<String> formatter, ListHolder<? extends Entity> entities, final Main parent, PresentationModelFactory pmf) {
		super(new EnhancedTable(new EntityTableModel(entities, formatter, pmf)));
		d_entities = entities;
				
		if (parent != null)
			getTable().addKeyListener(new EntityTableDeleteListener(parent));
		
		getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int row = ((EnhancedTable)e.getComponent()).rowAtPoint(e.getPoint());
					Entity entity = d_entities.getValue().get(row);
					parent.leftTreeFocus(entity);
				}
			}
		});
	}
}
