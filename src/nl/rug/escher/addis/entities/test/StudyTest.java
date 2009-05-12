package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Test;

public class StudyTest {
	@Test
	public void testSetId() {
		JUnitUtil.testSetter(new Study(), Study.PROPERTY_ID, null, "NCT00351273");
	}
	
	@Test
	public void testSetEndpoints() {
		List<Endpoint> list = Collections.singletonList(new Endpoint());
		JUnitUtil.testSetter(new Study(), Study.PROPERTY_ENDPOINTS, Collections.EMPTY_LIST, 
				list);
	}
	
	@Test
	public void testAddEndpoint() {
		JUnitUtil.testAdder(new Study(), Study.PROPERTY_ENDPOINTS, "addEndpoint", new Endpoint());
	}
	
	@Test
	public void testSetPatientGroups() {
		List<PatientGroup> list = Collections.singletonList(new PatientGroup());
		JUnitUtil.testSetter(new Study(), Study.PROPERTY_PATIENTGROUPS, Collections.EMPTY_LIST, 
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
		JUnitUtil.testAdder(new Study(), Study.PROPERTY_PATIENTGROUPS, "addPatientGroup", new PatientGroup());
	}
	
	@Test
	public void testToString() {
		String id = "NCT00351273";
		Study study = new Study();
		study.setId(id);
		assertEquals(id, study.toString());
	}
}
