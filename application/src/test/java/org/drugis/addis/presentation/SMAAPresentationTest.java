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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.drugis.common.JUnitUtil.assertAllAndOnly;

import java.io.IOException;
import java.util.Arrays;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.junit.Before;
import org.junit.Test;

public class SMAAPresentationTest {
	private SMAAPresentation<TreatmentDefinition, BenefitRiskAnalysis<TreatmentDefinition>> d_smaaPresentation;
	private MetaBenefitRiskAnalysis d_metaBRanalysis;

	@Before
	public void setUp() {
		d_metaBRanalysis = ExampleData.buildMetaBenefitRiskAnalysis();
		d_smaaPresentation = new SMAAPresentation<TreatmentDefinition, BenefitRiskAnalysis<TreatmentDefinition>>(d_metaBRanalysis);

	}

	@Test
	public void testInitialization() {
		TreatmentDefinition fluox = TreatmentDefinition.createTrivial(ExampleData.buildDrugFluoxetine());
		TreatmentDefinition parox = TreatmentDefinition.createTrivial(ExampleData.buildDrugParoxetine());

		assertAllAndOnly(d_metaBRanalysis.getAlternatives(), Arrays.asList(fluox, parox));

		Endpoint hamd = ExampleData.buildEndpointHamd();
		AdverseEvent convulsion = ExampleData.buildAdverseEventConvulsion();

		assertAllAndOnly(d_metaBRanalysis.getCriteria(), Arrays.asList(hamd, convulsion));

		assertEquals(hamd.getDirection(), Direction.HIGHER_IS_BETTER);
		assertEquals(convulsion.getDirection(), Direction.LOWER_IS_BETTER);
	}

	@Test
	public void testJSONSerialization() throws JsonGenerationException, JsonMappingException, IOException {
		JsonNode serializedJSON = d_smaaPresentation.getJSON();
		assertNotNull(serializedJSON.get("title"));
		JsonNode criteria = serializedJSON.get("criteria");
		assertNotNull(criteria);
		assertNotNull(criteria.get("ham-d-responders"));

		JsonNode alternatives = serializedJSON.get("alternatives");
		assertNotNull(alternatives);
		assertNotNull(alternatives.get("fluoxetine"));

		assertNotNull(serializedJSON.get("performanceTable"));
	}
}
