package org.drugis.addis.gui.components;

import java.util.List;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.EntityTableModel;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class EntitiesTablePanel<T extends Entity> extends TablePanel {
	public EntitiesTablePanel(List<String> formatter, List<PresentationModel<T>> entities, Main parent) {
		super(new EnhancedTable(new EntityTableModel<T>(entities, formatter)));
				
		getTable().addKeyListener(new EntityTableDeleteListener(parent));
		getTable().setPreferredScrollableViewportSize(d_table.getPreferredSize());
	}
}
