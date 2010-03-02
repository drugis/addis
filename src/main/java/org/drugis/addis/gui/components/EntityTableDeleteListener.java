package org.drugis.addis.gui.components;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.gui.Main;

public class EntityTableDeleteListener extends KeyAdapter {
	
	private Main d_main;

	public EntityTableDeleteListener(Main main) {
		d_main = main;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			EnhancedTable studyTable = (EnhancedTable)e.getComponent();
			int row = studyTable.getSelectedRow();

			Entity en = (Entity) studyTable.getModel().getValueAt(row, 0);
			d_main.deleteEntity(en);
		}
	}
}
