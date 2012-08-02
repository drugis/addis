package org.drugis.addis.entities.analysis;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.TreatmentCategorySet;
import org.junit.Before;
import org.junit.Test;

public class NetworkBuilderFactoryTest {
	private Transformer<TreatmentCategorySet, String> d_transformer;
	
	@Before
	public void setUp() {
		d_transformer = new NetworkBuilderFactory.NameTransformer();
	}

	@Test
	public void testTransformCombinationTreatment() {
		TreatmentCategorySet treatment = TreatmentCategorySet.createTrivial(
				Arrays.asList(ExampleData.buildDrugCandesartan(), ExampleData.buildDrugFluoxetine()));
		assertEquals("Candesartan_Fluoxetine", d_transformer.transform(treatment));
	}
	
	@Test
	public void testTransformTreatmentWithIllegalCharacters() {
		TreatmentCategorySet treatment = TreatmentCategorySet.createTrivial(Arrays.asList(new Drug("My Drug!", "3"), ExampleData.buildDrugFluoxetine()));
		assertEquals("Fluoxetine_MyDrug", d_transformer.transform(treatment));
	}
	
	@Test
	public void testTransformTreatmentDuplicateCleanName() {
		TreatmentCategorySet treatment1 = TreatmentCategorySet.createTrivial(new Drug("My Drug!", "3"));
		TreatmentCategorySet treatment2 = TreatmentCategorySet.createTrivial(new Drug("My!Drug", "4"));
		assertEquals("MyDrug", d_transformer.transform(treatment1));
		assertEquals("MyDrug2", d_transformer.transform(treatment2));
	}
	
	@Test 
	public void testTransformWithCategory() { 
		Category cat = Category.createTrivial(new Drug("My Drug!", "3"));
		cat.setName("AA");
		TreatmentCategorySet treatment1 = new TreatmentCategorySet(cat);
		assertEquals("MyDrugAA", d_transformer.transform(treatment1));
	}

	@Test 
	public void testTransformWithCategories() { 
		Category cat1 = Category.createTrivial(new Drug("My Drug!", "1"));
		Category cat2 = Category.createTrivial(new Drug("My Poison!", "2"));
		cat1.setName("SomeCat!!!");
		cat2.setName("Garfield Poison");
		TreatmentCategorySet treatment1 = new TreatmentCategorySet(Arrays.asList(cat1, cat2));
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
		
		Map<Study, Map<DrugSet, Arm>> armMap = new HashMap<Study, Map<DrugSet, Arm>>();
		Map<DrugSet, Arm> drugArmMap = new HashMap<DrugSet, Arm>();
		for (Arm a : study.getArms()) {
			drugArmMap.put(study.getDrugs(a), a);
		}
		armMap.put(study, drugArmMap);
		NetworkMetaAnalysis nma = new NetworkMetaAnalysis("don'tcare", study.getIndication(), study.getOutcomeMeasures().get(0), armMap);
		
		DrugSet treatment = new DrugSet(Arrays.asList(ExampleData.buildDrugCandesartan(), ExampleData.buildDrugFluoxetine()));
		assertEquals("Candesartan_Fluoxetine", nma.getBuilder().getTreatmentMap().get(treatment).getId());
		assertEquals("Candesartan + Fluoxetine", nma.getBuilder().getTreatmentMap().get(treatment).getDescription());
	}
}
