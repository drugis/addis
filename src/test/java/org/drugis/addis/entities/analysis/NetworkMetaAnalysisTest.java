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

package org.drugis.addis.entities.analysis;

import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.NetworkRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.presentation.NetworkTableModelTest;
import org.drugis.addis.util.XMLHelper;
import org.drugis.common.JUnitUtil;
import org.drugis.mtc.BasicParameter;
import org.drugis.mtc.Treatment;
import org.drugis.mtc.summary.NormalSummary;
import org.junit.Before;
import org.junit.Test;

public class NetworkMetaAnalysisTest {
	private NetworkMetaAnalysis d_analysis;
	private NetworkMetaAnalysis d_mockAnalysis;

	@Before
	public void setup() throws InterruptedException{
		d_analysis = ExampleData.buildNetworkMetaAnalysisHamD();
		d_mockAnalysis = NetworkTableModelTest.buildMockNetworkMetaAnalysis();
		d_mockAnalysis.run();
		while (!d_mockAnalysis.getConsistencyModel().isReady()) {
			Thread.sleep(10);
		}
	}
	
	@Test
	public void testSetName() {
		JUnitUtil.testSetter(d_analysis, MetaAnalysis.PROPERTY_NAME, d_analysis.getName(), "TEST");
	}
	
	@Test
	public void testGetType() {
		assertEquals("Markov Chain Monte Carlo Network Meta-Analysis", d_analysis.getType());
	}
	
	@Test
	public void testGetRelativeEffect() {
		Drug base = ExampleData.buildDrugFluoxetine();
		Drug subj = ExampleData.buildDrugParoxetine();
		RelativeEffect<?> actual = d_mockAnalysis.getRelativeEffect(base, subj, BasicOddsRatio.class);
		NormalSummary summary = d_mockAnalysis.getNormalSummary(d_mockAnalysis.getConsistencyModel(), 
				new BasicParameter(new Treatment(base.toString()), new Treatment(subj.toString())));
		RelativeEffect<?> expected = NetworkRelativeEffect.buildOddsRatio(summary.getMean(), summary.getStandardDeviation());
		assertNotNull(expected);
		assertNotNull(actual);
		assertEquals(expected.getConfidenceInterval().getPointEstimate(), actual.getConfidenceInterval().getPointEstimate());
		assertEquals(expected.getConfidenceInterval(), actual.getConfidenceInterval());
		assertEquals(expected.getAxisType(), actual.getAxisType());
	}
	
	@Test
	public void testXML() throws XMLStreamException {
		NetworkMetaAnalysis analysis = ExampleData.buildNetworkMetaAnalysisHamD();
		String xml = XMLHelper.toXml(analysis, NetworkMetaAnalysis.class);		
		NetworkMetaAnalysis importedAnalysis = (NetworkMetaAnalysis)XMLHelper.fromXml(xml);
		assertEntityEquals(analysis, importedAnalysis);
	}
}