package org.drugis.addis.gui.components;

import java.awt.Component;

import javax.swing.JScrollPane;

public class AddisScrollPane extends JScrollPane {
	private static final long serialVersionUID = 1842401085979159347L;
	
	public AddisScrollPane(Component c) {
		super(c);
		getVerticalScrollBar().setUnitIncrement(16);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
}
