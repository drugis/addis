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

package org.drugis.addis.presentation.wizard;

import static org.drugis.common.JUnitUtil.assertAllAndOnly;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyOutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.wizard.BenefitRiskWizardPM.BRAType;
import org.junit.Before;
import org.junit.Test;

public class BenefitRiskWizardPMTest {

	private Domain d_domain;
	private BenefitRiskWizardPM d_pm;
	private Indication d_indication;
	private Study d_study;
	private DrugSet d_fluoxSet;
	private DrugSet d_paroxSet;
	private DrugSet d_sertrSet;

	@Before
	public void setUp() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_indication = ExampleData.buildIndicationDepression();
		d_study = ExampleData.buildStudyChouinard().clone();
		
		d_fluoxSet = new DrugSet(ExampleData.buildDrugFluoxetine());
		d_paroxSet = new DrugSet(ExampleData.buildDrugParoxetine());
		d_sertrSet = new DrugSet(ExampleData.buildDrugSertraline());
		
		d_domain.getStudies().remove(ExampleData.buildStudyChouinard());
		d_domain.getStudies().add(d_study);
		d_domain.getAdverseEvents().add(ExampleData.buildAdverseEventSexualDysfunction());
		d_domain.getStudies().add(ExampleData.buildStudyFava2002());
		
		d_domain.getMetaAnalyses().add(ExampleData.buildNetworkMetaAnalysisHamD());
		d_domain.getMetaAnalyses().add(ExampleData.buildNetworkMetaAnalysisConvulsion());
		d_domain.getMetaAnalyses().add(ExampleData.buildMetaAnalysisConv());
		d_domain.getMetaAnalyses().add(ExampleData.buildMetaAnalysisHamd());
		d_domain.getMetaAnalyses().add(ExampleData.buildNetworkMetaAnalysisCgi());

