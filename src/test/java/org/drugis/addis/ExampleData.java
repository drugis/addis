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

package org.drugis.addis;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.CategoricalPopulationCharacteristic;
import org.drugis.addis.entities.ContinuousPopulationCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MockStudyBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.entities.relativeeffect.RelativeEffectFactory;
import org.drugis.addis.mocks.MockMetaBenefitRiskAnalysis;

public class ExampleData {
	private static Study s_studyFava02 = null;
	private static Indication s_indicationDepression;
	private static Endpoint s_endpointHamd;
	private static Endpoint s_endpointCgi;
	private static AdverseEvent s_convulsion;
	private static AdverseEvent s_sexdysf;
	private static AdverseEvent s_diarrhea;
	private static Drug s_parox;
	private static Drug s_fluox;
	private static Drug s_viagra;
	private static Indication s_indicationHeartFailure;
	private static Drug s_candesartan;
	private static Endpoint s_endpointCVdeath;
	private static Drug s_sertr;
	private static Drug s_placebo;
	private static Drug s_citalopram;
	private static Drug s_escitalopram;
	private static Study s_studyMcMurray;
	private static Study s_study3Arm;

	private static Study s_studyBennie;
	private static Study s_studyDeWilde;
	private static Study s_studyChouinard;
	private static Study s_MultipleArmsperDrugStudy;
	
	private static CategoricalPopulationCharacteristic s_gender;
	private static ContinuousPopulationCharacteristic s_age;
	private static Endpoint s_endpointMadrs;
	private static Study s_studyBurke;
	

	public static void initDefaultData(Domain domain) {
		clearAll();
		// depression data
		domain.addIndication(buildIndicationDepression());
		domain.addEndpoint(buildEndpointHamd());
		domain.addEndpoint(buildEndpointCgi());
		//domain.addEndpoint(buildEndpointMadrs());
		domain.addDrug(buildDrugFluoxetine());
		domain.addDrug(buildDrugParoxetine());
		domain.addDrug(buildDrugSertraline());
		//domain.addDrug(buildDrugCitalopram());
		//domain.addDrug(buildDrugEscitalopram());
		domain.addDrug(buildPlacebo());
		domain.addStudy(buildStudyChouinard());
		domain.addStudy(buildStudyDeWilde());		
		domain.addStudy(buildStudyBennie());	
		//domain.addStudy(buildStudyBurke());
		domain.addStudy(buildStudyMultipleArmsperDrug());
			
		// heart failure data
		domain.addIndication(buildIndicationChronicHeartFailure());
		domain.addDrug(buildDrugCandesartan());
		domain.addEndpoint(buildEndpointCVdeath());
		domain.addStudy(buildStudyMcMurray());
		
		// unused stuff
		domain.addPopulationCharacteristic(buildGenderVariable());
		domain.addPopulationCharacteristic(buildAgeVariable());
		domain.addAdverseEvent(buildAdverseEventConvulsion());
	}

	public static CategoricalPopulationCharacteristic buildGenderVariable() {
		if (s_gender == null) {
			s_gender = new CategoricalPopulationCharacteristic("Gender", new String[]{"Male", "Female"});
		}
		return s_gender;
	}
	
	public static ContinuousPopulationCharacteristic buildAgeVariable() {
		if (s_age == null) {
			s_age = new ContinuousPopulationCharacteristic("Age");
		}
		return s_age;
	}

	private static void clearAll() {
		s_indicationDepression = null;
		s_endpointHamd = null;
		s_endpointCgi = null;
		s_parox = null;
		s_fluox = null;
		s_viagra = null;
		s_indicationHeartFailure = null;
		s_candesartan = null;
		s_endpointCVdeath = null;
		s_sertr = null;
		s_placebo = null;
		s_studyMcMurray = null;
		s_study3Arm = null;

		s_studyBennie = null;
		s_studyDeWilde = null;
		s_studyChouinard = null;
	}

	public static Study buildStudyChouinard() {
		if (s_studyChouinard == null) {
			s_studyChouinard = realBuildStudyChouinard();
		}
		
		return s_studyChouinard;
	}
	
	public static Study buildStudyChouinardNoHamd() {
		Study s = realBuildStudyChouinard();
		List<Endpoint> endpoints = s.getEndpoints();
		endpoints.remove(buildEndpointHamd());
		s.setEndpoints(endpoints);
		return s;
	}

