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

package org.drugis.addis.mtc;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.presentation.NetworkMetaAnalysisPresentation;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.mtc.Treatment;
import org.jfree.data.category.CategoryDataset;
import org.junit.Before;
import org.junit.Test;

// rows are drugs, cols are ranks


// FIXME: handle with mock (no need for this to be IT)
public class RankProbabilityDataSetIT {
	private NetworkMetaAnalysis d_nma;
	private CategoryDataset d_dataSet;

	@Before
	public void setUp() {
		PresentationModelFactory pmf = new PresentationModelFactory(new DomainImpl());
		d_nma = ExampleData.buildNetworkMetaAnalysis();
		d_nma.getConsistencyModel().run();
		NetworkMetaAnalysisPresentation pm = (NetworkMetaAnalysisPresentation) pmf.getModel(d_nma);
		d_dataSet = pm.getRankProbabilityDataset();
	}
	
	@Test
	public void testGetColumnIndex() {
		Integer key = 3;
		assertEquals(key - 1, d_dataSet.getColumnIndex(key) );
	}
	
	@Test
	public void testGetColumnIndexThrows() {
		assertEquals(-1, d_dataSet.getColumnIndex(10000));
	}

	@Test
	public void testGetColumnKey() {
		Integer index = 2;
		assertEquals(index+1, d_dataSet.getColumnKey(index));
	}

	@Test
	public void testGetColumnKeys() {
		ArrayList<Integer> columnKeys = new ArrayList<Integer>();
		for(int i = 0; i < d_nma.getIncludedDrugs().size(); ++i)
			columnKeys.add(i+1);
		assertEquals(columnKeys, d_dataSet.getColumnKeys());
	}

	@Test
	public void testGetRowIndex() {
		Drug key = ExampleData.buildDrugFluoxetine();
		assertEquals(0, d_dataSet.getRowIndex(key));
		key = ExampleData.buildDrugParoxetine();
		assertEquals(1, d_dataSet.getRowIndex(key));
		key = ExampleData.buildDrugSertraline();
		assertEquals(2, d_dataSet.getRowIndex(key));
	}

	@Test
	public void testGetRowKey() {
		assertEquals(ExampleData.buildDrugParoxetine(), d_dataSet.getRowKey(1));
	}

	@Test
	public void testGetRowKeys() {
		assertEquals(d_nma.getIncludedDrugs(), d_dataSet.getRowKeys());
	}
	
	@Test
	public void testGetRowCount() {
		assertEquals (d_nma.getIncludedDrugs().size(), d_dataSet.getRowCount());
	}

	@Test
	public void testGetColumnCount() {
		assertEquals (d_nma.getIncludedDrugs().size(), d_dataSet.getColumnCount());
	}

	@Test
	public void testGetValue() {
		for (int row =0; row < d_dataSet.getRowCount(); ++row){
			for (int col =0; col < d_dataSet.getColumnCount(); ++col) {
				Drug rowKey = (Drug) d_dataSet.getRowKey(row); // rowKey is the Drug
				int  colKey = col+1; // colKey is the Rank

				String drugName = rowKey.toString();	
				Treatment treatment = d_nma.getBuilder().getTreatment(drugName);

				Double expected = d_nma.getConsistencyModel().rankProbability(treatment, colKey);
				assertEquals(expected, d_dataSet.getValue(rowKey, colKey));
			}
		}
	}
}