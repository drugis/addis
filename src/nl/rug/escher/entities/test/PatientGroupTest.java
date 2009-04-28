package nl.rug.escher.entities.test;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

import nl.rug.escher.entities.Dose;
import nl.rug.escher.entities.Drug;
import nl.rug.escher.entities.Measurement;
import nl.rug.escher.entities.PatientGroup;
import nl.rug.escher.entities.Study;

import org.junit.Test;

public class PatientGroupTest {
	@Test
	public void testSetStudy() {
		Helper.testSetter(new PatientGroup(), PatientGroup.PROPERTY_STUDY, null, new Study());
	}
	
	@Test
	public void testSetDrug() {
		Helper.testSetter(new PatientGroup(), PatientGroup.PROPERTY_DRUG, null, new Drug());
	}
	
	@Test
	public void testSetDose() {
		Helper.testSetter(new PatientGroup(), PatientGroup.PROPERTY_DOSE, null, new Dose());
	}
	
	@Test
	public void testInitialMeasurements() {
		PatientGroup p = new PatientGroup();
		assertNotNull(p.getMeasurements());
		assertTrue(p.getMeasurements().isEmpty());
	}
	
	@Test
	public void testSetMeasurements() {
		List<Measurement> list = Collections.singletonList(new Measurement());
		Helper.testSetter(new PatientGroup(), PatientGroup.PROPERTY_MEASUREMENTS, Collections.EMPTY_LIST, 
				list);
	}
	
	@Test
	public void testAddMeasurement() {
		Helper.testAdder(new PatientGroup(), PatientGroup.PROPERTY_MEASUREMENTS,
				"addMeasurement", new Measurement());
	}
}
