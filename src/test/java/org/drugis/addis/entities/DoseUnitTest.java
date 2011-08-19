package org.drugis.addis.entities;

import static org.junit.Assert.*;
import static org.drugis.addis.entities.AssertEntityEquals.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.drugis.addis.ExampleData;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class DoseUnitTest {

	private DoseUnit d_mgDay;
	private DoseUnit d_kgHr;
	private Unit d_gram;
	private Unit d_meter;

	@Before
	public void setUp() {
		d_gram = new Unit("gram", "g");
		d_meter = new Unit("meter", "m");
		d_mgDay = ExampleData.MILLIGRAMS_A_DAY.clone();
		d_kgHr = ExampleData.KILOGRAMS_PER_HOUR.clone();
	}
	
	@Test
	public void testEquals() {
		assertFalse(d_mgDay.equals(d_kgHr));
		assertEntityEquals(d_mgDay, d_mgDay);
		DoseUnit du = new DoseUnit(new Unit("gram", "gg"), ScaleModifier.MILLI, EntityUtil.createDuration("P1D"));
		DoseUnit du2 = new DoseUnit(new Unit("gram", "gg"), ScaleModifier.MILLI, EntityUtil.createDuration("P1D"));
		assertEntityEquals(du, du2);
		assertEquals(d_mgDay, du);
		assertFalse(d_mgDay.deepEquals(du));
	}
	
	@Test
	public void testEvents() {
		JUnitUtil.testSetter(d_mgDay, DoseUnit.PROPERTY_UNIT, d_gram, d_meter);
		JUnitUtil.testSetter(d_mgDay, DoseUnit.PROPERTY_SCALE_MODIFIER, ScaleModifier.MILLI, ScaleModifier.KILO);
		JUnitUtil.testSetter(d_mgDay, DoseUnit.PROPERTY_PER_TIME, EntityUtil.createDuration("P1D"), EntityUtil.createDuration("PT1H"));
	}
	
	@Test
	public void testClone() {
		DoseUnit cloned = d_mgDay.clone();
		assertEntityEquals(d_mgDay, cloned);
		assertFalse(cloned == d_mgDay);

		cloned.setScaleModifier(ScaleModifier.KILO);
		assertFalse(EntityUtil.deepEqual(d_mgDay, cloned));
		cloned.setScaleModifier(ScaleModifier.MILLI);
		assertEntityEquals(d_mgDay, cloned);
		
		cloned.setUnit(new Unit("nonsense", "ns"));
		assertFalse(EntityUtil.deepEqual(d_mgDay, cloned));
		cloned.setUnit(new Unit("gram", "g"));
		assertEntityEquals(d_mgDay, cloned);

		try {
			cloned.setPerTime(DatatypeFactory.newInstance().newDuration("P2D"));
		} catch (DatatypeConfigurationException e) {}
		assertFalse(EntityUtil.deepEqual(d_mgDay, cloned));
	}
	
}
