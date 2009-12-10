package org.drugis.addis.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
		EntityTableModel<T> etm = new EntityTableModel<T>(d_entities, d_characteristics);
		final JTable table = new StudyTable(etm);
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					int row = table.getSelectedRow();
					Entity en = d_entities.get(row).getBean();
					d_main.deleteEntity(en);
				}
			}
		});

		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(200, 250));		
		sp.setBorder(BorderFactory.createEmptyBorder());
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		setLayout(new BorderLayout());
		add(sp, BorderLayout.NORTH);
	}
	
}
