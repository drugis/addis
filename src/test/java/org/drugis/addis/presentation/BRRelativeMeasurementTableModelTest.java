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

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.junit.Before;
import org.junit.Test;

public class BRRelativeMeasurementTableModelTest {
	private BRRelativeMeasurementTableModel d_pm;
	private MetaBenefitRiskAnalysis d_brAnalysis;

	@Before
	public void setUp() {
		d_brAnalysis = ExampleData.buildMetaBenefitRiskAnalysis();
		d_pm = new BRRelativeMeasurementTableModel(d_brAnalysis);
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_brAnalysis.getDrugs().size(), d_pm.getColumnCount());
	}
	
	@Test
	public void testGetRowCount() {
		assertEquals(d_brAnalysis.getCriteria().size(), d_pm.getRowCount());
	}
	
	@Test
	public void testGetColumnNames() {
		List<Drug> drugs = getNonBaselines();
		for (int i = 0; i < drugs.size(); ++i) {
			assertEquals(drugs.get(i).getName(), d_pm.getColumnName(i + 1));
		}
	}

	private List<Drug> getNonBaselines() {
		List<Drug> drugs = new ArrayList<Drug>(d_brAnalysis.getDrugs());
		drugs.remove(d_brAnalysis.getBaseline());
		return drugs;
	}
	
	@Test
	public void testGetOutcomeNames() {
		List<OutcomeMeasure> outcomeMeasures = d_brAnalysis.getCriteria();
		for (int j = 0; j < outcomeMeasures.size(); ++j) {
			assertEquals(outcomeMeasures.get(j).toString(), d_pm.getValueAt(j, 0));
		}
	}

	@Test
	public void testGetValueAt() {
		List<Drug> drugs = getNonBaselines();
		for (int i = 0; i < drugs.size(); ++i) {
			for (int j=0; j < d_brAnalysis.getCriteria().size(); ++j) {
				Drug drug = drugs.get(i);
				OutcomeMeasure om = d_brAnalysis.getCriteria().get(j);
				GaussianBase expected = (GaussianBase) d_brAnalysis.getRelativeEffectDistribution(drug, om);
				GaussianBase actual = (GaussianBase) d_pm.getValueAt(j, i + 1);
				assertEquals(expected.getMu(), actual.getMu(), 0.000001);
				assertEquals(expected.getSigma(), actual.getSigma(), 0.000001);
			}
		}
	}
}
