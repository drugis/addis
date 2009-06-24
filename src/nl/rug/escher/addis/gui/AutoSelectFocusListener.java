package nl.rug.escher.addis.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class AutoSelectFocusListener implements FocusListener {
	
	private JTextField d_textField;
	
	public static void add(JTextField field) {
		AutoSelectFocusListener l = new AutoSelectFocusListener(field);
		field.addFocusListener(l);
	}

	private AutoSelectFocusListener(JTextField textField) {
		d_textField = textField;
	}
	
	public void focusGained(FocusEvent e) {
		d_textField.setCaretPosition(0);
		if (d_textField.getText()!=null) {
			d_textField.moveCaretPosition(d_textField.getText().length() );
		}
	}

	public void focusLost(FocusEvent e) {
	}

}
