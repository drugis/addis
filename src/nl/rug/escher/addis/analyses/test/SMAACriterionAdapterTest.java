/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package nl.rug.escher.addis.analyses.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import nl.rug.escher.addis.analyses.SMAAAdapter;
import nl.rug.escher.addis.analyses.SMAACriterionAdapter;
import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.addis.entities.ContinuousMeasurement;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.OddsRatio;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.RateContinuousAdapter;
import nl.rug.escher.addis.entities.RateMeasurement;
import nl.rug.escher.addis.entities.test.TestData;

import org.junit.Before;
import org.junit.Test;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.GaussianCriterion;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.LogNormalCriterion;

public class SMAACriterionAdapterTest {
	
	private BasicStudy d_study;
	private List<Alternative> d_alts;

	@Before
	public void setUp() {
		d_study = TestData.buildDefaultStudy();
		d_alts = SMAAAdapter.getModel(d_study).getAlternatives();
	}
	
	@Test
	public void testBuildGaussianCriterion() {
		Endpoint cgi = TestData.buildEndpointCgi();
		GaussianCriterion gc = 
			(GaussianCriterion) SMAACriterionAdapter.buildCriterion(cgi, d_study, d_alts);
		assertEquals(cgi.getName(), gc.getName());
		assertEquals(d_alts, gc.getAlternatives());
		List<? extends PatientGroup> groups = d_study.getPatientGroups();
		assertEquals(groups.size(), gc.getMeasurements().size());
		for (PatientGroup g : groups) {
			ContinuousMeasurement m = (ContinuousMeasurement) g.getMeasurement(cgi);
			Alternative protoAlt = new Alternative(g.getLabel());
			GaussianMeasurement gm = gc.getMeasurements().get(protoAlt);
			assertEquals(m.getMean(), gm.getMean());
			assertEquals(m.getStdDev(), gm.getStDev());
		}
	}
	
	@Test
	public void testBuildLogNormalCriterion() {
		Endpoint hamd = TestData.buildEndpointHamd();
		LogNormalCriterion gc = 
			(LogNormalCriterion) SMAACriterionAdapter.buildCriterion(hamd, d_study, d_alts);
		assertEquals(hamd.getName(), gc.getName());
		assertEquals(d_alts, gc.getAlternatives());
		List<? extends PatientGroup> groups = d_study.getPatientGroups();
		assertEquals(groups.size(), gc.getMeasurements().size());
		PatientGroup firstGroup = groups.get(0);
		ContinuousMeasurement firstMeasurement = getAdapted(firstGroup, hamd);
		for (PatientGroup g : groups) {
			OddsRatio od = new OddsRatio(getAdapted(g, hamd), firstMeasurement);
			Alternative protoAlt = new Alternative(g.getLabel());
			GaussianMeasurement gm = gc.getMeasurements().get(protoAlt);
			assertEquals(Math.log(od.getMean()), (double)gm.getMean(), 0.00001);
			assertEquals(Math.log(od.getStdDev()), (double)gm.getStDev(), 0.00001);
		}

	}
	
	private ContinuousMeasurement getAdapted(PatientGroup g, Endpoint e) {
		return new RateContinuousAdapter((RateMeasurement)g.getMeasurement(e));
	}

}
