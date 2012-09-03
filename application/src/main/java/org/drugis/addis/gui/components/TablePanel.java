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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class TablePanel extends JPanel {
	private static final long serialVersionUID = -3818839238577628096L;

	public TablePanel(JTable table) {
		super(new BorderLayout());
		add(createPanel(table), BorderLayout.CENTER);
	}

	private JPanel createPanel(final JTable table) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JScrollPane sp = new JScrollPane(table);
		sp.setBorder(BorderFactory.createEmptyBorder());
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		panel.add(sp);

		final ComponentAdapter scrollPaneSizer = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				final int padding = 2; // FIXME: magic number
				final int margin = 50; // FIXME: magic number

				if (findParent() == null) {
					return;
				}
				int tablewidth = table.getPreferredSize().width + padding;
				int panelwidth = findParent().getSize().width - margin;

				int headerHeight = 0;
				if (table.getTableHeader() != null) {
					headerHeight = table.getTableHeader().getHeight();
				}

				int height = table.getPreferredSize().height  + sp.getHorizontalScrollBar().getHeight() + headerHeight + padding;

				sp.setPreferredSize(new Dimension(Math.min(tablewidth, panelwidth), height));
				sp.revalidate();
			}
		};
		table.addComponentListener(scrollPaneSizer);

		this.addHierarchyListener(new HierarchyListener() {
			public void hierarchyChanged(HierarchyEvent e) {
				if (findParent() != null) {
					findParent().addComponentListener(scrollPaneSizer);
				}
			}
		});

		return panel;
	}

	protected Container findParent() {
		Container p = this;
		while (!(p instanceof JScrollPane) && p != null) {
			p = p.getParent();
		}
		return p;
	}
}
