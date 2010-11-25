package org.drugis.addis.gui.components;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class AddisTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = -8961793373881538352L;
	
	public AddisTabbedPane() {
		super();
		setOpaque(true);
	}

	@Override
	public void addTab(String title, Component component) {
		super.addTab(title, encapsulate(component));
	}


	@Override
	public void addTab(String title, Icon icon, Component component) {
		super.addTab(title, icon, encapsulate(component));
	}
	
	@Override
	public void addTab(String title, Icon icon, Component component, String tip) {
		super.addTab(title, icon, encapsulate(component), tip);
	}
	
	private JScrollPane encapsulate(Component panel) {
		return new AddisScrollPane(panel);
	}
}
