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

package org.drugis.addis.entities.metaanalysis;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.RelativeEffect.AxisType;
import org.drugis.addis.presentation.NetworkTableModelTest;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class NetworkMetaAnalysisTest {
	private NetworkMetaAnalysis d_analysis;
	private NetworkMetaAnalysis d_mockAnalysis;

	@Before
	public void setup(){
		d_analysis = ExampleData.buildNetworkMetaAnalysis();
		d_mockAnalysis = NetworkTableModelTest.buildMockNetworkMetaAnalysis();
		d_mockAnalysis.run();
	}
	
	@Test
	public void testSetName() {
		JUnitUtil.testSetter(d_analysis, MetaAnalysis.PROPERTY_NAME, d_analysis.getName(), "TEST");
	}
	
	@Test
	public void testGetType() {
		assertEquals("Markov Chain Monte Carlo Network Meta-Analysis", d_analysis.getType());
	}
	
	@Ignore
	public void testGetRelativeEffect() {
		// FIXME breaks
		RelativeEffect<? extends Measurement> actual = d_mockAnalysis.getRelativeEffect(ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine(), OddsRatio.class);
		RelativeEffect<? extends Measurement> expected = new MetaAnalysisRelativeEffect<Measurement>(Math.exp(1.0), 0.33333, 0, AxisType.LOGARITHMIC);
		System.out.println("expected: "+expected);
		System.out.println("actual: "+actual);
		assertEquals(expected.getMedian(), actual.getMedian());
		assertEquals(expected.getSigma(), actual.getSigma());
		assertEquals(expected.getAxisType(), actual.getAxisType());
	}
}

//
//@Test
//public void runModel() {
//	ProgressListener mock = createMock(ProgressListener.class);
//	mock.update(d_model, new ProgressEvent(EventType.MODEL_CONSTRUCTION_STARTED));
//	mock.update(d_model, new ProgressEvent(EventType.MODEL_CONSTRUCTION_FINISHED));
//	mock.update(d_model, new ProgressEvent(EventType.BURNIN_STARTED));
//	for (int i = 100; i < d_model.getBurnInIterations(); i+=100) {
//    	mock.update(d_model, new ProgressEvent(EventType.BURNIN_PROGRESS, i, d_model.getBurnInIterations()));
//	}
//	mock.update(d_model, new ProgressEvent(EventType.BURNIN_FINISHED));
//	mock.update(d_model, new ProgressEvent(EventType.SIMULATION_STARTED));
//	for (int i = 100; i < d_model.getSimulationIterations(); i+=100) {
//    	mock.update(d_model, new ProgressEvent(EventType.SIMULATION_PROGRESS, i, d_model.getSimulationIterations()));
//	}
//	mock.update(d_model, new ProgressEvent(EventType.SIMULATION_FINISHED));
//	replay(mock);
//	d_model.addProgressListener(mock);
//	d_model.run();
//	verify(mock);
//}