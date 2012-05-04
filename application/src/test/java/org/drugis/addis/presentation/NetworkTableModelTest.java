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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.mocks.MockNetworkMetaAnalysis;
import org.drugis.common.JUnitUtil;
import org.drugis.common.threading.TaskUtil;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.MCMCResultsEvent;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.summary.QuantileSummary;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public class NetworkTableModelTest {

	
	private PresentationModelFactory d_pmf;
	private NetworkTableModel d_tableModel;
	private NetworkMetaAnalysis d_contAnalysis;
	private NetworkMetaAnalysis d_analysis;
	private NetworkTableModel d_contTableModel;

	@Before
	public void setUp() {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		d_analysis = buildMockNetworkMetaAnalysis();
		d_pmf = new PresentationModelFactory(domain);
		d_tableModel = new NetworkTableModel((NetworkMetaAnalysisPresentation)d_pmf.getModel(d_analysis), d_pmf, d_analysis.getConsistencyModel());
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_analysis.getIncludedDrugs().size(), d_tableModel.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(d_analysis.getIncludedDrugs().size(), d_tableModel.getRowCount());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testValueAt() {
		assertTrue(d_tableModel.getColumnCount() > 0);
		assertTrue(d_tableModel.getRowCount() > 0);

		assertEquals(null, d_tableModel.getDescriptionAt(0, 0));
		assertEquals(null, d_tableModel.getDescriptionAt(1, 1));
		assertEquals(null, d_tableModel.getDescriptionAt(2, 2));
		assertEquals(d_analysis.getIncludedDrugs().get(0), ((PresentationModel<Drug>) d_tableModel.getValueAt(0, 0)).getBean());
		assertEquals(d_analysis.getIncludedDrugs().get(1), ((PresentationModel<Drug>) d_tableModel.getValueAt(1, 1)).getBean());
		assertEquals(d_analysis.getIncludedDrugs().get(2), ((PresentationModel<Drug>) d_tableModel.getValueAt(2, 2)).getBean());

		assertEquals("N/A", ((LabeledPresentation) d_tableModel.getValueAt(0, 1)).getLabelModel().getString());
		assertEquals("\"Paroxetine\" relative to \"Fluoxetine\"", d_tableModel.getDescriptionAt(0, 1));
		assertEquals("N/A", ((LabeledPresentation) d_tableModel.getValueAt(1, 0)).getLabelModel().getString());
		assertEquals("\"Fluoxetine\" relative to \"Paroxetine\"", d_tableModel.getDescriptionAt(1, 0));
		assertEquals("N/A", ((LabeledPresentation) d_tableModel.getValueAt(2, 0)).getLabelModel().getString());
		assertEquals("\"Fluoxetine\" relative to \"Sertraline\"", d_tableModel.getDescriptionAt(2, 0));

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtAfterModelRun() throws InterruptedException {
		ConsistencyModel model = d_analysis.getConsistencyModel();
		TaskUtil.run(model.getActivityTask());
		assertTrue(model.getActivityTask().isFinished());

		for(int i = 0; i < d_analysis.getIncludedDrugs().size(); ++i){
			for(int j = 0; j < d_analysis.getIncludedDrugs().size(); ++j){
				if(i == j){
					assertEquals(d_analysis.getIncludedDrugs().get(i), ((PresentationModel<Drug>) d_tableModel.getValueAt(i, j)).getBean());
				} else {
					Treatment t1 = d_analysis.getTreatment(d_analysis.getIncludedDrugs().get(i));
					Treatment t2 = d_analysis.getTreatment(d_analysis.getIncludedDrugs().get(j));
					QuantileSummary relEffect = d_analysis.getQuantileSummary(model, model.getRelativeEffect(t1, t2));
					assertEquals(d_pmf.getLabeledModel(relEffect).getLabelModel().getString(), ((LabeledPresentation) d_tableModel.getValueAt(i, j)).getLabelModel().getString());
				}
			}
		}
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
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueContinuousModelRun() throws InterruptedException {
		d_contAnalysis = buildMockContinuousNetworkMetaAnalysis();
		InconsistencyModel model = d_contAnalysis.getInconsistencyModel();
		d_contTableModel = new NetworkTableModel((NetworkMetaAnalysisPresentation)d_pmf.getModel(d_contAnalysis), d_pmf, model);
		TaskUtil.run(model.getActivityTask());

		for(int i = 0; i < d_contAnalysis.getIncludedDrugs().size(); ++i){
			for(int j = 0; j < d_contAnalysis.getIncludedDrugs().size(); ++j){
				if(i == j){
					assertEquals(d_contAnalysis.getIncludedDrugs().get(i), ((PresentationModel<Drug>) d_contTableModel.getValueAt(i, j)).getBean());
				} else {
					Treatment t1 = d_contAnalysis.getTreatment(d_contAnalysis.getIncludedDrugs().get(i));
					Treatment t2 = d_contAnalysis.getTreatment(d_contAnalysis.getIncludedDrugs().get(j));
					QuantileSummary relEffect = d_contAnalysis.getQuantileSummary(model, model.getRelativeEffect(t1, t2));
					assertEquals(d_pmf.getLabeledModel(relEffect).getLabelModel().getString(), ((LabeledPresentation) d_contTableModel.getValueAt(i, j)).getLabelModel().getString());
				}
			}
		}	
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