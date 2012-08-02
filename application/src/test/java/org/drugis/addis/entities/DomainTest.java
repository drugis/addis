/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.entities;

import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.PairWiseMetaAnalysis;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class DomainTest {
	private Domain d_domain;
	private Indication d_indication;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		d_indication = new Indication(0L, "ind");
	}
	
	@Test
	public void testEmptyDomain() {
		assertTrue(d_domain.getEndpoints().isEmpty());
		assertTrue(d_domain.getStudies().isEmpty());
		assertTrue(d_domain.getDrugs().isEmpty());
		assertTrue(d_domain.getIndications().isEmpty());
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddEndpointNull() {
		d_domain.getEndpoints().add(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddStudyNull() {
		d_domain.getStudies().add(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddDrugNull() {
		d_domain.getStudies().add(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddIndicationNull() {
		d_domain.getIndications().add(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddMetaAnalysisNull() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		d_domain.getMetaAnalyses().add(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddVariableNull() {
		d_domain.getPopulationCharacteristics().add(null);
	}	
	

	@Test
	public void testAddEndpoint() {
		Endpoint e = new Endpoint("e", Endpoint.convertVarType(Variable.Type.RATE));
		assertEquals(0, d_domain.getEndpoints().size());
		d_domain.getEndpoints().add(e);
		assertEquals(1, d_domain.getEndpoints().size());
		assertEquals(Collections.singletonList(e), d_domain.getEndpoints());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddMetaAnalysisThrowsOnUnknownStudy() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		studies.add(new Study("iiidddd", ExampleData.buildIndicationDepression()));
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(),
				studies, DrugSet.createTrivial(ExampleData.buildDrugFluoxetine()), DrugSet.createTrivial(ExampleData.buildDrugParoxetine()));
		d_domain.getMetaAnalyses().add(ma);
	}
	
	@Test
	public void testAddStudy() {
		d_domain.getIndications().add(d_indication);
		Study s = new Study("X", d_indication);
		assertEquals(0, d_domain.getStudies().size());
		d_domain.getStudies().add(s);
		assertEquals(1, d_domain.getStudies().size());
		assertEquals(Collections.singletonList(s), d_domain.getStudies());
	}
	
	
	@Test
	public void testAddStudyBenefitRiskAnalysis() throws Exception {
		StudyBenefitRiskAnalysis sbr = ExampleData.buildStudyBenefitRiskAnalysis();
		ExampleData.initDefaultData(d_domain);
		d_domain.getBenefitRiskAnalyses().add(sbr);
		assertTrue(d_domain.getBenefitRiskAnalyses().contains(sbr));
		assertEntityEquals(sbr, d_domain.getBenefitRiskAnalyses().get(0));
	}
	
	@Test
	public void testDeleteStudyBenefitRiskAnalysis() throws DependentEntitiesException {
		StudyBenefitRiskAnalysis sbr = ExampleData.buildStudyBenefitRiskAnalysis();
		ExampleData.initDefaultData(d_domain);
		d_domain.getBenefitRiskAnalyses().add(sbr);
		assertTrue(d_domain.getBenefitRiskAnalyses().contains(sbr));
		assertEntityEquals(sbr, d_domain.getBenefitRiskAnalyses().get(0));

		d_domain.deleteEntity(sbr);
		assertFalse(d_domain.getBenefitRiskAnalyses().contains(sbr));
	}

	
	@Test
	public void testAddMetaBenefitRiskAnalysis() throws Exception {
		MetaBenefitRiskAnalysis mbr = ExampleData.buildMetaBenefitRiskAnalysis();
		ExampleData.initDefaultData(d_domain);
		d_domain.getMetaAnalyses().add(ExampleData.buildMetaAnalysisHamd());
		d_domain.getMetaAnalyses().add(ExampleData.buildMetaAnalysisConv());
		d_domain.getBenefitRiskAnalyses().add(mbr);
		assertTrue(d_domain.getBenefitRiskAnalyses().contains(mbr));
		assertEntityEquals(mbr, d_domain.getBenefitRiskAnalyses().get(0));
	}
	
	@Test
	public void testDeleteMetaBenefitRiskAnalysis() throws DependentEntitiesException {
		MetaBenefitRiskAnalysis mbr = ExampleData.buildMetaBenefitRiskAnalysis();
		ExampleData.initDefaultData(d_domain);
		d_domain.getMetaAnalyses().add(ExampleData.buildMetaAnalysisHamd());
		d_domain.getMetaAnalyses().add(ExampleData.buildMetaAnalysisConv());
		d_domain.getBenefitRiskAnalyses().add(mbr);
		assertTrue(d_domain.getBenefitRiskAnalyses().contains(mbr));
		assertEntityEquals(mbr, d_domain.getBenefitRiskAnalyses().get(0));

		d_domain.deleteEntity(mbr);
		assertFalse(d_domain.getBenefitRiskAnalyses().contains(mbr));
	}

	@Test
	public void testAddMetaAnalysis() throws Exception {
		assertEquals(0, d_domain.getMetaAnalyses().size());
		ExampleData.initDefaultData(d_domain);
		RandomEffectsMetaAnalysis s = addMetaAnalysisToDomain();
		
		assertTrue(d_domain.getMetaAnalyses().contains(s));
		assertEquals(1, d_domain.getMetaAnalyses().size());
	}

	private RandomEffectsMetaAnalysis addMetaAnalysisToDomain() throws Exception {
		RandomEffectsMetaAnalysis ma = generateMetaAnalysis();
		d_domain.getMetaAnalyses().add(ma);
		return ma;
	}
	
	private NetworkMetaAnalysis addNetworkMetaAnalysisToDomain() throws Exception {
		NetworkMetaAnalysis ma = ExampleData.buildNetworkMetaAnalysisHamD();
		d_domain.getMetaAnalyses().add(ma);
		return ma;
	}

	private RandomEffectsMetaAnalysis generateMetaAnalysis() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(),
				studies, DrugSet.createTrivial(ExampleData.buildDrugFluoxetine()), DrugSet.createTrivial(ExampleData.buildDrugParoxetine()));
		return ma;
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddMetaAnalysisThrowsOnDifferentIndication() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		Study study2 = ExampleData.buildStudyDeWilde();
		study2.setIndication(ExampleData.buildIndicationChronicHeartFailure());
		studies.add(study2);
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", ExampleData.buildEndpointHamd(),
				studies, DrugSet.createTrivial(ExampleData.buildDrugFluoxetine()), DrugSet.createTrivial(ExampleData.buildDrugParoxetine()));
		d_domain.getMetaAnalyses().add(ma);
	}
	
	@Test
	public void testDeleteMetaAnalysis() throws Exception {
		ExampleData.initDefaultData(d_domain);
		RandomEffectsMetaAnalysis s = addMetaAnalysisToDomain();
		
		assertTrue(d_domain.getMetaAnalyses().contains(s));
		d_domain.deleteEntity(s);
		assertFalse(d_domain.getMetaAnalyses().contains(s));
	}		
	
	@Test
	public void testAddDrug() {
		Drug d = new Drug("name", "atc");
		assertEquals(0, d_domain.getDrugs().size());
		d_domain.getDrugs().add(d);
		assertEquals(1, d_domain.getDrugs().size());
		assertEquals(Collections.singletonList(d), d_domain.getDrugs());
	}
	
	@Test
	public void testAddIndication() {
		Indication i1 = new Indication(310497006L, "Severe depression");
		assertEquals(0, d_domain.getIndications().size());
		d_domain.getIndications().add(i1);
		assertEquals(1, d_domain.getIndications().size());
		assertEquals(Collections.singletonList(i1), d_domain.getIndications());
	}
	
	@Test
	public void testGetStudiesByEndpoint() {
		Endpoint e1 = new Endpoint("e1", Endpoint.convertVarType(Variable.Type.RATE));
		Endpoint e2 = new Endpoint("e2", Endpoint.convertVarType(Variable.Type.RATE));
		Endpoint e3 = new Endpoint("e3", Endpoint.convertVarType(Variable.Type.RATE));
		
		List<Endpoint> l1 = new ArrayList<Endpoint>();
		l1.add(e1);
		Study s1 = new Study("X", d_indication);
		s1.setName("s1");
		s1.getEndpoints().clear();
		s1.getEndpoints().addAll(Study.wrapVariables(l1));
		
		ArrayList<Endpoint> l2 = new ArrayList<Endpoint>();
		l2.add(e2);
		l2.add(e1);
		Study s2 = new Study("X", d_indication);
		s2.setName("s2");
		s2.getEndpoints().clear();
		s2.getEndpoints().addAll(Study.wrapVariables(l2));
		
		ObservableList<Study> e1Studies = d_domain.getStudies(e1);
		ObservableList<Study> e2Studies = d_domain.getStudies(e2);
		ObservableList<Study> e3Studies = d_domain.getStudies(e3);
		
		d_domain.getIndications().add(d_indication);
		d_domain.getEndpoints().addAll(Arrays.asList(e1, e2, e3));
		d_domain.getStudies().add(s1);
		d_domain.getStudies().add(s2);

		assertEquals(2, e1Studies.size());
		assertEquals(1, e2Studies.size());
		assertEquals(0, e3Studies.size());
		
		assertTrue(e1Studies.contains(s1));
		assertTrue(e1Studies.contains(s2));
		assertTrue(e2Studies.contains(s2));
	}
	
	@Test
	public void testGetStudiesByIndication() {
		Endpoint e1 = new Endpoint("e1", Endpoint.convertVarType(Variable.Type.RATE));
		Endpoint e2 = new Endpoint("e2", Endpoint.convertVarType(Variable.Type.RATE));
			
		ArrayList<Endpoint> l1 = new ArrayList<Endpoint>();
		l1.add(e1);
		Indication i1 = new Indication(0L, "");
		d_domain.getIndications().add(i1);
		Study s1 = new Study("s1", i1);
		s1.getEndpoints().clear();
		s1.getEndpoints().addAll(Study.wrapVariables(l1));
		
		ArrayList<Endpoint> l2 = new ArrayList<Endpoint>();
		l2.add(e2);
		l2.add(e1);
		Study s2 = new Study("s2", i1);
		s2.getEndpoints().clear();
		s2.getEndpoints().addAll(Study.wrapVariables(l2));
		
		d_domain.getEndpoints().addAll(Arrays.asList(e1, e2));
		d_domain.getStudies().add(s1);
		d_domain.getStudies().add(s2);
		
		Indication i2 = new Indication(007L,"This indication does not exist.");
		d_domain.getIndications().add(i2);
		
		ObservableList<Study> studies = d_domain.getStudies(i1);
		assertEquals(2, studies.size());
		
		assertEquals(0, d_domain.getStudies(i2).size());
		
		assertTrue(studies.contains(s1));
		assertTrue(studies.contains(s2));
		
		Study s3 = new Study("s3", i1);
		s3.getEndpoints().clear();
		s3.getEndpoints().addAll(Study.wrapVariables(l2));
		
		d_domain.getStudies().add(s3);
		assertTrue(studies.contains(s3));
	}
	
	@Test
	public void testGetStudiesByDrug() {
		Drug d1 = new Drug("drug1", "atccode1");
		Drug d2 = new Drug("drug2", "atccode2");
		Drug d3 = new Drug("drug3", "atccode3");
		
		Endpoint e = new Endpoint("Death", Endpoint.convertVarType(Variable.Type.RATE));
		
		Study s1 = new Study("s1", d_indication);
		ExampleData.addDefaultEpochs(s1);
		s1.getEndpoints().clear();
		s1.getEndpoints().addAll(Study.wrapVariables(Collections.singletonList(e)));
		Arm g1 = s1.createAndAddArm("g1", 100, d1, new FixedDose(1.0, DoseUnit.MILLIGRAMS_A_DAY));
		BasicMeasurement m1 = new BasicRateMeasurement(10, g1.getSize());
		ExampleData.addDefaultMeasurementMoments(s1);
		s1.setMeasurement(e, g1, m1);
		d_domain.getIndications().add(d_indication);
		
		Indication indic2 = new Indication(1L, "");
		d_domain.getIndications().add(indic2);
		Study s2 = new Study("s2", indic2);
		ExampleData.addDefaultEpochs(s2);
		s2.getEndpoints().clear();
		s2.getEndpoints().addAll(Study.wrapVariables(Collections.singletonList(e)));
		Arm g2 = s2.createAndAddArm("g2", 250, d1, new FixedDose(5.0, DoseUnit.MILLIGRAMS_A_DAY));		
		Arm g3 = s2.createAndAddArm("g3", 250, d2, new FixedDose(5.0, DoseUnit.MILLIGRAMS_A_DAY));
		BasicMeasurement m2 = new BasicRateMeasurement(10, g2.getSize());
		BasicMeasurement m3 = new BasicRateMeasurement(10, g3.getSize());		
		ExampleData.addDefaultMeasurementMoments(s2);
		s2.setMeasurement(e, g2, m2);
		s2.setMeasurement(e, g3, m3);
		
		
		ObservableList<Study> d1Studies = d_domain.getStudies(d1);
		ObservableList<Study> d2Studies = d_domain.getStudies(d2);
		ObservableList<Study> d3Studies = d_domain.getStudies(d3);		
		
		d_domain.getEndpoints().add(e);
		d_domain.getDrugs().addAll(Arrays.asList(d1, d2, d3));
		d_domain.getStudies().add(s1);
		d_domain.getStudies().add(s2);
		
		assertEquals(2, d1Studies.size());
		assertEquals(1, d2Studies.size());
		assertEquals(0, d3Studies.size());
		
		assertTrue(d1Studies.contains(s1));
		assertTrue(d1Studies.contains(s2));
		assertTrue(d2Studies.contains(s2));
	}
	
	@Test
	public void testGetStudies() {
		Drug d1 = new Drug("drug1", "atccode1");
		Drug d2 = new Drug("drug2", "atccode2");
		
		Endpoint e = new Endpoint("Death", Endpoint.convertVarType(Variable.Type.RATE));
		
		Study s1 = new Study("s1", d_indication);
		ExampleData.addDefaultEpochs(s1);
		s1.getEndpoints().clear();
		s1.getEndpoints().addAll(Study.wrapVariables(Collections.singletonList(e)));
		Arm g1 = s1.createAndAddArm("g1", 100, d1, new FixedDose(1.0, DoseUnit.MILLIGRAMS_A_DAY));
		BasicMeasurement m1 = new BasicRateMeasurement(10, g1.getSize());
		ExampleData.addDefaultMeasurementMoments(s1);
		s1.setMeasurement(e, g1, m1);
		d_domain.getIndications().add(d_indication);
		
		Indication indic2 = new Indication(1L, "");
		d_domain.getIndications().add(indic2);
		Study s2 = new Study("s2", indic2);
		ExampleData.addDefaultEpochs(s2);
		s2.getEndpoints().clear();
		s2.getEndpoints().addAll(Study.wrapVariables(Collections.singletonList(e)));
		Arm g2 = s2.createAndAddArm("g2", 250, d1, new FixedDose(5.0, DoseUnit.MILLIGRAMS_A_DAY));		
		Arm g3 = s2.createAndAddArm("g3", 250, d2, new FixedDose(5.0, DoseUnit.MILLIGRAMS_A_DAY));
		BasicMeasurement m2 = new BasicRateMeasurement(10, g2.getSize());
		BasicMeasurement m3 = new BasicRateMeasurement(10, g3.getSize());		
		ExampleData.addDefaultMeasurementMoments(s1);
		s2.setMeasurement(e, g2, m2);
		s2.setMeasurement(e, g3, m3);
		
		d_domain.getEndpoints().add(e);
		d_domain.getDrugs().addAll(Arrays.asList(d1, d2));
		ObservableList<Study> studies = d_domain.getStudies();
		
		d_domain.getStudies().add(s1);
		d_domain.getStudies().add(s2);
		
		assertEquals(2, studies.size());
		
		assertTrue(studies.contains(s1));
		assertTrue(studies.contains(s2));
	}
		
	@Test
	public void testEquals() {
		Domain d1 = new DomainImpl();
		Domain d2 = new DomainImpl();
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
		
		Endpoint e1 = new Endpoint("e1", Endpoint.convertVarType(Variable.Type.RATE));
		Endpoint e2 = new Endpoint("e2", Endpoint.convertVarType(Variable.Type.RATE));
		d1.getEndpoints().add(e1);
		d1.getEndpoints().add(e2);
		d2.getEndpoints().add(e1);
		JUnitUtil.assertNotEquals(d1, d2);
		d2.getEndpoints().add(e2);
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
		
		Drug d = new Drug("d1", "atc");
		d1.getDrugs().add(d);
		JUnitUtil.assertNotEquals(d1, d2);
		d2.getDrugs().add(d);
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
		
		d1.getIndications().add(d_indication);
		Study s = new Study("s1", d_indication);
		d1.getStudies().add(s);
		JUnitUtil.assertNotEquals(d1, d2);
		d2.getIndications().add(d_indication);
		d2.getStudies().add(s);
		assertEquals(d1, d2);
		assertEquals(d1.hashCode(), d2.hashCode());
	}
	
	@Test
	public void testDeleteStudy() throws DependentEntitiesException {
		Study s = new Study("X", d_indication);
		d_domain.getIndications().add(d_indication);
		d_domain.getStudies().add(s);
		d_domain.deleteEntity(s);
		assertTrue(d_domain.getStudies().isEmpty());
	}
	
	@Test(expected=DependentEntitiesException.class)
	public void testDeleteStudyThrowsCorrectException() throws DependentEntitiesException, NullPointerException, IllegalArgumentException, EntityIdExistsException {
		Drug fluox = ExampleData.buildDrugFluoxetine();
		Drug parox = ExampleData.buildDrugParoxetine();
		
		Study s1 = new Study("X", d_indication);
		ExampleData.addDefaultEpochs(s1);
		s1.createAndAddArm("fluox", 23, fluox, new FixedDose(20, DoseUnit.MILLIGRAMS_A_DAY));
		s1.createAndAddArm("parox", 23, parox, new FixedDose(20, DoseUnit.MILLIGRAMS_A_DAY));
	
		Study s2 = new Study("Y", d_indication);
		ExampleData.addDefaultEpochs(s2);
		s2.createAndAddArm("fluox", 23, fluox, new FixedDose(20, DoseUnit.MILLIGRAMS_A_DAY));
		s2.createAndAddArm("parox", 23, parox, new FixedDose(20, DoseUnit.MILLIGRAMS_A_DAY));
		
		d_domain.getIndications().add(d_indication);
		d_domain.getDrugs().addAll(Arrays.asList(fluox, parox));
		d_domain.getStudies().add(s1);
		d_domain.getStudies().add(s2);
		
		Endpoint e = new Endpoint("e", Endpoint.convertVarType(Variable.Type.RATE));
		d_domain.getEndpoints().add(e);
		s1.getEndpoints().add(new StudyOutcomeMeasure<Endpoint>(e));
		s2.getEndpoints().add(new StudyOutcomeMeasure<Endpoint>(e));
		
		ArrayList<Study> studies = new ArrayList<Study>(d_domain.getStudies());
		RandomEffectsMetaAnalysis ma = new RandomEffectsMetaAnalysis("meta", e, studies, DrugSet.createTrivial(fluox), DrugSet.createTrivial(parox)); 
		d_domain.getMetaAnalyses().add(ma);
		d_domain.deleteEntity(s1);
	}
	
	@Test
	public void testDeleteDrug() throws DependentEntitiesException {
		Drug d = new Drug("X", "atc");
		d_domain.getDrugs().add(d);
		d_domain.deleteEntity(d);
		assertTrue(d_domain.getDrugs().isEmpty());
	}
	
	@Test(expected=DependentEntitiesException.class)
	public void testDeleteDrugThrowsCorrectException() throws DependentEntitiesException {
		Study s1 = new Study("X", d_indication);
		ExampleData.addDefaultEpochs(s1);
		d_domain.getIndications().add(d_indication);
		d_domain.getStudies().add(s1);
		
		Drug d = new Drug("d", "atc");
		d_domain.getDrugs().add(d);
	
		s1.createAndAddArm("g", 10, d, new FixedDose(10.0, DoseUnit.MILLIGRAMS_A_DAY));
		d_domain.deleteEntity(d);
	}

	@Test
	public void testDeleteEndpoint() throws DependentEntitiesException {
		Endpoint e = new Endpoint("e", Endpoint.convertVarType(Variable.Type.RATE));
		d_domain.getEndpoints().add(e);
		d_domain.deleteEntity(e);
		assertTrue(d_domain.getEndpoints().isEmpty());
	}
	
	@Test(expected=DependentEntitiesException.class)
	public void testDeleteEndpointThrowsCorrectException() throws DependentEntitiesException {
		Study s1 = new Study("X", d_indication);
		d_domain.getIndications().add(d_indication);
		d_domain.getStudies().add(s1);
		
		Endpoint e = new Endpoint("e", Endpoint.convertVarType(Variable.Type.RATE));
		d_domain.getEndpoints().add(e);
		s1.getEndpoints().add(new StudyOutcomeMeasure<Endpoint>(e));
			
		d_domain.deleteEntity(e);
	}

	@Test
	public void testDeleteIndication() throws DependentEntitiesException {
		Indication i = new Indication(01L, "i");
		d_domain.getIndications().add(i);
		d_domain.deleteEntity(i);
		assertTrue(d_domain.getIndications().isEmpty());
	}
	
	@Test(expected=DependentEntitiesException.class)
	public void testDeleteIndicationThrowsCorrectException() throws DependentEntitiesException {
		Indication indication = new Indication(5L, "");
		Study s1 = new Study("X", indication);
		d_domain.getIndications().add(indication);
		d_domain.getStudies().add(s1);
			
		d_domain.deleteEntity(indication);
	}
	
	@Test
	public void testGetVariables() {
		PopulationCharacteristic c = new PopulationCharacteristic("x", new CategoricalVariableType(Arrays.asList((new String[]{"x", "y", "z"}))));
		d_domain.getPopulationCharacteristics().add(c);
		
		assertEquals(Collections.singletonList(c), d_domain.getPopulationCharacteristics());
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddAdeNull() {
		d_domain.getAdverseEvents().add(null);
	}
	
	@Test
	public void testAddAde() {
		AdverseEvent ade = new AdverseEvent("a", AdverseEvent.convertVarType(Variable.Type.RATE));
		assertEquals(0, d_domain.getAdverseEvents().size());
		d_domain.getAdverseEvents().add(ade);
		assertEquals(1, d_domain.getAdverseEvents().size());
		assertEquals(Collections.singletonList(ade), d_domain.getAdverseEvents());
	}
	
	@Test
	public void testDeleteAde() throws DependentEntitiesException {
		AdverseEvent e = new AdverseEvent("e", AdverseEvent.convertVarType(Variable.Type.RATE));
		d_domain.getAdverseEvents().add(e);
		d_domain.deleteEntity(e);
		assertTrue(d_domain.getAdverseEvents().isEmpty());
	}
	
	@Test(expected=DependentEntitiesException.class)
	public void testDeleteAdeThrowsCorrectException() throws DependentEntitiesException {
		Study s1 = new Study("X", d_indication);
		d_domain.getIndications().add(d_indication);
		d_domain.getStudies().add(s1);
		
		AdverseEvent a = new AdverseEvent("e", AdverseEvent.convertVarType(Variable.Type.RATE));
		d_domain.getAdverseEvents().add(a);
		s1.getAdverseEvents().add(new StudyOutcomeMeasure<AdverseEvent>(a));
			
		d_domain.deleteEntity(a);
	}
	
	@Test
	public void testGetCategories() {
		assertEquals(11, d_domain.getCategories().size());
		List<Class<?>> cats = Arrays.<Class<?>>asList(Unit.class, Indication.class, Drug.class, TreatmentCategorization.class, Endpoint.class, AdverseEvent.class, PopulationCharacteristic.class, 
				Study.class, PairWiseMetaAnalysis.class, NetworkMetaAnalysis.class, BenefitRiskAnalysis.class);
		List<Class<?>> domainCats = new ArrayListModel<Class<?>>();
		for (EntityCategory ec : d_domain.getCategories()) {
			domainCats.add(ec.getEntityClass());
		}
		assertEquals(cats, domainCats);
	}
	
	@Test
	public void testGetCategoryForClass() {
		assertEquals(Indication.class, d_domain.getCategory(Indication.class).getEntityClass());
		assertEquals(null, d_domain.getCategory(Arm.class));
		assertEquals(Drug.class, d_domain.getCategory(Drug.class).getEntityClass());
		assertEquals(PairWiseMetaAnalysis.class, d_domain.getCategory(PairWiseMetaAnalysis.class).getEntityClass());
		assertEquals(NetworkMetaAnalysis.class, d_domain.getCategory(NetworkMetaAnalysis.class).getEntityClass());
	}
	
	@Test
	public void testGetCategoryForEntity() {
		ExampleData.initDefaultData(d_domain);
		assertEquals(Indication.class, d_domain.getCategory(new Indication(13L, "indication")).getEntityClass());
		assertEquals(null, d_domain.getCategory(new Arm("arm", 3)));
		assertEquals(Drug.class, d_domain.getCategory(new Drug("drug", "")).getEntityClass());
		assertEquals(BenefitRiskAnalysis.class, 
				d_domain.getCategory(ExampleData.buildMetaBenefitRiskAnalysis()).getEntityClass());
	}
	
	@Test
	public void testGetCategoryContents() {
		assertEquals(d_domain.getIndications(), d_domain.getCategoryContents(
				d_domain.getCategory(Indication.class)));
		assertEquals(d_domain.getDrugs(), d_domain.getCategoryContents(
				d_domain.getCategory(Drug.class)));
		assertEquals(d_domain.getEndpoints(), d_domain.getCategoryContents(
				d_domain.getCategory(Endpoint.class)));
		assertEquals(d_domain.getAdverseEvents(), d_domain.getCategoryContents(
				d_domain.getCategory(AdverseEvent.class)));
		assertEquals(d_domain.getPopulationCharacteristics(), d_domain.getCategoryContents(
				d_domain.getCategory(PopulationCharacteristic.class)));
		assertEquals(d_domain.getStudies(), d_domain.getCategoryContents(
				d_domain.getCategory(Study.class)));
		assertEquals(d_domain.getPairWiseMetaAnalyses(), d_domain.getCategoryContents(
				d_domain.getCategory(PairWiseMetaAnalysis.class)));
		assertEquals(d_domain.getBenefitRiskAnalyses(), d_domain.getCategoryContents(
				d_domain.getCategory(BenefitRiskAnalysis.class)));
	}
	
	@Test
	public void testGetCategoryContentsModel() {
		assertSame(d_domain.getIndications(), d_domain.getCategoryContents(d_domain.getCategory(Indication.class)));
		assertSame(d_domain.getDrugs(), d_domain.getCategoryContents(d_domain.getCategory(Drug.class)));
		assertSame(d_domain.getEndpoints(), d_domain.getCategoryContents(d_domain.getCategory(Endpoint.class)));
	}
	
	@Test
	public void testGetPairWiseMetaAnalyses() throws Exception {
		ExampleData.initDefaultData(d_domain);
		assertEquals(Collections.emptyList(), d_domain.getPairWiseMetaAnalyses());
		PairWiseMetaAnalysis anl = addMetaAnalysisToDomain();
		assertEquals(Collections.singletonList(anl), d_domain.getPairWiseMetaAnalyses());
		d_domain.getAdverseEvents().add(ExampleData.buildAdverseEventSexualDysfunction());
		d_domain.getStudies().add(ExampleData.buildStudyFava2002());
		addNetworkMetaAnalysisToDomain();
		assertEquals(Collections.singletonList(anl), d_domain.getPairWiseMetaAnalyses());
	}
	
	@Test
	public void testGetNetworkMetaAnalyses() throws Exception {
		ExampleData.initDefaultData(d_domain);
		assertEquals(Collections.emptyList(), d_domain.getNetworkMetaAnalyses());
		d_domain.getAdverseEvents().add(ExampleData.buildAdverseEventSexualDysfunction());
		d_domain.getStudies().add(ExampleData.buildStudyFava2002());
		NetworkMetaAnalysis anl = addNetworkMetaAnalysisToDomain();
		assertEquals(Collections.singletonList(anl), d_domain.getNetworkMetaAnalyses());
		addMetaAnalysisToDomain();
		assertEquals(Collections.singletonList(anl), d_domain.getNetworkMetaAnalyses());
	}
}
