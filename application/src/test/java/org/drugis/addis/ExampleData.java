/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
import org.drugis.addis.entities.CategoricalVariableType;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.ScaleModifier;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.StudyOutcomeMeasure;
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
import org.drugis.addis.util.EntityUtil;

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
	private static Study s_studyMultipleArmsPerDrug;
	
	private static PopulationCharacteristic s_gender;
	private static PopulationCharacteristic s_age;
	private static Endpoint s_endpointMadrs;
	private static Study s_studyBurke;
	
	public static DoseUnit MILLIGRAMS_A_DAY = new DoseUnit(Domain.GRAM, ScaleModifier.MILLI, EntityUtil.createDuration("P1D"));
	public static DoseUnit KILOGRAMS_PER_HOUR = new DoseUnit(Domain.GRAM, ScaleModifier.KILO, EntityUtil.createDuration("PT1H"));

	public static void initDefaultData(Domain domain) {
		clearAll();
		// depression data
		domain.getIndications().add(buildIndicationDepression());
		domain.getEndpoints().add(buildEndpointHamd());
		domain.getEndpoints().add(buildEndpointCgi());
		domain.getAdverseEvents().add(buildAdverseEventConvulsion());
		//domain.addEndpoint(buildEndpointMadrs());
		domain.getDrugs().add(buildDrugFluoxetine());
		domain.getDrugs().add(buildDrugParoxetine());
		domain.getDrugs().add(buildDrugSertraline());
		//domain.addDrug(buildDrugCitalopram());
		//domain.addDrug(buildDrugEscitalopram());
		domain.getDrugs().add(buildPlacebo());
		domain.getStudies().add(buildStudyChouinard());
		domain.getStudies().add(buildStudyDeWilde());		
		domain.getStudies().add(buildStudyBennie());	
		//domain.addStudy(buildStudyBurke());
		domain.getStudies().add(buildStudyMultipleArmsperDrug());
			
		// heart failure data
		domain.getIndications().add(buildIndicationChronicHeartFailure());
		domain.getDrugs().add(buildDrugCandesartan());
		domain.getEndpoints().add(buildEndpointCVdeath());
		domain.getStudies().add(buildStudyMcMurray());
		
		// unused stuff
		domain.getPopulationCharacteristics().add(buildGenderVariable());
		domain.getPopulationCharacteristics().add(buildAgeVariable());
	}

	public static PopulationCharacteristic buildGenderVariable() {
		if (s_gender == null) {
			s_gender = new PopulationCharacteristic("Gender", new CategoricalVariableType(Arrays.asList((new String[]{"Male", "Female"}))));
		}
		return s_gender;
	}
	
	public static PopulationCharacteristic buildAgeVariable() {
		if (s_age == null) {
			s_age = new PopulationCharacteristic("Age", new ContinuousVariableType());
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
		s_studyMultipleArmsPerDrug = null;
	}

	public static Study buildStudyChouinard() {
		if (s_studyChouinard == null) {
			s_studyChouinard = realBuildStudyChouinard();
		}
		
		return s_studyChouinard;
	}
	
	public static Study buildStudyChouinardNoHamd() {
		Study s = realBuildStudyChouinard();
		List<Endpoint> endpoints = Study.extractVariables(s.getEndpoints());
		endpoints.remove(buildEndpointHamd());
		s.getEndpoints().clear();
		s.getEndpoints().addAll(Study.wrapVariables(endpoints));
		return s;
	}

	private static Study realBuildStudyChouinard() {
		Study study = new Study("Chouinard et al, 1999", buildIndicationDepression());
		study.getEndpoints().clear();
		study.getEndpoints().addAll(Study.wrapVariables(new ArrayList<Endpoint>(
						Arrays.asList(new Endpoint[]{buildEndpointHamd(), buildEndpointCgi()}))));
		study.getAdverseEvents().add(new StudyOutcomeMeasure<AdverseEvent>(buildAdverseEventConvulsion()));
		
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
		
		addDefaultEpochs(study);
		
		// Paroxetine data 1
		FixedDose dose = new FixedDose(25.5, ExampleData.MILLIGRAMS_A_DAY);
		Arm parox = study.createAndAddArm("Paroxetine-0", 102, buildDrugParoxetine(), dose);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(parox);
		pHamd.setRate(67);
		BasicContinuousMeasurement pCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(parox);
		pCgi.setMean(-1.69);
		pCgi.setStdDev(0.16);
		BasicRateMeasurement pConv = (BasicRateMeasurement) buildAdverseEventConvulsion().buildMeasurement(parox);
		pConv.setRate(10);
		pConv.setSampleSize(40);
		
		// Fluoxetine data
		dose = new FixedDose(27.5, ExampleData.MILLIGRAMS_A_DAY);
		Arm fluox = study.createAndAddArm("Fluoxetine-1", 101, buildDrugFluoxetine(), dose);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(fluox);
		fHamd.setRate(67);
		BasicContinuousMeasurement fCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(fluox);
		fCgi.setMean(-1.8);
		fCgi.setStdDev(0.16);
		BasicRateMeasurement fConv = (BasicRateMeasurement) buildAdverseEventConvulsion().buildMeasurement(parox);
		fConv.setRate(12);
		fConv.setSampleSize(40);
		
		addDefaultMeasurementMoments(study);

		// only set measurements once studyactivities are initialised
		study.setMeasurement(buildEndpointHamd(), parox, pHamd);
		study.setMeasurement(buildEndpointCgi(), parox, pCgi);
		study.setMeasurement(buildAdverseEventConvulsion(),parox, pConv);
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
		study.getEndpoints().clear();
		study.getEndpoints().addAll(Study.wrapVariables(Collections.singletonList(hamd)));
		study.getAdverseEvents().clear();
		study.getAdverseEvents().addAll(Study.wrapVariables(Collections.singletonList(buildAdverseEventConvulsion())));
		
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
		
		addDefaultEpochs(study);

		// Paroxetine data
		FixedDose dose = new FixedDose(25.5, ExampleData.MILLIGRAMS_A_DAY);
		Arm parox = study.createAndAddArm("Paroxetine-0", 37, buildDrugParoxetine(), dose);
		
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(parox);
		pHamd.setRate(23);
		BasicRateMeasurement pConv = (BasicRateMeasurement) buildAdverseEventConvulsion().buildMeasurement(parox);
		pConv.setRate(10);
		pConv.setSampleSize(40);

		// Fluoxetine data
		dose = new FixedDose(27.5, ExampleData.MILLIGRAMS_A_DAY);
		Arm fluox = study.createAndAddArm("Fluoxetine-1", 41, fluoxetine, dose);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(26);
		BasicRateMeasurement fConv = (BasicRateMeasurement) buildAdverseEventConvulsion().buildMeasurement(fluox);
		fConv.setRate(10);
		fConv.setSampleSize(34);
		
		addBaselineMeasurementMoment(study, Endpoint.class);
		addDefaultMeasurementMoments(study);
		
		study.setMeasurement(hamd, parox, pHamd);
		study.setMeasurement(buildAdverseEventConvulsion(),parox, pConv);		
		study.setMeasurement(hamd, fluox, fHamd);
		study.setMeasurement(buildAdverseEventConvulsion(), fluox, fConv);
		return study;
	}

	public static Study buildStudyMultipleArmsperDrug() {
		if (s_studyMultipleArmsPerDrug == null) {
			s_studyMultipleArmsPerDrug = realBuildMultipleArmsperDrugStudy();
		}
		
		return s_studyMultipleArmsPerDrug;
	}
	
	private static Study realBuildMultipleArmsperDrugStudy() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		Study study = new Study("MultipleArms, 1993", buildIndicationDepression());
		study.getEndpoints().clear();
		study.getEndpoints().addAll(Study.wrapVariables(Collections.singletonList(hamd)));
		
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
		
		addDefaultEpochs(study);
		
		// Paroxetine data 1
		FixedDose dose = new FixedDose(25.5, ExampleData.MILLIGRAMS_A_DAY);
		Arm parox0 = study.createAndAddArm("Paroxetine-0", 37, buildDrugParoxetine(), dose);
		BasicRateMeasurement pHamd0 = (BasicRateMeasurement)hamd.buildMeasurement(parox0);
		pHamd0.setRate(23);
		
		// Paroxetine data 2
		dose = new FixedDose(5.5, ExampleData.MILLIGRAMS_A_DAY);
		Arm parox1 = study.createAndAddArm("Paroxetine-1", 54, buildDrugParoxetine(), dose);
		BasicRateMeasurement pHamd1 = (BasicRateMeasurement)hamd.buildMeasurement(parox1);
		pHamd1.setRate(23);

		// Fluoxetine data
		dose = new FixedDose(27.5, ExampleData.MILLIGRAMS_A_DAY);
		Arm fluox = study.createAndAddArm("Fluoxetine-2", 41, fluoxetine, dose);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(26);

		// Initialise measurement moment data structure (only after arms are created)
		addDefaultMeasurementMoments(study);
		
		study.setMeasurement(hamd, parox0, pHamd0);
		study.setMeasurement(hamd, parox1, pHamd1);
		study.setMeasurement(hamd, fluox, fHamd);

		return study;
	}

	public static void addDefaultMeasurementMoments(Study study) {
		addDefaultMeasurementMoment(study, Endpoint.class);
		addDefaultMeasurementMoment(study, AdverseEvent.class);
		addDefaultMeasurementMoment(study, PopulationCharacteristic.class);
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
		study.getEndpoints().clear();
		study.getEndpoints().addAll(Study.wrapVariables(new ArrayList<Endpoint>(Arrays.asList(new Endpoint[]{buildEndpointCgi(), buildEndpointMadrs()}))));
		study.getAdverseEvents().add(new StudyOutcomeMeasure<AdverseEvent>(buildAdverseEventDiarrhea()));
		
		addDefaultEpochs(study);
		
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
		FixedDose dose = new FixedDose(40, ExampleData.MILLIGRAMS_A_DAY);
		Arm cita = study.createAndAddArm("Citalopram-0", 125, buildDrugCitalopram(), dose);
		BasicContinuousMeasurement cCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(cita);
		cCgi.setMean(-1.2);
		cCgi.setStdDev(0.1);
		BasicRateMeasurement cMadrs = (BasicRateMeasurement)buildEndpointMadrs().buildMeasurement(cita);
		cMadrs.setRate(57);
		
		// Escitalopram high dose data
		dose = new FixedDose(20, ExampleData.MILLIGRAMS_A_DAY);
		Arm esciHigh = study.createAndAddArm("Escitalopram-1", 125, buildDrugEscitalopram(), dose);
		BasicContinuousMeasurement ehCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(esciHigh);
		ehCgi.setMean(-1.4);
		ehCgi.setStdDev(0.1);
		BasicRateMeasurement ehMadrs = (BasicRateMeasurement)buildEndpointMadrs().buildMeasurement(esciHigh);
		ehMadrs.setRate(64);

		// Escitalopram low dose data
		dose = new FixedDose(10, ExampleData.MILLIGRAMS_A_DAY);
		Arm esciLow = study.createAndAddArm("Escitalopram-2", 119, buildDrugEscitalopram(), dose);
		BasicContinuousMeasurement elCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(esciLow);
		elCgi.setMean(-1.3);
		elCgi.setStdDev(0.1);
		BasicRateMeasurement elMadrs = (BasicRateMeasurement)buildEndpointMadrs().buildMeasurement(esciLow);
		elMadrs.setRate(59);
		
		// Placebo data
		dose = new FixedDose(0, ExampleData.MILLIGRAMS_A_DAY);
		Arm placebo = study.createAndAddArm("Placebo-3", 122, buildPlacebo(), dose);
		BasicContinuousMeasurement plCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(placebo);
		plCgi.setMean(-0.8);
		plCgi.setStdDev(0.1);
		BasicRateMeasurement plMadrs = (BasicRateMeasurement)buildEndpointMadrs().buildMeasurement(placebo);
		plMadrs.setRate(33);
		
		addDefaultMeasurementMoments(study);
		
		study.setMeasurement(buildEndpointCgi(), cita, cCgi);
		study.setMeasurement(buildEndpointMadrs(), cita, cMadrs);
		study.setMeasurement(buildEndpointCgi(), esciHigh, ehCgi);
		study.setMeasurement(buildEndpointMadrs(), esciHigh, ehMadrs);
		study.setMeasurement(buildEndpointCgi(), esciLow, elCgi);
		study.setMeasurement(buildEndpointMadrs(), esciLow, elMadrs);
		study.setMeasurement(buildEndpointCgi(), placebo, plCgi);
		study.setMeasurement(buildEndpointMadrs(), placebo, plMadrs);
		return study;
}
	
	
	private static Study realBuildStudyBennie() {
		Study study = new Study("Bennie et al, 1995", buildIndicationDepression());
		study.getEndpoints().clear();
		study.getEndpoints().addAll(Study.wrapVariables(new ArrayList<Endpoint>(
						Arrays.asList(new Endpoint[]{buildEndpointHamd(), buildEndpointCgi()}))));
		study.getAdverseEvents().add(new StudyOutcomeMeasure<AdverseEvent>(buildAdverseEventConvulsion()));
		
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
		
		addDefaultEpochs(study);
		
		FixedDose fluoxDose = new FixedDose(20, ExampleData.MILLIGRAMS_A_DAY);
		FixedDose sertrDose = new FixedDose(50, ExampleData.MILLIGRAMS_A_DAY);
		Arm fluox = study.createAndAddArm("Fluoxetine-0", 144, buildDrugFluoxetine(), fluoxDose);
		Arm sertr = study.createAndAddArm("Sertraline-1", 142, buildDrugSertraline(), sertrDose);
		
		// note: must be after arms added, because of finding treatment epoch
		addDefaultMeasurementMoments(study);

		// Fluoxetine data
		BasicContinuousMeasurement fCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(fluox);
		fCgi.setMean(0.67);
		fCgi.setStdDev(0.5);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(fluox);
		fHamd.setRate(63);
		study.setMeasurement(buildEndpointCgi(), fluox, fCgi);
		study.setMeasurement(buildEndpointHamd(), fluox, fHamd);

		// Sertraline data
		BasicContinuousMeasurement sCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(sertr);
		sCgi.setMean(0.69);
		sCgi.setStdDev(0.5);
		BasicRateMeasurement sHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(sertr);
		sHamd.setRate(73);
		study.setMeasurement(buildEndpointCgi(), sertr, sCgi);
		study.setMeasurement(buildEndpointHamd(), sertr, sHamd);
		return study;
	}

	private static <T extends Variable> void addDefaultMeasurementMoment(Study study, Class<T> type) {
		for (StudyOutcomeMeasure<T> om : study.getStudyOutcomeMeasures(type)) {
			om.getWhenTaken().add(study.defaultMeasurementMoment());
		}
	}
	
	private static <T extends Variable> void addBaselineMeasurementMoment(Study study, Class<T> type) {
		for (StudyOutcomeMeasure<T> om : study.getStudyOutcomeMeasures(type)) {
			om.getWhenTaken().add(study.baselineMeasurementMoment());
		}
	}

	public static void addDefaultEpochs(Study study) {
		study.getEpochs().add(new Epoch("Randomization", EntityUtil.createDuration("P0D")));
		study.getEpochs().add(new Epoch("Main phase", EntityUtil.createDuration("P0D")));
	}

	public static Study buildStudyAdditionalThreeArm() {
		if (s_study3Arm == null) {
			s_study3Arm = realBuildStudyThreeArm(); 
		}
		
		return s_study3Arm;
	}

	private static Study realBuildStudyThreeArm() {
		Study study = new Study("SciFictional et al, 2359", buildIndicationDepression());
		study.getEndpoints().clear();
		study.getEndpoints().addAll(Study.wrapVariables(new ArrayList<Endpoint>(
						Arrays.asList(new Endpoint[]{buildEndpointHamd(), buildEndpointCgi()}))));
		
		study.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, 
				"This is a fictional study that I just created because I need a three-arm study.");
		study.setCharacteristic(BasicStudyCharacteristic.STATUS,
				BasicStudyCharacteristic.Status.COMPLETED);

		addDefaultEpochs(study);
		
		// Paroxetine data
		FixedDose dose = new FixedDose(25.5, ExampleData.MILLIGRAMS_A_DAY);
		Arm parox = study.createAndAddArm("Paroxetine-0", 37, buildDrugParoxetine(), dose);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(parox);
		pHamd.setRate(23);
		BasicContinuousMeasurement pCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(parox);
		pCgi.setMean(-1.69);
		pCgi.setStdDev(0.16);
		
		// Fluoxetine data
		dose = new FixedDose(20, ExampleData.MILLIGRAMS_A_DAY);
		Arm fluox = study.createAndAddArm("Fluoxetine-1", 144, buildDrugFluoxetine(), dose);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(fluox);
		fHamd.setRate(63);
		BasicContinuousMeasurement fCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(fluox);
		fCgi.setMean(-1.8);
		fCgi.setStdDev(0.16);

		// Sertraline data
		dose = new FixedDose(50, ExampleData.MILLIGRAMS_A_DAY);
		Arm sertr = study.createAndAddArm("Sertraline-2", 142, buildDrugSertraline(), dose);
		BasicRateMeasurement sHamd = (BasicRateMeasurement)buildEndpointHamd().buildMeasurement(sertr);
		sHamd.setRate(73);
		BasicContinuousMeasurement sCgi = (BasicContinuousMeasurement)buildEndpointCgi().buildMeasurement(sertr);
		sCgi.setMean(-0.84);
		sCgi.setStdDev(0.24);

		addDefaultMeasurementMoments(study);
		
		study.setMeasurement(buildEndpointHamd(), parox, pHamd);
		study.setMeasurement(buildEndpointCgi(), parox, pCgi);
		study.setMeasurement(buildEndpointHamd(), fluox, fHamd);
		study.setMeasurement(buildEndpointCgi(), fluox, fCgi);
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
		study.getEndpoints().clear();
		study.getEndpoints().addAll(Study.wrapVariables(Collections.singletonList(buildEndpointCVdeath())));
		
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
		
		addDefaultEpochs(study);
		
		// Candesartan data
		FixedDose cDose = new FixedDose(32, ExampleData.MILLIGRAMS_A_DAY);
		Arm cand = study.createAndAddArm("Candesartan-0", 1273, buildDrugCandesartan(), cDose);
		BasicRateMeasurement cDeath = new BasicRateMeasurement(302, cand.getSize());
		
		// Placebo data
		FixedDose pDose = new FixedDose(32, ExampleData.MILLIGRAMS_A_DAY);
		Arm placebo = study.createAndAddArm("Placebo-1", 1271, buildPlacebo(), pDose);
		BasicRateMeasurement pDeath = new BasicRateMeasurement(347, placebo.getSize());

		addDefaultMeasurementMoments(study);
		
		study.setMeasurement(buildEndpointCVdeath(), cand, cDeath);
		study.setMeasurement(buildEndpointCVdeath(), placebo, pDeath);
		
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
		study.getEndpoints().clear();
		study.getEndpoints().add(Study.wrapVariable(hamd));
		List<AdverseEvent> ade = new ArrayList<AdverseEvent>();
		ade.add(buildAdverseEventConvulsion());
		ade.add(buildAdverseEventSexualDysfunction());
		study.getAdverseEvents().clear();
		study.getAdverseEvents().addAll(Study.wrapVariables(ade));
		study.getPopulationChars().clear();
		study.getPopulationChars().add(Study.wrapVariable(buildGenderVariable()));

		addDefaultEpochs(study);
		
		// Study characteristics
		study.setCharacteristic(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.DOUBLE_BLIND);
		study.setCharacteristic(BasicStudyCharacteristic.CENTERS, 1);
		study.setCharacteristic(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.RANDOMIZED);
		study.setCharacteristic(BasicStudyCharacteristic.INCLUSION, "");
		study.setCharacteristic(BasicStudyCharacteristic.EXCLUSION, "");
		study.setCharacteristic(BasicStudyCharacteristic.OBJECTIVE, "");
		study.setCharacteristic(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.COMPLETED);
		
		// Sertraline data
		FixedDose dose = new FixedDose(75.0, ExampleData.MILLIGRAMS_A_DAY);
		Arm sertr = study.createAndAddArm("Sertraline-0", 96, sertraline, dose);
		BasicRateMeasurement sHamd = (BasicRateMeasurement)hamd.buildMeasurement(sertr);
		sHamd.setRate(70);

		// Fluoxetine data
		dose = new FixedDose(30.0, ExampleData.MILLIGRAMS_A_DAY);
		Arm fluox = study.createAndAddArm("Fluoxetine-1", 92, fluoxetine, dose);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(57);
		
		// Paroxetine data
		dose = new FixedDose(0.0, ExampleData.MILLIGRAMS_A_DAY);
		Arm parox = study.createAndAddArm("Paroxetine-2", 93, paroxetine, dose);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(parox);
		pHamd.setRate(64);

		addDefaultMeasurementMoments(study);
		
		study.setMeasurement(hamd, parox, pHamd);
		study.setMeasurement(hamd, fluox, fHamd);
		study.setMeasurement(hamd, sertr, sHamd);
		
		return study;
	}
	
	public static AdverseEvent buildAdverseEventSexualDysfunction() {
		if (s_sexdysf == null) {
			s_sexdysf = new AdverseEvent("Sexual Dysfunction", AdverseEvent.convertVarType(Variable.Type.RATE));
			s_sexdysf.setDescription("Rate");
		}
		return s_sexdysf;
	}

	public static AdverseEvent buildAdverseEventDiarrhea() {
		if (s_diarrhea == null) {
			s_diarrhea = new AdverseEvent("Diarrhea", AdverseEvent.convertVarType(Variable.Type.RATE));
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
			Endpoint e = new Endpoint("HAM-D Responders", Endpoint.convertVarType(Variable.Type.RATE));
			e.setDescription("Responders with a 50% increase in HAM-D score");
			s_endpointHamd = e;
		}
		return s_endpointHamd;
	}

	public static Endpoint buildEndpointCgi() {
		if (s_endpointCgi == null) { 
			Endpoint cgi = new Endpoint("CGI Severity Change", new ContinuousVariableType("Deviation from the baseline of CGI Severity of Illness score"));
			cgi.setDescription("Change from baseline CGI Severity of Illness score");
			s_endpointCgi = cgi;
		}
		return s_endpointCgi;
	}

	public static Endpoint buildEndpointMadrs() {
		if (s_endpointMadrs == null) { 
			Endpoint madrs = new Endpoint("MADRS Responders", new RateVariableType());
			madrs.setDescription("Responders with a 50% increase in MADRS score");
			s_endpointMadrs = madrs;
		}
		return s_endpointMadrs;
	}

	public static Endpoint buildEndpointCVdeath() {
		if (s_endpointCVdeath == null) {
			Endpoint e = new Endpoint("Cardiovascular Death Incidence", Endpoint.convertVarType(Variable.Type.RATE), Direction.LOWER_IS_BETTER);
			e.setDescription("Rate of mortality due to cardiovascular causes");
			s_endpointCVdeath = e;
		}
		return s_endpointCVdeath;
	}


	public static NetworkMetaAnalysis buildNetworkMetaAnalysisHamD() {
		List<Study> studies = Arrays.asList(new Study[] {
				buildStudyBennie(), buildStudyChouinard(), buildStudyDeWilde(), buildStudyFava2002()});
		List<DrugSet> drugs = Arrays.asList(new DrugSet[] {
				new DrugSet(buildDrugFluoxetine()), 
				new DrugSet(buildDrugParoxetine()), 
				new DrugSet(buildDrugSertraline())});
		
		NetworkMetaAnalysis analysis = new NetworkMetaAnalysis("Test Network", 
				buildIndicationDepression(), buildEndpointHamd(),
				studies, drugs, buildMap(studies, drugs));
		
		return analysis;
	}
	
	public static NetworkMetaAnalysis buildNetworkMetaAnalysisConvulsion() {
		List<Study> studies = Arrays.asList(new Study[] {
				buildStudyBennie(), buildStudyChouinard()});
		List<DrugSet> drugs = Arrays.asList(new DrugSet[] {
				new DrugSet(buildDrugFluoxetine()),
				new DrugSet(buildDrugParoxetine()), 
				new DrugSet(buildDrugSertraline())});
		
		NetworkMetaAnalysis analysis = new NetworkMetaAnalysis("Test Network2", 
				buildIndicationDepression(), buildAdverseEventConvulsion(),
				studies, drugs, buildMap(studies, drugs));
		
		return analysis;
	}
	
	public static NetworkMetaAnalysis buildNetworkMetaAnalysisCgi() {
		List<Study> studies = Arrays.asList(new Study[] {
				buildStudyBennie(), buildStudyChouinard()});
		List<DrugSet> drugs = Arrays.asList(new DrugSet[] {
				new DrugSet(buildDrugFluoxetine()),
				new DrugSet(buildDrugParoxetine()), 
				new DrugSet(buildDrugSertraline())});
		
		NetworkMetaAnalysis analysis = new NetworkMetaAnalysis("CGI network", 
				buildIndicationDepression(), buildEndpointCgi(),
				studies, drugs, buildMap(studies, drugs));
		
		return analysis;
	}
	
	
	public static Map<Study, Map<DrugSet, Arm>> buildMap(List<Study> studies,
			List<DrugSet> drugs) {
		Map<Study, Map<DrugSet, Arm>> map = new HashMap<Study, Map<DrugSet, Arm>>();
		for (Study s : studies) {
			Map<DrugSet, Arm> drugMap = new HashMap<DrugSet, Arm>();
			for (DrugSet d : drugs) {
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
			s_convulsion = new AdverseEvent("Convulsion", AdverseEvent.convertVarType(Variable.Type.RATE));
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
		List<DrugSet> fluoxList = Collections.singletonList(new DrugSet(buildDrugFluoxetine()));
		
		return new MockMetaBenefitRiskAnalysis("testBenefitRiskAnalysis",
										indication, metaAnalysisList, new DrugSet(parox), fluoxList);										
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
		MetaAnalysis ma = new RandomEffectsMetaAnalysis("ma", om, Collections.singletonList(study), new DrugSet(fluox), new DrugSet(parox));
		MetaBenefitRiskAnalysis br = new MockMetaBenefitRiskAnalysis("br", study.getIndication(), 
				Collections.singletonList(ma), 
				new DrugSet(fluox), 
				Collections.singletonList(new DrugSet(parox)));
		return br;
	}
	
    public static Study realBuildStudyZeroRate() {
        Endpoint hamd = buildEndpointHamd();
        Drug fluoxetine = buildDrugFluoxetine();
        Drug sertraline = buildDrugSertraline();
        Drug paroxetine = buildDrugParoxetine();
        Study study = new Study("fluoxRatingZeroStudy", buildIndicationDepression());
        study.getEndpoints().clear();
		study.getEndpoints().addAll(Study.wrapVariables(Collections.singletonList(hamd)));
        
        // Sertraline data
        FixedDose dose = new FixedDose(75.0, ExampleData.MILLIGRAMS_A_DAY);
        Arm sertr = study.createAndAddArm("Sertraline-0", 96, sertraline, dose);
        BasicRateMeasurement sHamd = (BasicRateMeasurement)hamd.buildMeasurement(sertr);
        sHamd.setRate(70);
        study.setMeasurement(hamd, sertr, sHamd);

        // Fluoxetine data
        dose = new FixedDose(30.0, ExampleData.MILLIGRAMS_A_DAY);
        Arm fluox = study.createAndAddArm("Fluoxetine-1", 92, fluoxetine, dose);
        BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
        fHamd.setRate(0);
        study.setMeasurement(hamd, fluox, fHamd);
        
        // Paroxetine data
        dose = new FixedDose(0.0, ExampleData.MILLIGRAMS_A_DAY);
        Arm parox = study.createAndAddArm("Paroxetine-2", 93, paroxetine, dose);
        BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(parox);
        pHamd.setRate(64);
        study.setMeasurement(hamd, parox, pHamd);
        
        return study;
    }
}