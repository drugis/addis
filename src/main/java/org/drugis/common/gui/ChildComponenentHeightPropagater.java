/**
 * 
 */
package org.drugis.common.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JTabbedPane;

public class ChildComponenentHeightPropagater extends ComponentAdapter {
	private final Container d_panel;

	private ChildComponenentHeightPropagater(Container panel) {
		d_panel = panel;

	}

	@Override
	public void componentResized(ComponentEvent e) {
		JTabbedPane pane = (JTabbedPane) d_panel.getParent();
		pane.setVisible(false);
		
		int total = 0;
		for (Component c : d_panel.getComponents()) {
			total += c.getSize().height;
		}
		int spacing = 7; //FIXME: Get from jgoodies.
		int height = total + (d_panel.getComponentCount() - 1) * spacing;
		int width = d_panel.getPreferredSize().width;
		d_panel.setSize(new Dimension(width, height));
		d_panel.setPreferredSize(new Dimension(width, height));
		
		pane.setPreferredSize(new Dimension(width, height + 40)); // FIXME: magic numbers
		pane.setVisible(true);
	}
	
	public static void attachToContainer(Container panel) {
		ChildComponenentHeightPropagater prop = new ChildComponenentHeightPropagater(panel);
		for (Component c : panel.getComponents()) {
			c.addComponentListener(prop);
		}
	}
}