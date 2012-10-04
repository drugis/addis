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

package org.drugis.addis.entities.analysis;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.junit.Before;
import org.junit.Test;

public class NetworkBuilderFactoryTest {
	private Transformer<TreatmentDefinition, String> d_transformer;
	
	@Before
	public void setUp() {
		d_transformer = new NetworkBuilderFactory.NameTransformer();
	}

	@Test
	public void testTransformCombinationTreatment() {
		TreatmentDefinition treatment = TreatmentDefinition.createTrivial(
				Arrays.asList(ExampleData.buildDrugCandesartan(), ExampleData.buildDrugFluoxetine()));
		assertEquals("Candesartan_Fluoxetine", d_transformer.transform(treatment));
	}
	
	@Test
	public void testTransformTreatmentWithIllegalCharacters() {
		TreatmentDefinition treatment = TreatmentDefinition.createTrivial(Arrays.asList(new Drug("My Drug!", "3"), ExampleData.buildDrugFluoxetine()));
		assertEquals("Fluoxetine_MyDrug", d_transformer.transform(treatment));
	}
	
	@Test
	public void testTransformTreatmentDuplicateCleanName() {
		TreatmentDefinition treatment1 = TreatmentDefinition.createTrivial(new Drug("My Drug!", "3"));
		TreatmentDefinition treatment2 = TreatmentDefinition.createTrivial(new Drug("My!Drug", "4"));
		assertEquals("MyDrug", d_transformer.transform(treatment1));
		assertEquals("MyDrug2", d_transformer.transform(treatment2));
	}
	
	@Test 
	public void testTransformWithCategory() { 
		Category cat = Category.createTrivial(new Drug("My Drug!", "3"));
		cat.setName("AA");
		TreatmentDefinition treatment1 = new TreatmentDefinition(cat);
		assertEquals("MyDrugAA", d_transformer.transform(treatment1));
	}

	@Test 
	public void testTransformWithCategories() { 
		Category cat1 = Category.createTrivial(new Drug("My Drug!", "1"));
		Category cat2 = Category.createTrivial(new Drug("My Poison!", "2"));
		cat1.setName("SomeCat!!!");
		cat2.setName("Garfield Poison");
		TreatmentDefinition treatment1 = new TreatmentDefinition(Arrays.asList(cat1, cat2));
		assertEquals("MyDrugSomeCat_MyPoisonGarfieldPoison", d_transformer.transform(treatment1));
	}
	
	@Test
	public void testBuilderUsesTransform() {
		Study study = ExampleData.buildStudyMcMurray().clone();
		DrugTreatment ta1 = new DrugTreatment(ExampleData.buildDrugCandesartan(), null);
		DrugTreatment ta2 = new DrugTreatment(ExampleData.buildDrugFluoxetine(), null);
		StudyActivity activity = new StudyActivity("DRUGS", new TreatmentActivity(Arrays.asList(ta1, ta2)));
		study.getStudyActivities().add(activity);
		study.setStudyActivityAt(study.getArms().get(0), study.findTreatmentEpoch(), activity);
		
		Map<Study, Map<TreatmentDefinition, Arm>> armMap = new HashMap<Study, Map<TreatmentDefinition, Arm>>();
		Map<TreatmentDefinition, Arm> drugArmMap = new HashMap<TreatmentDefinition, Arm>();
		for (Arm a : study.getArms()) {
			drugArmMap.put(study.getTreatmentDefinition(a), a);
		}
		armMap.put(study, drugArmMap);
		NetworkMetaAnalysis nma = new NetworkMetaAnalysis("don'tcare", study.getIndication(), study.getOutcomeMeasures().get(0), armMap);
		
		TreatmentDefinition treatment = TreatmentDefinition.createTrivial(Arrays.asList(ExampleData.buildDrugCandesartan(), ExampleData.buildDrugFluoxetine()));
		assertEquals("Candesartan_Fluoxetine", nma.getBuilder().getTreatmentMap().get(treatment).getId());
		assertEquals("Candesartan + Fluoxetine", nma.getBuilder().getTreatmentMap().get(treatment).getDescription());
	}
}
