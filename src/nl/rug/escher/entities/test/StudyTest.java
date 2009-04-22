package nl.rug.escher.entities.test;

import static org.junit.Assert.*;
import nl.rug.escher.entities.Study;

import org.junit.Test;

public class StudyTest {
	@Test
	public void testSetId() {
		Helper.testSetter(new Study(), Study.PROPERTY_ID, null, "NCT00351273");
	}
	
	@Test
	public void testToString() {
		String id = "NCT00351273";
		Study study = new Study();
		study.setId(id);
		assertEquals(id, study.toString());
	}
}
