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

package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.mocks.MockNetworkMetaAnalysis;
import org.drugis.common.JUnitUtil;
import org.drugis.common.threading.TaskUtil;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.MCMCResultsEvent;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.summary.QuantileSummary;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class NetworkTableModelTest {
	private PresentationModelFactory d_pmf;
	private NetworkRelativeEffectTableModel d_tableModel;
	private NetworkMetaAnalysis d_analysis;

	@Before
	public void setUp() {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		d_analysis = buildMockNetworkMetaAnalysis();
		d_pmf = new PresentationModelFactory(domain);
		d_tableModel = new NetworkRelativeEffectTableModel((NetworkMetaAnalysisPresentation)d_pmf.getModel(d_analysis), d_analysis.getConsistencyModel());
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_analysis.getIncludedDrugs().size(), d_tableModel.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(d_analysis.getIncludedDrugs().size(), d_tableModel.getRowCount());
	}

	@Test
	public void testValueAt() {
		assertTrue(d_tableModel.getColumnCount() > 0);
		assertTrue(d_tableModel.getRowCount() > 0);

		assertEquals(null, d_tableModel.getDescriptionAt(0, 0));
		assertEquals(null, d_tableModel.getDescriptionAt(1, 1));
		assertEquals(null, d_tableModel.getDescriptionAt(2, 2));
		assertEquals(d_analysis.getIncludedDrugs().get(0), d_tableModel.getValueAt(0, 0));
		assertEquals(d_analysis.getIncludedDrugs().get(1), d_tableModel.getValueAt(1, 1));
		assertEquals(d_analysis.getIncludedDrugs().get(2), d_tableModel.getValueAt(2, 2));

		
		ConsistencyModel consModel = d_analysis.getConsistencyModel();
		Parameter relativeEffect01 = consModel.getRelativeEffect(d_analysis.getTreatment(d_analysis.getIncludedDrugs().get(0)), d_analysis.getTreatment(d_analysis.getIncludedDrugs().get(1)));
		Parameter relativeEffect10 = consModel.getRelativeEffect(d_analysis.getTreatment(d_analysis.getIncludedDrugs().get(1)), d_analysis.getTreatment(d_analysis.getIncludedDrugs().get(0)));
		Parameter relativeEffect20 = consModel.getRelativeEffect(d_analysis.getTreatment(d_analysis.getIncludedDrugs().get(2)), d_analysis.getTreatment(d_analysis.getIncludedDrugs().get(0)));

		assertSame(d_analysis.getQuantileSummary(consModel, relativeEffect01), d_tableModel.getValueAt(0, 1));
		assertEquals("\"Paroxetine\" relative to \"Fluoxetine\"", d_tableModel.getDescriptionAt(0, 1));
		assertSame(d_analysis.getQuantileSummary(consModel, relativeEffect10), d_tableModel.getValueAt(1, 0));
		assertEquals("\"Fluoxetine\" relative to \"Paroxetine\"", d_tableModel.getDescriptionAt(1, 0));
		assertSame(d_analysis.getQuantileSummary(consModel, relativeEffect20), d_tableModel.getValueAt(2, 0));
		assertEquals("\"Fluoxetine\" relative to \"Sertraline\"", d_tableModel.getDescriptionAt(2, 0));

	}
	
	
	@Test
	public void testUpdateFiresTableDataChangedEvent() throws InterruptedException {
		ConsistencyModel model = d_analysis.getConsistencyModel();
		TaskUtil.run(model.getActivityTask());
		Treatment d1 = d_analysis.getTreatment(d_analysis.getIncludedDrugs().get(0));
		Treatment d2 = d_analysis.getTreatment(d_analysis.getIncludedDrugs().get(1));
		QuantileSummary normalSummary = d_analysis.getQuantileSummary(model, model.getRelativeEffect(d1, d2));
		
		TableModelListener mock = JUnitUtil.mockTableModelListener(new TableModelEvent(d_tableModel));
		d_tableModel.addTableModelListener(mock);
		
		// fire some event
		normalSummary.resultsEvent(new MCMCResultsEvent(null));
		
		EasyMock.verify(mock);
	}
	

	public static NetworkMetaAnalysis buildMockContinuousNetworkMetaAnalysis() {
		List<Study> studies = Arrays.asList(new Study[] {
				ExampleData.buildStudyBennie(), ExampleData.buildStudyChouinard()});
		List<DrugSet> drugs = Arrays.asList(new DrugSet[] {
				new DrugSet(ExampleData.buildDrugFluoxetine()),
				new DrugSet(ExampleData.buildDrugParoxetine()), 
				new DrugSet(ExampleData.buildDrugSertraline())});
		
		NetworkMetaAnalysis analysis = new MockNetworkMetaAnalysis("Test Network", 
				ExampleData.buildIndicationDepression(), ExampleData.buildEndpointCgi(),
				studies, drugs, ExampleData.buildMap(studies, drugs));
		
		return analysis;
	}

	public static NetworkMetaAnalysis buildMockNetworkMetaAnalysis() {
		List<Study> studies = Arrays.asList(new Study[] {
				ExampleData.buildStudyBennie(), ExampleData.buildStudyChouinard(), ExampleData.buildStudyDeWilde(), ExampleData.buildStudyFava2002()});
		List<DrugSet> drugs = Arrays.asList(new DrugSet[] {
				new DrugSet(ExampleData.buildDrugFluoxetine()),
				new DrugSet(ExampleData.buildDrugParoxetine()), 
				new DrugSet(ExampleData.buildDrugSertraline())});
		
		NetworkMetaAnalysis analysis = new MockNetworkMetaAnalysis("Test Network", 
				ExampleData.buildIndicationDepression(), ExampleData.buildEndpointHamd(),
				studies, drugs, ExampleData.buildMap(studies, drugs));
		
		return analysis;
	}
}