/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.relativeeffect.BasicRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public abstract class RelativeEffectTableModelBaseTest {
	protected Study d_standardStudy;
	protected Study d_threeArmStudy;
	protected AbstractRelativeEffectTableModel d_stdModel;
	protected RelativeEffectTableModel d_threeArmModel;
	protected Endpoint d_endpoint;
	protected PresentationModelFactory d_pmf;
	protected Class<? extends RelativeEffect<?>> d_relativeEffectClass;

	@Test
	public void testGetColumnCount() {
		assertEquals(d_standardStudy.getArms().size(), d_stdModel.getColumnCount());
		assertEquals(d_threeArmStudy.getArms().size(), d_threeArmModel.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(d_standardStudy.getArms().size(), d_stdModel.getRowCount());
		assertEquals(d_threeArmStudy.getArms().size(), d_threeArmModel.getRowCount());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtUpperRightPart() {
		assertEquals(3, d_threeArmStudy.getArms().size());
		Arm pg0 = d_threeArmStudy.getArms().get(0);
		Arm pg1 = d_threeArmStudy.getArms().get(1);
		Arm pg2 = d_threeArmStudy.getArms().get(2);
		
		PresentationModel<BasicRelativeEffect<?>> val01 = (PresentationModel<BasicRelativeEffect<?>>)d_threeArmModel.getValueAt(0, 1);
		assertTrue(d_relativeEffectClass.isInstance(val01.getBean()));
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg0), val01.getBean().getBaseline());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg1), val01.getBean().getSubject());
		
		PresentationModel<BasicRelativeEffect<?>> val12 = (PresentationModel<BasicRelativeEffect<?>>)d_threeArmModel.getValueAt(1, 2);
		assertTrue(d_relativeEffectClass.isInstance(val12.getBean()));
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg1), val12.getBean().getBaseline());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg2), val12.getBean().getSubject());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtLowerLeftPart() {
		assertEquals(3, d_threeArmStudy.getArms().size());
		Arm pg0 = d_threeArmStudy.getArms().get(0);
		Arm pg1 = d_threeArmStudy.getArms().get(1);
		Arm pg2 = d_threeArmStudy.getArms().get(2);
		
		PresentationModel<BasicRelativeEffect<?>> val20 = (PresentationModel<BasicRelativeEffect<?>>)d_threeArmModel.getValueAt(2, 0);
		assertTrue(d_relativeEffectClass.isInstance(val20.getBean()));
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg2), val20.getBean().getBaseline());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg0), val20.getBean().getSubject());
		
		PresentationModel<BasicRelativeEffect<?>> val21 = (PresentationModel<BasicRelativeEffect<?>>)d_threeArmModel.getValueAt(2, 1);
		assertTrue(d_relativeEffectClass.isInstance(val21.getBean()));
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg2), val21.getBean().getBaseline());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg1), val21.getBean().getSubject());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testGetValueAtDiagonal() {
		for (int i = 0; i < d_standardStudy.getArms().size(); ++i) {
			Object val = d_stdModel.getValueAt(i, i);
			assertTrue("Instance of PresentationModel", val instanceof PresentationModel);
			assertEquals(d_standardStudy.getArms().get(i), ((PresentationModel) val).getBean());
		}
		for (int i = 0; i < d_threeArmStudy.getArms().size(); ++i) {
			Object val = d_threeArmModel.getValueAt(i, i);
			assertTrue("Instance of PresentationModel", val instanceof PresentationModel);
			assertEquals(d_threeArmStudy.getArms().get(i), ((PresentationModel) val).getBean());
		}
	}

	@Test
	public void testGetDescriptionAtDiagonal() {
		assertNull(d_threeArmModel.getDescriptionAt(1, 1));
	}

	@Test
	public void testGetDescriptionAt() {
		LabeledPresentation pg0 = d_pmf.getLabeledModel(d_threeArmStudy.getArms().get(0));
		LabeledPresentation pg1 = d_pmf.getLabeledModel(d_threeArmStudy.getArms().get(1));
		String expected = "\"" + pg1.getLabelModel().getValue() + "\" relative to \"" +
				pg0.getLabelModel().getValue() + "\"";
		assertEquals(expected, d_threeArmModel.getDescriptionAt(0, 1));
	}

	@Test
	public void testGetDescription() {
		String description = d_threeArmModel.getTitle() + " for \"" + d_threeArmStudy.getName()
				+ "\" on Endpoint \"" + d_endpoint.getName() + "\"";
		assertEquals(description, d_threeArmModel.getDescription());
	}
	
	@Test
	public void testGetPlotPresentation() {
		ForestPlotPresentation pm = d_stdModel.getPlotPresentation(1, 0);
		assertEquals(d_relativeEffectClass, pm.getRelativeEffectAt(0).getClass());
		assertEquals(d_standardStudy.toString(), pm.getStudyLabelAt(0));
		assertEquals(1, pm.getNumRelativeEffects());
		Measurement bl = (Measurement) ((BasicRelativeEffect<?>) pm.getRelativeEffectAt(0)).getBaseline();
		assertEquals(d_standardStudy.getArms().get(1).getSize(),
				bl.getSampleSize());
		Measurement subj = (Measurement) ((BasicRelativeEffect<?>) pm.getRelativeEffectAt(0)).getSubject();
		assertEquals(d_standardStudy.getArms().get(0).getSize(),
				subj.getSampleSize());
		assertEquals(d_endpoint, pm.getOutcomeMeasure());
	}

	protected void baseSetUpRate() {
		d_standardStudy = ExampleData.realBuildStudyDeWilde();
		d_threeArmStudy = ExampleData.buildStudyAdditionalThreeArm();
		d_endpoint = ExampleData.buildEndpointHamd();
		Domain domain = new DomainImpl();
		d_pmf = new PresentationModelFactory(domain);
	}
	
	protected void baseSetUpContinuous() {
		d_standardStudy = ExampleData.buildStudyChouinard();
		d_threeArmStudy = ExampleData.buildStudyAdditionalThreeArm();
		d_endpoint = ExampleData.buildEndpointCgi();
		Domain domain = new DomainImpl();
		d_pmf = new PresentationModelFactory(domain);
	}
}
