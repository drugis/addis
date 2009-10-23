package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.ExampleData;
import org.drugis.addis.entities.MetaAnalysis;
import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.PooledPatientGroup;
import org.drugis.addis.entities.Study;
import org.junit.Before;
import org.junit.Test;

public class PooledPatientGroupPresentationTest {
	private MetaAnalysis d_analysis;
	private MetaStudy d_study;
	private PooledPatientGroup d_pg;
	
	@Before
	public void setUp() {
		Domain d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_analysis = new MetaAnalysis(ExampleData.buildEndpointHamd(), 
				new ArrayList<Study>(d_domain.getStudies()));		
		d_study = new MetaStudy("s", d_analysis);
		d_pg = new PooledPatientGroup(d_study, ExampleData.buildDrugFluoxetine());
	}
	
	@Test
	public void testGetLabel() {
		PooledPatientGroupPresentation pres = new PooledPatientGroupPresentation(d_pg);
		assertEquals("META " + d_pg.getDrug().toString(), pres.getLabelModel().getValue());
	}
}
