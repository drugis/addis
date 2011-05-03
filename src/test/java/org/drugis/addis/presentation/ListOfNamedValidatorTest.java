package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Arm;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class ListOfNamedValidatorTest {
	private ObservableList<Arm> d_list;
	private ListOfNamedValidator<Arm> d_validator;

	@Before
	public void setUp() {
		d_list = new ArrayListModel<Arm>();
		d_list.add(new Arm("My arm!", 0));
		d_validator = new ListOfNamedValidator<Arm>(d_list, 2);
	}
	
	@Test
	public void testMinElements() {
		assertFalse(d_validator.getValue());
		d_list.add(new Arm("His arm!", 0));
		assertTrue(d_validator.getValue());
	}
	
	@Test
	public void testAddElementsFiresChange() {
		PropertyChangeListener mockListener = JUnitUtil.mockListener(d_validator, "value", null, Boolean.TRUE);
		d_validator.addPropertyChangeListener(mockListener);
		d_list.add(new Arm("His arm!", 0));
		verify(mockListener);
		d_validator.removeValueChangeListener(mockListener);
		
		mockListener = JUnitUtil.mockListener(d_validator, "value", null, Boolean.FALSE);
		d_validator.addPropertyChangeListener(mockListener);
		d_list.remove(0);
		verify(mockListener);
	}
	
	@Test
	public void testNamesShouldBeUniqueAndNotEmpty() {
		Arm arm = new Arm("My arm!", 0);
		d_list.add(arm);
		assertFalse(d_validator.getValue());
		arm.setName("His Arm!");
		assertTrue(d_validator.getValue());
		arm.setName("");
		assertFalse(d_validator.getValue());
	}
	
	@Test
	public void testNameChangeFiresChange() {
		Arm arm = new Arm("My arm!", 0);
		d_list.add(arm);

		PropertyChangeListener mockListener = JUnitUtil.mockListener(d_validator, "value", null, Boolean.TRUE);
		d_validator.addPropertyChangeListener(mockListener);
		arm.setName("His Arm!");
		verify(mockListener);
		d_validator.removeValueChangeListener(mockListener);
		
		// also test listening to elements initially in the list.
		d_validator = new ListOfNamedValidator<Arm>(d_list, 2);
		mockListener = JUnitUtil.mockListener(d_validator, "value", null, Boolean.FALSE);
		d_validator.addPropertyChangeListener(mockListener);
		d_list.get(0).setName("His Arm!");
		verify(mockListener);
		d_validator.removeValueChangeListener(mockListener);
	}
}
