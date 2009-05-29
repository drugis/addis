package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.rug.escher.addis.entities.Drug;
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
	public void testGetDrugs() {
		Study s = TestData.buildDefaultStudy2();
		Set<Drug> expected = new HashSet<Drug>();
		expected.add(TestData.buildDrugFluoxetine());
		expected.add(TestData.buildDrugParoxetine());
		expected.add(TestData.buildDrugViagra());
		assertEquals(expected, s.getDrugs());
	}
	
	@Test
	public void testToString() {
		String id = "NCT00351273";
		Study study = new Study();
		study.setId(id);
		assertEquals(id, study.toString());
	}

	@Test
	public void testEquals() {
		fail();
		// also test hashCode() for one case where equals is true
	}
}