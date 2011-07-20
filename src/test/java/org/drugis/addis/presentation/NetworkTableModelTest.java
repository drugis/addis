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
import static org.junit.Assert.assertTrue;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.LogGaussian;
import org.drugis.addis.mocks.MockNetworkMetaAnalysis;
import org.drugis.addis.mocks.MockNormalSummary;
import org.drugis.common.JUnitUtil;
import org.drugis.common.threading.TaskUtil;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.Treatment;
import org.drugis.mtc.summary.NormalSummary;
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
		DomainImpl domain = new DomainImpl();
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
		for(int x = 0; x < d_tableModel.getColumnCount(); ++x) {
			for(int y = 0; y < d_tableModel.getRowCount(); ++y) {
				if(x == y){
					assertEquals(d_analysis.getIncludedDrugs().get(x), ((PresentationModel<Drug>) d_tableModel.getValueAt(x, y)).getBean());
					assertEquals(null, d_tableModel.getDescriptionAt(x, y));
				} else {
					assertEquals("N/A", ((LabeledPresentation) d_tableModel.getValueAt(x, y)).getLabelModel().getString());
					String expected = "\""+d_analysis.getIncludedDrugs().get(y)+"\" relative to \""+d_analysis.getIncludedDrugs().get(x)+"\"";
					assertEquals(expected, d_tableModel.getDescriptionAt(x, y));
				}
			}
		}	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtAfterModelRun() throws InterruptedException {
		ConsistencyModel model = d_analysis.getConsistencyModel();
		TaskUtil.run(model.getActivityTask());
		assertTrue(model.getActivityTask().isFinished());

		for(int x = 0; x < d_analysis.getIncludedDrugs().size(); ++x){
			for(int y = 0; y < d_analysis.getIncludedDrugs().size(); ++y){
				if(x == y){
					assertEquals(d_analysis.getIncludedDrugs().get(x), ((PresentationModel<Drug>) d_tableModel.getValueAt(x, y)).getBean());
				} else {
					Treatment t1 = d_analysis.getBuilder().getTreatment(d_analysis.getIncludedDrugs().get(x).getDescription());
					Treatment t2 = d_analysis.getBuilder().getTreatment(d_analysis.getIncludedDrugs().get(y).getDescription());
					NormalSummary relEffect = d_analysis.getNormalSummary(model, model.getRelativeEffect(t1, t2));
					assertEquals(distributionToString(new LogGaussian(relEffect.getMean(), relEffect.getStandardDeviation())), ((LabeledPresentation) d_tableModel.getValueAt(x, y)).getLabelModel().getString());
				}
			}
		}
	}
	
	@Test
	public void testUpdateFiresTableDataChangedEvent() throws InterruptedException {
		ConsistencyModel model = d_analysis.getConsistencyModel();
		TaskUtil.run(model.getActivityTask());
		Treatment d1 = new Treatment(d_analysis.getIncludedDrugs().get(0).getDescription());
		Treatment d2 = new Treatment(d_analysis.getIncludedDrugs().get(1).getDescription());
		MockNormalSummary normalSummary = (MockNormalSummary)d_analysis.getNormalSummary(model, model.getRelativeEffect(d1, d2));
		
		TableModelListener mock = JUnitUtil.mockTableModelListener(new TableModelEvent(d_tableModel));
		d_tableModel.addTableModelListener(mock);
		
		// fire some event
		normalSummary.fireChange();
		
		EasyMock.verify(mock);
	}
	
	private String distributionToString(Distribution distr) {
		DecimalFormat df = new DecimalFormat("##0.00");
		return "" + df.format(distr.getQuantile(0.50)) + " (" + df.format(distr.getQuantile(0.025)) + ", " + df.format(distr.getQuantile(0.975)) +")"; 
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueContinuousModelRun() throws InterruptedException {
		d_contAnalysis = buildMockContinuousNetworkMetaAnalysis();
		InconsistencyModel model = d_contAnalysis.getInconsistencyModel();
		d_contTableModel = new NetworkTableModel((NetworkMetaAnalysisPresentation)d_pmf.getModel(d_contAnalysis), d_pmf, model);
		
		TaskUtil.run(model.getActivityTask());
		d_tableModel = new NetworkTableModel((NetworkMetaAnalysisPresentation)d_pmf.getModel(d_contAnalysis), d_pmf, model);

		for(int x = 0; x < d_contAnalysis.getIncludedDrugs().size(); ++x){
			for(int y = 0; y < d_contAnalysis.getIncludedDrugs().size(); ++y){
				if(x == y){
					assertEquals(d_contAnalysis.getIncludedDrugs().get(x), ((PresentationModel<Drug>) d_contTableModel.getValueAt(x, y)).getBean());
				} else {
					Treatment t1 = d_contAnalysis.getBuilder().getTreatment(d_contAnalysis.getIncludedDrugs().get(x).getDescription());
					Treatment t2 = d_contAnalysis.getBuilder().getTreatment(d_contAnalysis.getIncludedDrugs().get(y).getDescription());
					NormalSummary relEffect = d_contAnalysis.getNormalSummary(model, model.getRelativeEffect(t1, t2));
					assertEquals(distributionToString(new Gaussian(relEffect.getMean(), relEffect.getStandardDeviation())), ((LabeledPresentation) d_tableModel.getValueAt(x, y)).getLabelModel().getString());
				}
			}
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