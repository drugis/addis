package nl.rug.escher.entities.test;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.entities.PatientGroup;
import nl.rug.escher.entities.Study;

import org.junit.Test;

public class StudyTest {
	@Test
	public void testSetId() {
		Helper.testSetter(new Study(), Study.PROPERTY_ID, null, "NCT00351273");
	}
	
	@Test
	public void testSetEndpoints() {
		List<Endpoint> list = Collections.singletonList(new Endpoint());
		Helper.testSetter(new Study(), Study.PROPERTY_ENDPOINTS, Collections.EMPTY_LIST, 
				list);
	}
	
	@Test
	public void testSetPatientGroups() {
		List<PatientGroup> list = Collections.singletonList(new PatientGroup());
		Helper.testSetter(new Study(), Study.PROPERTY_PATIENTGROUPS, Collections.EMPTY_LIST, 
				list);
	}
	
	@Test
	public void testInitialPatientGroups() {
		Study study = new Study();
		assertNotNull(study.getPatientGroups());
		assertTrue(study.getPatientGroups().isEmpty());
	}
	
	@Test
	public void testAddPatientGroup() {
		Study study = new Study();
		String propertyName = Study.PROPERTY_PATIENTGROUPS;
		PatientGroup g2 = new PatientGroup();
		
		Helper.testAdder(study, propertyName, "addPatientGroup", g2);
	}
	
	@Test
	public void testToString() {
		String id = "NCT00351273";
		Study study = new Study();
		study.setId(id);
		assertEquals(id, study.toString());
	}
}
