package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.*;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Test;

public class DrugTest {
	@Test
	public void testSetUnit() {
		JUnitUtil.testSetter(new Drug(), Drug.PROPERTY_NAME, null, "Paroxetine");
	}
	
	@Test
	public void testToString() {
		Drug d = new Drug();
		d.setName("Paroxetine");
		assertEquals("Paroxetine", d.toString());
	}
}