	private static Study realBuildStudyChouinard() {
		Study study = new Study("Chouinard et al, 1999", buildIndicationDepression());
		study.setEndpoints(new ArrayList<Endpoint>(
				Arrays.asList(new Endpoint[]{buildEndpointHamd(), buildEndpointCgi()})));
		study.addAdverseEvent(buildAdverseEventConvulsion());
		
		// Study characteristics
		study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(BasicStudyCharacteristic.CENTERS, 8);
		study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(BasicStudyCharacteristic.INCLUSION,
				"Patients were recruited " + 
				"through newspaper ads and referrals. Patients were " +
				"included if they had symptoms of depression for at " +
				"least one month prior to the screening visit, a total " +
				"score of 20 on the 21-item Hamilton Depression " +
				"Rating Scale (HAM-D) (Hamilton, 1960), and a " +
				"score of two on item one HAM-D at the screening " +
				"visit (5–14 days prior to baseline) and at entry (Day " +
				"0).");
		study.setCharacteristic(BasicStudyCharacteristic.EXCLUSION,
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
		study.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, 
				"The antidepressant and anxiolytic efficacy of the selective serotonin " +
				"reuptake inhibitors paroxetine and fluoxetine was compared in patients " +
				"with moderate to severe depression.");
		study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.COMPLETED);
		Calendar startDate = Calendar.getInstance();
		startDate.set(Calendar.MILLISECOND, 0);
		startDate.set(1991, Calendar.DECEMBER, 13, 0, 0, 0);
		study.setCharacteristic(BasicStudyCharacteristic.STUDY_START, startDate.getTime());
		
		// Paroxetine data 1
		FixedDose dose = new FixedDose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		Arm parox = new Arm(buildDrugParoxetine(), dose, 102);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(parox);
		pHamd.setRate(67);
		BasicContinuousMeasurement pCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(parox);
		pCgi.setMean(-1.69);
		pCgi.setStdDev(0.16);
		BasicRateMeasurement pConv = (BasicRateMeasurement) buildAdverseEventConvulsion().buildMeasurement(parox);
		pConv.setRate(10);
		pConv.setSampleSize(40);
		
		study.addArm(parox);
		study.setMeasurement(buildEndpointHamd(), parox, pHamd);
		study.setMeasurement(buildEndpointCgi(), parox, pCgi);
		study.setMeasurement(buildAdverseEventConvulsion(),parox, pConv);
		
	
		
		// Fluoxetine data
		dose = new FixedDose(27.5, SIUnit.MILLIGRAMS_A_DAY);
		Arm fluox = new Arm(buildDrugFluoxetine(), dose, 101);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(fluox);
		fHamd.setRate(67);
		BasicContinuousMeasurement fCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(fluox);
		fCgi.setMean(-1.8);
		fCgi.setStdDev(0.16);
		BasicRateMeasurement fConv = (BasicRateMeasurement) buildAdverseEventConvulsion().buildMeasurement(parox);
		fConv.setRate(12);
		fConv.setSampleSize(40);
		
