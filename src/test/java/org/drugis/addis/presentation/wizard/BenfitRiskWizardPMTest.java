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
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.presentation.ValueHolder;
import org.junit.Before;
import org.junit.Test;

public class BenfitRiskWizardPMTest {

	private DomainImpl d_domain;
	private BenefitRiskWizardPM d_pm;
	private Indication d_indication;

	@Before
	public void setUp() throws NullPointerException, IllegalArgumentException, EntityIdExistsException {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pm = new BenefitRiskWizardPM(d_domain); 
		d_indication = ExampleData.buildIndicationDepression();
		
		d_domain.addMetaAnalysis(ExampleData.buildNetworkMetaAnalysis());
		d_domain.addMetaAnalysis(ExampleData.buildNetworkMetaAnalysisAlternative());
		d_domain.addMetaAnalysis(ExampleData.buildMetaAnalysisConv());
		d_domain.addMetaAnalysis(ExampleData.buildMetaAnalysisHamd());
		d_domain.addMetaAnalysis(ExampleData.buildNetworkMetaAnalysisCgi());
	
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
	}
	
	@Test
	public void testOutcomesListModelIncludesOutcomes() { // FIXME: should be on basis of analyses, not studies.
		for (Indication indication : d_domain.getIndications()) {
			TreeSet<OutcomeMeasure> expected = new TreeSet<OutcomeMeasure>();
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
		assertTrue(origModel.getValue());
	}
	
	@Test
	public void testOutcomeSelectedSingleAnalysisShouldSelect() {
		OutcomeMeasure om = ExampleData.buildEndpointCgi();
		d_pm.getOutcomeSelectedModel(om).setValue(true);
		assertTrue(d_pm.getMetaAnalyses(om).size() == 1);
		assertNotNull(d_pm.getMetaAnalysesSelectedModel(om).getValue());	
	}
	
	@Test
	public void testOutcomeSelectedMultipleAnalysisShouldNotSelect() {
		OutcomeMeasure om = ExampleData.buildEndpointHamd();
		d_pm.getOutcomeSelectedModel(om).setValue(true);
		assertTrue(d_pm.getMetaAnalyses(om).size() > 1);
		assertNull(d_pm.getMetaAnalysesSelectedModel(om).getValue());	
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
	public void testCompletedModelFalseWithLessThanTwoDrugs() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getOutcomeSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysis());
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(ExampleData.buildMetaAnalysisConv());
		assertTrue(d_pm.getAlternativeSelectedModel(ExampleData.buildDrugFluoxetine()).getValue());
		d_pm.getAlternativeSelectedModel(ExampleData.buildDrugParoxetine()).setValue(false);
		assertFalse(d_pm.getCompleteModel().getValue());
	}

	@Test
	public void testCompletedModelFalseWithLessThanTwoCriteria() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildMetaAnalysisHamd());
		assertTrue(d_pm.getAlternativeSelectedModel(ExampleData.buildDrugFluoxetine()).getValue());
		assertTrue(d_pm.getAlternativeSelectedModel(ExampleData.buildDrugParoxetine()).getValue());
		assertFalse(d_pm.getCompleteModel().getValue());
	}

	@Test
	public void testCompletedModelFalseWithCriteriaWithoutAnalysis() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildMetaAnalysisHamd());
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointCgi()).setValue(true);
		assertTrue(d_pm.getCompleteModel().getValue());
		d_pm.getOutcomeSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		assertFalse(d_pm.getCompleteModel().getValue());
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(ExampleData.buildMetaAnalysisConv());
		assertTrue(d_pm.getCompleteModel().getValue());
	}

	@Test
	public void testCompletedModelTrueWithTwoDrugsTwoCriteria() {
		d_pm.getIndicationModel().setValue(d_indication);
		d_pm.getOutcomeSelectedModel(ExampleData.buildEndpointHamd()).setValue(true);
		d_pm.getOutcomeSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(true);
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd()).setValue(ExampleData.buildNetworkMetaAnalysis());
		d_pm.getMetaAnalysesSelectedModel(ExampleData.buildAdverseEventConvulsion()).setValue(ExampleData.buildMetaAnalysisConv());
		assertTrue(d_pm.getAlternativeSelectedModel(ExampleData.buildDrugFluoxetine()).getValue());
		assertTrue(d_pm.getAlternativeSelectedModel(ExampleData.buildDrugParoxetine()).getValue());
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
		assertFalse(d_pm.getCompleteModel().getValue());
	}
}
