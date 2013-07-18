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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.DependentEntitiesException;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("rawtypes")
public class PresentationModelFactoryTest {
	
	private PresentationModelFactory d_manager;
	private Domain d_domain;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		this.d_manager = new PresentationModelFactory(d_domain);
	}

	@Test
	public void testMetaAnalysisGetModel() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		RandomEffectsMetaAnalysis anal = ExampleData.buildRandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(), studies, TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine()), TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine()));
		
		PresentationModel m = d_manager.getModel(anal);
		assertEquals(anal, m.getBean());
		PresentationModel m2 = d_manager.getModel(anal);
		assertTrue(m == m2);
		assertTrue(m instanceof PairWiseMetaAnalysisPresentation);
	}
	
	@Test
	public void testStudyGetModel() {
		Study s = new Study("Study", new Indication(666L, "evil"));
		PresentationModel m = d_manager.getModel(s);
		assertEquals(s, m.getBean());
		assertTrue(m instanceof StudyPresentation);
	}
	
	@Test
	public void testGetIndicationModel() {
		Indication indication = new Indication(0L, "");
		PresentationModel m = d_manager.getModel(indication);
		
		assertEquals(indication, m.getBean());
		assertEquals(m.getClass(), IndicationPresentation.class);
		assertEquals(m, d_manager.getLabeledModel(indication));
	}
	
	@Test
	public void testGetExistingIndicationModelHasCorrectStudies() {
		IndicationPresentation m = (IndicationPresentation) d_manager.getModel(ExampleData.buildIndicationDepression());
		
		assertEquals(ExampleData.buildIndicationDepression(), m.getBean());
		assertEquals(m.getClass(), IndicationPresentation.class);
		
		assertTrue(m.getStudyListPresentation().getIncludedStudies().containsAll(
				d_domain.getStudies(ExampleData.buildIndicationDepression())));
	}
	
	@Test
	public void testGetDrugModel(){
		Drug d = ExampleData.buildDrugFluoxetine();
		PresentationModel m = d_manager.getModel(d);
		
		assertEquals(d, m.getBean());
		assertEquals(DrugPresentation.class, m.getClass());
		assertEquals(d_domain.getStudies(d).size(),
				((DrugPresentation) m).getStudyListPresentation().getIncludedStudies().size());
		assertTrue(d_domain.getStudies(d).containsAll(
				((DrugPresentation) m).getStudyListPresentation().getIncludedStudies()));		
	}
	
	@Test
	public void testGetEndpointModel() {
		OutcomeMeasure e = ExampleData.buildEndpointHamd();
		PresentationModel m = d_manager.getModel(e);
		
		assertEquals(e, m.getBean());
		assertEquals(VariablePresentation.class, m.getClass());
		assertEquals(d_domain.getStudies(e).size(),
				((VariablePresentation) m).getStudyListPresentation().getIncludedStudies().size());
		assertTrue(d_domain.getStudies(e).containsAll(
				((VariablePresentation) m).getStudyListPresentation().getIncludedStudies()));
	}

	@Test
	public void testGetOtherModel() {
		assertNotNull(d_manager.getModel((Study) d_domain.getStudies().get(0)));
	}
	
	@Test
	public void testPresentationModelDoesntCacheDeleted () throws DependentEntitiesException {
		String id = "whichEveriD";
		d_domain.getStudies().add(new Study(id, d_domain.getIndications().get(0)));
		
		Study lastStudy = d_domain.getStudies().get(d_domain.getStudies().size()-1);
		lastStudy.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, "This value should not be retained");
		assertEquals("This value should not be retained",lastStudy.getCharacteristic(BasicStudyCharacteristic.OBJECTIVE));
		d_manager.getModel(lastStudy);
		d_domain.deleteEntity(lastStudy);
		
		Study myStudy = new Study(id, new Indication(0l, ""));
		Object expected = myStudy.getCharacteristic(BasicStudyCharacteristic.OBJECTIVE);
		Object actual = d_manager.getModel(myStudy).getBean().getCharacteristic(BasicStudyCharacteristic.OBJECTIVE);
		assertEquals(expected, actual);
	}
}
