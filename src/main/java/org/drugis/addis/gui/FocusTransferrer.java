package org.drugis.addis.gui;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class FocusTransferrer implements FocusListener {

	private Component comp;

	public FocusTransferrer(Component comp) {
		this.comp = comp;
	}

	public void focusGained(FocusEvent e) {
		comp.requestFocusInWindow();
	}

	public void focusLost(FocusEvent e) {
	}
}

