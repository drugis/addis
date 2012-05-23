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

package org.drugis.addis.entities.mtcwrapper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.analysis.NetworkBuilderFactory;
import org.drugis.addis.entities.mtcwrapper.AbstractSimulationWrapper;
import org.drugis.addis.mocks.MockConsistencyModel;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Treatment;
import org.junit.Before;
import org.junit.Test;

public class AbstractSimulationWrapperTest {
	private AbstractSimulationWrapper<ConsistencyModel> d_model;
	private List<DrugSet> d_treatments;

	@Before
	public void setUp() {
		d_treatments = Arrays.asList(new DrugSet(new Drug("A", "")), new DrugSet(new Drug("B", "")), new DrugSet(new Drug("C", "")));
		NetworkBuilder<DrugSet> builder = NetworkBuilderFactory.createBuilderStub(d_treatments);
		ArrayList<Treatment> treatmentList = new ArrayList<Treatment>();
		for(DrugSet s : d_treatments) { 
			treatmentList.add(builder.getTreatmentMap().get(s));
		}
		ConsistencyModel mtc = MockConsistencyModel.buildMockSimulationConsistencyModel(treatmentList);
		d_model = new AbstractSimulationWrapper<ConsistencyModel>(builder, mtc, "Stub Model") {};
	}

	@Test
	public void testGetQuantileSummary() {
		Parameter dAB = d_model.getRelativeEffect(d_treatments.get(0), d_treatments.get(1));
		Parameter dAC = d_model.getRelativeEffect(d_treatments.get(0), d_treatments.get(2));
		assertNotNull(d_model.getQuantileSummary(dAB));
		assertSame(d_model.getQuantileSummary(dAB), d_model.getQuantileSummary(dAB));
		assertNotNull(d_model.getQuantileSummary(dAC));
		assertNotSame(d_model.getQuantileSummary(dAB), d_model.getQuantileSummary(dAC));
	}
}
