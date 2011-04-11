package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;

import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class ObjectWithNotesTest {

	@Test
	public void testEquals() {
		ObjectWithNotes<String> a = new ObjectWithNotes<String>("A");
		ObjectWithNotes<String> b = new ObjectWithNotes<String>("B");
		JUnitUtil.assertNotEquals(a, b);
		b.setValue("A");
		assertEquals(a, b);
		
		b.getNotes().add(new Note(Source.CLINICALTRIALS, "This used to be B"));
		JUnitUtil.assertNotEquals(a, b);
		a.getNotes().add(new Note(Source.CLINICALTRIALS, "This used to be B"));
		assertEquals(a, b);
	}
	
}
