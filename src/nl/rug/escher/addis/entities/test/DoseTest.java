package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.*;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.SIUnit;

import org.junit.Test;

public class DoseTest {
	@Test
	public void testSetUnit() {
		Helper.testSetter(new Dose(), Dose.PROPERTY_UNIT, null, SIUnit.MILLIGRAMS_A_DAY);
	}
	
	@Test
	public void testSetQuantity() {
		Helper.testSetter(new Dose(), Dose.PROPERTY_QUANTITY, null, 40.0);
	}
	
	@Test
	public void testToString() {
		Dose d = new Dose();
		assertEquals("INCOMPLETE", d.toString());
		d.setQuantity(25.5);
		d.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		assertEquals("25.5 " + SIUnit.MILLIGRAMS_A_DAY.toString(), d.toString());
	}
}
