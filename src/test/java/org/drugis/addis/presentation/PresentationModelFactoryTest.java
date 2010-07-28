/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public class PresentationModelFactoryTest {
	
	private PresentationModelFactory d_manager;
	private Domain d_domain;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		this.d_manager = new PresentationModelFactory(d_domain);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMetaAnalysisGetModel() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		RandomEffectsMetaAnalysis anal = new RandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(),
				studies, ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine());
		
		PresentationModel m = d_manager.getModel(anal);
		assertEquals(anal, m.getBean());
		PresentationModel m2 = d_manager.getModel(anal);
		assertTrue(m == m2);
		assertTrue(m instanceof RandomEffectsMetaAnalysisPresentation);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testStudyGetModel() {
		Study s = new Study("Study", new Indication(666L, "evil"));
		PresentationModel m = d_manager.getModel(s);
		assertEquals(s, m.getBean());
		assertTrue(m instanceof StudyPresentation);
	}
	
	@SuppressWarnings("unchecked")
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
		
		assertTrue(m.getIncludedStudies().getValue().containsAll(
				d_domain.getStudies(ExampleData.buildIndicationDepression()).getValue()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetDrugModel(){
		Drug d = ExampleData.buildDrugFluoxetine();
		PresentationModel m = d_manager.getModel(d);
		
		assertEquals(d, m.getBean());
		assertEquals(DrugPresentation.class, m.getClass());
		assertEquals(d_domain.getStudies(d).getValue().size(),
				((DrugPresentation) m).getIncludedStudies().getValue().size());
		assertTrue(d_domain.getStudies(d).getValue().containsAll(
				((DrugPresentation) m).getIncludedStudies().getValue()));		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetEndpointModel() {
		OutcomeMeasure e = ExampleData.buildEndpointHamd();
		PresentationModel m = d_manager.getModel(e);
		
		assertEquals(e, m.getBean());
		assertEquals(VariablePresentation.class, m.getClass());
		assertEquals(d_domain.getStudies(e).getValue().size(),
				((VariablePresentation) m).getIncludedStudies().getValue().size());
		assertTrue(d_domain.getStudies(e).getValue().containsAll(
				((VariablePresentation) m).getIncludedStudies().getValue()));
	}

	@Test
	public void testGetOtherModel() {
		assertNotNull(d_manager.getModel((Study) d_domain.getStudies().first()));
	}
	
	private void makeStudyPmOnce (Study study) {
		d_manager.getModel(study);
	}
	
	@Test
	public void testPresentationModelDoesntCacheDeleted () throws DependentEntitiesException {
		String id = "whichEveriD";
		d_domain.addStudy(new Study(id, d_domain.getIndications().first()));
		
		d_domain.getStudies().last().setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, "This value should not be retained");
		assertEquals("This value should not be retained",d_domain.getStudies().last().getCharacteristic(BasicStudyCharacteristic.OBJECTIVE));
		makeStudyPmOnce(d_domain.getStudies().last());
		d_domain.deleteEntity(d_domain.getStudies().last());
		
		Study myStudy = new Study(id, new Indication(0l, ""));
		Object expected = myStudy.getCharacteristic(BasicStudyCharacteristic.OBJECTIVE);
		Object actual = d_manager.getModel(myStudy).getBean().getCharacteristic(BasicStudyCharacteristic.OBJECTIVE);
		assertEquals(expected, actual);
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetEndpointCreationModel() {
		OutcomeMeasure e = ExampleData.buildEndpointHamd();
		PresentationModel m = d_manager.getCreationModel(e);
		
		assertEquals(e, m.getBean());
		assertEquals(OutcomeMeasureCreationPresentation.class, m.getClass());
	}
}
