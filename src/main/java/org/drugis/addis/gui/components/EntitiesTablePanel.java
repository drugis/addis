package org.drugis.addis.gui.components;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.EntityTableModel;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class EntitiesTablePanel<T extends Entity> extends JPanel {

	private List<String> d_characteristics;
	private List<PresentationModel<T>> d_entities;
	private Main d_main;
	
	public EntitiesTablePanel(List<String> formatter, List<PresentationModel<T>> entities, Main parent) {
		d_characteristics = formatter;
		d_entities = entities;
		d_main = parent;
				
		createComponents();
	}
	
	private void createComponents() {
		final EntityTableModel<T> etm = new EntityTableModel<T>(d_entities, d_characteristics);
		final JTable table = new StudyTable(etm);
		table.addKeyListener(new EntityTableDeleteListener(d_main));

		JScrollPane sp = new JScrollPane(table);		
		sp.setBorder(BorderFactory.createEmptyBorder());
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		setLayout(new BorderLayout());
		add(sp, BorderLayout.NORTH);
	}
	
}
