/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import java.util.Arrays;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.DrugSet;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.mocks.MockNetworkMetaAnalysis;
import org.drugis.addis.mocks.MockNormalSummary;
import org.drugis.common.JUnitUtil;
import org.drugis.common.threading.TaskUtil;
import org.drugis.mtc.InconsistencyParameter;
import org.drugis.mtc.summary.NormalSummary;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class NetworkInconsistencyTableModelTest {

	private PresentationModelFactory d_pmf;
	private NetworkInconsistencyFactorsTableModel d_tableModel;
	private NetworkMetaAnalysis d_analysis;
	
	@Before
	public void setUp() {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		d_analysis = NetworkTableModelTest.buildMockNetworkMetaAnalysis();
		d_pmf = new PresentationModelFactory(domain);
		
		NetworkMetaAnalysisPresentation pm = (NetworkMetaAnalysisPresentation) d_pmf.getModel(d_analysis);
		d_tableModel = new NetworkInconsistencyFactorsTableModel((NetworkMetaAnalysisPresentation) pm, d_pmf);
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(2, d_tableModel.getColumnCount());
	}

	@Test
	public void testGetRowCount() throws InterruptedException {
		assertEquals(0, d_tableModel.getRowCount());
		TaskUtil.run(d_analysis.getInconsistencyModel().getActivityTask().getModel().getStartState());
		assertEquals(d_analysis.getInconsistencyFactors().size(), d_tableModel.getRowCount());
	}

	@Test
	public void testValueAt() throws InterruptedException {
		TaskUtil.run(d_analysis.getInconsistencyModel().getActivityTask());
		
		for(int y = 0; y < d_tableModel.getRowCount(); ++y) {
			InconsistencyParameter ip = (InconsistencyParameter)d_analysis.getInconsistencyModel().getInconsistencyFactors().get(y);
			assertEquals("Fluoxetine, Sertraline, Paroxetine", d_tableModel.getValueAt(y, 0));
		
			NormalSummary summary = d_analysis.getNormalSummary(d_analysis.getInconsistencyModel(), ip);
			Gaussian dist = new Gaussian(summary.getMean(), summary.getStandardDeviation());
			assertEquals(d_pmf.getLabeledModel(dist).getLabelModel().getValue(), d_tableModel.getValueAt(y, 1));
		}
	}
	
	@Test
	public void testValueNA() throws InterruptedException {
		assertEquals("N/A", d_tableModel.getValueAt(0, 1));
	}
	
	@Test
	public void testContinuousValueAt() throws InterruptedException {
		NetworkMetaAnalysis d_contAnalysis = buildMockContinuousNetworkMetaAnalysis();
		NetworkInconsistencyFactorsTableModel d_contTableModel = new NetworkInconsistencyFactorsTableModel((NetworkMetaAnalysisPresentation) d_pmf.getModel(d_contAnalysis), d_pmf);
		TaskUtil.run(d_analysis.getInconsistencyModel().getActivityTask());
		
		for(int y = 0; y < d_contTableModel.getRowCount(); ++y) {
			InconsistencyParameter ip = (InconsistencyParameter)d_analysis.getInconsistencyModel().getInconsistencyFactors().get(y);
			assertEquals("Fluoxetine, Sertraline, Paroxetine", d_contTableModel.getValueAt(y, 0));
			NormalSummary icModel = d_analysis.getNormalSummary(d_analysis.getInconsistencyModel(), ip);					
			BasicContinuousMeasurement contMeas = new BasicContinuousMeasurement(icModel.getMean(), icModel.getStandardDeviation(), 0);
			ContinuousMeasurementPresentation<BasicContinuousMeasurement> pm = 
								(ContinuousMeasurementPresentation<BasicContinuousMeasurement>) d_pmf.getModel(contMeas);
			assertEquals(pm.normConfIntervalString(), d_contTableModel.getValueAt(y, 1));
		}
	}
	
	private NetworkMetaAnalysis buildMockContinuousNetworkMetaAnalysis() {
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
	
	@Test
	public void testUpdateFiresTableDataChangedEvent() throws InterruptedException {
		TaskUtil.run(d_analysis.getInconsistencyModel().getActivityTask());
		InconsistencyParameter ip = (InconsistencyParameter)d_analysis.getInconsistencyModel().getInconsistencyFactors().get(0);
		MockNormalSummary summary = (MockNormalSummary) d_analysis.getNormalSummary(d_analysis.getInconsistencyModel(), ip);
		
		TableModelListener mock = JUnitUtil.mockTableModelListener(new TableModelEvent(d_tableModel));
		d_tableModel.addTableModelListener(mock);
		
		// fire some event
		summary.fireChange();
		
		EasyMock.verify(mock);
	}
}