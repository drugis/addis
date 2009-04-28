package nl.rug.escher.entities.test;

import nl.rug.escher.entities.Drug;

import org.junit.Test;

public class DrugTest {
	@Test
	public void testSetUnit() {
		Helper.testSetter(new Drug(), Drug.PROPERTY_NAME, null, "Paroxetine");
	}
}
