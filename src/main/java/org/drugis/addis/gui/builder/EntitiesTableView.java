package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.SortedSet;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.gui.components.StudyTable;
import org.drugis.addis.presentation.EntityTableModel;
import org.drugis.common.gui.ViewBuilder;

public class EntitiesTableView<T extends Entity> implements ViewBuilder {

	private List<String> d_characteristics;
	private SortedSet<T> d_entities;
	private Domain d_domain;
	
	public EntitiesTableView(List<String> formatter, SortedSet<T> entities, Domain d) {
		d_characteristics = formatter;
		d_entities = entities;
		d_domain = d;
	}
	
	public JComponent buildPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		
		EntityTableModel<T> etm = new EntityTableModel<T>(d_entities, d_domain, d_characteristics);
		final JTable table = new StudyTable(etm);
		
		JScrollPane pane = new JScrollPane(table);
		pane.setPreferredSize(new Dimension(200, 250));
		pane.setBorder(BorderFactory.createEmptyBorder());
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		panel.add(pane, BorderLayout.NORTH);
		
		return panel;
	}

}
