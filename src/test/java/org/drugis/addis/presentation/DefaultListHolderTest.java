package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class DefaultListHolderTest {

	private DefaultListHolder<String> d_holder;

	@Before
	public void setUp() {
		List<String> list = new ArrayList<String>();
		d_holder = new DefaultListHolder<String>(list);
	}
	
	@Test
	public void testSetValue() {
		List<String> list = new ArrayList<String>();
		list.add("sss");
		JUnitUtil.testSetter(d_holder, AbstractValueModel.PROPERTYNAME_VALUE, d_holder.getValue(), list);
	}
}
