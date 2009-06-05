package nl.rug.escher.addis.analyses.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import nl.rug.escher.addis.analyses.SMAAAdapter;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.addis.entities.test.TestData;

import org.junit.Before;
import org.junit.Test;

import fi.smaa.Alternative;
import fi.smaa.Criterion;
import fi.smaa.GaussianCriterion;
import fi.smaa.LogNormalCriterion;
import fi.smaa.SMAAModel;

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
			assertTrue(alts.contains(new Alternative(g.getLabel())));
		}
	}
	
	@Test
	public void testModelName() {
		SMAAModel model = SMAAAdapter.getModel(d_study);
		assertEquals(d_study.getId(), model.getName());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetCriteria() {
		SMAAModel model = SMAAAdapter.getModel(d_study);
		List<Criterion> crit = model.getCriteria();
		List<Endpoint> endpoints = d_study.getEndpoints();
		assertEquals(endpoints.size(), crit.size());
		for (Endpoint e : endpoints) {
			if (e.getType().equals(Endpoint.Type.CONTINUOUS)) {
				GaussianCriterion gaussianCriterion = new GaussianCriterion(e.getName());
				assertTrue(crit.contains(gaussianCriterion));
				GaussianCriterion gc = (GaussianCriterion) crit.get(crit.indexOf(gaussianCriterion));
				assertEquals(d_study.getDrugs().size(),
						gc.getMeasurements().size());
			} else if (e.getType().equals(Endpoint.Type.RATE)) {
				assertTrue(crit.contains(new LogNormalCriterion(e.getName())));				
				LogNormalCriterion logNormalCriterion = new LogNormalCriterion(e.getName());
				assertTrue(crit.contains(logNormalCriterion));
				LogNormalCriterion lnc = (LogNormalCriterion) crit.get(crit.indexOf(logNormalCriterion));
				assertEquals(d_study.getDrugs().size(),
						lnc.getMeasurements().size());				
			}
		}
	}	
	
	@Test
	public void testGetModel() {
		assertNotNull(SMAAAdapter.getModel(d_study));
	}
	
}
