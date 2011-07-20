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

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.presentation.DefaultListHolder;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.PropertyListHolder;

@SuppressWarnings("serial")
public class ListPanel extends JPanel {
	private ListHolder<? extends Entity> d_entities;
	private JLabel d_listLabel = new JLabel();
	
	public ListPanel(ListHolder<? extends Entity> entities) {
		super(new BorderLayout());
		d_listLabel.setBackground(null);
		d_listLabel.setOpaque(true);
		
		resetItems(entities);
		refreshItems();
		
		d_entities.addValueChangeListener(new PropertyChangeListener() {	
			public void propertyChange(PropertyChangeEvent evt) {
				refreshItems();
			}
		});
		
		super.add(d_listLabel, BorderLayout.CENTER);
	}
	
	public <T extends Entity> ListPanel(List<T> entityList) {
		this(new DefaultListHolder<T>(entityList));
	}
	
	public <T extends Entity> ListPanel(Object bean, String propertyName, Class<T> objType) {
		this(new PropertyListHolder<T>(bean, propertyName, objType));
	}
	
	public void resetItems(ListHolder<? extends Entity> entities) {
		d_entities = entities;
	}
	
	public void refreshItems() {
		d_listLabel.setText(extractListItems());
	}
	
	private String extractListItems() {
		String listItems = "<html><ul style='list-style-type: circle; padding:0 px; margin:0 px; margin-left:10px;'>";
		for(int i=0; i < d_entities.getValue().size(); i++) {
			listItems += makeListItem(d_entities.getValue().get(i));
		}
		return listItems + "</ul></html>";
	}
	
	private String makeListItem(Entity e) {
		return "<li>" + e.getDescription() + "</li>";
	}
}
