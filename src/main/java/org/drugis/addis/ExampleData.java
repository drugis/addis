/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package org.drugis.addis;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;

import org.drugis.addis.entities.AbstractStudy;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.entities.Endpoint.Type;


public class ExampleData {
	private static Indication s_indicationDepression;
	private static Endpoint s_endpointHamd;
	private static Endpoint s_endpointCgi;
	private static Drug s_parox;
	private static Drug s_fluox;
	private static Drug s_viagra;
	private static Endpoint s_endpointUnused;
	private static Indication s_indicationHeartFailure;
	private static Drug s_candesartan;
	private static Endpoint s_endpointCVdeath;
	private static Drug s_sertr;
	private static Drug s_placebo;

	public static void initDefaultData(Domain domain) {
		// depression data
		domain.addIndication(buildIndicationDepression());
		domain.addEndpoint(buildEndpointHamd());
		domain.addEndpoint(buildEndpointCgi());
		domain.addDrug(buildDrugFluoxetine());
		domain.addDrug(buildDrugParoxetine());
		domain.addDrug(buildDrugSertraline());
		domain.addDrug(buildPlacebo());
		domain.addStudy(buildDefaultStudy1());
		domain.addStudy(buildDefaultStudy2());
		domain.addStudy(buildDefaultStudy3());
		
		// heart failure data
		domain.addIndication(buildIndicationChronicHeartFailure());
		domain.addDrug(buildDrugCandesartan());
		domain.addEndpoint(buildEndpointCVdeath());
		domain.addStudy(buildHeartStudy());
		
		// unused stuff
		domain.addEndpoint(buildEndpointUnused());
	}