		d_pm = new BenefitRiskWizardPM(d_domain); 
		d_pm.getIndicationModel().setValue(d_indication);
	}
	
	@Test
	public void testOutcomesListModelIncludesOutcomes() {
		d_pm.getEvidenceTypeHolder().setValue(BRAType.Synthesis);
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		assertEquals(Arrays.asList(ExampleData.buildEndpointCgi(), ExampleData.buildEndpointHamd(), 
				ExampleData.buildAdverseEventConvulsion()), 
				pm.getCriteriaListModel());
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationChronicHeartFailure());
		assertEquals(Collections.emptyList(), pm.getCriteriaListModel());

	}
	
	@Test
	public void testMetaAnalysesForEachOutcome() {
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		for (OutcomeMeasure om : pm.getCriteriaListModel()) {
			List<MetaAnalysis> analyses = new ArrayList<MetaAnalysis>();
			for (MetaAnalysis analysis : d_domain.getMetaAnalyses()) {
				if (om.equals(analysis.getOutcomeMeasure()))
					analyses.add(analysis);
			}
			assertAllAndOnly(analyses, pm.getMetaAnalyses(om));
		}
	}
	
	@Test
	public void testOutcomeSelectedModelKeepsChanges() {
		OutcomeMeasure om = ExampleData.buildEndpointHamd();
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		ValueHolder<Boolean> origModel = pm.getCriterionSelectedModel(om);
		assertFalse(origModel.getValue());
		pm.getCriterionSelectedModel(om).setValue(true);
		assertEquals(pm.getCriterionSelectedModel(om).getValue(), origModel.getValue());
	}


	@Test
	public void testOutcomeSelectedMultipleAnalysisShouldNotSelect() {
		OutcomeMeasure om = ExampleData.buildEndpointHamd();
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		pm.getCriterionSelectedModel(om).setValue(true);
		assertTrue(pm.getMetaAnalyses(om).size() > 1);
		assertNull(pm.getMetaAnalysesSelectedModel(om).getValue());	
	}
	
	@Test
	public void testOutcomeSelectedSingleAnalysisShouldSelect() {
		OutcomeMeasure om = ExampleData.buildEndpointCgi();
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		pm.getCriterionSelectedModel(om).setValue(true);
		assertTrue(pm.getMetaAnalyses(om).size() == 1);
		assertNotNull(pm.getMetaAnalysesSelectedModel(om).getValue());	
	}
	
	@Test
	public void testMetaAnalysesSelectedModelKeepsChanges() {
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		ValueHolder<MetaAnalysis> metaAnal1 = pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd());
		assertNull(metaAnal1.getValue());
		
		metaAnal1.setValue(ExampleData.buildNetworkMetaAnalysisHamD());
		assertEquals(ExampleData.buildNetworkMetaAnalysisHamD(), metaAnal1.getValue());
		
		ValueHolder<MetaAnalysis> metaAnal2 = pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd());
		assertEquals(metaAnal1.getValue(), metaAnal2.getValue());
	}
	
	@Test
	public void testAlternativesListModelShouldBeUnionOfAnalyzedDrugs() {
		List<DrugSet> expected = new ArrayList<DrugSet>();
		for (MetaAnalysis ma : d_domain.getMetaAnalyses()) {
			if (ma.getIndication().equals(d_indication))
				expected.addAll(ma.getIncludedDrugs());
		}
		
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		
		assertAllAndOnly(expected, pm.getAlternativesListModel());
	}
	
	@Test
	public void testAlternativeEnabledModelShouldReflectInclusion() {
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();

		for (DrugSet d : pm.getAlternativesListModel()) {
			assertEquals(false, pm.getAlternativeEnabledModel(d).getValue());
		}
		
		d_pm.getIndicationModel().setValue(d_indication);
		Endpoint outcomeM = ExampleData.buildEndpointHamd();
		pm.getCriterionSelectedModel(outcomeM).setValue(true);
		pm.getMetaAnalysesSelectedModel(outcomeM).setValue(ExampleData.buildNetworkMetaAnalysisHamD());

		assertTrue(pm.getAlternativesListModel().size() > 0);
		
		for (DrugSet d : pm.getAlternativesListModel()) {
			boolean expected = true;
			for (MetaAnalysis mah : pm.getSelectedMetaAnalyses()) {
				if (mah != null && !mah.getIncludedDrugs().contains(d)) {
					expected = false;
				}
			}
			
			assertEquals(expected, pm.getAlternativeEnabledModel(d).getValue());
		}
	}
	
	@Test
	public void testGetAlternativeSelectedModel() {
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		pm.getCriterionSelectedModel(ExampleData.buildEndpointCgi()).setValue(true);
		DrugSet d = new DrugSet(ExampleData.buildDrugParoxetine());
		ValueHolder<Boolean> actual = pm.getAlternativeSelectedModel(d);
		assertEquals(false, actual.getValue());
		actual.setValue(true);
		assertEquals(true, pm.getAlternativeEnabledModel(d).getValue());
		assertEquals(true, actual.getValue());
	}
	
	@Test
	public void testCompletedMetaModelFalseWithLessThanTwoDrugs() {
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getCriterionSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysisHamD());
		pm.getMetaAnalysesSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(ExampleData.buildMetaAnalysisConv());
		pm.getAlternativeSelectedModel(d_fluoxSet).setValue(true);
		assertTrue(pm.getAlternativeSelectedModel(d_fluoxSet).getValue());
		pm.getAlternativeSelectedModel(d_paroxSet).setValue(false);
		assertFalse((Boolean)pm.getCompleteModel().getValue());
	}

	@Test
	public void testCompletedMetaModelFalseWithLessThanTwoCriteria() {
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildMetaAnalysisHamd());
		pm.getAlternativeSelectedModel(d_fluoxSet).setValue(true);
		pm.getAlternativeSelectedModel(d_paroxSet).setValue(true);
		assertFalse((Boolean)pm.getCompleteModel().getValue());
	}

	@Test
	public void testCompletedStudyFalseWithLessThanTwoArms() {
		d_pm.getEvidenceTypeHolder().setValue(BRAType.SingleStudy);
		StudyCriteriaAndAlternativesPresentation pm = d_pm.getStudyBRPresentation();
		// note: using local copy of chouinard (has 2 arms) so that test won't fail if setup is changed to different study
		Study study = ExampleData.buildStudyChouinard().clone();
		pm.getStudyModel().setValue(study);
		
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getCriterionSelectedModel(ExampleData.buildEndpointCgi()).setValue(true);
		
		pm.getAlternativeSelectedModel(study.getArms().get(1)).setValue(true);
		
		assertFalse((Boolean)pm.getCompleteModel().getValue());
	}
	
	@Test
	public void testCompletedSingleStudyFalseWithLessThanTwoCriteria() {
		d_pm.getEvidenceTypeHolder().setValue(BRAType.SingleStudy);
		StudyCriteriaAndAlternativesPresentation pm = d_pm.getStudyBRPresentation();

		// note: using local copy of chouinard (has 2 arms) so that test won't fail if setup is changed to different study
		Study local = ExampleData.buildStudyChouinard().clone();
		pm.getStudyModel().setValue(local);
		
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);

		pm.getAlternativeSelectedModel(local.getArms().get(0)).setValue(true);
		pm.getAlternativeSelectedModel(local.getArms().get(1)).setValue(true);
		
		assertFalse((Boolean)pm.getCompleteModel().getValue());
	}

	@Test
	public void testCompletedModelFalseWithCriteriaWithoutAnalysis() {
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildMetaAnalysisHamd());
		pm.getCriterionSelectedModel(ExampleData.buildEndpointCgi()).setValue(true);
		pm.getAlternativeSelectedModel(d_fluoxSet).setValue(true);
		pm.getAlternativeSelectedModel(d_paroxSet).setValue(true);
		pm.getBaselineModel().setValue(d_fluoxSet);
		assertTrue((Boolean)pm.getCompleteModel().getValue());
		pm.getCriterionSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		assertEquals(null, pm.getMetaAnalysesSelectedModel(ExampleData.buildAdverseEventConvulsion()).getValue());
		assertFalse((Boolean)pm.getCompleteModel().getValue());
		pm.getMetaAnalysesSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(ExampleData.buildMetaAnalysisConv());
		assertTrue((Boolean)pm.getCompleteModel().getValue());
	}

	@Test
	public void testCompletedMetaAnalysisModelTrueWithTwoDrugsTwoCriteria() {
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getCriterionSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysisHamD());
		pm.getMetaAnalysesSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(ExampleData.buildMetaAnalysisConv());
		pm.getAlternativeSelectedModel(d_fluoxSet).setValue(true);
		pm.getAlternativeSelectedModel(d_paroxSet).setValue(true);
		assertFalse((Boolean)pm.getCompleteModel().getValue());
		pm.getBaselineModel().setValue(d_fluoxSet);
		assertTrue((Boolean)pm.getCompleteModel().getValue());
	}

	@Test
	public void testCompletedSingleStudyModelTrueWithTwoDrugsTwoCriteria() {
		d_pm.getEvidenceTypeHolder().setValue(BRAType.SingleStudy);
		StudyCriteriaAndAlternativesPresentation pm = d_pm.getStudyBRPresentation();
		// note: using local copy of chouinard (has 2 arms) so that test won't fail if setup is changed to different study
		Study local = ExampleData.buildStudyChouinard().clone();
		pm.getStudyModel().setValue(local);
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getCriterionSelectedModel(ExampleData.buildEndpointCgi()).setValue(true);

		pm.getAlternativeSelectedModel(local.getArms().get(0)).setValue(true);
		pm.getAlternativeSelectedModel(local.getArms().get(1)).setValue(true);
		
		assertFalse((Boolean)pm.getCompleteModel().getValue());
		pm.getBaselineModel().setValue(local.getArms().get(0));
		assertTrue((Boolean)pm.getCompleteModel().getValue());
	}
	
	@Test
	public void testChangeIndicationShouldClearValues() {
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getCriterionSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysisHamD());
		pm.getMetaAnalysesSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(ExampleData.buildMetaAnalysisConv());
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationChronicHeartFailure());
		assertTrue(pm.getSelectedCriteria().isEmpty());
		assertTrue(pm.getSelectedAlternatives().isEmpty());
		assertFalse((Boolean)pm.getCompleteModel().getValue());
	}
	
	@Test
	public void testChangeAnalysisTypeShouldClearValues() {
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.LyndOBrien); 
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getAlternativeSelectedModel(new DrugSet(ExampleData.buildDrugFluoxetine()));
		pm.getAlternativeSelectedModel(new DrugSet(ExampleData.buildDrugParoxetine()));
		pm.getAlternativeSelectedModel(new DrugSet(ExampleData.buildDrugSertraline()));
		pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysisHamD());
		
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.SMAA);
		assertTrue(pm.getSelectedCriteria().isEmpty());
		assertTrue(pm.getSelectedAlternatives().isEmpty());
		assertFalse((Boolean)pm.getCompleteModel().getValue());
	}
	
	@Test
	public void testResetSelectedOutcomesShouldCascadeToEnabledModels() {
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.LyndOBrien); 
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		
		assertTrue(pm.getCriteriaListModel().size() > 2);
		
		for (OutcomeMeasure om: pm.getCriteriaListModel()) {
			assertTrue(pm.getCriterionEnabledModel(om).getValue());
		}
		
		pm.getCriterionSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		
		for (OutcomeMeasure om: pm.getCriteriaListModel()) {
			if (om.equals(ExampleData.buildEndpointHamd()) || om.equals(ExampleData.buildAdverseEventConvulsion())) {
				assertTrue(pm.getCriterionEnabledModel(om).getValue());
			} else {
				assertFalse(pm.getCriterionEnabledModel(om).getValue());
			}
		}
		
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(false);

		for (OutcomeMeasure om: pm.getCriteriaListModel()) {
			assertTrue(pm.getCriterionEnabledModel(om).getValue());
		}
	}

	@Test
	public void testChangeAnalysisTypeShouldCascadeToEnabledModels() {
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.LyndOBrien); 
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getCriterionSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.SMAA);
	
		for (OutcomeMeasure om: pm.getCriteriaListModel()) {
			assertTrue(pm.getCriterionEnabledModel(om).getValue());
		}
	}
	
	@Test
	public void testChangeAnalysisTypeShouldCascadeToEnabledModelsSingleStudy() {
		d_pm.getEvidenceTypeHolder().setValue(BRAType.SingleStudy);
		StudyCriteriaAndAlternativesPresentation pm = d_pm.getStudyBRPresentation();
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.LyndOBrien); 
		pm.getStudyModel().setValue(ExampleData.buildStudyChouinard());
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getCriterionSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.SMAA);
	
		for (OutcomeMeasure om: pm.getCriteriaListModel()) {
			assertTrue(pm.getCriterionEnabledModel(om).getValue());
		}
	}
	
	@Test
	public void testSelectedMetaAnalysesRestrictAlternatives() {
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.SMAA); 
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		
		// First set a network-analysis with >3 alternatives
		pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysisHamD());
		// Select all alternatives
		for (DrugSet d : pm.getAlternativesListModel()) {
			pm.getAlternativeSelectedModel(d).setValue(true);
		}
		
		// Change to a pair-wise meta-analysis (RandomEffectsMetaAnalysis)
		pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildMetaAnalysisHamd());
		
		// The non-included alternative should be deselected and disabled.
		assertTrue(pm.getAlternativeEnabledModel(new DrugSet(ExampleData.buildDrugFluoxetine())).getValue());
		assertTrue(pm.getAlternativeEnabledModel(new DrugSet(ExampleData.buildDrugParoxetine())).getValue());
		assertFalse(pm.getAlternativeEnabledModel(new DrugSet(ExampleData.buildDrugSertraline())).getValue());
		assertTrue(pm.getAlternativeSelectedModel(new DrugSet(ExampleData.buildDrugFluoxetine())).getValue());
		assertTrue(pm.getAlternativeSelectedModel(new DrugSet(ExampleData.buildDrugParoxetine())).getValue());
		assertFalse(pm.getAlternativeSelectedModel(new DrugSet(ExampleData.buildDrugSertraline())).getValue());
	}
	
	@Test
	public void testSelectedMetaAnalysesRestrictAlternativesLyndOBrien() {
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.LyndOBrien); 
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);

		// First set a network-analysis with >3 alternatives
		pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysisHamD());
		pm.getAlternativeSelectedModel(d_sertrSet).setValue(true);
		pm.getAlternativeSelectedModel(d_fluoxSet).setValue(true);
		
		// Change to a pair-wise meta-analysis (RandomEffectsMetaAnalysis)
		pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildMetaAnalysisHamd());
		
		// The non-included alternative should be deselected and disabled.
		assertTrue(pm.getAlternativeEnabledModel(d_fluoxSet).getValue());
		assertTrue(pm.getAlternativeEnabledModel(d_paroxSet).getValue());
		assertFalse(pm.getAlternativeEnabledModel(d_sertrSet).getValue());
		assertTrue(pm.getAlternativeSelectedModel(d_fluoxSet).getValue());
		assertFalse(pm.getAlternativeSelectedModel(d_paroxSet).getValue());
		assertFalse(pm.getAlternativeSelectedModel(d_sertrSet).getValue());
	}
	
	@Test
	public void testLyndOBrienAlternativesRestrictions(){
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.LyndOBrien);
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getCriterionSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);

		// set a couple of network-analyses with >3 alternatives
		pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysisHamD());
		//d_pm.getMetaAnalysesSelectedModel(ExampleData.buildAdverseEventDiarrhea()).setValue(ExampleData.buildNetworkMetaAnalysis());
		// Select two alternatives
		pm.getAlternativeSelectedModel(d_sertrSet).setValue(true);
		pm.getAlternativeSelectedModel(d_fluoxSet).setValue(true);
		
		// The other alternative should be disabled.
		assertFalse(pm.getAlternativeEnabledModel(d_paroxSet).getValue());
	}

	@Test
	public void testLyndOBrienOutcomesRestrictions(){
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.LyndOBrien); 
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getCriterionSelectedModel(ExampleData.buildEndpointCgi()).setValue(true);
		assertFalse(pm.getCriterionEnabledModel(ExampleData.buildAdverseEventConvulsion()).getValue());
		pm.getCriterionSelectedModel(ExampleData.buildEndpointCgi()).setValue(false);
		assertTrue(pm.getCriterionEnabledModel(ExampleData.buildAdverseEventConvulsion()).getValue());
	}
	
	@Test
	public void testLyndOBrienOutcomesRestrictionsSingleStudy(){
		d_pm.getEvidenceTypeHolder().setValue(BRAType.SingleStudy);
		StudyCriteriaAndAlternativesPresentation pm = d_pm.getStudyBRPresentation();
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.LyndOBrien); 
		pm.getStudyModel().setValue(d_study);
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getCriterionSelectedModel(ExampleData.buildEndpointCgi()).setValue(true);
		assertFalse(pm.getCriterionEnabledModel(ExampleData.buildAdverseEventConvulsion()).getValue());
		pm.getCriterionSelectedModel(ExampleData.buildEndpointCgi()).setValue(false);
		assertTrue(pm.getCriterionEnabledModel(ExampleData.buildAdverseEventConvulsion()).getValue());
	}
	
	@Test
	public void testSMAANoRestrictions(){
		MetaCriteriaAndAlternativesPresentation pm = d_pm.getMetaBRPresentation();
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.SMAA); 
		
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getCriterionSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);

		// set a couple of network-analyses with >3 alternatives
		pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysisHamD());
		// Select two alternatives
		pm.getAlternativeSelectedModel(d_paroxSet).setValue(true);
		pm.getAlternativeSelectedModel(d_fluoxSet).setValue(true);
		
		// The other alternative should be enabled.
		assertTrue(pm.getAlternativeEnabledModel(d_sertrSet).getValue());
	}
	
	@Test
	public void testSMAANoRestrictionsSingleStudy(){
		d_pm.getEvidenceTypeHolder().setValue(BRAType.SingleStudy);
		StudyCriteriaAndAlternativesPresentation pm = d_pm.getStudyBRPresentation();
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.SMAA); 
		Study study = ExampleData.buildStudyFava2002().clone();
		pm.getStudyModel().setValue(study);
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);

		// Select two alternatives
		pm.getAlternativeSelectedModel(study.getArms().get(0)).setValue(true);
		pm.getAlternativeSelectedModel(study.getArms().get(2)).setValue(true);
		
		// The other alternative should be enabled.
		assertTrue(pm.getAlternativeEnabledModel(study.getArms().get(1)).getValue());
	}
	
	@Test
	public void testChangeStudyShouldClearValues() {
		d_pm.getEvidenceTypeHolder().setValue(BRAType.SingleStudy);
		StudyCriteriaAndAlternativesPresentation pm = d_pm.getStudyBRPresentation();
		pm.getStudyModel().setValue(d_study);
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		pm.getCriterionSelectedModel(ExampleData.buildEndpointCgi()).setValue(true);
		
		pm.getAlternativeSelectedModel(d_study.getArms().get(0)).setValue(true);
		pm.getAlternativeSelectedModel(d_study.getArms().get(1)).setValue(true);
		
		pm.getStudyModel().setValue(ExampleData.buildStudyBennie().clone());
		assertTrue(pm.getSelectedCriteria().isEmpty());
		assertTrue(pm.getSelectedAlternatives().isEmpty());
		assertFalse((Boolean)pm.getCompleteModel().getValue());
	}
	
	@Test
	public void testNoDataShouldDisableAlternatives() {
		Study study = ExampleData.buildStudyFava2002().clone();
		study.getMeasurement(ExampleData.buildEndpointHamd(), study.getArms().get(0)).setSampleSize(null);
		study.getEndpoints().add(new StudyOutcomeMeasure<Endpoint>(ExampleData.buildEndpointCgi())); 
		
		d_pm.getEvidenceTypeHolder().setValue(BRAType.SingleStudy);
		StudyCriteriaAndAlternativesPresentation pm = d_pm.getStudyBRPresentation();
		
		
		pm.getStudyModel().setValue(study);

		// break measurement in 1 arm
		pm.getCriterionSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		
		// that arm should now be disabled
		assertFalse(pm.getAlternativeEnabledModel(study.getArms().get(0)).getValue());
		assertTrue(pm.getAlternativeEnabledModel(study.getArms().get(1)).getValue());

		// select entirely missing outcomemeasure; criterion should now be disabled

		pm.getCriterionSelectedModel(ExampleData.buildEndpointCgi()).setValue(true);
		assertFalse(pm.getCriterionSelectedModel(ExampleData.buildEndpointCgi()).getValue());
	}
	
	@Test
	public void testDecisionContext() {
		assertNull(d_pm.getDecisionContext());
		d_pm.getIncludeDescriptivesModel().setValue(true);
		assertNotNull(d_pm.getDecisionContext());
		assertSame(d_pm.getDecisionContext(), d_pm.getDecisionContext());
		
		d_pm.getDecisionContextFields().get(0).getModel().setValue("Test");
		assertEquals("Test", d_pm.getDecisionContext().getTherapeuticContext());
	}
}