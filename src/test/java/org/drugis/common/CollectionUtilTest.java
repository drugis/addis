package org.drugis.common;

import static org.junit.Assert.*;

import java.util.SortedSet;
import java.util.TreeSet;


import org.drugis.common.CollectionUtil;
import org.junit.Before;
import org.junit.Test;

public class CollectionUtilTest {
	private SortedSet<String> d_set;
	
	@Before
	public void setUp() {
		d_set = new TreeSet<String>();
		d_set.add("B");
		d_set.add("A");
	}

	@Test
	public void testGetElementAtIndex() {
		assertEquals("A", CollectionUtil.getElementAtIndex(d_set, 0));
		assertEquals("B", CollectionUtil.getElementAtIndex(d_set, 1));
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testGetElementAtIndexTooHigh() {
		CollectionUtil.getElementAtIndex(d_set, 2);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testGetElementAtIndexNegative() {
		CollectionUtil.getElementAtIndex(d_set, -1);
	}
	
	@Test
	public void testGetIndexOfElement() {
		assertEquals(0, CollectionUtil.getIndexOfElement(d_set, "A"));
		assertEquals(1, CollectionUtil.getIndexOfElement(d_set, "B"));
		assertEquals(-1, CollectionUtil.getIndexOfElement(d_set, "C"));
	}
}
