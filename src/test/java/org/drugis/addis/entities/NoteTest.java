package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;

import org.drugis.common.JUnitUtil;
import org.junit.Test;


public class NoteTest {
	@Test
	public void testSetText() {
		JUnitUtil.testSetter(new Note(Source.CLINICALTRIALS), Note.PROPERTY_TEXT, "", "testText");
	}
	
	@Test
	public void testToString() {
		Note n = new Note(Source.CLINICALTRIALS, "test text");
		assertEquals("test text", n.toString());
	}

	@Test
	public void testGetSource() {
		Note n = new Note(Source.CLINICALTRIALS, "test");
		assertEquals(Source.CLINICALTRIALS, n.getSource());
	}
}
