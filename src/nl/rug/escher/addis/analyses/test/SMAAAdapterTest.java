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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import nl.rug.escher.addis.analyses.SMAAAdapter;
import nl.rug.escher.addis.entities.ContinuousMeasurement;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.RateMeasurement;
import nl.rug.escher.addis.entities.RiskRatio;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.addis.entities.test.TestData;

import org.junit.Before;
import org.junit.Test;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.CardinalCriterion;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.ImpactMatrix;
import fi.smaa.jsmaa.model.LogNormalMeasurement;
import fi.smaa.jsmaa.model.NoSuchValueException;
import fi.smaa.jsmaa.model.SMAAModel;

public class SMAAAdapterTest {
	
	private Study d_study;

	@Before
	public void setUp() {
		d_study = TestData.buildDefaultStudy();
	}
	
	@Test
	public void testGetAlternatives() {
		SMAAModel model = SMAAAdapter.getModel(d_study);
		List<Alternative> alts = model.getAlternatives();
		List<? extends PatientGroup> groups = d_study.getPatientGroups();
		
		assertEquals(groups.size(), alts.size());
		for (PatientGroup g : groups) {
			assertNotNull(SMAAAdapter.findAlternative(g, model));
		}
	}
	
	@Test
	public void testModelName() {
		SMAAModel model = SMAAAdapter.getModel(d_study);
		assertEquals(d_study.getId(), model.getName());
	}
	
	@Test
	public void testGetCriteria() throws NoSuchValueException {
		SMAAModel model = SMAAAdapter.getModel(d_study);
		List<Criterion> crit = model.getCriteria();
		List<Endpoint> endpoints = d_study.getEndpoints();
		assertEquals(endpoints.size(), crit.size());
		for (Endpoint e : endpoints) {
			CardinalCriterion c = findCriterion(e, model);
			if (e.getType().equals(Endpoint.Type.CONTINUOUS)) {
				checkGaussianMeasurements(e, c, model);
			} else if (e.getType().equals(Endpoint.Type.RATE)) {
				checkLogNormalMeasurements(e, c, model);
			}
		}
	}
	
	private void checkLogNormalMeasurements(Endpoint e, CardinalCriterion c, SMAAModel model) throws NoSuchValueException {
		ImpactMatrix mat = model.getImpactMatrix();
		for (PatientGroup g : d_study.getPatientGroups()) {
			LogNormalMeasurement m = (LogNormalMeasurement) mat.getMeasurement(
					c, SMAAAdapter.findAlternative(g, model)
					);
			PatientGroup first = d_study.getPatientGroups().get(0);
			RiskRatio ratio = new RiskRatio((RateMeasurement) first.getMeasurement(e),
					(RateMeasurement) g.getMeasurement(e));
			assertEquals(ratio.getMean(), m.getMean());
			assertEquals(ratio.getStdDev(), m.getStDev());
		}		
	}

	private void checkGaussianMeasurements(Endpoint e, CardinalCriterion c, SMAAModel model) throws NoSuchValueException {
		ImpactMatrix mat = model.getImpactMatrix();
		for (PatientGroup g : d_study.getPatientGroups()) {
			ContinuousMeasurement cm = (ContinuousMeasurement) g.getMeasurement(e);
			GaussianMeasurement m = (GaussianMeasurement) mat.getMeasurement(c, SMAAAdapter.findAlternative(g, model));
			assertEquals(cm.getMean(), m.getMean());
			assertEquals(cm.getStdDev(), m.getStDev());
		}		
	}

	private CardinalCriterion findCriterion(Endpoint e, SMAAModel model) {
		for (Criterion c : model.getCriteria()) {
			if (e.getName().equals(c.getName())) {
				return (CardinalCriterion) c;
			}
		}
		fail("Criterion not found");
		return null;
	}

	@Test
	public void testGetModel() {
		assertNotNull(SMAAAdapter.getModel(d_study));
	}
	
}
