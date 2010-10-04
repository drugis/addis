/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.wizard.BenefitRiskWizardPM.BRAType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BenefitRiskWizardPMTest {

	private DomainImpl d_domain;
	private BenefitRiskWizardPM d_pm;
	private Indication d_indication;
	private Study d_study;

	@Before
	public void setUp() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pm = new BenefitRiskWizardPM(d_domain); 
		d_indication = ExampleData.buildIndicationDepression();
		d_study = ExampleData.buildStudyChouinard();
		
		d_domain.addMetaAnalysis(ExampleData.buildNetworkMetaAnalysis());
		d_domain.addMetaAnalysis(ExampleData.buildNetworkMetaAnalysisAlternative());
		d_domain.addMetaAnalysis(ExampleData.buildMetaAnalysisConv());
		d_domain.addMetaAnalysis(ExampleData.buildMetaAnalysisHamd());
		d_domain.addMetaAnalysis(ExampleData.buildNetworkMetaAnalysisCgi());
		
		d_domain.addStudy(d_study);
	
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
	}
	
	@Test
	public void testOutcomesListModelIncludesOutcomes() { // FIXME: should be on basis of analyses, not studies.
		for (Indication indication : d_domain.getIndications()) {
			TreeSet<OutcomeMeasure> expected = new TreeSet<OutcomeMeasure>();
			/*
			for (MetaAnalysis analysis : d_domain.getMetaAnalyses()) {
				expected.add(analysis.getOutcomeMeasure());
			}*/
			for (Study s : d_domain.getStudies(indication).getValue()) 
				expected.addAll(s.getOutcomeMeasures());
			d_pm.getIndicationModel().setValue(indication);
			assertAllAndOnly(expected, d_pm.getOutcomesListModel().getValue());
		}
	}
	
	@Test
	public void testMetaAnalysesForEachOutcome() {
		d_pm.getIndicationModel().setValue(d_indication);
		for (OutcomeMeasure om : d_pm.getOutcomesListModel().getValue()) {
			List<MetaAnalysis> analyses = new ArrayList<MetaAnalysis>();
			for (MetaAnalysis analysis : d_domain.getMetaAnalyses()) {
				if (om.equals(analysis.getOutcomeMeasure()))
					analyses.add(analysis);
			}
			assertAllAndOnly(analyses, d_pm.getMetaAnalyses(om));
		}
	}
	
	@Test
	public void testOutcomeSelectedModelKeepsChanges() {
		OutcomeMeasure om = ExampleData.buildEndpointHamd();
		ValueHolder<Boolean> origModel = d_pm.getOutcomeSelectedModel(om);
		assertFalse(origModel.getValue());
		d_pm.getOutcomeSelectedModel(om).setValue(true);
		assertEquals(d_pm.getOutcomeSelectedModel(om).getValue(), origModel.getValue());
	}
	
	@Test
	public void testArmSelectedModelKeepsChanges() {
		Arm a = d_study.getArms().get(0);
		ValueHolder<Boolean> origArm = d_pm.getArmSelectedModel(a);
		assertFalse(origArm.getValue());
		d_pm.getArmSelectedModel(a).setValue(true);
		assertEquals(d_pm.getArmSelectedModel(a).getValue(), origArm.getValue());
	}

	@Test
	public void testOutcomeSelectedMultipleAnalysisShouldNotSelect() {
		OutcomeMeasure om = ExampleData.buildEndpointHamd();
		d_pm.getOutcomeSelectedModel(om).setValue(true);
		assertTrue(d_pm.getMetaAnalyses(om).size() > 1);
		assertNull(d_pm.getMetaAnalysesSelectedModel(om).getValue());	
	}
	
	@Test
	public void testOutcomeSelectedSingleAnalysisShouldSelect() {
		OutcomeMeasure om = ExampleData.buildEndpointCgi();
		d_pm.getOutcomeSelectedModel(om).setValue(true);
		assertTrue(d_pm.getMetaAnalyses(om).size() == 1);
		assertNotNull(d_pm.getMetaAnalysesSelectedModel(om).getValue());	
	}
	
	@Test
	public void testMetaAnalysesSelectedModelKeepsChanges() {
		ValueHolder<MetaAnalysis> metaAnal1 = d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd());
		assertNull(metaAnal1.getValue());
		
		metaAnal1.setValue(ExampleData.buildNetworkMetaAnalysis());
		assertEquals(ExampleData.buildNetworkMetaAnalysis(), metaAnal1.getValue());
		
		ValueHolder<MetaAnalysis> metaAnal2 = d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd());
		assertEquals(metaAnal1.getValue(), metaAnal2.getValue());
	}
	
	@Test
	public void testAlternativesListModelShouldBeUnionOfAnalyzedDrugs() {
		List<Drug> expected = new ArrayList<Drug>();
		for (MetaAnalysis ma : d_domain.getMetaAnalyses()) {
			if (ma.getIndication().equals(d_indication))
				expected.addAll(ma.getIncludedDrugs());
		}
		
		assertAllAndOnly(expected, d_pm.getAlternativesListModel().getValue());
	}
	
	@Test
	public void testAlternativeEnabledModelShouldReflectInclusion() {
		
		for (Drug d : d_pm.getAlternativesListModel().getValue()) {
			assertEquals(false, d_pm.getAlternativeEnabledModel(d).getValue());
		}
		
		d_pm.getIndicationModel().setValue(d_indication);
		Endpoint outcomeM = ExampleData.buildEndpointHamd();
		d_pm.getOutcomeSelectedModel(outcomeM).setValue(true);
		d_pm.getMetaAnalysesSelectedModel(outcomeM).setValue(ExampleData.buildNetworkMetaAnalysis());

		assertTrue(d_pm.getAlternativesListModel().getValue().size() > 0);
		
		for (Drug d : d_pm.getAlternativesListModel().getValue()) {
			boolean expected = true;
			for (ValueHolder<MetaAnalysis> mah : d_pm.getSelectedMetaAnalysisHolders())
				if (!mah.getValue().getIncludedDrugs().contains(d))
					expected = false;
			
			assertEquals(expected, d_pm.getAlternativeEnabledModel(d).getValue());
		}
	}
	
	@Test
	public void testGetAlternativeSelectedModel() {
		Drug d = ExampleData.buildDrugParoxetine();
		ValueHolder<Boolean> actual = d_pm.getAlternativeSelectedModel(d);
		assertEquals(false,actual.getValue());
		actual.setValue(true);
		assertEquals(true,actual.getValue());
	}
	
	@Test
	public void testGetArmsSelectedModel() {
		Arm a = d_study.getArms().get(0);
		
		ValueHolder<Boolean> actual = d_pm.getArmSelectedModel(a);
		assertEquals(false,actual.getValue());
		actual.setValue(true);
		assertEquals(true,actual.getValue());
	}
		
	@Test
	public void testCompletedMetaModelFalseWithLessThanTwoDrugs() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getOutcomeSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysis());
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(ExampleData.buildMetaAnalysisConv());
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugFluoxetine()).setValue(true);
		assertTrue(d_pm.getAlternativeSelectedModel(ExampleData.buildDrugFluoxetine()).getValue());
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugParoxetine()).setValue(false);
		assertFalse(d_pm.getCompleteModel().getValue());
	}

	@Test
	public void testCompletedMetaModelFalseWithLessThanTwoCriteria() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildMetaAnalysisHamd());
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugFluoxetine()).setValue(true);
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugParoxetine()).setValue(true);
		assertFalse(d_pm.getCompleteModel().getValue());
	}

	@Test
	public void testCompletedStudyFalseWithLessThanTwoDrugs() {
		d_pm.getIndicationModel().setValue(d_indication);

		// note: using local copy of chouinard (has 2 arms) so that test won't fail if setup is changed to different study
		Study local = ExampleData.buildStudyChouinard();
		d_pm.getStudyModel().setValue(local);
		
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointCgi()).setValue(true);
		
		d_pm.getArmSelectedModel(local.getArms().get(1)).setValue(true);
		
		assertFalse(d_pm.getCompleteModel().getValue());
	}
	
	@Test
	public void testCompletedSingleStudyFalseWithLessThanTwoCriteria() {
		d_pm.getIndicationModel().setValue(d_indication);

		// note: using local copy of chouinard (has 2 arms) so that test won't fail if setup is changed to different study
		Study local = ExampleData.buildStudyChouinard();
		d_pm.getStudyModel().setValue(local);
		
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);

		d_pm.getArmSelectedModel(local.getArms().get(0)).setValue(true);
		d_pm.getArmSelectedModel(local.getArms().get(1)).setValue(true);
		
		assertFalse(d_pm.getCompleteModel().getValue());
	}

	@Test
	public void testCompletedModelFalseWithCriteriaWithoutAnalysis() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildMetaAnalysisHamd());
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointCgi()).setValue(true);
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugFluoxetine()).setValue(true);
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugParoxetine()).setValue(true);
		assertTrue(d_pm.getCompleteModel().getValue());
		d_pm.getOutcomeSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		assertEquals(null, d_pm.getMetaAnalysesSelectedModel(ExampleData.buildAdverseEventConvulsion()).getValue());
		assertFalse(d_pm.getCompleteModel().getValue());
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(ExampleData.buildMetaAnalysisConv());
		assertTrue(d_pm.getCompleteModel().getValue());
	}

	@Test
	public void testCompletedMetaAnalysisModelTrueWithTwoDrugsTwoCriteria() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getOutcomeSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysis());
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(ExampleData.buildMetaAnalysisConv());
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugFluoxetine()).setValue(true);
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugParoxetine()).setValue(true);
		assertTrue(d_pm.getCompleteModel().getValue());
	}

	@Test
	public void testCompletedSingleStudyModelTrueWithTwoDrugsTwoCriteria() {
		d_pm.getIndicationModel().setValue(d_indication);

		// note: using local copy of chouinard (has 2 arms) so that test won't fail if setup is changed to different study
		Study local = ExampleData.buildStudyChouinard();
		d_pm.getStudyModel().setValue(local);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointCgi()).setValue(true);

		d_pm.getArmSelectedModel(local.getArms().get(0)).setValue(true);
		d_pm.getArmSelectedModel(local.getArms().get(1)).setValue(true);
		
		assertTrue(d_pm.getCompleteModel().getValue());
	}
	
	@Test
	public void testChangeIndicationShouldClearValues() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getOutcomeSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysis());
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(ExampleData.buildMetaAnalysisConv());
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationChronicHeartFailure());
		assertTrue(d_pm.getSelectedCriteria().isEmpty());
		assertTrue(d_pm.getSelectedAlternatives().isEmpty());
		assertNull(d_pm.getStudyModel().getValue());
		assertFalse(d_pm.getCompleteModel().getValue());
	}

	@Test
	public void testChangeEvidenceTypeShouldClearValues() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getEvidenceTypeHolder().setValue(BRAType.SingleStudy);
		d_pm.getStudyModel().setValue(d_study);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		
		d_pm.getEvidenceTypeHolder().setValue(BRAType.Synthesis);
		assertTrue(d_pm.getSelectedCriteria().isEmpty());
		assertTrue(d_pm.getSelectedAlternatives().isEmpty());
		assertNull(d_pm.getStudyModel().getValue());
		assertFalse(d_pm.getCompleteModel().getValue());
	}
	
	@Test
	public void testChangeAnalysisTypeShouldClearValues() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.LyndOBrien); 
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugFluoxetine());
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugParoxetine());
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugSertraline());
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysis());
		
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.SMAA);
		assertTrue(d_pm.getSelectedCriteria().isEmpty());
		assertTrue(d_pm.getSelectedAlternatives().isEmpty());
		assertFalse(d_pm.getCompleteModel().getValue());
	}
	
	@Test
	public void testResetSelectedOutcomesShouldCascadeToEnabledModels() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.LyndOBrien); 
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		
		assertTrue(d_pm.getOutcomesListModel().getValue().size() > 2);
		
		for (OutcomeMeasure om: d_pm.getOutcomesListModel().getValue()) {
			assertTrue(d_pm.getOutcomeEnabledModel(om).getValue());
		}
		
		d_pm.getOutcomeSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		
		for (OutcomeMeasure om: d_pm.getOutcomesListModel().getValue()) {
			if (om.equals(ExampleData.buildEndpointHamd()) || om.equals(ExampleData.buildAdverseEventConvulsion())) {
				assertTrue(d_pm.getOutcomeEnabledModel(om).getValue());
			} else {
				assertFalse(d_pm.getOutcomeEnabledModel(om).getValue());
			}
		}
		
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(false);

		for (OutcomeMeasure om: d_pm.getOutcomesListModel().getValue()) {
			assertTrue(d_pm.getOutcomeEnabledModel(om).getValue());
		}
	}
	
	@Test
	public void testChangeEvidenceTypeShouldCascadeToEnabledModels() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getEvidenceTypeHolder().setValue(BRAType.SingleStudy);
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.LyndOBrien); 
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getOutcomeSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);

		d_pm.getEvidenceTypeHolder().setValue(BRAType.Synthesis);
	
		for (OutcomeMeasure om: d_pm.getOutcomesListModel().getValue()) {
			assertTrue(d_pm.getOutcomeEnabledModel(om).getValue());
		}
	}
	
	@Test
	public void testChangeAnalysisTypeShouldCascadeToEnabledModels() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.LyndOBrien); 
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getOutcomeSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.SMAA);
	
		for (OutcomeMeasure om: d_pm.getOutcomesListModel().getValue()) {
			assertTrue(d_pm.getOutcomeEnabledModel(om).getValue());
		}
	}
	
	@Ignore
	@Test
	public void testSelectedMetaAnalysesRestrictAlternatives() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getEvidenceTypeHolder().setValue(BRAType.Synthesis);
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.SMAA); 
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		
		// First set a network-analysis with >3 alternatives
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysis());
		// Select all alternatives
		for (Drug d : d_pm.getAlternativesListModel().getValue()) {
			d_pm.getAlternativeSelectedModel(d).setValue(true);
		}
		
		// Change to a pair-wise meta-analysis (RandomEffectsMetaAnalysis)
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildMetaAnalysisHamd());
		
		// The non-included alternative should be deselected and disabled.
		assertTrue(d_pm.getAlternativeEnabledModel(ExampleData.buildDrugFluoxetine()).getValue());
		assertTrue(d_pm.getAlternativeEnabledModel(ExampleData.buildDrugParoxetine()).getValue());
		assertFalse(d_pm.getAlternativeEnabledModel(ExampleData.buildDrugSertraline()).getValue());
		assertTrue(d_pm.getAlternativeSelectedModel(ExampleData.buildDrugFluoxetine()).getValue());
		assertTrue(d_pm.getAlternativeSelectedModel(ExampleData.buildDrugParoxetine()).getValue());
		assertFalse(d_pm.getAlternativeSelectedModel(ExampleData.buildDrugSertraline()).getValue());
	}
	
	@Ignore
	@Test
	public void testSelectedMetaAnalysesRestrictAlternativesLyndOBrien() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getEvidenceTypeHolder().setValue(BRAType.Synthesis);
		d_pm.getAnalysisTypeHolder().setValue(AnalysisType.LyndOBrien); 
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);

		// First set a network-analysis with >3 alternatives
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysis());
		// Select two alternatives
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugSertraline()).setValue(true);
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugFluoxetine()).setValue(true);
		
		// Change to a pair-wise meta-analysis (RandomEffectsMetaAnalysis)
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildMetaAnalysisHamd());
		
		// The non-included alternative should be deselected and disabled.
		assertTrue(d_pm.getAlternativeEnabledModel(ExampleData.buildDrugFluoxetine()).getValue());
		assertTrue(d_pm.getAlternativeEnabledModel(ExampleData.buildDrugParoxetine()).getValue());
		assertFalse(d_pm.getAlternativeEnabledModel(ExampleData.buildDrugSertraline()).getValue());
		assertTrue(d_pm.getAlternativeSelectedModel(ExampleData.buildDrugFluoxetine()).getValue());
		assertFalse(d_pm.getAlternativeSelectedModel(ExampleData.buildDrugParoxetine()).getValue());
		assertFalse(d_pm.getAlternativeSelectedModel(ExampleData.buildDrugSertraline()).getValue());
	}
	
	// FIXME: also test that single-study criteria/alternatives are cleared.
	
	@Test
	public void testChangeStudyShouldClearValues() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getStudyModel().setValue(d_study);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointCgi()).setValue(true);
		
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugParoxetine()).setValue(true);
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugFluoxetine()).setValue(true);
		
		d_pm.getStudyModel().setValue(ExampleData.buildStudyBennie());
		assertTrue(d_pm.getSelectedCriteria().isEmpty());
		assertTrue(d_pm.getSelectedAlternatives().isEmpty());
		assertFalse(d_pm.getCompleteModel().getValue());
	}	
}