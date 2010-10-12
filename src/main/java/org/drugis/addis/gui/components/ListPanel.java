package org.drugis.addis.gui.components;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.presentation.DefaultListHolder;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.PropertyListHolder;

@SuppressWarnings("serial")
public class ListPanel extends JPanel {
	private ListHolder<? extends Entity> d_entities;
	private JLabel d_listLabel = new JLabel();
	
	public ListPanel(ListHolder<? extends Entity> entities) {
		super(new BorderLayout());
		d_listLabel.setBackground(null);
		d_listLabel.setOpaque(true);
		
		resetItems(entities);
		refreshItems();
		
		d_entities.addValueChangeListener(new PropertyChangeListener() {	
			public void propertyChange(PropertyChangeEvent evt) {
				refreshItems();
			}
		});
		
		super.add(d_listLabel, BorderLayout.CENTER);
	}
	
	public <T extends Entity> ListPanel(List<T> entityList) {
		this(new DefaultListHolder<T>(entityList));
	}
	
	public <T extends Entity> ListPanel(Object bean, String propertyName, Class<T> objType) {
		this(new PropertyListHolder<T>(bean, propertyName, objType));
	}
	
	public void resetItems(ListHolder<? extends Entity> entities) {
		d_entities = entities;
	}
	
	public void refreshItems() {
		d_listLabel.setText(extractListItems());
	}
	
	private String extractListItems() {
		String listItems = "<html><ul>";
		for(int i=0; i < d_entities.getValue().size(); i++) {
			listItems += makeListItem(d_entities.getValue().get(i));
		}
		return listItems + "</ul></html>";
	}
	
	private String makeListItem(Entity e) {
		return "<li>" + e.toString() + "</li>";
	}
}
