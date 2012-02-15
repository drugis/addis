/**
 * 
 */
package org.drugis.addis.gui.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.gui.AddisWindow;

public class EntityTableDoubleClickListener extends MouseAdapter {
	private final AddisWindow d_main;

	public EntityTableDoubleClickListener(AddisWindow main) {
		d_main = main;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
			JTable table = (JTable)e.getComponent();
			int row = table.convertRowIndexToModel(table.rowAtPoint(e.getPoint()));
			Entity entity = (Entity) table.getModel().getValueAt(row, 0);
			d_main.leftTreeFocus(entity);
		}
	}
}