		study.addArm(fluox);
		study.setMeasurement(buildEndpointHamd(), fluox, fHamd);		
		study.setMeasurement(buildEndpointCgi(), fluox, fCgi);
		study.setMeasurement(buildAdverseEventConvulsion(), fluox, pConv);
		return study;
	}

	public static Study buildStudyDeWilde() {
		if (s_studyDeWilde == null) {
			s_studyDeWilde = realBuildStudyDeWilde();
		}
		
		return s_studyDeWilde;
	}

	public static Study realBuildStudyDeWilde() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		Study study = new Study("De Wilde et al, 1993", buildIndicationDepression());
		study.setEndpoints(Collections.singletonList(hamd));
		study.setAdverseEvents(Collections.singletonList(buildAdverseEventConvulsion()));
		
		// Study characteristics
		study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(BasicStudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(BasicStudyCharacteristic.INCLUSION,
				"After a 1-week placebo wash-out, patients suffering from DSM-III " + 
				"major depression and with a score of 18 or more on the 21-item " +
				"Hamilton Rating Scale for Depression (HRSD) received either " +
				"paroxetine or fluoxetine.");
		study.setCharacteristic(BasicStudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, 
				"To compare the efficacy and tolerability of once or twice daily " +
				"administration of the selective serotonin reuptake inhibitors " +
				"paroxetine and fluoxetine.");
		study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.COMPLETED);
		// STUDY_START, STUDY_END missing
		
		// Paroxetine data
		FixedDose dose = new FixedDose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		Arm parox = new Arm(buildDrugParoxetine(), dose, 37);
		
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(parox);
		pHamd.setRate(23);
		BasicRateMeasurement pConv = (BasicRateMeasurement) buildAdverseEventConvulsion().buildMeasurement(parox);
		pConv.setRate(10);
		pConv.setSampleSize(40);
		
		study.addArm(parox);
		study.setMeasurement(hamd, parox, pHamd);
		study.setMeasurement(buildAdverseEventConvulsion(),parox, pConv);

		// Fluoxetine data
		dose = new FixedDose(27.5, SIUnit.MILLIGRAMS_A_DAY);
		Arm fluox = new Arm(fluoxetine, dose, 41);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(26);
		BasicRateMeasurement fConv = (BasicRateMeasurement) buildAdverseEventConvulsion().buildMeasurement(fluox);
		fConv.setRate(10);
		fConv.setSampleSize(34);
		
		study.addArm(fluox);
		study.setMeasurement(hamd, fluox, fHamd);
		study.setMeasurement(buildAdverseEventConvulsion(), fluox, fConv);
		return study;
	}

	public static Study buildStudyMultipleArmsperDrug() {
		if (s_MultipleArmsperDrugStudy == null) {
			s_MultipleArmsperDrugStudy = realBuildMultipleArmsperDrugStudy();
		}
		
		return s_MultipleArmsperDrugStudy;
	}
	
	private static Study realBuildMultipleArmsperDrugStudy() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		Study study = new Study("MultipleArms, 1993", buildIndicationDepression());
		study.setEndpoints(Collections.singletonList(hamd));
		
		// Study characteristics
		study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(BasicStudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(BasicStudyCharacteristic.INCLUSION,
				"After a 1-week placebo wash-out, patients suffering from DSM-III " + 
				"major depression and with a score of 18 or more on the 21-item " +
				"Hamilton Rating Scale for Depression (HRSD) received either " +
				"paroxetine or fluoxetine.");
		study.setCharacteristic(BasicStudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, 
				"To compare the efficacy and tolerability of once or twice daily " +
				"administration of the selective serotonin reuptake inhibitors " +
				"paroxetine and fluoxetine.");
		study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.COMPLETED);
		// STUDY_START, STUDY_END missing
		
		// Paroxetine data 1
		FixedDose dose = new FixedDose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		Arm parox = new Arm(buildDrugParoxetine(), dose, 37);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(parox);
		pHamd.setRate(23);
		study.addArm(parox);
		study.setMeasurement(hamd, parox, pHamd);
		
		// Paroxetine data 2
		dose = new FixedDose(5.5, SIUnit.MILLIGRAMS_A_DAY);
		parox = new Arm(buildDrugParoxetine(), dose, 54);
		pHamd = (BasicRateMeasurement)hamd.buildMeasurement(parox);
		pHamd.setRate(23);
		study.addArm(parox);
		study.setMeasurement(hamd, parox, pHamd);

		// Fluoxetine data
		dose = new FixedDose(27.5, SIUnit.MILLIGRAMS_A_DAY);
		Arm fluox = new Arm(fluoxetine, dose, 41);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(26);
		study.addArm(fluox);
		study.setMeasurement(hamd, fluox, fHamd);
		return study;
	}
	
	public static Study buildStudyBennie() {
		if (s_studyBennie == null) {
			s_studyBennie = realBuildStudyBennie();
		}
		
		return s_studyBennie;
	}

	public static Study buildStudyBurke() {
		if (s_studyBurke == null) {
			s_studyBurke = realBuildStudyBurke();
		}
		return s_studyBurke;
	}

	private static Study realBuildStudyBurke() {
		Study study = new Study("Burke et al, 2002", buildIndicationDepression());
		study.setEndpoints(new ArrayList<Endpoint>(Arrays.asList(new Endpoint[]{buildEndpointCgi(), buildEndpointMadrs()})));
		study.addAdverseEvent(buildAdverseEventDiarrhea());
		
		// Study characteristics
		study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(BasicStudyCharacteristic.INCLUSION,
		"Eligible participants were male or female outpatients, 18 to 65 years of age, with DSM-IV diagnosis of major depressive disorder. Patients were required to meet DSM-IV criteria for a major depressive episode, at least 4 weeks in duration, and to have a " +
		"minimum score of 22 on the Montgomery-Asberg Depression Rating Scale (MADRS), and a minimum score " +
		"of 2 on item 1 (depressed mood) of the Hamilton Rating Scale for Depression (HAM-D).");
		study.setCharacteristic(BasicStudyCharacteristic.EXCLUSION,
		"Patients were excluded if they had any DSM-IV Axis I disorder other than major depression, any personality disorder, a history of substance abuse, a suicide attempt within the past year, or evidence of active suicidal ideation (as indicated by a score of at least 5 on item 10 of the MADRS). Women of childbearing potential were included only if they agreed to use a medically acceptable method of contraception; pregnant or lactating women were excluded. No concomitant psychotropic medication was permitted, except zolpidem for insomnia (no more than 3 times per week).");
		study.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, 
		"Escitalopram is the single isomer responsible for the serotonin reuptake inhibition produced by the racemic antidepressant citalopram. The present randomized, double-blind, placebo-controlled, fixed-dose multicenter trial was designed to evaluate the efficacy and tolerability of escitalopram in the treatment of major depressive disorder.");
		study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.COMPLETED);
		// STUDY_START, STUDY_END missing
		
		// Citalopram data
		FixedDose dose = new FixedDose(40, SIUnit.MILLIGRAMS_A_DAY);
		Arm cita = new Arm(buildDrugCitalopram(), dose, 125);
		BasicContinuousMeasurement cCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(cita);
		cCgi.setMean(-1.2);
		cCgi.setStdDev(0.1);
		BasicRateMeasurement cMadrs = (BasicRateMeasurement)buildEndpointMadrs().buildMeasurement(cita);
		cMadrs.setRate(57);
		study.addArm(cita);
		study.setMeasurement(buildEndpointCgi(), cita, cCgi);
		study.setMeasurement(buildEndpointMadrs(), cita, cMadrs);
		
		// Escitalopram high dose data
		dose = new FixedDose(20, SIUnit.MILLIGRAMS_A_DAY);
		Arm esciHigh = new Arm(buildDrugEscitalopram(), dose, 125);
		BasicContinuousMeasurement ehCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(esciHigh);
		ehCgi.setMean(-1.4);
		ehCgi.setStdDev(0.1);
		BasicRateMeasurement ehMadrs = (BasicRateMeasurement)buildEndpointMadrs().buildMeasurement(esciHigh);
		ehMadrs.setRate(64);
		study.addArm(esciHigh);
		study.setMeasurement(buildEndpointCgi(), esciHigh, ehCgi);
		study.setMeasurement(buildEndpointMadrs(), esciHigh, ehMadrs);

		// Escitalopram low dose data
		dose = new FixedDose(10, SIUnit.MILLIGRAMS_A_DAY);
		Arm esciLow = new Arm(buildDrugEscitalopram(), dose, 119);
		BasicContinuousMeasurement elCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(esciLow);
		elCgi.setMean(-1.3);
		elCgi.setStdDev(0.1);
		BasicRateMeasurement elMadrs = (BasicRateMeasurement)buildEndpointMadrs().buildMeasurement(esciLow);
		elMadrs.setRate(59);
		study.addArm(esciLow);
		study.setMeasurement(buildEndpointCgi(), esciLow, elCgi);
		study.setMeasurement(buildEndpointMadrs(), esciLow, elMadrs);
		
		// Placebo data
		dose = new FixedDose(0, SIUnit.MILLIGRAMS_A_DAY);
		Arm placebo = new Arm(buildPlacebo(), dose, 122);
		BasicContinuousMeasurement plCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(placebo);
		plCgi.setMean(-0.8);
		plCgi.setStdDev(0.1);
		BasicRateMeasurement plMadrs = (BasicRateMeasurement)buildEndpointMadrs().buildMeasurement(placebo);
		plMadrs.setRate(33);
		study.addArm(placebo);
		study.setMeasurement(buildEndpointCgi(), placebo, plCgi);
		study.setMeasurement(buildEndpointMadrs(), placebo, plMadrs);
		return study;
}
	
	
	private static Study realBuildStudyBennie() {
		Study study = new Study("Bennie et al, 1995", buildIndicationDepression());
		study.setEndpoints(new ArrayList<Endpoint>(
				Arrays.asList(new Endpoint[]{buildEndpointHamd(), buildEndpointCgi()})));
		study.addAdverseEvent(buildAdverseEventConvulsion());
		
		// Study characteristics
		study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(BasicStudyCharacteristic.INCLUSION,
				"Psychiatric outpatients with DSM-III-R major depression or bipolar disorder (depressed).");
		study.setCharacteristic(BasicStudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, 
				"Comparing the efficacy and safety of sertraline with those of fluoxetine.");
		study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.COMPLETED);
		// STUDY_START, STUDY_END missing
		
		// Fluoxetine data
		FixedDose dose = new FixedDose(20, SIUnit.MILLIGRAMS_A_DAY);
		Arm fluox = new Arm(buildDrugFluoxetine(), dose, 144);
		BasicContinuousMeasurement fCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(fluox);
		fCgi.setMean(0.67);
		fCgi.setStdDev(0.5);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(fluox);
		fHamd.setRate(63);
		study.addArm(fluox);
		study.setMeasurement(buildEndpointCgi(), fluox, fCgi);
		study.setMeasurement(buildEndpointHamd(), fluox, fHamd);

		// Sertraline data
		dose = new FixedDose(50, SIUnit.MILLIGRAMS_A_DAY);
		Arm sertr = new Arm(buildDrugSertraline(), dose, 142);
		BasicContinuousMeasurement sCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(sertr);
		sCgi.setMean(0.69);
		sCgi.setStdDev(0.5);
		BasicRateMeasurement sHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(sertr);
		sHamd.setRate(73);
		study.addArm(sertr);
		study.setMeasurement(buildEndpointCgi(), sertr, sCgi);
		study.setMeasurement(buildEndpointHamd(), sertr, sHamd);
		return study;
	}

	public static Study buildStudyAdditionalThreeArm() {
		if (s_study3Arm == null) {
			s_study3Arm = realBuildStudyThreeArm(); 
		}
		
		return s_study3Arm;
	}

	private static Study realBuildStudyThreeArm() {
		Study study = new Study("SciFictional et al, 2359", buildIndicationDepression());
		study.setEndpoints(new ArrayList<Endpoint>(
				Arrays.asList(new Endpoint[]{buildEndpointHamd(), buildEndpointCgi()})));
		
		study.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, 
				"This is a fictional study that I just created because I need a three-arm study.");
		study.setCharacteristic(BasicStudyCharacteristic.STATUS,
				BasicStudyCharacteristic.Status.COMPLETED);

		// Paroxetine data
		FixedDose dose = new FixedDose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		Arm parox = new Arm(buildDrugParoxetine(), dose, 37);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(parox);
		pHamd.setRate(23);
		BasicContinuousMeasurement pCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(parox);
		pCgi.setMean(-1.69);
		pCgi.setStdDev(0.16);
		study.addArm(parox);
		study.setMeasurement(buildEndpointHamd(), parox, pHamd);
		study.setMeasurement(buildEndpointCgi(), parox, pCgi);
		
		// Fluoxetine data
		dose = new FixedDose(20, SIUnit.MILLIGRAMS_A_DAY);
		Arm fluox = new Arm(buildDrugFluoxetine(), dose, 144);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(fluox);
		fHamd.setRate(63);
		BasicContinuousMeasurement fCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(fluox);
		fCgi.setMean(-1.8);
		fCgi.setStdDev(0.16);
		study.addArm(fluox);
		study.setMeasurement(buildEndpointHamd(), fluox, fHamd);
		study.setMeasurement(buildEndpointCgi(), fluox, fCgi);

		// Sertraline data
		dose = new FixedDose(50, SIUnit.MILLIGRAMS_A_DAY);
		Arm sertr = new Arm(buildDrugSertraline(), dose, 142);
		BasicRateMeasurement sHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(sertr);
		sHamd.setRate(73);
		BasicContinuousMeasurement sCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(sertr);
		sCgi.setMean(-0.84);
		sCgi.setStdDev(0.24);
		study.addArm(sertr);
		study.setMeasurement(buildEndpointHamd(), sertr, sHamd);
		study.setMeasurement(buildEndpointCgi(), sertr, sCgi);
		return study;
	}

	public static Study buildStudyMcMurray() {
		if (s_studyMcMurray == null) {
			s_studyMcMurray = realBuildStudyMcMurray();
		}
		
		return s_studyMcMurray;
	}

	private static Study realBuildStudyMcMurray() {
		Study study = new Study("McMurray et al, 2003", buildIndicationChronicHeartFailure());
		study.setEndpoints(Collections.singletonList(buildEndpointCVdeath()));
		
		// Study characteristics
		study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(BasicStudyCharacteristic.CENTERS, 618);
		study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(BasicStudyCharacteristic.INCLUSION,
				"Eligible patients were aged 18 years or older, had left-" +
				"ventricular ejection fraction 40% or lower measured " +
				"within the past 6 months, New York Heart Association " +
				"functional class II–IV (if class II, patients had to have " +
				"admission to hospital for a cardiac reason in the previous " +
				"6 months), and treatment with an ACE inhibitor at a " +
				"constant dose for 30 days or longer.");
		study.setCharacteristic(BasicStudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, 
				"Angiotensin II type 1 receptor blockers have " + 
				"favourable effects on heamodynamic measurements, " +
				"neurohumoral activity and left-ventricular remodelling when " +
				"added to angiotensin-converting-enzyme (ACE) inhibitors in " +
				"patients with chronic heart failure (CHF). We aimed to find " +
				"out whether these drugs improve clinical outcome.");
		study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.COMPLETED);
		Calendar startDate = Calendar.getInstance();
		startDate.set(1999, Calendar.MARCH, 1, 0, 0, 0);
		study.setCharacteristic(BasicStudyCharacteristic.STUDY_START, startDate.getTime());
		Calendar endDate = Calendar.getInstance();
		endDate.set(2003, Calendar.MARCH, 31, 0, 0, 0);
		study.setCharacteristic(BasicStudyCharacteristic.STUDY_END, endDate.getTime());
		
		// Candesartan data
		FixedDose cDose = new FixedDose(32, SIUnit.MILLIGRAMS_A_DAY);
		Arm cand = new Arm(buildDrugCandesartan(), cDose, 1273);
		BasicRateMeasurement cDeath = new BasicRateMeasurement(302, cand.getSize());
		study.addArm(cand);
		study.setMeasurement(buildEndpointCVdeath(), cand, cDeath);
		
		// Placebo data
		FixedDose pDose = new FixedDose(32, SIUnit.MILLIGRAMS_A_DAY);
		Arm placebo = new Arm(buildPlacebo(), pDose, 1271);
		BasicRateMeasurement pDeath = new BasicRateMeasurement(347, placebo.getSize());
		study.addArm(placebo);
		OutcomeMeasure om = buildEndpointCVdeath();
		study.setMeasurement(om, placebo, pDeath);
		
		return study;
	}
	
	public static Study buildStudyFava2002() {
		if (s_studyFava02 == null) {
			s_studyFava02 = realBuildStudyFava02();
		}
	
		return s_studyFava02;
	}

	private static Study realBuildStudyFava02() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		Drug sertraline = buildDrugSertraline();
		Drug paroxetine = buildDrugParoxetine();
		Study study = new Study("Fava et al, 2002", buildIndicationDepression());
		study.setEndpoints(Collections.singletonList(hamd));
		List<AdverseEvent> ade = new ArrayList<AdverseEvent>();
		ade.add(buildAdverseEventConvulsion());
		ade.add(buildAdverseEventSexualDysfunction());
		study.setAdverseEvents(ade);
		study.setPopulationCharacteristics(Collections.<PopulationCharacteristic>singletonList(buildGenderVariable()));
		
		// Study characteristics
		study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(BasicStudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(BasicStudyCharacteristic.INCLUSION,
				"");
		study.setCharacteristic(BasicStudyCharacteristic.EXCLUSION,
				"");
		study.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, 
				"");
		study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.COMPLETED);
		
		// Sertraline data
		FixedDose dose = new FixedDose(75.0, SIUnit.MILLIGRAMS_A_DAY);
		Arm sertr = new Arm(sertraline, dose, 96);
		BasicRateMeasurement sHamd = (BasicRateMeasurement)hamd.buildMeasurement(sertr);
		sHamd.setRate(70);
		study.addArm(sertr);
		study.setMeasurement(hamd, sertr, sHamd);

		// Fluoxetine data
		dose = new FixedDose(30.0, SIUnit.MILLIGRAMS_A_DAY);
		Arm fluox = new Arm(fluoxetine, dose, 92);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(57);
		study.addArm(fluox);
		study.setMeasurement(hamd, fluox, fHamd);
		
		// Paroxetine data
		dose = new FixedDose(0.0, SIUnit.MILLIGRAMS_A_DAY);
		Arm parox = new Arm(paroxetine, dose, 93);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(parox);
		pHamd.setRate(64);
		study.addArm(parox);
		study.setMeasurement(hamd, parox, pHamd);
		
		return study;
	}
	
	private static AdverseEvent buildAdverseEventSexualDysfunction() {
		if (s_sexdysf == null) {
			s_sexdysf = new AdverseEvent("Sexual Dysfunction", Variable.Type.RATE);
			s_sexdysf.setDescription("Rate");
		}
		return s_sexdysf;
	}

	public static AdverseEvent buildAdverseEventDiarrhea() {
		if (s_diarrhea == null) {
			s_diarrhea = new AdverseEvent("Diarrhea", Variable.Type.RATE);
			s_diarrhea.setDescription("Rate");
		}
		return s_diarrhea;
	}

	public static Drug buildPlacebo() {
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
	
	public static Drug buildDrugCitalopram() {
		if (s_citalopram == null) {
			s_citalopram = new Drug("Citalopram", "N06AB04");
		}
		return s_citalopram;
	}
	
	public static Drug buildDrugEscitalopram() {
		if (s_escitalopram == null) {
			s_escitalopram = new Drug("Escitalopram", "N06AB10");
		}
		return s_escitalopram;
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
			Endpoint e = new Endpoint("HAM-D Responders", Variable.Type.RATE);
			e.setDescription("Responders with a 50% increase in HAM-D score");
			s_endpointHamd = e;
		}
		return s_endpointHamd;
	}

	public static Endpoint buildEndpointCgi() {
		if (s_endpointCgi == null) { 
			Endpoint cgi = new Endpoint("CGI Severity Change", Variable.Type.CONTINUOUS);
			cgi.setDescription("Change from baseline CGI Severity of Illness score");
			cgi.setUnitOfMeasurement("Deviation from the baseline of CGI Severity of Illness score");
			s_endpointCgi = cgi;
		}
		return s_endpointCgi;
	}

	public static Endpoint buildEndpointMadrs() {
		if (s_endpointMadrs == null) { 
			Endpoint madrs = new Endpoint("MADRS Responders", Variable.Type.RATE);
			madrs.setDescription("Responders with a 50% increase in MADRS score");
			madrs.setUnitOfMeasurement("Ratio of Patients");
			s_endpointMadrs = madrs;
		}
		return s_endpointMadrs;
	}

	public static Endpoint buildEndpointCVdeath() {
		if (s_endpointCVdeath == null) {
			Endpoint e = new Endpoint("Cardiovascular Death Incidence", Variable.Type.RATE, Direction.LOWER_IS_BETTER);
			e.setDescription("Rate of mortality due to cardiovascular causes");
			s_endpointCVdeath = e;
		}
		return s_endpointCVdeath;
	}


	public static NetworkMetaAnalysis buildNetworkMetaAnalysisHamD() {
		List<Study> studies = Arrays.asList(new Study[] {
				buildStudyBennie(), buildStudyChouinard(), buildStudyDeWilde(), buildStudyFava2002()});
		List<Drug> drugs = Arrays.asList(new Drug[] {buildDrugFluoxetine(), buildDrugParoxetine(), 
				buildDrugSertraline()});
		
		NetworkMetaAnalysis analysis = new NetworkMetaAnalysis("Test Network", 
				buildIndicationDepression(), buildEndpointHamd(),
				studies, drugs, buildMap(studies, drugs));
		
		return analysis;
	}
	
	public static NetworkMetaAnalysis buildNetworkMetaAnalysisConvulsion() {
		List<Study> studies = Arrays.asList(new Study[] {
				buildStudyBennie(), buildStudyChouinard()});
		List<Drug> drugs = Arrays.asList(new Drug[] {buildDrugFluoxetine(), buildDrugParoxetine(), 
				buildDrugSertraline()});
		
		NetworkMetaAnalysis analysis = new NetworkMetaAnalysis("Test Network2", 
				buildIndicationDepression(), buildAdverseEventConvulsion(),
				studies, drugs, buildMap(studies, drugs));
		
		return analysis;
	}
	
	public static NetworkMetaAnalysis buildNetworkMetaAnalysisCgi() {
		List<Study> studies = Arrays.asList(new Study[] {
				buildStudyBennie(), buildStudyChouinard()});
		List<Drug> drugs = Arrays.asList(new Drug[] {buildDrugFluoxetine(), buildDrugParoxetine(), 
				buildDrugSertraline()});
		
		NetworkMetaAnalysis analysis = new NetworkMetaAnalysis("CGI network", 
				buildIndicationDepression(), buildEndpointCgi(),
				studies, drugs, buildMap(studies, drugs));
		
		return analysis;
	}
	
	
	public static Map<Study, Map<Drug, Arm>> buildMap(List<Study> studies,
			List<Drug> drugs) {
		Map<Study, Map<Drug, Arm>> map = new HashMap<Study, Map<Drug,Arm>>();
		for (Study s : studies) {
			Map<Drug, Arm> drugMap = new HashMap<Drug, Arm>();
			for (Drug d : drugs) {
				if (s.getDrugs().contains(d)) {
					drugMap.put(d, RelativeEffectFactory.findFirstArm(s, d));
				}
			}
			map.put(s, drugMap);
		}
		return map;
	}
	
	public static AdverseEvent buildAdverseEventConvulsion() {
		if (s_convulsion == null) {
			s_convulsion = new AdverseEvent("Convulsion", Variable.Type.RATE);
			s_convulsion.setDescription("Rate of convulsion during study");
		}
		return s_convulsion;
	}

	public static MetaBenefitRiskAnalysis buildMetaBenefitRiskAnalysis() {
		Indication indication = buildIndicationDepression();
		
		List<OutcomeMeasure> outcomeMeasureList = new ArrayList<OutcomeMeasure>();
		outcomeMeasureList.add(buildEndpointHamd());
		outcomeMeasureList.add(buildAdverseEventConvulsion());
		
		List<MetaAnalysis> metaAnalysisList = new ArrayList<MetaAnalysis>();
		metaAnalysisList.add(buildMetaAnalysisHamd());
		metaAnalysisList.add(buildMetaAnalysisConv());
		
		Drug parox = buildDrugParoxetine();
		List<Drug> fluoxList = Collections.singletonList(buildDrugFluoxetine());
		
		return new MockMetaBenefitRiskAnalysis("testBenefitRiskAnalysis",
										indication, metaAnalysisList, parox, fluoxList);										
	}

	public static StudyBenefitRiskAnalysis buildStudyBenefitRiskAnalysis() {
		Indication indication = buildIndicationDepression();
		
		List<OutcomeMeasure> outcomeMeasureList = new ArrayList<OutcomeMeasure>();
		Study study = ExampleData.buildStudyChouinard();
		outcomeMeasureList.add(buildEndpointHamd());
		outcomeMeasureList.add(buildEndpointCgi());
		
		List<Arm> arms = study.getArms();
	
		return new MockStudyBenefitRiskAnalysis("testBenefitRiskAnalysis",
										indication, study, outcomeMeasureList, arms, AnalysisType.SMAA);										
	}
	
	public static StudyBenefitRiskAnalysis buildStudyLOBenefitRiskAnalysis() {
		Indication indication = buildIndicationDepression();
		
		List<OutcomeMeasure> outcomeMeasureList = new ArrayList<OutcomeMeasure>();
		Study study = ExampleData.buildStudyChouinard();
		outcomeMeasureList.add(buildEndpointHamd());
		outcomeMeasureList.add(buildAdverseEventConvulsion());
		
		List<Arm> arms = study.getArms();
	
		return new MockStudyBenefitRiskAnalysis("testBenefitRiskAnalysis",
										indication, study, outcomeMeasureList, arms, AnalysisType.LyndOBrien);										
	}

	public static MetaAnalysis buildMetaAnalysisConv() {
		List<StudyArmsEntry> studyArms = new ArrayList<StudyArmsEntry>();
		
		Study s1 = buildStudyChouinard();
		studyArms.add(new StudyArmsEntry(s1, s1.getArms().get(0), s1.getArms().get(1)));
		
		Study s2 = buildStudyDeWilde();
		studyArms.add(new StudyArmsEntry(s2, s2.getArms().get(0), s2.getArms().get(1)));		
		
		return new RandomEffectsMetaAnalysis("Convulsion test analysis", buildAdverseEventConvulsion(), studyArms);
	}

	public static MetaAnalysis buildMetaAnalysisHamd() {
		List<StudyArmsEntry> studyArms = new ArrayList<StudyArmsEntry>();
		
		Study s1 = buildStudyChouinard();
		studyArms.add(new StudyArmsEntry(s1, s1.getArms().get(0), s1.getArms().get(1)));
		Study s2 = buildStudyDeWilde();
		studyArms.add(new StudyArmsEntry(s2, s2.getArms().get(0), s2.getArms().get(1)));		
		
		return new RandomEffectsMetaAnalysis("Hamd test analysis", buildEndpointHamd(), studyArms);
	}

	public static MetaBenefitRiskAnalysis realBuildContinuousMockBenefitRisk() {
		OutcomeMeasure om = buildEndpointCgi();
		Drug fluox = buildDrugFluoxetine();
		Drug parox = buildDrugParoxetine();
		Study study = buildStudyChouinard();
		MetaAnalysis ma = new RandomEffectsMetaAnalysis("ma", om, Collections.singletonList(study), fluox, parox);
		MetaBenefitRiskAnalysis br = new MockMetaBenefitRiskAnalysis("br", study.getIndication(), 
				Collections.singletonList(ma), 
				fluox, 
				Collections.singletonList(parox));
		return br;
	}
	
    public static Study realBuildStudyZeroRate() {
        Endpoint hamd = buildEndpointHamd();
        Drug fluoxetine = buildDrugFluoxetine();
        Drug sertraline = buildDrugSertraline();
        Drug paroxetine = buildDrugParoxetine();
        Study study = new Study("fluoxRatingZeroStudy", buildIndicationDepression());
        study.setEndpoints(Collections.singletonList(hamd));
        
        // Sertraline data
        FixedDose dose = new FixedDose(75.0, SIUnit.MILLIGRAMS_A_DAY);
        Arm sertr = new Arm(sertraline, dose, 96);
        BasicRateMeasurement sHamd = (BasicRateMeasurement)hamd.buildMeasurement(sertr);
        sHamd.setRate(70);
        study.addArm(sertr);
        study.setMeasurement(hamd, sertr, sHamd);

        // Fluoxetine data
        dose = new FixedDose(30.0, SIUnit.MILLIGRAMS_A_DAY);
        Arm fluox = new Arm(fluoxetine, dose, 92);
        BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
        fHamd.setRate(0);
        study.addArm(fluox);
        study.setMeasurement(hamd, fluox, fHamd);
        
        // Paroxetine data
        dose = new FixedDose(0.0, SIUnit.MILLIGRAMS_A_DAY);
        Arm parox = new Arm(paroxetine, dose, 93);
        BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(parox);
        pHamd.setRate(64);
        study.addArm(parox);
        study.setMeasurement(hamd, parox, pHamd);
        
        return study;
    }
}