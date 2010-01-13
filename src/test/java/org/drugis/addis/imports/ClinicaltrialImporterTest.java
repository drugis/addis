package org.drugis.addis.imports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.imports.ClinicaltrialsImporter;
import org.junit.Before;
import org.junit.Test;


public class ClinicaltrialImporterTest {
	
	Domain d_testDomain;
	Study  d_testStudy;
	
	@Before
	public void setUp() {
		d_testDomain = new DomainImpl();
		d_testStudy  = new Study("testStudyID", new Indication(-1L, "testIndicationID"));
	}
	
	@Test
	public void testGetClinicaltrialsData(){
		ClinicaltrialsImporter.getClinicaltrialsData(d_testStudy, new File("./src/main/xml/NCT00644527"));
		
		assertEquals("MIPH-2008-MT-1",d_testStudy.getId());
		assertEquals(BasicStudyCharacteristic.Allocation.RANDOMIZED, d_testStudy.getCharacteristic( BasicStudyCharacteristic.ALLOCATION));
		assertEquals(BasicStudyCharacteristic.Blinding.DOUBLE_BLIND, d_testStudy.getCharacteristic( BasicStudyCharacteristic.BLINDING));
		//assertEquals(1, d_testStudy.getCharacteristic(BasicStudyCharacteristic.CENTERS)); // FIXME: default 1?
		assertTrue(((String)d_testStudy.getCharacteristic(BasicStudyCharacteristic.OBJECTIVE)).contains("specific interest is the use of music in the evening") );
		assertEquals("Depression", ((Indication) d_testStudy.getCharacteristic(BasicStudyCharacteristic.INDICATION)).getName()); // FIXME: code?

		Date expectedStartDate = null, expectedEndDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
		try {
			expectedStartDate = sdf.parse("March 2008");
			expectedEndDate = sdf.parse("July 2008");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		assertEquals(expectedStartDate , d_testStudy.getCharacteristic(BasicStudyCharacteristic.STUDY_START));
		assertEquals(expectedEndDate, d_testStudy.getCharacteristic(BasicStudyCharacteristic.STUDY_END));
		assertEquals(BasicStudyCharacteristic.Status.FINISHED, d_testStudy.getCharacteristic(BasicStudyCharacteristic.STATUS));
		assertTrue(((String)d_testStudy.getCharacteristic(BasicStudyCharacteristic.INCLUSION)).contains("Patients aged 18 to 70 years with a Goldberg Depression Test Score of 15 to 65"));
		assertTrue(((String)d_testStudy.getCharacteristic(BasicStudyCharacteristic.EXCLUSION)).contains("Patients under psychiatric treatment because of psychoses"));
		
		//assertEquals(new Endpoint("Change of a composite measure including the Hamilton Depression Scale (double weighted), the Beck Depression Inventory (single weighted) and the HADS-D-scale (single weighted) between study entry and 5 / 10 and 15-week-follow-up.", Type.RATE), testStudy); //:TODO how should this be set?
 	}
	
}
