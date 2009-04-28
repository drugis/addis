package nl.rug.escher.entities.test;

import static org.junit.Assert.*;
import nl.rug.escher.entities.Drug;

import org.junit.Test;

public class DrugTest {
	@Test
	public void testSetUnit() {
		Helper.testSetter(new Drug(), Drug.PROPERTY_NAME, null, "Paroxetine");
	}
	
	@Test
	public void testToString() {
		Drug d = new Drug();
		d.setName("Paroxetine");
		assertEquals("Paroxetine", d.toString());
	}
}
