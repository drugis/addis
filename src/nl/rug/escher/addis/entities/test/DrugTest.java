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
	
	@Test
	public void testEquals() {
		Drug d1 = new Drug("Paroxetine");
		Drug d2 = new Drug("Paroxetine");
		Drug d3 = new Drug("Fluoxetine");
		
		assertTrue(d1.equals(d2));
		assertFalse(d1.equals(d3));
	}
	
	@Test
	public void testHashCode() {
		Drug d1 = new Drug("Paroxetine");
		Drug d2 = new Drug("Paroxetine");
		assertEquals(d1.hashCode(), d2.hashCode());
	}
}
