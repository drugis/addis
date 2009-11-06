package org.drugis.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class JUnitUtilTest {
	@Test
	public void testAllAndOnlySameContents() {
		List<String> expected = new ArrayList<String>();
		expected.add("objectA");
		expected.add("objectB");
		expected.add("objectC");
		List<String> actual = new ArrayList<String>();
		actual.add("objectC");
		actual.add("objectA");
		actual.add("objectB");
		JUnitUtil.assertAllAndOnly(expected, actual);
	}
	
	@Test
	public void testAllAndOnlyExpectedLarger() {
		List<String> expected = new ArrayList<String>();
		expected.add("objectA");
		expected.add("objectB");
		expected.add("objectC");
		List<String> actual = new ArrayList<String>();
		actual.add("objectC");
		actual.add("objectB");
		
		String msg = null;
		try {
			JUnitUtil.assertAllAndOnly(expected, actual);
		} catch (AssertionError e) {
			msg = e.getMessage();
		}
		assertEquals("AllAndOnly: actual does not contain the expected.\nexpected = [objectA, objectB, objectC] actual = [objectC, objectB]", msg);
	}
	
	@Test
	public void testAllAndOnlyActualLarger() {
		List<String> expected = new ArrayList<String>();
		expected.add("objectB");
		expected.add("objectC");
		List<String> actual = new ArrayList<String>();
		actual.add("objectC");
		actual.add("objectA");
		actual.add("objectB");
		
		String msg = null;
		try {
			JUnitUtil.assertAllAndOnly(expected, actual);
		} catch (AssertionError e) {
			msg = e.getMessage();
		}
		assertEquals("AllAndOnly: expected does not contain the actual.\nexpected = [objectB, objectC] actual = [objectC, objectA, objectB]", msg);
	}
}
