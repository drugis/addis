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

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.common.JUnitUtil;
import org.drugis.common.threading.TaskUtil;
import org.drugis.mtc.parameterization.InconsistencyParameter;
import org.drugis.mtc.summary.QuantileSummary;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class NetworkInconsistencyTableModelTest {

	private PresentationModelFactory d_pmf;
	private NetworkInconsistencyFactorsTableModel d_tableModel;
	private NetworkMetaAnalysis d_analysis;
	
	@Before
	public void setUp() {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		d_analysis = NetworkTableModelTest.buildMockNetworkMetaAnalysis();
		d_pmf = new PresentationModelFactory(domain);
		
		NetworkMetaAnalysisPresentation pm = (NetworkMetaAnalysisPresentation) d_pmf.getModel(d_analysis);
		d_tableModel = new NetworkInconsistencyFactorsTableModel((NetworkMetaAnalysisPresentation) pm);
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(2, d_tableModel.getColumnCount());
	}

	@Test
	public void testGetRowCount() throws InterruptedException {
		assertEquals(0, d_tableModel.getRowCount());
		TaskUtil.run(d_analysis.getInconsistencyModel().getActivityTask().getModel().getStartState());
		assertEquals(d_analysis.getInconsistencyModel().getInconsistencyFactors().size(), d_tableModel.getRowCount());
	}

	@Test
	public void testValueAt() throws InterruptedException {
		TaskUtil.run(d_analysis.getInconsistencyModel().getActivityTask());

		InconsistencyParameter ip = (InconsistencyParameter)d_analysis.getInconsistencyModel().getInconsistencyFactors().get(0);
		assertEquals("Fluoxetine, Sertraline, Paroxetine", d_tableModel.getValueAt(0, 0));

		QuantileSummary summary = d_analysis.getInconsistencyModel().getQuantileSummary(ip);
		Object valueAt = d_tableModel.getValueAt(0, 1);
		assertEquals(summary, valueAt);
	}
	
	@Test
	public void testValueNA() {
		assertEquals("N/A", d_tableModel.getValueAt(0, 1));
	}
	
	@Test
	public void testUpdateFiresTableDataChangedEvent() throws InterruptedException {
		TaskUtil.run(d_analysis.getInconsistencyModel().getActivityTask());
		InconsistencyParameter ip = (InconsistencyParameter)d_analysis.getInconsistencyModel().getInconsistencyFactors().get(0);
		QuantileSummary summary = d_analysis.getInconsistencyModel().getQuantileSummary(ip);
		
		TableModelListener mock = JUnitUtil.mockTableModelListener(new TableModelEvent(d_tableModel));
		d_tableModel.addTableModelListener(mock);
		
		// fire some event
		summary.resultsEvent(null);
		
		EasyMock.verify(mock);
	}
}