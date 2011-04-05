package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class EpochTest {
	
	private Epoch d_null;
	private Epoch d_randomization;
	private Epoch d_treatment;

	@Before
	public void setUp() throws DatatypeConfigurationException {
		d_null = new Epoch(null, null);
		d_randomization = new Epoch("Randomization", null);
		d_treatment = new Epoch("Main phase", DatatypeFactory.newInstance().newDuration("P42D"));
	}
	
	@Test
	public void testConstruction() throws DatatypeConfigurationException {
		assertEquals(null, d_null.getName());
		assertEquals("Randomization", d_randomization.getName());
		assertEquals("Main phase", d_treatment.getName());
		
		assertEquals(null, d_null.getDuration());
		assertEquals(DatatypeFactory.newInstance().newDuration("P42D"), d_treatment.getDuration());
		
		assertEquals(Collections.emptyList(), d_null.getNotes());
		Note n = new Note(Source.MANUAL, "Zis is a note");
		d_treatment.getNotes().add(n);
		assertEquals(Collections.singletonList(n), d_treatment.getNotes());
	}
	
	@Test
	public void testDependencies() {
		assertEquals(Collections.emptySet(), d_treatment.getDependencies());
	}
	
	@Test
	public void testEquals() throws DatatypeConfigurationException {
		Epoch e = new Epoch("Main phase", DatatypeFactory.newInstance().newDuration("P42D"));
		Epoch e2 = new Epoch("Main phase", null);
		Epoch e3 = new Epoch("Randomization", null);
		assertEquals(e, d_treatment);
		JUnitUtil.assertNotEquals(e2, d_randomization);
		JUnitUtil.assertNotEquals(d_null, d_treatment);
		assertEquals(e3, d_randomization);
		assertEquals(e.hashCode(), d_treatment.hashCode());
		assertEquals(e3.hashCode(), d_randomization.hashCode());
		d_null.hashCode();
	}
	
	@Test
	public void testSetName() {
		JUnitUtil.testSetter(d_null, Epoch.PROPERTY_NAME, null, "Randomization");
	}

	@Test
	public void testSetDuration() throws DatatypeConfigurationException {
		JUnitUtil.testSetter(d_null, Epoch.PROPERTY_DURATION, null, DatatypeFactory.newInstance().newDuration("P5DT3H"));
	}

}
