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

package org.drugis.addis.mtc;

import static org.junit.Assert.assertNotNull;

import org.drugis.common.threading.TaskUtil;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.DefaultModelFactory;
import org.drugis.mtc.MCMCModel.ExtendSimulation;
import org.drugis.mtc.ModelFactory;
import org.drugis.mtc.model.ContinuousNetworkBuilder;
import org.drugis.mtc.model.Network;
import org.drugis.mtc.model.Treatment;
import org.junit.Before;
import org.junit.Test;

public class ContinuousConsistencyModelIT {
	/* This test apparently cannot run in sequence with NetworkMetaAnalysisTest so this is commented out entirely */

	    private ContinuousNetworkBuilder<String> d_builder;
		private Network d_network;
		private ConsistencyModel d_model;

		@Before
	    public void setUp() {
	        d_builder = new ContinuousNetworkBuilder<String>();
	        d_builder.add("1", "A", 12.0, 3.0, 100);
	        d_builder.add("1", "B", 23.0, 2.0, 100);
	        d_builder.add("2", "B", 12.1, 9.0, 43);
	        d_builder.add("2", "C", 15.0, 8.0, 40);
	        d_builder.add("3", "A", 30.0, 5.0, 150);
	        d_builder.add("3", "C", 100.0, 40.0, 150);
	        d_network = d_builder.buildNetwork();

	        ModelFactory factory = DefaultModelFactory.instance();
			d_model = factory.getConsistencyModel(d_network);
	    }

	    @Test
	    public void getResults() throws InterruptedException {
	    	d_model.setExtendSimulation(ExtendSimulation.FINISH);
	    	TaskUtil.run(d_model.getActivityTask());
	    	Treatment a = d_builder.getTreatmentMap().get("A");
	    	Treatment b = d_builder.getTreatmentMap().get("B");
	    	Treatment c = d_builder.getTreatmentMap().get("C");
	    	assertNotNull(d_model.getRelativeEffect(a, b));
	    	assertNotNull(d_model.getRelativeEffect(b, a));
	    	assertNotNull(d_model.getRelativeEffect(a, c));
	    	assertNotNull(d_model.getRelativeEffect(c, a));
	    	assertNotNull(d_model.getRelativeEffect(c, b));
	    	assertNotNull(d_model.getRelativeEffect(b, c));
	    }
}
