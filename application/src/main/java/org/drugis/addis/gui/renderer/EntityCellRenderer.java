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

package org.drugis.addis.gui.renderer;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang.StringUtils;
import org.drugis.addis.entities.Entity;

public class EntityCellRenderer implements TableCellRenderer {
	private final TableCellRenderer d_defaultRenderer;

	public EntityCellRenderer(TableCellRenderer defaultRenderer) {
		d_defaultRenderer = defaultRenderer;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		return d_defaultRenderer.getTableCellRendererComponent(table, getDescription(value, false), isSelected, hasFocus, row, column);
	}

	private String getDescription(Object value, boolean nested) {
		if (value instanceof Entity) {
			return ((Entity)value).getLabel();
		}
		if (value instanceof Collection) {
			return getElementDescriptions((Collection<?>) value, nested);
		}
		return value == null ? "N/A" : value.toString();
	}

	private String getElementDescriptions(Collection<?> c, boolean nested) {
		List<String> desc = new ArrayList<String>();
		for (Object o : c) {
			desc.add(getDescription(o, true));
		}
		String str = StringUtils.join(desc, ", ");
		return nested ? ("[" + str + "]") : str;
	}

	public static void insertEntityRenderer(JTable table) {
		final TableCellRenderer defaultRenderer = table.getDefaultRenderer(Object.class);
		EntityCellRenderer renderer = new EntityCellRenderer(defaultRenderer);
		table.setDefaultRenderer(Object.class, renderer);
		// Entity is an interface, and if something returns a sub-interface of Entity, that is not a sub-type of Object
		// Hence, we have to attach the renderer to Entity.class as well.
		table.setDefaultRenderer(Entity.class, renderer);
	}
}