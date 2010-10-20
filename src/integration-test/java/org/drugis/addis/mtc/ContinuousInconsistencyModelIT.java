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

package org.drugis.addis.mtc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.util.threading.TaskUtil;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.Measurement;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Treatment;
import org.junit.Before;
import org.junit.Test;

public class ContinuousInconsistencyModelIT {

    private NetworkBuilder<? extends Measurement> d_builder;
	private NetworkMetaAnalysis d_nma;
	private InconsistencyModel d_model;

	@Before
    public void setUp() {
    	d_nma = buildContinuousNetworkMetaAnalysis();
    	d_builder = d_nma.getBuilder();
        
		d_model = d_nma.getInconsistencyModel();
    }
    
    @Test
    public void getResults() throws InterruptedException {
    	TaskUtil.run(d_model.getActivityTask());
    	
    	assertEquals(1, d_nma.getInconsistencyFactors().size());
    	assertNotNull(d_nma.getInconsistency(d_nma.getInconsistencyFactors().get(0)));
    	Treatment a = d_builder.getTreatment("Fluoxetine");
    	Treatment b = d_builder.getTreatment("Paroxetine");
    	Treatment c = d_builder.getTreatment("Sertraline");
    	assertNotNull(d_model.getRelativeEffect(a, b));
    	assertNotNull(d_model.getRelativeEffect(b, a));
    	assertNotNull(d_model.getRelativeEffect(a, c));
    	assertNotNull(d_model.getRelativeEffect(c, a));
    	assertNotNull(d_model.getRelativeEffect(c, b));
    	assertNotNull(d_model.getRelativeEffect(b, c));
    }
    
    private NetworkMetaAnalysis buildContinuousNetworkMetaAnalysis() {
		List<Study> studies = Arrays.asList(new Study[] {
				ExampleData.buildStudyBennie(), ExampleData.buildStudyChouinard(), ExampleData.buildStudyAdditionalThreeArm()});
		List<Drug> drugs = Arrays.asList(new Drug[] {ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine(), 
				ExampleData.buildDrugSertraline()});
		
		NetworkMetaAnalysis analysis = new NetworkMetaAnalysis("Test Network", 
				ExampleData.buildIndicationDepression(), ExampleData.buildEndpointCgi(),
				studies, drugs, ExampleData.buildMap(studies, drugs));
		
		return analysis;
	}

}
