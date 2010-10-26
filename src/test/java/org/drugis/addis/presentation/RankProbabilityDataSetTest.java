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

package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.jfree.data.category.CategoryDataset;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class RankProbabilityDataSetTest {
	private NetworkMetaAnalysis d_nma;
	private CategoryDataset d_dataSet;

	@Before
	public void setUp() throws InterruptedException {
		// FIXME: set up dataset on basis of RankProbabilitySummary
	}
	
	@Test @Ignore
	public void testGetRowIndex() {
		Integer key = 3;
		assertEquals(key - 1, d_dataSet.getRowIndex("Rank " + key) );
	}
	
	@Test @Ignore
	public void testGetColumnIndex() {
		Drug key = ExampleData.buildDrugFluoxetine();
		assertEquals(0, d_dataSet.getColumnIndex(key));
		key = ExampleData.buildDrugParoxetine();
		assertEquals(1, d_dataSet.getColumnIndex(key));
		key = ExampleData.buildDrugSertraline();
		assertEquals(2, d_dataSet.getColumnIndex(key));
	}
	
	@Test @Ignore
	public void testGetRowIndexThrows() {
		assertEquals(-1, d_dataSet.getRowIndex(10000));
	}
	
	@Test @Ignore
	public void testGetColumnIndexThrows() {
		assertEquals(-1, d_dataSet.getColumnIndex(10000));
	}
	
	@Test @Ignore
	public void testGetRowKey() {
		Integer index = 2;
		assertEquals("Rank " + (index+1), d_dataSet.getRowKey(index));
	}

	@Test @Ignore
	public void testGetColumnKey() {
		assertEquals(ExampleData.buildDrugParoxetine(), d_dataSet.getColumnKey(1));
	}
	
	@Test @Ignore
	public void testGetRowKeys() {
		ArrayList<String> columnKeys = new ArrayList<String>();
		for(int i = 0; i < d_nma.getIncludedDrugs().size(); ++i)
			columnKeys.add("Rank " + (i+1));
		assertEquals(columnKeys, d_dataSet.getRowKeys());
	}

	@Test @Ignore
	public void testGetColumnKeys() {
		assertEquals(d_nma.getIncludedDrugs(), d_dataSet.getColumnKeys());
	}
	
	@Test @Ignore
	public void testGetRowCount() {
		assertEquals (d_nma.getIncludedDrugs().size(), d_dataSet.getRowCount());
	}

	@Test @Ignore
	public void testGetColumnCount() {
		assertEquals (d_nma.getIncludedDrugs().size(), d_dataSet.getColumnCount());
	}
//
//	@Test
//	public void testGetValue() {
//		for (int row =0; row < d_dataSet.getRowCount(); ++row){
//			for (int col =0; col < d_dataSet.getColumnCount(); ++col) {
//				Drug colKey = (Drug) d_dataSet.getColumnKey(col); // colKey is the Drug
//				int  rowKey = col+1; // rowKey is the Rank
//
//				String drugName = colKey.toString();	
//				Treatment treatment = d_nma.getBuilder().getTreatment(drugName);
//
//				Double expected = d_nma.getConsistencyModel().rankProbability(treatment, rowKey);
//				assertEquals(expected, d_dataSet.getValue("Rank " +rowKey, colKey));
//			}
//		}
//	}
}