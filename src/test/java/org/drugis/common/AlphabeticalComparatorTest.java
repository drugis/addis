package org.drugis.common;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class AlphabeticalComparatorTest {
	private AlphabeticalComparator d_comparator;

	@Before
	public void setup(){
		d_comparator = new AlphabeticalComparator();
	}
	
	@Test
	public void testCompareObject(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("c");
		list.add("B");
		list.add("a");
		Collections.sort(list, d_comparator);
		assertEquals(new String("a"), list.get(0));
		assertEquals(new String("B"), list.get(1));
		assertEquals(new String("c"), list.get(2));
	}
}
