package org.drugis.common.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import fi.smaa.jsmaa.gui.ViewBuilder;

public abstract class AbstractViewBuilder implements ViewBuilder {
	public class ResizedListener extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {	
			if (d_panel != null && d_parentScrollPane != null) {
				d_panel.setPreferredSize(new Dimension(d_parentScrollPane.getWidth(), d_panel.getHeight()));
				d_panel.setSize(d_panel.getPreferredSize());
			}
		}
	}
	
	protected Container d_parentScrollPane;
	protected JPanel d_panel;
	protected ResizedListener d_resizedListener = null;
	
	
	private void attachResizedListenerToScrollPane() {
		
		d_parentScrollPane = d_panel.getParent();
		if (d_resizedListener == null && d_parentScrollPane != null) {
			d_parentScrollPane.addComponentListener(d_resizedListener = new ResizedListener());
		}
	}
	
	/** 
	 * Resize the panel when its parent, often a Scrollpane viewport, resizes 
	 */
	protected void attachResizedListener(JPanel panel) {
		d_panel = panel;
		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				attachResizedListenerToScrollPane();
			}
		});
	}
}
