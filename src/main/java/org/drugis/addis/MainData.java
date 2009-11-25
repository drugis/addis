package org.drugis.addis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;

public class MainData extends ExampleData {
	
	private static BasicStudy s_studySechter;
	private static BasicStudy s_studyNewhouse;
	private static BasicStudy s_studyFava02;
	private static BasicStudy s_studyBoyer;
	
	public static void initDefaultData(Domain domain) {
		ExampleData.initDefaultData(domain);
		//studies testdata:
		domain.addStudy(buildStudyBoyer1998());
		domain.addStudy(buildStudyFava2002());
		domain.addStudy(buildStudyNewhouse2000());
		domain.addStudy(buildStudySechter1999());
		
		domain.addMetaAnalysis(buildMetaHansen2005());		
	}

	public static RandomEffectsMetaAnalysis buildMetaHansen2005() {
		
		List<Study> studylist = new ArrayList<Study>();
		
		studylist.add(buildStudyBennie());
		studylist.add(buildStudyBoyer1998());
		studylist.add(buildStudyFava2002());
		studylist.add(buildStudyNewhouse2000());
		studylist.add(buildStudySechter1999());
		
		return new RandomEffectsMetaAnalysis("Hansen et al, 2005", buildEndpointHamd(), studylist, buildDrugFluoxetine(), buildDrugSertraline());
	}
	
	public static BasicStudy buildStudyFava2002() {
		if (s_studyFava02 == null) {
			s_studyFava02 = realBuildStudyFava02();
		}
	
		return s_studyFava02;
	}

	private static BasicStudy realBuildStudyFava02() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		Drug sertraline = buildDrugSertraline();
		BasicStudy study = new BasicStudy("Fava et al, 2002", buildIndicationDepression());
		study.setEndpoints(Collections.singleton(hamd));
		
		// Study characteristics
		study.setCharacteristic(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(StudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(StudyCharacteristic.ARMS, 2);
		study.setCharacteristic(StudyCharacteristic.INCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.OBJECTIVE, 
				"");
		study.setCharacteristic(StudyCharacteristic.STATUS, StudyCharacteristic.Status.FINISHED);
		
		// Sertraline data
		Dose dose = new Dose(75.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup sertr = new BasicPatientGroup(study, sertraline, dose, 96);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(sertr);
		pHamd.setRate(70);
		study.addPatientGroup(sertr);
		study.setMeasurement(hamd, sertr, pHamd);

		// Fluoxetine data
		dose = new Dose(30.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup fluox = new BasicPatientGroup(study, fluoxetine, dose, 92);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(57);
		study.addPatientGroup(fluox);
		study.setMeasurement(hamd, fluox, fHamd);
		return study;
	}
	
	public static BasicStudy buildStudyNewhouse2000() {
		if (s_studyNewhouse == null) {
			s_studyNewhouse = realBuildStudyNewhouse();
		}
	
		return s_studyNewhouse;
	}

	private static BasicStudy realBuildStudyNewhouse() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		Drug sertraline = buildDrugSertraline();
		BasicStudy study = new BasicStudy("Newhouse et al, 2000", buildIndicationDepression());
		study.setEndpoints(Collections.singleton(hamd));
		
		// Study characteristics
		study.setCharacteristic(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(StudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(StudyCharacteristic.ARMS, 2);
		study.setCharacteristic(StudyCharacteristic.INCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.OBJECTIVE, 
				"");
		study.setCharacteristic(StudyCharacteristic.STATUS, StudyCharacteristic.Status.FINISHED);
		
		// Sertraline data
		Dose dose = new Dose(75.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup sertr = new BasicPatientGroup(study, sertraline, dose, 117);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(sertr);
		pHamd.setRate(85);
		study.addPatientGroup(sertr);
		study.setMeasurement(hamd, sertr, pHamd);

		// Fluoxetine data
		dose = new Dose(30.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup fluox = new BasicPatientGroup(study, fluoxetine, dose, 119);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(84);
		study.addPatientGroup(fluox);
		study.setMeasurement(hamd, fluox, fHamd);
		return study;
	}
	
	public static BasicStudy buildStudySechter1999() {
		if (s_studySechter == null) {
			s_studySechter = realBuildStudySechter();
		}
	
		return s_studySechter;
	}

	private static BasicStudy realBuildStudySechter() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		Drug sertraline = buildDrugSertraline();
		BasicStudy study = new BasicStudy("Sechter et al, 1999", buildIndicationDepression());
		study.setEndpoints(Collections.singleton(hamd));
		
		// Study characteristics
		study.setCharacteristic(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(StudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(StudyCharacteristic.ARMS, 2);
		study.setCharacteristic(StudyCharacteristic.INCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.OBJECTIVE, 
				"");
		study.setCharacteristic(StudyCharacteristic.STATUS, StudyCharacteristic.Status.FINISHED);
		
		// Sertraline data
		Dose dose = new Dose(75.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup sertr = new BasicPatientGroup(study, sertraline, dose, 118);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(sertr);
		pHamd.setRate(86);
		study.addPatientGroup(sertr);
		study.setMeasurement(hamd, sertr, pHamd);

		// Fluoxetine data
		dose = new Dose(30.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup fluox = new BasicPatientGroup(study, fluoxetine, dose, 120);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(76);
		study.addPatientGroup(fluox);
		study.setMeasurement(hamd, fluox, fHamd);
		return study;
	}
	
	public static BasicStudy buildStudyBoyer1998() {
		if (s_studyBoyer == null){ 
			s_studyBoyer = realBuildStudyBoyer();
		}
	
		return s_studyBoyer;
	}

	private static BasicStudy realBuildStudyBoyer() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		Drug sertraline = buildDrugSertraline();
		BasicStudy study = new BasicStudy("Boyer et al, 1998", buildIndicationDepression());
		study.setEndpoints(Collections.singleton(hamd));
		
		// Study characteristics
		study.setCharacteristic(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(StudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(StudyCharacteristic.ARMS, 2);
		study.setCharacteristic(StudyCharacteristic.INCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.OBJECTIVE, 
				"");
		study.setCharacteristic(StudyCharacteristic.STATUS, StudyCharacteristic.Status.FINISHED);
		
		// Sertraline data
		Dose dose = new Dose(75.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup sertr = new BasicPatientGroup(study, sertraline, dose, 122);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(sertr);
		pHamd.setRate(63);
		study.addPatientGroup(sertr);
		study.setMeasurement(hamd, sertr, pHamd);

		// Fluoxetine data
		dose = new Dose(30.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup fluox = new BasicPatientGroup(study, fluoxetine, dose, 120);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(61);
		study.addPatientGroup(fluox);
		study.setMeasurement(hamd, fluox, fHamd);
		return study;
	}
}
