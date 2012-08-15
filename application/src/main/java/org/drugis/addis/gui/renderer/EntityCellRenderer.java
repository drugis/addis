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