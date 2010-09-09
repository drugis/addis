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

import java.util.Collections;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.ContinuousPopulationCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Variable;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;


public class VariablePresentationModelTest {
	
	private Domain d_domain;
	private PresentationModelFactory d_pmf;
	private Variable d_omEndpoint;
	private VariablePresentation d_pmEndpoint;

	//OutcomePresentationModel d_opm = new OutcomePresentationModel(om, );
	
	
	@Before
	public void setUp() {
		
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pmf = new PresentationModelFactory(d_domain);
		
		d_omEndpoint = new Endpoint("testendpoint", Variable.Type.CONTINUOUS);
		d_pmEndpoint = (VariablePresentation) d_pmf.getModel(d_omEndpoint);
	}
	
	@Test
	public void testGetNameEndpoint() {
		assertEquals ("Endpoint", d_pmEndpoint.getCategoryName());
	}	
	
	@Test
	public void testGetNameADE() {
		Variable omAde = new AdverseEvent("testade", Variable.Type.CONTINUOUS);
		VariablePresentation pm_ade = (VariablePresentation) d_pmf.getModel(omAde);
		assertEquals ("Adverse event", pm_ade.getCategoryName());
	}
	
	@Test
	public void testGetNamePopChar() {
		Variable omAde = new ContinuousPopulationCharacteristic("testvar");
		VariablePresentation pm = (VariablePresentation) d_pmf.getModel(omAde);
		assertEquals ("Population characteristic", pm.getCategoryName());
	}
	
	@Test
	public void testGetLabelModel() {
		assertEquals("testendpoint", d_pmEndpoint.getLabelModel().getString());
	}
	
	@Test
	public void testGetIncludedStudies() {
		VariablePresentation pmCardovascular = (VariablePresentation) d_pmf.getModel((Variable) ExampleData.buildEndpointCVdeath());
		JUnitUtil.assertAllAndOnly(Collections.singleton(ExampleData.buildStudyMcMurray()), pmCardovascular.getIncludedStudies().getValue());
	}

}
