package org.drugis.addis.gui.test;

import static org.junit.Assert.*;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;


import org.drugis.addis.gui.NotEmptyValidator;
import org.junit.Before;
import org.junit.Test;

public class NotEmptyValidatorTest {
	
	private NotEmptyValidator v;
	private JButton button;
	
	@Before
	public void setUp() {
		button = new JButton("but");
		v = new NotEmptyValidator(button);
	}
	
	@Test
	public void testValidatesCorretly() {
		JTextField f1 = new JTextField("");
		JTextField f2 = new JTextField("");
		JComboBox box = new JComboBox(new Object[] { "x", "y"});
		box.setSelectedIndex(-1);
		v.add(f1);
		v.add(f2);
		v.add(box);
		assertFalse(button.isEnabled());
		f1.setText("jaa");
		assertFalse(button.isEnabled());
		f2.setText("hgmm");
		assertFalse(button.isEnabled());
		box.setSelectedIndex(0);
		assertTrue(button.isEnabled());
		f1.setText("");
		assertFalse(button.isEnabled());
	}

}
