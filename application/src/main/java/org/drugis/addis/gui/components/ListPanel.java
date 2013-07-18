/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.presentation.PropertyListHolder;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class ListPanel extends JPanel {
	private ObservableList<?> d_entities;
	private JLabel d_listLabel = new JLabel();

	public <E> ListPanel(ObservableList<E> entities) {
		super(new BorderLayout());
		d_listLabel.setBackground(null);
		d_listLabel.setOpaque(true);

		d_entities = entities;
		refreshItems();

		d_entities.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				refreshItems();
			}
			public void intervalAdded(ListDataEvent e) {
				refreshItems();
			}
			public void contentsChanged(ListDataEvent e) {
				refreshItems();
			}
		});

		super.add(d_listLabel, BorderLayout.CENTER);
	}

	public <E> ListPanel(Object bean, String propertyName, Class<E> objType) {
		this(new PropertyListHolder<E>(bean, propertyName, objType).getValue());
	}

	public <E> ListPanel(List<E> value) {
		this(new ArrayListModel<E>(value));
	}

	public void refreshItems() {
		d_listLabel.setText(extractListItems());
	}

	private String extractListItems() {
		String listItems = "<html><ul style='list-style-type: circle; padding:0 px; margin:0 px; margin-left:10px;'>";
		for(int i=0; i < d_entities.size(); i++) {
			listItems += makeListItem(d_entities.get(i));
		}
		return listItems + "</ul></html>";
	}

	private String makeListItem(Object obj) {
		return "<li>" + (obj instanceof Entity ? ((Entity)obj).getLabel() : obj.toString()) + "</li>";
	}
}
