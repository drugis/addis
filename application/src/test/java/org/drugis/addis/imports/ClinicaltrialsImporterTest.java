/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.imports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyOutcomeMeasure;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.WhenTaken;
import org.drugis.addis.entities.WhenTaken.RelativeTo;
import org.drugis.addis.util.EntityUtil;
import org.junit.Before;
import org.junit.Test;


public class ClinicaltrialsImporterTest {
	private static InputStream getXMLResource(String name) {
		return ClinicaltrialsImporterTest.class.getResourceAsStream(name);
	}

	Domain d_testDomain;
	Study  d_testStudy;

	@Before
	public void setUp() {
		d_testDomain = new DomainImpl();
		d_testStudy  = new Study();
	}

	@Test
	public void testGetClinicaltrialsDataFromUri() {
		try {
			ClinicaltrialsImporter.getClinicaltrialsData(d_testStudy, "http://clinicaltrials.gov/show/NCT00644527?resultsxml=true", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetClinicaltrialsData(){
		ClinicaltrialsImporter.getClinicaltrialsData(d_testStudy, getXMLResource("NCT00644527.xml"), true);

		testRetrievedStudy();
 	}

	private void testRetrievedStudy() {
		assertEquals("NCT00644527", d_testStudy.getName());
		assertEquals("Receptive Music Therapy for the Treatment of Depression", d_testStudy.getCharacteristic(BasicStudyCharacteristic.TITLE));
		assertEquals(BasicStudyCharacteristic.Allocation.RANDOMIZED, d_testStudy.getCharacteristic( BasicStudyCharacteristic.ALLOCATION));
		assertEquals(BasicStudyCharacteristic.Blinding.DOUBLE_BLIND, d_testStudy.getCharacteristic( BasicStudyCharacteristic.BLINDING));
		assertEquals(1, d_testStudy.getCharacteristic(BasicStudyCharacteristic.CENTERS));
		assertTrue(((String)d_testStudy.getCharacteristic(BasicStudyCharacteristic.OBJECTIVE)).contains("specific interest is the use of music in the evening") );
		assertNull(d_testStudy.getIndication());

		Date expectedStartDate = null, expectedEndDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
		try {
			expectedStartDate = sdf.parse("March 2008");
			expectedEndDate = sdf.parse("July 2008");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		assertEquals(new Date().toString(), d_testStudy.getCharacteristic(BasicStudyCharacteristic.CREATION_DATE).toString());
		assertEquals(Source.CLINICALTRIALS, d_testStudy.getCharacteristic(BasicStudyCharacteristic.SOURCE));

		assertEquals(expectedStartDate , d_testStudy.getCharacteristic(BasicStudyCharacteristic.STUDY_START));
		assertEquals(expectedEndDate, d_testStudy.getCharacteristic(BasicStudyCharacteristic.STUDY_END));
		assertEquals(BasicStudyCharacteristic.Status.COMPLETED, d_testStudy.getCharacteristic(BasicStudyCharacteristic.STATUS));
		assertTrue(((String)d_testStudy.getCharacteristic(BasicStudyCharacteristic.INCLUSION)).contains("Patients aged 18 to 70 years with a Goldberg Depression Test Score of 15 to 65"));
		assertTrue(((String)d_testStudy.getCharacteristic(BasicStudyCharacteristic.EXCLUSION)).contains("Patients under psychiatric treatment because of psychoses"));


		assertTrue(Study.extractVariables(d_testStudy.getEndpoints()).size() > 0);
		Note note = d_testStudy.getEndpoints().get(0).getNotes().get(0);


		Boolean checkNote = note.getText().contains("the HADS-D-scale (single weighted) between study entry and 5 / 10 and 15-week-follow-up")
		                 || note.getText().contains("Quality of life (SF 36), Vital Exhaustion Brief Questionnaire, Primary outcome measure at 5 and 10 weeks.");
		assertTrue(checkNote);
	}

	@Test
	public void testGetClinicaltrialsDataWithResults() {
		ClinicaltrialsImporter.getClinicaltrialsData(d_testStudy, getXMLResource("NCT00696436.xml"), true);
		WhenTaken wt = new WhenTaken(EntityUtil.createDuration("P0D"), RelativeTo.BEFORE_EPOCH_END, d_testStudy.getEpochs().get(1));
		Arm arm1 = d_testStudy.getArms().get(0);
		Arm arm2 = d_testStudy.getArms().get(1);

		assertEquals("Azilsartan Medoxomil 40 mg QD", arm1.getName());
		assertEquals("Azilsartan Medoxomil 80 mg QD", arm2.getName());
		assertEquals(5, d_testStudy.getArms().size());

		StudyOutcomeMeasure<? extends Variable> som1 = d_testStudy.getStudyOutcomeMeasures().get(0);

		assertTrue(som1.getNotes().get(0).getText().startsWith("Change From Baseline in the 24-hour Mean Systolic Blood Pressure Measured by Ambulatory Blood Pressure Monitoring."));
		assertTrue(som1.getIsPrimary());
		BasicContinuousMeasurement m1 = new BasicContinuousMeasurement(-13.42, 10.622, 237);
		BasicContinuousMeasurement m2 = new BasicContinuousMeasurement(-14.53, 10.623, 229);
		assertMeasurementEquals(m1, d_testStudy.getMeasurement(som1, arm1, wt));
		assertMeasurementEquals(m2, d_testStudy.getMeasurement(som1, arm2, wt));

		StudyOutcomeMeasure<? extends Variable> som2 = d_testStudy.getStudyOutcomeMeasures().get(1);
		assertTrue(som2.getNotes().get(0).getText().startsWith("Change From Baseline in Mean Trough Clinic Sitting Systolic Blood Pressure."));
		assertFalse(som2.getIsPrimary());

		BasicContinuousMeasurement m3 = new BasicContinuousMeasurement(-16.38, 15.728, 269);
		BasicContinuousMeasurement m4 = new BasicContinuousMeasurement(-16.74, 15.725, 270);

		assertMeasurementEquals(m3, d_testStudy.getMeasurement(som2, arm1, wt));
		assertMeasurementEquals(m4, d_testStudy.getMeasurement(som2, arm2, wt));
		// TODO add tests for categorical and rate measurements

	}

	private static void assertMeasurementEquals(BasicContinuousMeasurement m1, BasicMeasurement m2) {
		if (m2 instanceof BasicContinuousMeasurement) {
			assertEquals(m1.getMean(), ((BasicContinuousMeasurement) m2).getMean(), 0.001);
			assertEquals(m1.getStdDev(), ((BasicContinuousMeasurement) m2).getStdDev(), 0.001);
			assertEquals(m1.getSampleSize(), m2.getSampleSize());
		} else {
			assertEquals(m1, m2);
		}
	}


}
