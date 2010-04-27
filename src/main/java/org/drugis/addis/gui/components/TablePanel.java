package org.drugis.addis.gui.components;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class TablePanel extends JPanel {
	
	protected JTable d_table;

	public TablePanel() {
		super(new BorderLayout());
	}
	
	public TablePanel(JTable table) {
		super(new BorderLayout());
		init(table);
	}
	
	public void init(JTable table) {
		d_table = table;
		addScrollPane();
	}
	
	public JTable getTable() {
		return d_table;
	}

	private void addScrollPane() {
		JScrollPane sp = new JScrollPane(d_table);		
		sp.setBorder(BorderFactory.createEmptyBorder());
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.getVerticalScrollBar().setUnitIncrement(16);
	
		add(sp, BorderLayout.NORTH);
	}

}
