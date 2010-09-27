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

import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public class BenefitRiskMeasurementTableModelTest {

	private PresentationModelFactory d_pmf;
	private BenefitRiskMeasurementTableModel<Drug> d_pm;
	private MetaBenefitRiskAnalysis d_brAnalysis;

	@Before
	public void setUp() {
		d_pmf = new PresentationModelFactory(new DomainImpl());
		d_brAnalysis = ExampleData.buildMetaBenefitRiskAnalysis();
		d_pm = new BenefitRiskMeasurementTableModel<Drug>(d_brAnalysis, d_brAnalysis.getRelativeMeasurementSource(), d_pmf);
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_brAnalysis.getOutcomeMeasures().size() + 1, d_pm.getColumnCount());
	}
	
	@Test
	public void testGetRowCount() {
		assertEquals(d_brAnalysis.getDrugs().size(), d_pm.getRowCount());
	}
	
	@Test
	public void testGetDrugNames() {
		for (int i=0; i<d_brAnalysis.getDrugs().size(); ++i)
			assertEquals(d_brAnalysis.getDrugs().get(i).getName(), d_pm.getValueAt(i, 0));
	}
	
	@Test
	public void testGetOutcomeNames() {
		List<OutcomeMeasure> outcomeMeasures = d_brAnalysis.getOutcomeMeasures();
		for (int j=0; j<outcomeMeasures.size(); ++j) {
			assertEquals(outcomeMeasures.get(j).toString(), d_pm.getColumnName(j+1));
		}
	}
	
	@Test
	public void testGetValueAt() {
		for (int i=0; i<d_brAnalysis.getDrugs().size(); ++i)
			for (int j=0; j<d_brAnalysis.getOutcomeMeasures().size(); ++j) {
				Drug drug = d_brAnalysis.getDrugs().get(i);
				OutcomeMeasure om = d_brAnalysis.getOutcomeMeasures().get(j);
				Object expected = d_pmf.getLabeledModel(d_brAnalysis.getRelativeEffectDistribution(drug, om));
				Object actual = d_pm.getValueAt(i, j+1);
				assertEquals(expected.toString(), actual.toString());
			}
	}

	@Test
	public void testGetValueAtAbsolute() {
		d_pm = new BenefitRiskMeasurementTableModel<Drug>(d_brAnalysis, d_brAnalysis.getAbsoluteMeasurementSource(), d_pmf);
		for (int i=0; i < d_brAnalysis.getDrugs().size(); ++i) {
			Drug drug = d_brAnalysis.getDrugs().get(i);
			for (int j=0; j < d_brAnalysis.getOutcomeMeasures().size(); ++j) {
				OutcomeMeasure om = d_brAnalysis.getOutcomeMeasures().get(j);
				GaussianBase expected = (GaussianBase)d_brAnalysis.getAbsoluteEffectDistribution(drug, om);
				GaussianBase actual = (GaussianBase)((PresentationModel)d_pm.getValueAt(i, j+1)).getBean();
				assertEquals(expected.getMu(), actual.getMu(), 0.000001);
				assertEquals(expected.getSigma(), actual.getSigma(), 0.000001);
			}
		}
	}
}
