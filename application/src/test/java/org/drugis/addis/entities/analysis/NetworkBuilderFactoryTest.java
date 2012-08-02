package org.drugis.addis.entities.analysis;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.TreatmentActivity;
import org.junit.Test;

public class NetworkBuilderFactoryTest {
	@Test
	public void testTransformCombinationTreatment() {
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
		
		assertEquals("Candesartan_Fluoxetine", nma.getBuilder().getTreatmentMap().get(new DrugSet(Arrays.asList(ExampleData.buildDrugCandesartan(), ExampleData.buildDrugFluoxetine()))).getId());
	}
	
	@Test
	public void testTransformTreatmentWithIllegalCharacters() {
		Study study = ExampleData.buildStudyMcMurray().clone();
		Drug myDrug = new Drug("My Drug!", "3");
		DrugTreatment ta1 = new DrugTreatment(myDrug, null);
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
		
		assertEquals("Fluoxetine_MyDrug", nma.getBuilder().getTreatmentMap().get(new DrugSet(Arrays.asList(myDrug, ExampleData.buildDrugFluoxetine()))).getId());
	}
	
	@Test
	public void testTransformTreatmentDuplicateCleanName() {
		Study study = ExampleData.buildStudyMcMurray().clone();
		Drug myDrug1 = new Drug("My Drug!", "3");
		Drug myDrug2 = new Drug("My!Drug", "4");
		DrugTreatment ta1 = new DrugTreatment(myDrug1, null);
		DrugTreatment ta2 = new DrugTreatment(myDrug2, null);
		StudyActivity act1 = new StudyActivity("DRUGS1", new TreatmentActivity(Arrays.asList(ta1)));
		StudyActivity act2 = new StudyActivity("DRUGS2", new TreatmentActivity(Arrays.asList(ta2)));
		study.getStudyActivities().add(act1);
		study.getStudyActivities().add(act2);
		study.setStudyActivityAt(study.getArms().get(0), study.findTreatmentEpoch(), act1);
		study.setStudyActivityAt(study.getArms().get(1), study.findTreatmentEpoch(), act2);
		
		Map<Study, Map<DrugSet, Arm>> armMap = new HashMap<Study, Map<DrugSet, Arm>>();
		Map<DrugSet, Arm> drugArmMap = new HashMap<DrugSet, Arm>();
		for (Arm a : study.getArms()) {
			drugArmMap.put(study.getDrugs(a), a);
		}
		armMap.put(study, drugArmMap);
		NetworkMetaAnalysis nma = new NetworkMetaAnalysis("don'tcare", study.getIndication(), study.getOutcomeMeasures().get(0), armMap);
		
		assertEquals("MyDrug", nma.getBuilder().getTreatmentMap().get(new DrugSet(Arrays.asList(myDrug1))).getId());
		assertEquals("MyDrug2", nma.getBuilder().getTreatmentMap().get(new DrugSet(Arrays.asList(myDrug2))).getId());
	}

}