	public static AbstractStudy buildDefaultStudy1() {
		BasicStudy study = new BasicStudy("Chouinard et al, 1999", buildIndicationDepression());
		study.setEndpoints(new HashSet<Endpoint>(
				Arrays.asList(new Endpoint[]{buildEndpointHamd(), buildEndpointCgi()})));
		
		// Study characteristics
		study.setCharacteristic(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(StudyCharacteristic.CENTERS, 8);
		study.setCharacteristic(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(StudyCharacteristic.ARMS, 2);
		study.setCharacteristic(StudyCharacteristic.INCLUSION,
				"Patients were recruited " + 
				"through newspaper ads and referrals. Patients were " +
				"included if they had symptoms of depression for at " +
				"least one month prior to the screening visit, a total " +
				"score of 20 on the 21-item Hamilton Depression " +
				"Rating Scale (HAM-D) (Hamilton, 1960), and a " +
				"score of two on item one HAM-D at the screening " +
				"visit (5–14 days prior to baseline) and at entry (Day " +
				"0).");
		study.setCharacteristic(StudyCharacteristic.EXCLUSION,
				"Patients were excluded if they had significant " + 
				"coexisting illness, including renal, hepatic, gastroin" +
				"testinal, cardiovascular or neurological disease; non-" +
				"stabilized diabetes; other current Axis I psychiatric " +
				"diagnosis; organic brain syndrome; past or present " +
				"abuse of alcohol or illicit drugs; were at significant " +
				"risk of suicide; or were pregnant or lactating. Other " +
				"exclusion criteria included ECT or continuous " +
				"lithium therapy in the preceding two months, mono" +
				"amine oxidase inhibitor or oral neuroleptic use in the " +
				"preceding 21 days, any antidepressant or sedative " +
				"hypnotic (except chloral hydrate) in the previous " +
				"seven days, fluoxetine in the previous 35 days, or " +
				"current therapy with an anticoagulant or type 1C " +
				"antiarrhythmic (e.g. flecainide, propafenone). Patients " +
				"who had clinically significant abnormalities on the " +
				"prestudy physical examination, ECG or laboratory " +
				"tests (hematology, biochemistry and thyroid tests) " +
				"were also excluded. The use of formal psychotherapy " +
				"was not permitted for the duration of the study.");
		study.setCharacteristic(StudyCharacteristic.OBJECTIVE, 
				"The antidepressant and anxiolytic efficacy of the selective serotonin " +
				"reuptake inhibitors paroxetine and fluoxetine was compared in patients " +
				"with moderate to severe depression.");
		study.setCharacteristic(StudyCharacteristic.STATUS, StudyCharacteristic.Status.FINISHED);
		Calendar startDate = Calendar.getInstance();
		startDate.set(1991, Calendar.DECEMBER, 13, 0, 0, 0);
		study.setCharacteristic(StudyCharacteristic.STUDY_START, startDate.getTime());
//		Calendar endDate = Calendar.getInstance();
//		endDate.set(1991, Calendar.DECEMBER, 13, 0, 0, 0);
//		study.setCharacteristic(StudyCharacteristic.STUDY_END, endDate.getTime());
		
		// Paroxetine data
		Dose dose = new Dose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup parox = new BasicPatientGroup(study, buildDrugParoxetine(), dose, 102);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(parox);
		pHamd.setRate(67);
		BasicContinuousMeasurement pCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(parox);
		pCgi.setMean(-1.69);
		pCgi.setStdDev(0.16);
		
		study.addPatientGroup(parox);
		study.setMeasurement(buildEndpointHamd(), parox, pHamd);
		study.setMeasurement(buildEndpointCgi(), parox, pCgi);
		
		// Fluoxetine data
		dose = new Dose(27.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup fluox = new BasicPatientGroup(study, buildDrugFluoxetine(), dose, 101);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(fluox);
		fHamd.setRate(67);
		BasicContinuousMeasurement fCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(fluox);
		fCgi.setMean(-1.8);
		fCgi.setStdDev(0.16);
		
		study.addPatientGroup(fluox);
		study.setMeasurement(buildEndpointHamd(), fluox, fHamd);		
		study.setMeasurement(buildEndpointCgi(), fluox, fCgi);
		
		return study;
	}

	public static AbstractStudy buildDefaultStudy2() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		BasicStudy study = new BasicStudy("De Wilde et al, 1993", buildIndicationDepression());
		study.setEndpoints(Collections.singleton(hamd));
		
		// Study characteristics
		study.setCharacteristic(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(StudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(StudyCharacteristic.ARMS, 2);
		study.setCharacteristic(StudyCharacteristic.INCLUSION,
				"After a 1-week placebo wash-out, patients suffering from DSM-III " + 
				"major depression and with a score of 18 or more on the 21-item " +
				"Hamilton Rating Scale for Depression (HRSD) received either " +
				"paroxetine or fluoxetine.");
		study.setCharacteristic(StudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.OBJECTIVE, 
				"To compare the efficacy and tolerability of once or twice daily " +
				"administration of the selective serotonin reuptake inhibitors " +
				"paroxetine and fluoxetine.");
		study.setCharacteristic(StudyCharacteristic.STATUS, StudyCharacteristic.Status.FINISHED);
		// STUDY_START, STUDY_END missing
		
		// Paroxetine data
		Dose dose = new Dose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup parox = new BasicPatientGroup(study, buildDrugParoxetine(), dose, 37);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(parox);
		pHamd.setRate(23);
		study.addPatientGroup(parox);
		study.setMeasurement(hamd, parox, pHamd);

		// Fluoxetine data
		dose = new Dose(27.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup fluox = new BasicPatientGroup(study, fluoxetine, dose, 41);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(26);
		study.addPatientGroup(fluox);
		study.setMeasurement(hamd, fluox, fHamd);
	
		return study;
	}

	public static AbstractStudy buildDefaultStudy3() {
		BasicStudy study = new BasicStudy("Bennie et al, 1995", buildIndicationDepression());
		study.setEndpoints(new HashSet<Endpoint>(
				Arrays.asList(new Endpoint[]{buildEndpointHamd(), buildEndpointCgi()})));
		
		// Study characteristics
		study.setCharacteristic(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		//study.setCharacteristic(StudyCharacteristic.CENTERS, );
		study.setCharacteristic(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(StudyCharacteristic.ARMS, 2);
		study.setCharacteristic(StudyCharacteristic.INCLUSION,
				"Psychiatric outpatients with DSM-III-R major depression or bipolar disorder (depressed).");
		study.setCharacteristic(StudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.OBJECTIVE, 
				"Comparing the efficacy and safety of sertraline with those of fluoxetine.");
		study.setCharacteristic(StudyCharacteristic.STATUS, StudyCharacteristic.Status.FINISHED);
		// STUDY_START, STUDY_END missing
		
		// Fluoxetine data
		Dose dose = new Dose(20, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup fluox = new BasicPatientGroup(study, buildDrugFluoxetine(), dose, 144);
		BasicContinuousMeasurement fCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(fluox);
		fCgi.setMean(0.67);
		fCgi.setStdDev(0.5);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(fluox);
		fHamd.setRate(63);
		study.addPatientGroup(fluox);
		study.setMeasurement(buildEndpointCgi(), fluox, fCgi);
		study.setMeasurement(buildEndpointHamd(), fluox, fHamd);

		// Sertraline data
		dose = new Dose(50, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup sertr = new BasicPatientGroup(study, buildDrugSertraline(), dose, 142);
		BasicContinuousMeasurement sCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(sertr);
		sCgi.setMean(0.69);
		sCgi.setStdDev(0.5);
		BasicRateMeasurement sHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(sertr);
		sHamd.setRate(73);
		study.addPatientGroup(sertr);
		study.setMeasurement(buildEndpointCgi(), sertr, sCgi);
		study.setMeasurement(buildEndpointHamd(), sertr, sHamd);
		
		return study;
	}

	public static AbstractStudy buildHeartStudy() {
		BasicStudy study = new BasicStudy("McMurray et al, 2003", buildIndicationChronicHeartFailure());
		study.setEndpoints(Collections.singleton(buildEndpointCVdeath()));
		
		// Study characteristics
		study.setCharacteristic(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(StudyCharacteristic.CENTERS, 618);
		study.setCharacteristic(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(StudyCharacteristic.ARMS, 2);
		study.setCharacteristic(StudyCharacteristic.INCLUSION,
				"Eligible patients were aged 18 years or older, had left-" +
				"ventricular ejection fraction 40% or lower measured " +
				"within the past 6 months, New York Heart Association " +
				"functional class II–IV (if class II, patients had to have " +
				"admission to hospital for a cardiac reason in the previous " +
				"6 months), and treatment with an ACE inhibitor at a " +
				"constant dose for 30 days or longer.");
		study.setCharacteristic(StudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(StudyCharacteristic.OBJECTIVE, 
				"Angiotensin II type 1 receptor blockers have " + 
				"favourable effects on heamodynamic measurements, " +
				"neurohumoral activity and left-ventricular remodelling when " +
				"added to angiotensin-converting-enzyme (ACE) inhibitors in " +
				"patients with chronic heart failure (CHF). We aimed to find " +
				"out whether these drugs improve clinical outcome.");
		study.setCharacteristic(StudyCharacteristic.STATUS, StudyCharacteristic.Status.FINISHED);
		Calendar startDate = Calendar.getInstance();
		startDate.set(1999, Calendar.MARCH, 1, 0, 0, 0);
		study.setCharacteristic(StudyCharacteristic.STUDY_START, startDate.getTime());
		Calendar endDate = Calendar.getInstance();
		endDate.set(2003, Calendar.MARCH, 31, 0, 0, 0);
		study.setCharacteristic(StudyCharacteristic.STUDY_END, endDate.getTime());
		
		// Candesartan data
		Dose cDose = new Dose(32, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup cand = new BasicPatientGroup(study, buildDrugCandesartan(), cDose , 1273);
		BasicRateMeasurement cDeath = new BasicRateMeasurement(buildEndpointCVdeath(), cand);
		cDeath.setRate(302);
		study.addPatientGroup(cand);
		study.setMeasurement(buildEndpointCVdeath(), cand, cDeath);
		
		// Placebo data
		Dose pDose = new Dose(32, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup placebo = new BasicPatientGroup(study, buildPlacebo(), pDose , 1271);
		BasicRateMeasurement pDeath = new BasicRateMeasurement(buildEndpointCVdeath(), placebo);
		pDeath.setRate(347);
		study.addPatientGroup(placebo);
		study.setMeasurement(buildEndpointCVdeath(), placebo, pDeath);
		
		return study;
	}
	
	private static Drug buildPlacebo() {
		if (s_placebo == null) {
			s_placebo = new Drug("Placebo", "");
		}
		return s_placebo;
	}


	public static Drug buildDrugParoxetine() {
		if (s_parox == null) {
			s_parox = new Drug("Paroxetine", "A04AA01");
		}
		return s_parox;
	}
	
	public static Drug buildDrugSertraline() {
		if (s_sertr == null) {
			s_sertr = new Drug("Sertraline", "N06AB06");
		}
		return s_sertr;
	}
	
	public static Drug buildDrugFluoxetine() {
		if (s_fluox == null) {
			s_fluox = new Drug("Fluoxetine", "N06AB03");
		}
		return s_fluox;
	}

	public static Drug buildDrugCandesartan() {
		if (s_candesartan == null) {
			s_candesartan = new Drug("Candesartan", "C09CA06");
		}
		return s_candesartan;
	}
	
	public static Indication buildIndicationDepression() {
		if (s_indicationDepression == null) {
			s_indicationDepression = new Indication(310497006L, "Severe depression");
		}
		return s_indicationDepression;
	}
	
	public static Indication buildIndicationChronicHeartFailure() {
		if (s_indicationHeartFailure == null) {
			s_indicationHeartFailure = new Indication(48447003L, "Chronic Heart Failure");
		}
		return s_indicationHeartFailure;
	}
	
	public static Drug buildDrugViagra() {
		if (s_viagra == null) {
			s_viagra = new Drug("Viagra", "atc");
		}
		return s_viagra;
	}

	public static Endpoint buildEndpointHamd() {
		if (s_endpointHamd == null) {
			Endpoint e = new Endpoint("HAM-D", Type.RATE);
			s_endpointHamd = e;
		}
		return s_endpointHamd;
	}

	public static Endpoint buildEndpointCgi() {
		if (s_endpointCgi == null) { 
			Endpoint cgi = new Endpoint("CGI Severity", Type.CONTINUOUS);
			cgi.setDescription("Change from baseline CGI Severity of Illness score");
			s_endpointCgi = cgi;
		}
		return s_endpointCgi;
	}
	
	public static Endpoint buildEndpointUnused() {
		if (s_endpointUnused == null) { 
			Endpoint unused = new Endpoint("Unused Endpoint", Type.RATE);
			unused.setDescription("");
			s_endpointUnused = unused;
		}
		return s_endpointUnused;
	}
	
	public static Endpoint buildEndpointCVdeath() {
		if (s_endpointCVdeath == null) {
			Endpoint e = new Endpoint("Cardiovascular Death", Type.RATE);
			s_endpointCVdeath = e;
		}
		return s_endpointCVdeath;
	}

}
