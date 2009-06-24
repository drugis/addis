package nl.rug.escher.addis.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComboBox;

public class ComboBoxPopupOnFocusListener implements FocusListener {

	private JComboBox box;
	
	public static void add (JComboBox box) {
		ComboBoxPopupOnFocusListener l = new ComboBoxPopupOnFocusListener(box);
		box.addFocusListener(l);
	}

	private ComboBoxPopupOnFocusListener(JComboBox box) {
		this.box = box;
	}

	public void focusGained(FocusEvent arg0) {
		box.showPopup();
	}

	public void focusLost(FocusEvent arg0) {
	}
}
