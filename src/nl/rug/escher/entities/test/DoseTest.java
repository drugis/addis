package nl.rug.escher.entities.test;

import nl.rug.escher.entities.Dose;
import nl.rug.escher.entities.SIUnit;

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
}
