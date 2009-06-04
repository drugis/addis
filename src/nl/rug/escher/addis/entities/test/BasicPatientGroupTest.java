package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.rug.escher.addis.entities.BasicContinuousMeasurement;
import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.BasicMeasurement;
import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.SIUnit;
import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Before;
import org.junit.Test;

public class BasicPatientGroupTest {
	
	private BasicPatientGroup d_pg;

	@Before
	public void setUp() {
		d_pg = new BasicPatientGroup(null, null, null, 0);
	}
	
	@Test
	public void testSetStudy() {
		JUnitUtil.testSetter(d_pg, BasicPatientGroup.PROPERTY_STUDY, null, new BasicStudy("X"));
	}
	
	@Test
	public void testSetSize() {
		JUnitUtil.testSetter(d_pg, BasicPatientGroup.PROPERTY_SIZE, 0, 1);
	}
	
	@Test
	public void testSetDrug() {
		JUnitUtil.testSetter(d_pg, BasicPatientGroup.PROPERTY_DRUG, null, new Drug("D"));
	}
	
	@Test
	public void testSetDose() {
		JUnitUtil.testSetter(d_pg, BasicPatientGroup.PROPERTY_DOSE, null, new Dose(1.0, SIUnit.MILLIGRAMS_A_DAY));
	}
	
	@Test
	public void testInitialMeasurements() {
		BasicPatientGroup p = d_pg;
		assertNotNull(p.getMeasurements());
		assertTrue(p.getMeasurements().isEmpty());
	}
	
	@Test
	public void testSetMeasurements() {
		List<BasicContinuousMeasurement> list = Collections.singletonList(new BasicContinuousMeasurement());
		JUnitUtil.testSetter(d_pg, BasicPatientGroup.PROPERTY_MEASUREMENTS, Collections.EMPTY_LIST, 
				list);
	}
	
	@Test
	public void testAddMeasurement() {
		JUnitUtil.testAdder(d_pg, BasicPatientGroup.PROPERTY_MEASUREMENTS,
				"addMeasurement", new BasicContinuousMeasurement());
	}
	
	@Test
	public void testAddMeasurementSetsPatientGroup() {
		BasicPatientGroup g = d_pg;
		BasicMeasurement m = new BasicContinuousMeasurement();
		g.addMeasurement(m);
		assertEquals(g, m.getPatientGroup());
	}
	
	@Test
	public void testGetLabel() {
		BasicPatientGroup group = d_pg;
		assertEquals("INCOMPLETE", group.getLabel());
		
		Dose dose = new Dose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		group.setDose(dose);
		Drug drug = new Drug();
		drug.setName("Fluoxetine");
		group.setDrug(drug);
		assertEquals("Fluoxetine " + dose.toString(), group.getLabel());
	}
	
	@Test
	public void testFireLabelChanged() {
		BasicPatientGroup group;
		PropertyChangeListener l;
		Drug drug = new Drug();
		drug.setName("Fluoxetine");
		
		group = d_pg;
		group.setDrug(drug);
		Dose dose = new Dose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		group.setDose(dose);
		String expect = group.getLabel();
		group.setDose(null);
		assertEquals("INCOMPLETE", group.getLabel());
		l = JUnitUtil.mockListener(group, BasicPatientGroup.PROPERTY_LABEL, "INCOMPLETE", expect);
		group.addPropertyChangeListener(l);
		group.setDose(dose);
		assertEquals(expect, group.getLabel());
		verify(l);
		
		group = d_pg;
		group.setDose(dose);
		Drug drug2 = new Drug();
		drug2.setName("Paroxetine");
		group.setDrug(drug2);
		l = JUnitUtil.mockListener(group, BasicPatientGroup.PROPERTY_LABEL, group.getLabel(), expect);
		group.addPropertyChangeListener(l);
		group.setDrug(drug);
		verify(l);
	}
	
	@Test
	public void testGetMeasurementByEndpoint() {
		Endpoint e1 = new Endpoint();
		e1.setName("e1");
		Endpoint e2 = new Endpoint();
		e2.setName("e2");
		Endpoint e3 = new Endpoint();
		e3.setName("e3");
		
		BasicContinuousMeasurement m1 = new BasicContinuousMeasurement();
		m1.setEndpoint(e1);
		BasicContinuousMeasurement m2 = new BasicContinuousMeasurement();
		m2.setEndpoint(e2);
		
		BasicPatientGroup g = d_pg;
		g.addMeasurement(m2);
		g.addMeasurement(m1);
		
		assertEquals(m2, g.getMeasurement(e2));
		assertEquals(null, g.getMeasurement(e3));
	}
	
	@Test
	public void testEquals() {
		BasicStudy study1 = new BasicStudy("X");
		BasicStudy study2 = new BasicStudy("Y");
		Drug drug1 = new Drug("Drug");
		Drug drug2 = new Drug("Drug 2");
		Dose dose1 = new Dose(12, SIUnit.MILLIGRAMS_A_DAY);
		Dose dose2 = new Dose(8, SIUnit.MILLIGRAMS_A_DAY);
		int size1 = 1;
		int size2 = 2;
		List<BasicMeasurement> m1 = new ArrayList<BasicMeasurement>();
		List<BasicMeasurement> m2 = new ArrayList<BasicMeasurement>();
		m2.add(new BasicRateMeasurement());
		
		assertEquals(new BasicPatientGroup(study1, drug1, dose1, size1, m1),
				new BasicPatientGroup(study1, drug1, dose1, size1, m1));
		
		JUnitUtil.assertNotEquals(new BasicPatientGroup(study1, drug1, dose1, size1, m1),
				new BasicPatientGroup(study2, drug1, dose1, size1, m1));
		JUnitUtil.assertNotEquals(new BasicPatientGroup(study1, drug1, dose1, size1, m1),
				new BasicPatientGroup(study1, drug2, dose1, size1, m1));
		JUnitUtil.assertNotEquals(new BasicPatientGroup(study1, drug1, dose1, size1, m1),
				new BasicPatientGroup(study1, drug1, dose2, size1, m1));
		
		assertEquals(new BasicPatientGroup(study1, drug1, dose1, size1, m1),
				new BasicPatientGroup(study1, drug1, dose1, size2, m1));
		assertEquals(new BasicPatientGroup(study1, drug1, dose1, size1, m1),
				new BasicPatientGroup(study1, drug1, dose1, size1, m2));
		
		assertEquals(new BasicPatientGroup(study1, drug1, dose1, size1, m1).hashCode(),
				new BasicPatientGroup(study1, drug1, dose1, size1, m1).hashCode());
	}
	
	@Test
	public void testConstructorAddsItselfToMeasurements() {
		List<BasicMeasurement> l = new ArrayList<BasicMeasurement>();
		l.add(new BasicRateMeasurement());
		
		BasicPatientGroup pg = new BasicPatientGroup(new BasicStudy("X"), new Drug("Drug 2"), 
				new Dose(8, SIUnit.MILLIGRAMS_A_DAY), 2, l);
		assertEquals(pg, pg.getMeasurements().get(0).getPatientGroup());
	}
}
