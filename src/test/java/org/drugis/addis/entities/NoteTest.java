package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;

import org.drugis.common.JUnitUtil;
import org.junit.Test;


public class NoteTest {
	@Test
	public void testSetText() {
		JUnitUtil.testSetter(new Note(), Note.PROPERTY_TEXT, "", "testText");
	}
	
	@Test
	public void testToString() {
		Note n = new Note("test text");
		assertEquals("test text", n.toString());
	}

}
