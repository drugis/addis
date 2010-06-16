/**
 * 
 */
package org.drugis.common.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ChildComponenentHeightPropagater extends ComponentAdapter {
	private final Container d_panel;

	private ChildComponenentHeightPropagater(Container panel) {
		d_panel = panel;

	}

	@Override
	public void componentResized(ComponentEvent e) {
		int total = 0;
		Container panel = d_panel;
		for (Component c : panel.getComponents()) {
			total += c.getSize().height;
		}
		int spacing = 7;
		int height = total + (panel.getComponentCount() - 1) * spacing;
		int width = panel.getPreferredSize().width;
		panel.setSize(new Dimension(width, height));
		panel.setPreferredSize(new Dimension(width, height));
	}
	
	public static void attachToContainer(Container panel) {
		ChildComponenentHeightPropagater prop = new ChildComponenentHeightPropagater(panel);
		for (Component c : panel.getComponents()) {
			c.addComponentListener(prop);
		}
	}
}