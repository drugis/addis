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

package org.drugis.addis.mtc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.common.threading.TaskUtil;
import org.drugis.mtc.MCMCModel.ExtendSimulation;
import org.drugis.mtc.presentation.SimulationInconsistencyWrapper;
import org.junit.Before;
import org.junit.Test;

public class ContinuousInconsistencyModelIT {
	private NetworkMetaAnalysis d_nma;
	private SimulationInconsistencyWrapper<TreatmentDefinition> d_wrapper;

	@Before
    public void setUp() {
    	d_nma = buildContinuousNetworkMetaAnalysis();
       
		d_wrapper = (SimulationInconsistencyWrapper<TreatmentDefinition>) d_nma.getInconsistencyModel();
    }
    
    @Test
    public void getResults() throws InterruptedException {
    	d_wrapper.getModel().setExtendSimulation(ExtendSimulation.FINISH);
    	TaskUtil.run(d_wrapper.getModel().getActivityTask());
    	
    	assertEquals(1, d_nma.getInconsistencyModel().getInconsistencyFactors().size());
    	assertNotNull(d_wrapper.getQuantileSummary(d_wrapper.getInconsistencyFactors().get(0)));
    	TreatmentDefinition a = TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine());
    	TreatmentDefinition b = TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine());
    	TreatmentDefinition c = TreatmentDefinition.createTrivial(ExampleData.buildDrugSertraline());
    	assertNotNull(d_wrapper.getRelativeEffect(a, b));
    	assertNotNull(d_wrapper.getRelativeEffect(b, a));
    	assertNotNull(d_wrapper.getRelativeEffect(a, c));
    	assertNotNull(d_wrapper.getRelativeEffect(c, a));
    	assertNotNull(d_wrapper.getRelativeEffect(c, b));
    	assertNotNull(d_wrapper.getRelativeEffect(b, c));
    }
    
    private NetworkMetaAnalysis buildContinuousNetworkMetaAnalysis() {
		List<Study> studies = Arrays.asList(new Study[] {
				ExampleData.buildStudyBennie(), ExampleData.buildStudyChouinard(), ExampleData.buildStudyAdditionalThreeArm()});
		List<TreatmentDefinition> drugs = Arrays.asList(new TreatmentDefinition[] {
				TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine()),
				TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine()), 
				TreatmentDefinition.createTrivial(ExampleData.buildDrugSertraline())});
		
		NetworkMetaAnalysis analysis = new NetworkMetaAnalysis("Test Network", 
				ExampleData.buildIndicationDepression(), ExampleData.buildEndpointCgi(),
				studies, drugs, ExampleData.buildMap(studies, drugs));
		
		return analysis;
	}

}
