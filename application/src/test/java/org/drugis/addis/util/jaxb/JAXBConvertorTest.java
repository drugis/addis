/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.util.jaxb;

import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.drugis.addis.util.jaxb.JAXBConvertor.nameReference;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.Diff;
import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Activity;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.BasicStudyCharacteristic.Allocation;
import org.drugis.addis.entities.BasicStudyCharacteristic.Blinding;
import org.drugis.addis.entities.BasicStudyCharacteristic.Status;
import org.drugis.addis.entities.CategoricalVariableType;
import org.drugis.addis.entities.CharacteristicsMap;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.MeasurementKey;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.ObjectWithNotes;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.PredefinedActivity;
import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.StudyActivity.UsedBy;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.StudyOutcomeMeasure;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.addis.entities.WhenTaken;
import org.drugis.addis.entities.WhenTaken.RelativeTo;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.entities.analysis.DecisionContext;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.data.ActivityUsedBy;
import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.entities.data.AnalysisArms;
import org.drugis.addis.entities.data.ArmReference;
import org.drugis.addis.entities.data.ArmReferences;
import org.drugis.addis.entities.data.Arms;
import org.drugis.addis.entities.data.BaselineArmReference;
import org.drugis.addis.entities.data.BenefitRiskAnalyses;
import org.drugis.addis.entities.data.CategoricalMeasurement;
import org.drugis.addis.entities.data.CategoricalVariable;
import org.drugis.addis.entities.data.CategoryMeasurement;
import org.drugis.addis.entities.data.Characteristics;
import org.drugis.addis.entities.data.ContinuousMeasurement;
import org.drugis.addis.entities.data.ContinuousVariable;
import org.drugis.addis.entities.data.DateWithNotes;
import org.drugis.addis.entities.data.Epochs;
import org.drugis.addis.entities.data.Measurements;
import org.drugis.addis.entities.data.MetaAnalyses;
import org.drugis.addis.entities.data.MetaAnalysisAlternative;
import org.drugis.addis.entities.data.MetaAnalysisReferences;
import org.drugis.addis.entities.data.MetaBenefitRiskAnalysis.Alternatives;
import org.drugis.addis.entities.data.MetaBenefitRiskAnalysis.Baseline;
import org.drugis.addis.entities.data.NameReferenceWithNotes;
import org.drugis.addis.entities.data.NetworkMetaAnalysis;
import org.drugis.addis.entities.data.Notes;
import org.drugis.addis.entities.data.OutcomeMeasure;
import org.drugis.addis.entities.data.OutcomeMeasuresReferences;
import org.drugis.addis.entities.data.PairwiseMetaAnalysis;
import org.drugis.addis.entities.data.RateMeasurement;
import org.drugis.addis.entities.data.RateVariable;
import org.drugis.addis.entities.data.References;
import org.drugis.addis.entities.data.RelativeTime;
import org.drugis.addis.entities.data.StudyActivities;
import org.drugis.addis.entities.data.StudyOutcomeMeasures;
import org.drugis.addis.entities.data.Treatment;
import org.drugis.addis.entities.data.TreatmentCategoryRef;
import org.drugis.addis.entities.data.TrivialCategory;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.imports.PubMedDataBankRetriever;
import org.drugis.addis.util.EntityUtil;
import org.drugis.addis.util.jaxb.JAXBConvertor.ConversionException;
import org.drugis.addis.util.jaxb.JAXBHandler.XmlFormatType;
import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class JAXBConvertorTest {
	public static final String TEST_DATA_PATH = "../../";
	private static final String TEST_DATA_A_0 = TEST_DATA_PATH + "testDataA-0.xml";
	private static final String TEST_DATA_3 = TEST_DATA_PATH + "testData-3.addis";
	private static final String TEST_DATA_A_1 = TEST_DATA_PATH + "testDataA-1.addis";
	private static final Duration ZERO_DAYS = EntityUtil.createDuration("P0D");

	private JAXBContext d_jaxb;
	private static Unmarshaller d_unmarshaller;
	private static Marshaller d_marshaller;

	@Before
	public void setup() throws JAXBException {
		d_jaxb = JAXBContext.newInstance("org.drugis.addis.entities.data");
		d_unmarshaller = d_jaxb.createUnmarshaller();
		d_marshaller = d_jaxb.createMarshaller();
	}

	@Test
	public void testConvertContinuousEndpoint() throws ConversionException {
		String name = "Onset of erection";
		String desc = "Time to onset of erection of >= 60 % rigidity";
		String unit = "Minutes";
		Direction dir = Direction.LOWER_IS_BETTER;

		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		ContinuousVariable value = new ContinuousVariable();
		m.setDirection(dir);
		value.setUnitOfMeasurement(unit);
		m.setContinuous(value);

		Endpoint e = new Endpoint(name,
				Endpoint.convertVarType(Type.CONTINUOUS), dir);
		e.setDescription(desc);
		((ContinuousVariableType) e.getVariableType())
				.setUnitOfMeasurement(unit);

		assertEntityEquals(e, JAXBConvertor.convertEndpoint(m));
		assertEquals(m, JAXBConvertor.convertEndpoint(e));
	}

	@Test
	public void testConvertRateEndpoint() throws ConversionException {
		String name = "Efficacy";
		String desc = "Erection of >= 60% rigidity within 1 hr of medication";
		Direction dir = Direction.HIGHER_IS_BETTER;

		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		RateVariable value = new RateVariable();
		m.setDirection(dir);
		m.setRate(value);

		Endpoint e = new Endpoint(name, Endpoint.convertVarType(Type.RATE), dir);
		e.setDescription(desc);

		assertEntityEquals(e, JAXBConvertor.convertEndpoint(m));

		m.setDirection(Direction.LOWER_IS_BETTER);
		e.setDirection(org.drugis.addis.entities.OutcomeMeasure.Direction.LOWER_IS_BETTER);

		assertEntityEquals(e, JAXBConvertor.convertEndpoint(m));
		assertEquals(m, JAXBConvertor.convertEndpoint(e));
	}

	@Test(expected = ConversionException.class)
	public void testConvertCategoricalEndpointThrows()
			throws ConversionException {
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName("Gender");
		m.setDescription("Which gender you turn out to be after taking medication");
		CategoricalVariable value = new CategoricalVariable();
		m.setCategorical(value);

		JAXBConvertor.convertEndpoint(m);
	}

	@Test
	public void testConvertContinuousAdverseEvent() throws ConversionException {
		String name = "Onset of erection";
		String desc = "Time to onset of erection of >= 60 % rigidity";
		String unit = "Minutes";

		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		ContinuousVariable value = new ContinuousVariable();
		m.setDirection(Direction.LOWER_IS_BETTER);
		value.setUnitOfMeasurement(unit);
		m.setContinuous(value);

		org.drugis.addis.entities.OutcomeMeasure e = new AdverseEvent(name,
				AdverseEvent.convertVarType(Type.CONTINUOUS));
		e.setDescription(desc);
		((ContinuousVariableType) e.getVariableType())
				.setUnitOfMeasurement(unit);

		assertEntityEquals(e, JAXBConvertor.convertAdverseEvent(m));
		assertEquals(m, JAXBConvertor.convertAdverseEvent(e));
	}

	@Test
	public void testConvertRateAdverseEvent() throws ConversionException {
		String name = "Seizure";
		String desc = "Its bad hmmkay";

		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		RateVariable value = new RateVariable();
		m.setDirection(Direction.LOWER_IS_BETTER);
		m.setRate(value);

		AdverseEvent e = new AdverseEvent(name,
				AdverseEvent.convertVarType(Type.RATE));
		e.setDescription(desc);

		assertEntityEquals(e, JAXBConvertor.convertAdverseEvent(m));

		m.setDirection(Direction.HIGHER_IS_BETTER);
		e.setDirection(org.drugis.addis.entities.OutcomeMeasure.Direction.HIGHER_IS_BETTER);

		assertEntityEquals(e, JAXBConvertor.convertAdverseEvent(m));
		assertEquals(m, JAXBConvertor.convertAdverseEvent(e));
	}

	@Test(expected = ConversionException.class)
	public void testConvertCategoricalAdverseEventThrows()
			throws ConversionException {
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName("Efficacy");
		m.setDescription("Erection of >= 60% rigidity within 1 hr of medication");
		CategoricalVariable value = new CategoricalVariable();
		m.setCategorical(value);

		JAXBConvertor.convertAdverseEvent(m);
	}

	@Test
	public void testConvertContinuousPopChar() throws ConversionException {
		String name = "Age";
		String desc = "Age (years since birth)";
		String unit = "Years";

		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		ContinuousVariable value = new ContinuousVariable();
		value.setUnitOfMeasurement(unit);
		m.setContinuous(value);

		PopulationCharacteristic p = new PopulationCharacteristic(name,
				new ContinuousVariableType());
		((ContinuousVariableType) p.getVariableType())
				.setUnitOfMeasurement(unit);
		p.setDescription(desc);

		assertEntityEquals(p, JAXBConvertor.convertPopulationCharacteristic(m));
		assertEquals(m, JAXBConvertor.convertPopulationCharacteristic(p));
	}

	@Test
	public void testConvertRatePopChar() throws ConversionException {
		String name = "Seizure";
		String description = "Its bad hmmkay";
		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(description);
		m.setRate(new RateVariable());

		PopulationCharacteristic p = new PopulationCharacteristic(name,
				new RateVariableType());
		p.setDescription(description);

		assertEntityEquals(p, JAXBConvertor.convertPopulationCharacteristic(m));
		assertEquals(m, JAXBConvertor.convertPopulationCharacteristic(p));
	}

	@Test
	public void testConvertCategoricalPopChar() throws ConversionException {
		String name = "Smoking habits";
		String desc = "Classification of smoking habits";
		String[] categories = new String[] { "Non-smoker", "Smoker",
				"Ex-smoker" };

		OutcomeMeasure m = new OutcomeMeasure();
		m.setName(name);
		m.setDescription(desc);
		CategoricalVariable var = new CategoricalVariable();
		for (String s : categories) {
			var.getCategory().add(s);
		}
		m.setCategorical(var);

		PopulationCharacteristic catChar = new PopulationCharacteristic(name,
				new CategoricalVariableType(Arrays.asList(categories)));
		catChar.setDescription(desc);

		assertEntityEquals(catChar,
				JAXBConvertor.convertPopulationCharacteristic(m));
		assertEquals(m, JAXBConvertor.convertPopulationCharacteristic(catChar));
	}

	@Test
	public void testConvertIndication() {
		String name = "Erectile Dysfunction";
		long code = 12;

		org.drugis.addis.entities.data.Indication i1 = new org.drugis.addis.entities.data.Indication();
		i1.setCode(code);
		i1.setName(name);

		Indication i2 = new Indication(code, name);

		assertEntityEquals(i2, JAXBConvertor.convertIndication(i1));
		assertEquals(i1, JAXBConvertor.convertIndication(i2));
	}

	@Test
	public void testConvertDrug() {
		String name = "Sildenafil";
		String code = "G04BE03";

		org.drugis.addis.entities.data.Drug d1 = new org.drugis.addis.entities.data.Drug();
		d1.setAtcCode(code);
		d1.setName(name);

		Drug d2 = new Drug(name, code);

		assertEntityEquals(d2, JAXBConvertor.convertDrug(d1));
		assertEquals(d1, JAXBConvertor.convertDrug(d2));
	}

	@Test
	public void testConvertArm() throws ConversionException {
		int size = 99;
		String name = "Sildenafil";

		org.drugis.addis.entities.data.Arm arm1 = new org.drugis.addis.entities.data.Arm();
		arm1.setName(name + "-12");
		arm1.setSize(size);
		Notes armNotes = new Notes();
		Note note = new Note(Source.CLINICALTRIALS,
				"This is an arm note content");
		armNotes.getNote().add(JAXBConvertor.convertNote(note));
		arm1.setNotes(armNotes);

		Arm arm2 = new Arm(name + "-12", size);
		arm2.getNotes().add(note);

		assertEntityEquals(arm2, JAXBConvertor.convertArm(arm1));
		assertEquals(arm1, JAXBConvertor.convertArm(arm2));
	}

	@Test
	public void testConvertTreatmentActivity() throws ConversionException {
		String name = "Sildenafil";
		String code = "G04BE03";
		double quantity = 12.5;
		double maxQuantity = 34.5;

		Domain domain = new DomainImpl();
		Drug drug = new Drug(name, code);
		domain.getDrugs().add(drug);

		// fixdose part
		org.drugis.addis.entities.data.FixedDose fixDose = new org.drugis.addis.entities.data.FixedDose();
		fixDose.setQuantity(quantity);
		fixDose.setDoseUnit(JAXBConvertor
				.convertDoseUnit(DoseUnit.MILLIGRAMS_A_DAY));

		org.drugis.addis.entities.data.DrugTreatment dt = new org.drugis.addis.entities.data.DrugTreatment();
		dt.setDrug(nameReference(name));
		dt.setFixedDose(fixDose);
		TreatmentActivity ta = buildFixedDoseTreatmentActivity(drug, quantity);

		assertTrue(EntityUtil.deepEqual(ta, JAXBConvertor
				.convertTreatmentActivity(wrapTreatment(dt), domain)));
		assertEquals(wrapTreatment(dt), JAXBConvertor.convertActivity(ta)
				.getTreatment());

		// flexdose part
		org.drugis.addis.entities.data.FlexibleDose flexDose = new org.drugis.addis.entities.data.FlexibleDose();
		flexDose.setMinDose(quantity);
		flexDose.setMaxDose(maxQuantity);
		flexDose.setDoseUnit(JAXBConvertor
				.convertDoseUnit(DoseUnit.MILLIGRAMS_A_DAY));

		org.drugis.addis.entities.data.DrugTreatment dt2 = new org.drugis.addis.entities.data.DrugTreatment();
		dt2.setDrug(nameReference(name));
		dt2.setFlexibleDose(flexDose);
		TreatmentActivity ta2 = buildFlexibleDoseTreatmentActivity(drug,
				quantity, maxQuantity);
		assertTrue(EntityUtil.deepEqual(ta2, JAXBConvertor
				.convertTreatmentActivity(wrapTreatment(dt2), domain)));
		assertEquals(wrapTreatment(dt2), JAXBConvertor.convertActivity(ta2)
				.getTreatment());

	}

	private static Treatment wrapTreatment(
			org.drugis.addis.entities.data.DrugTreatment dt2) {
		Treatment t2 = new org.drugis.addis.entities.data.Treatment();
		t2.getDrugTreatment().add(dt2);
		return t2;
	}

	@Test
	public void testConvertEpoch() throws DatatypeConfigurationException {
		String name = "Randomization";
		Duration duration = DatatypeFactory.newInstance().newDuration("P42D");

		Epoch e = new Epoch(name, duration);
		org.drugis.addis.entities.data.Epoch de = new org.drugis.addis.entities.data.Epoch();
		de.setName(name);
		de.setDuration(duration);
		de.setNotes(new Notes());

		assertEquals(de, JAXBConvertor.convertEpoch(e));
		assertTrue(EntityUtil.deepEqual(e, JAXBConvertor.convertEpoch(de)));
	}

	@Test
	public void testConvertStudyActivity() throws ConversionException,
			DatatypeConfigurationException {
		String drugName1 = "Sildenafil";
		String drugName2 = "Paroxeflox";
		String code1 = "G04BE03";
		String code2 = "G04BE04";
		double quantity = 12.5;

		Domain domain = new DomainImpl();
		Drug drug1 = new Drug(drugName1, code1);
		Drug drug2 = new Drug(drugName2, code2);
		domain.getDrugs().add(drug1);
		domain.getDrugs().add(drug2);

		// test with predefined activity
		String activityName = "Randomization";
		PredefinedActivity activity = PredefinedActivity.RANDOMIZATION;
		StudyActivity sa = new StudyActivity(activityName, activity);

		org.drugis.addis.entities.data.StudyActivity saData = new org.drugis.addis.entities.data.StudyActivity();
		org.drugis.addis.entities.data.Activity activData = new org.drugis.addis.entities.data.Activity();
		activData.setPredefined(activity);
		saData.setName(activityName);
		saData.setActivity(activData);
		saData.setNotes(new Notes());

		assertTrue(EntityUtil
				.deepEqual(sa, JAXBConvertor.convertStudyActivity(saData,
						new Study(), domain)));
		assertEquals(saData, JAXBConvertor.convertStudyActivity(sa));

		// test with treatmentactivity
		org.drugis.addis.entities.data.FixedDose fixDose = new org.drugis.addis.entities.data.FixedDose();
		fixDose.setQuantity(quantity);
		fixDose.setDoseUnit(JAXBConvertor
				.convertDoseUnit(DoseUnit.MILLIGRAMS_A_DAY));
		org.drugis.addis.entities.data.DrugTreatment t = new org.drugis.addis.entities.data.DrugTreatment();
		t.setDrug(nameReference(drugName1));
		t.setFixedDose(fixDose);
		TreatmentActivity ta1 = buildFixedDoseTreatmentActivity(drug1, quantity);

		sa = new StudyActivity(activityName, ta1);
		saData.getActivity().setPredefined(null);
		saData.getActivity().setTreatment(wrapTreatment(t));

		assertTrue(EntityUtil
				.deepEqual(sa, JAXBConvertor.convertStudyActivity(saData,
						new Study(), domain)));
		assertEquals(saData, JAXBConvertor.convertStudyActivity(sa));

		// test with Combination Treatment
		org.drugis.addis.entities.data.DrugTreatment t2 = new org.drugis.addis.entities.data.DrugTreatment();
		t2.setDrug(nameReference(drugName2));
		t2.setFixedDose(fixDose);
		org.drugis.addis.entities.data.Treatment ctData = new org.drugis.addis.entities.data.Treatment();
		ctData.getDrugTreatment().add(t);
		ctData.getDrugTreatment().add(t2);

		TreatmentActivity ct = new TreatmentActivity();
		ct.getTreatments().add(JAXBConvertor.convertDrugTreatment(t, domain));
		ct.getTreatments().add(JAXBConvertor.convertDrugTreatment(t2, domain));
		sa = new StudyActivity(activityName, ct);
		saData.getActivity().setTreatment(ctData);

		assertTrue(EntityUtil
				.deepEqual(sa, JAXBConvertor.convertStudyActivity(saData,
						new Study(), domain)));
		assertEquals(saData, JAXBConvertor.convertStudyActivity(sa));

		// test UsedBys
		String armName1 = "armName1";
		String armName2 = "armName2";
		Arm arm1 = new Arm(armName1, 100);
		Arm arm2 = new Arm(armName2, 200);
		String epochName1 = "epoch 1";
		String epochName2 = "epoch 2";
		Epoch epoch1 = new Epoch(epochName1, DatatypeFactory.newInstance()
				.newDuration("P24D"));
		Epoch epoch2 = new Epoch(epochName2, null);
		sa.setUsedBy(Collections.singleton(new UsedBy(arm1, epoch1)));

		// dummy study containing arms & epochs
		Study s = new Study("studyname",
				ExampleData.buildIndicationChronicHeartFailure());
		s.getEpochs().add(epoch1);
		s.getEpochs().add(epoch2);
		s.getArms().add(arm1);
		s.getArms().add(arm2);

		ActivityUsedBy usedByData = buildActivityUsedby(armName1, epochName1);
		saData.getUsedBy().add(usedByData);

		assertTrue(EntityUtil.deepEqual(sa,
				JAXBConvertor.convertStudyActivity(saData, s, domain)));
		assertEquals(saData, JAXBConvertor.convertStudyActivity(sa));

		Set<UsedBy> usedBy = new HashSet<UsedBy>(sa.getUsedBy());
		usedBy.add(new UsedBy(arm2, epoch2));
		sa.setUsedBy(usedBy);
		assertFalse(EntityUtil.deepEqual(sa,
				JAXBConvertor.convertStudyActivity(saData, s, domain)));
		JUnitUtil.assertNotEquals(saData,
				JAXBConvertor.convertStudyActivity(sa));

	}

	private ActivityUsedBy buildActivityUsedby(String armName, String epochName) {
		ActivityUsedBy usedByData = new ActivityUsedBy();
		usedByData.setArm(armName);
		usedByData.setEpoch(epochName);
		return usedByData;
	}

	private TreatmentActivity buildFixedDoseTreatmentActivity(Drug drug,
			double quantity) {
		return new TreatmentActivity(
				buildFixedDoseDrugTreatment(drug, quantity));
	}

	private DrugTreatment buildFixedDoseDrugTreatment(Drug drug, double quantity) {
		FixedDose dose = new FixedDose(quantity, DoseUnit.MILLIGRAMS_A_DAY);
		DrugTreatment dt = new DrugTreatment(drug, dose);
		return dt;
	}

	private TreatmentActivity buildFlexibleDoseTreatmentActivity(Drug drug,
			double minQuantity, double maxQuantity) {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(minQuantity,
				maxQuantity), DoseUnit.MILLIGRAMS_A_DAY);
		return new TreatmentActivity(new DrugTreatment(drug, dose));
	}

	@Test
	public void testConvertStudyChars() {
		Allocation alloc = Allocation.RANDOMIZED;
		Blinding blind = Blinding.UNKNOWN;
		String title = "MyStudy";
		int centers = 5;
		String objective = "The loftiest of goals";
		String incl = "Obesity";
		String excl = "Diabetes";
		Status status = Status.ENROLLING;
		Source source = Source.MANUAL;
		GregorianCalendar studyStart = new GregorianCalendar(2008, 8, 12);
		GregorianCalendar studyEnd = new GregorianCalendar(2010, 1, 18);
		GregorianCalendar created = new GregorianCalendar(2011, 2, 15);
		PubMedIdList pmids = new PubMedIdList();
		pmids.add(new PubMedId("1"));
		pmids.add(new PubMedId("12345"));
		List<BigInteger> pmints = new ArrayList<BigInteger>();
		for (PubMedId id : pmids) {
			pmints.add(new BigInteger(id.getId()));
		}

		org.drugis.addis.entities.data.Characteristics chars1 = new org.drugis.addis.entities.data.Characteristics();
		chars1.setTitle(JAXBConvertor.stringWithNotes(title));
		chars1.setAllocation(JAXBConvertor.allocationWithNotes(alloc));
		chars1.setBlinding(JAXBConvertor.blindingWithNotes(blind));
		chars1.setCenters(JAXBConvertor.intWithNotes(centers));
		chars1.setObjective(JAXBConvertor.stringWithNotes(objective));
		chars1.setStudyStart(JAXBConvertor.dateWithNotes(studyStart.getTime()));
		chars1.setStudyEnd(JAXBConvertor.dateWithNotes(studyEnd.getTime()));
		chars1.setStatus(JAXBConvertor.statusWithNotes(status));
		chars1.setInclusion(JAXBConvertor.stringWithNotes(incl));
		chars1.setExclusion(JAXBConvertor.stringWithNotes(excl));
		References refs = new References();
		refs.getPubMedId().addAll(pmints);
		chars1.setReferences(refs);
		chars1.setSource(JAXBConvertor.sourceWithNotes(source));
		chars1.setCreationDate(JAXBConvertor.dateWithNotes(created.getTime()));

		CharacteristicsMap chars2 = new CharacteristicsMap();
		chars2.put(BasicStudyCharacteristic.TITLE, new ObjectWithNotes<Object>(
				title));
		chars2.put(BasicStudyCharacteristic.ALLOCATION,
				new ObjectWithNotes<Object>(alloc));
		chars2.put(BasicStudyCharacteristic.BLINDING,
				new ObjectWithNotes<Object>(blind));
		chars2.put(BasicStudyCharacteristic.CENTERS,
				new ObjectWithNotes<Object>(centers));
		chars2.put(BasicStudyCharacteristic.OBJECTIVE,
				new ObjectWithNotes<Object>(objective));
		chars2.put(BasicStudyCharacteristic.STUDY_START,
				new ObjectWithNotes<Object>(studyStart.getTime()));
		chars2.put(BasicStudyCharacteristic.STUDY_END,
				new ObjectWithNotes<Object>(studyEnd.getTime()));
		chars2.put(BasicStudyCharacteristic.INCLUSION,
				new ObjectWithNotes<Object>(incl));
		chars2.put(BasicStudyCharacteristic.EXCLUSION,
				new ObjectWithNotes<Object>(excl));
		chars2.put(BasicStudyCharacteristic.PUBMED,
				new ObjectWithNotes<Object>(pmids)); // References
		chars2.put(BasicStudyCharacteristic.STATUS,
				new ObjectWithNotes<Object>(status));
		chars2.put(BasicStudyCharacteristic.SOURCE,
				new ObjectWithNotes<Object>(source));
		chars2.put(BasicStudyCharacteristic.CREATION_DATE,
				new ObjectWithNotes<Object>(created.getTime()));

		assertEntityEquals(chars2,
				JAXBConvertor.convertStudyCharacteristics(chars1));
		assertEquals(chars1, JAXBConvertor.convertStudyCharacteristics(chars2));
	}

	@Test
	public void testConvertCharacteristicsWithNulls() {
		String title = "title";
		org.drugis.addis.entities.data.Characteristics chars1 = new org.drugis.addis.entities.data.Characteristics();
		initializeCharacteristics(chars1, title);

		CharacteristicsMap chars2 = new CharacteristicsMap();
		chars2.put(BasicStudyCharacteristic.TITLE, new ObjectWithNotes<Object>(
				title));
		chars2.put(BasicStudyCharacteristic.PUBMED,
				new ObjectWithNotes<Object>(new PubMedIdList()));
		chars2.put(BasicStudyCharacteristic.CENTERS,
				new ObjectWithNotes<Object>(null));

		assertEquals(chars1, JAXBConvertor.convertStudyCharacteristics(chars2));
		assertEntityEquals(chars2,
				JAXBConvertor.convertStudyCharacteristics(chars1));
	}

	@Test
	public void testConvertStudyOutcomeMeasure() throws ConversionException {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		Epoch epoch = new Epoch("Measurement phase",
				EntityUtil.createDuration("P2D"));
		List<Epoch> epochs = new ArrayList<Epoch>();
		epochs.add(epoch);

		Endpoint ep = ExampleData.buildEndpointHamd();
		org.drugis.addis.entities.data.StudyOutcomeMeasure om = new org.drugis.addis.entities.data.StudyOutcomeMeasure();
		om.setNotes(new Notes());
		om.setEndpoint(nameReference(ep.getName()));
		om.setPrimary(false);
		RelativeTime rt = buildRelativeTime(epoch.getName(),
				ZERO_DAYS, RelativeTo.BEFORE_EPOCH_END);
		om.getWhenTaken().add(rt);

		assertEntityEquals(ep, (Endpoint) JAXBConvertor
				.convertStudyOutcomeMeasure(om, epochs, domain).getValue());
		StudyOutcomeMeasure<Variable> sOm1 = new StudyOutcomeMeasure<Variable>(
				ep);
		WhenTaken wt = new WhenTaken(ZERO_DAYS,
				RelativeTo.BEFORE_EPOCH_END, epoch);
		wt.commit();
		sOm1.getWhenTaken().add(wt);
		assertEquals(JAXBConvertor.convertStudyOutcomeMeasure(sOm1), om);

		AdverseEvent ade = ExampleData.buildAdverseEventDiarrhea();
		domain.getAdverseEvents().add(ade);
		om.setEndpoint(null);
		om.setAdverseEvent(nameReference(ade.getName()));
		om.setPrimary(false);

		assertEntityEquals(ade,
				(org.drugis.addis.entities.OutcomeMeasure) JAXBConvertor
						.convertStudyOutcomeMeasure(om, epochs, domain)
						.getValue());
		StudyOutcomeMeasure<Variable> sOm2 = new StudyOutcomeMeasure<Variable>(
				ade);
		sOm2.getWhenTaken().add(wt);
		assertEquals(JAXBConvertor.convertStudyOutcomeMeasure(sOm2), om);

		PopulationCharacteristic pc = ExampleData.buildGenderVariable();
		om.setAdverseEvent(null);
		om.setPopulationCharacteristic(nameReference(pc.getName()));

		assertEntityEquals(pc, (PopulationCharacteristic) JAXBConvertor
				.convertStudyOutcomeMeasure(om, epochs, domain).getValue());
		StudyOutcomeMeasure<Variable> sOm3 = new StudyOutcomeMeasure<Variable>(
				pc);
		sOm3.getWhenTaken().add(wt);
		assertEquals(JAXBConvertor.convertStudyOutcomeMeasure(sOm3), om);
	}

	@Test(expected = ConversionException.class)
	public void testConvertStudyOutcomeMeasureThrows()
			throws ConversionException {
		Domain domain = new DomainImpl();
		org.drugis.addis.entities.data.StudyOutcomeMeasure om = new org.drugis.addis.entities.data.StudyOutcomeMeasure();
		JAXBConvertor.convertStudyOutcomeMeasure(om, null, domain);
	}

	@Test
	public void testConvertStudyOutcomeMeasures() throws ConversionException {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		Endpoint ep = ExampleData.buildEndpointHamd();
		domain.getAdverseEvents().add(ExampleData.buildAdverseEventDiarrhea());

		LinkedHashMap<String, StudyOutcomeMeasure<?>> vars = new LinkedHashMap<String, StudyOutcomeMeasure<?>>();
		StudyOutcomeMeasure<Variable> epSom = new StudyOutcomeMeasure<Variable>(
				ep);
		Epoch epoch = new Epoch("Measurement phase",
				EntityUtil.createDuration("P2D"));
		List<Epoch> epochs = new ArrayList<Epoch>();
		epochs.add(epoch);

		WhenTaken wt = new WhenTaken(ZERO_DAYS,
				RelativeTo.BEFORE_EPOCH_END, epoch);
		wt.commit();
		epSom.getWhenTaken().add(wt);
		vars.put("X", epSom);
		vars.put(
				"Y",
				new StudyOutcomeMeasure<Variable>(ExampleData
						.buildAdverseEventDiarrhea()));

		org.drugis.addis.entities.data.StudyOutcomeMeasure epRef = new org.drugis.addis.entities.data.StudyOutcomeMeasure();
		epRef.setNotes(new Notes());
		epRef.setId("X");
		epRef.setEndpoint(nameReference(ep.getName()));
		epRef.setPrimary(false);
		epRef.getWhenTaken().add(
				buildRelativeTime(epoch.getName(), wt.getDuration(),
						wt.getRelativeTo()));

		org.drugis.addis.entities.data.StudyOutcomeMeasure adeRef = new org.drugis.addis.entities.data.StudyOutcomeMeasure();
		adeRef.setNotes(new Notes());
		adeRef.setId("Y");
		adeRef.setAdverseEvent(nameReference(ExampleData
				.buildAdverseEventDiarrhea().getName()));
		adeRef.setPrimary(false);
		StudyOutcomeMeasures oms = new StudyOutcomeMeasures();
		oms.getStudyOutcomeMeasure().add(epRef);
		oms.getStudyOutcomeMeasure().add(adeRef);

		assertEquals(vars,
				JAXBConvertor.convertStudyOutcomeMeasures(oms, epochs, domain));
		assertEquals(JAXBConvertor.convertStudyOutcomeMeasures(vars), oms);
	}

	/**
	 * Test whether the numerical measurements (so NOT measurement time etc) are
	 * converted properly
	 */
	@Test
	public void testConvertMeasurementData() throws ConversionException {
		org.drugis.addis.entities.data.RateMeasurement rm = new org.drugis.addis.entities.data.RateMeasurement();
		int c = 12;
		int s = 42;
		rm.setRate(c);
		rm.setSampleSize(s);

		org.drugis.addis.entities.data.Measurement meas = buildRateMeasurement(
				null, null, rm);
		BasicRateMeasurement expected1 = new BasicRateMeasurement(c, s);
		assertEntityEquals(expected1, JAXBConvertor.convertMeasurement(meas));
		assertEquals(meas, JAXBConvertor.convertMeasurement(expected1));

		org.drugis.addis.entities.data.ContinuousMeasurement cm = new org.drugis.addis.entities.data.ContinuousMeasurement();
		double m = 3.14;
		double e = 2.71;
		cm.setMean(m);
		cm.setStdDev(e);
		cm.setSampleSize(s);
		meas = buildContinuousMeasurement(null, null, cm);
		BasicContinuousMeasurement expected2 = new BasicContinuousMeasurement(
				m, e, s);
		assertEntityEquals(expected2, JAXBConvertor.convertMeasurement(meas));
		assertEquals(meas, JAXBConvertor.convertMeasurement(expected2));

		List<CategoryMeasurement> cms = new ArrayList<CategoryMeasurement>();
		CategoryMeasurement c1 = new CategoryMeasurement();
		c1.setName("Cats");
		c1.setRate(18);
		CategoryMeasurement c2 = new CategoryMeasurement();
		c2.setName("Dogs");
		c2.setRate(2145);
		cms.add(c1);
		cms.add(c2);
		meas = buildCategoricalMeasurement(null, null, cms);

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("Dogs", 2145);
		map.put("Cats", 18);
		FrequencyMeasurement expected3 = new FrequencyMeasurement(
				Arrays.asList((new String[] { "Cats", "Dogs" })), map);
		assertEntityEquals(expected3, JAXBConvertor.convertMeasurement(meas));
		assertEquals(meas, JAXBConvertor.convertMeasurement(expected3));
	}

	@Test
	public void testConvertMeasurements() throws ConversionException {
		List<Arm> arms = new ArrayList<Arm>();
		Arm arm5 = new Arm("Opium", 42);
		arms.add(arm5);
		Arm arm8 = new Arm("LSD", 42);
		arms.add(arm8);
		List<Epoch> epochs = new ArrayList<Epoch>();
		Epoch mainPhase = new Epoch("Measurement phase",
				EntityUtil.createDuration("P2D"));
		epochs.add(mainPhase);

		WhenTaken whenTaken = new WhenTaken(ZERO_DAYS,
				RelativeTo.BEFORE_EPOCH_END, mainPhase);
		whenTaken.commit();

		Map<String, StudyOutcomeMeasure<?>> oms = new HashMap<String, StudyOutcomeMeasure<?>>();
		String pcName = "popChar-hair";
		PopulationCharacteristic pc = new PopulationCharacteristic(
				"Hair Length", new ContinuousVariableType());
		oms.put(pcName, new StudyOutcomeMeasure<Variable>(pc, whenTaken));
		String epName = "endpoint-tripping";
		Endpoint ep = new Endpoint("Tripping achieved",
				Endpoint.convertVarType(Type.RATE), Direction.HIGHER_IS_BETTER);
		oms.put(epName, new StudyOutcomeMeasure<Variable>(ep, whenTaken));
		String aeName = "ade-nojob";
		AdverseEvent ae = new AdverseEvent("Job loss",
				AdverseEvent.convertVarType(Type.RATE));
		oms.put(aeName, new StudyOutcomeMeasure<Variable>(ae, whenTaken));

		org.drugis.addis.entities.data.RateMeasurement rm1 = new org.drugis.addis.entities.data.RateMeasurement();
		rm1.setRate(10);
		rm1.setSampleSize(100);
		BasicRateMeasurement crm1 = new BasicRateMeasurement(10, 100);

		org.drugis.addis.entities.data.RateMeasurement rm2 = new org.drugis.addis.entities.data.RateMeasurement();
		rm2.setRate(20);
		rm2.setSampleSize(100);
		BasicRateMeasurement crm2 = new BasicRateMeasurement(20, 100);

		org.drugis.addis.entities.data.ContinuousMeasurement cm1 = new org.drugis.addis.entities.data.ContinuousMeasurement();
		cm1.setMean(1.5);
		cm1.setStdDev(1.0);
		cm1.setSampleSize(100);
		BasicContinuousMeasurement ccm1 = new BasicContinuousMeasurement(1.5,
				1.0, 100);

		Measurements measurements = new Measurements();
		List<org.drugis.addis.entities.data.Measurement> list = measurements
				.getMeasurement();
		list.add(buildRateMeasurement(arm5.getName(), epName,
				"Measurement phase", whenTaken.getDuration(),
				whenTaken.getRelativeTo(), rm1));
		list.add(buildRateMeasurement(arm8.getName(), epName,
				"Measurement phase", whenTaken.getDuration(),
				whenTaken.getRelativeTo(), rm2));
		list.add(buildRateMeasurement(arm5.getName(), aeName,
				"Measurement phase", whenTaken.getDuration(),
				whenTaken.getRelativeTo(), rm2));
		list.add(buildRateMeasurement(arm8.getName(), aeName,
				"Measurement phase", whenTaken.getDuration(),
				whenTaken.getRelativeTo(), rm1));
		list.add(buildContinuousMeasurement(arm5.getName(), pcName,
				"Measurement phase", whenTaken.getDuration(),
				whenTaken.getRelativeTo(), cm1));
		list.add(buildContinuousMeasurement(arm8.getName(), pcName,
				"Measurement phase", whenTaken.getDuration(),
				whenTaken.getRelativeTo(), cm1));
		list.add(buildContinuousMeasurement(null, pcName, "Measurement phase",
				whenTaken.getDuration(), whenTaken.getRelativeTo(), cm1));

		Map<MeasurementKey, BasicMeasurement> expected = new HashMap<MeasurementKey, BasicMeasurement>();
		expected.put(new MeasurementKey(ep, arm5, whenTaken), crm1);
		expected.put(new MeasurementKey(ep, arm8, whenTaken), crm2);
		expected.put(new MeasurementKey(ae, arm5, whenTaken), crm2);
		expected.put(new MeasurementKey(ae, arm8, whenTaken), crm1);
		expected.put(new MeasurementKey(pc, arm5, whenTaken), ccm1);
		expected.put(new MeasurementKey(pc, arm8, whenTaken), ccm1);
		expected.put(new MeasurementKey(pc, null, whenTaken), ccm1);

		assertEquals(expected, JAXBConvertor.convertMeasurements(measurements,
				arms, epochs, oms));
		JUnitUtil.assertAllAndOnly(measurements.getMeasurement(), JAXBConvertor
				.convertMeasurements(expected, oms).getMeasurement());
	}

	private org.drugis.addis.entities.data.Measurement buildContinuousMeasurement(
			String armName, String omName, String epochName, Duration duration,
			RelativeTo relativeTo,
			org.drugis.addis.entities.data.ContinuousMeasurement cm) {
		org.drugis.addis.entities.data.Measurement m = buildContinuousMeasurement(
				armName, omName, cm);
		m.setWhenTaken(buildRelativeTime(epochName, duration, relativeTo));
		return m;
	}

	private org.drugis.addis.entities.data.Measurement buildContinuousMeasurement(
			String armName, String omName,
			org.drugis.addis.entities.data.ContinuousMeasurement cm) {
		org.drugis.addis.entities.data.Measurement m = initMeasurement(armName,
				omName);
		m.setContinuousMeasurement(cm);
		return m;
	}

	private RelativeTime buildRelativeTime(String epochName, Duration duration,
			RelativeTo relativeTo) {
		RelativeTime rt = new RelativeTime();
		rt.setEpoch(nameReference(epochName));
		rt.setRelativeTo(relativeTo);
		rt.setHowLong(duration);
		return rt;
	}

	private org.drugis.addis.entities.data.Measurement initMeasurement(
			String armName, String omName) {
		org.drugis.addis.entities.data.Measurement m = new org.drugis.addis.entities.data.Measurement();
		if (armName != null) {
			m.setArm(JAXBConvertor.nameReference(armName));
		}
		if (omName != null) {
			m.setStudyOutcomeMeasure(JAXBConvertor.stringIdReference(omName));
		}
		return m;
	}

	private org.drugis.addis.entities.data.Measurement buildRateMeasurement(
			String armName, String omName, String epochName, Duration duration,
			RelativeTo relativeTo,
			org.drugis.addis.entities.data.RateMeasurement rm) {
		org.drugis.addis.entities.data.Measurement m = buildRateMeasurement(
				armName, omName, rm);
		m.setWhenTaken(buildRelativeTime(epochName, duration, relativeTo));
		return m;
	}

	private org.drugis.addis.entities.data.Measurement buildRateMeasurement(
			String armName, String omName,
			org.drugis.addis.entities.data.RateMeasurement rm) {
		org.drugis.addis.entities.data.Measurement m = initMeasurement(armName,
				omName);
		m.setRateMeasurement(rm);
		return m;
	}

	@SuppressWarnings("unused")
	private org.drugis.addis.entities.data.Measurement buildCategoricalMeasurement(
			String armName, String omName, List<CategoryMeasurement> cmList,
			String epochName, Duration duration, RelativeTo relativeTo) {
		org.drugis.addis.entities.data.Measurement m = buildCategoricalMeasurement(
				armName, omName, cmList);
		m.setWhenTaken(buildRelativeTime(epochName, duration, relativeTo));
		return m;
	}

	private org.drugis.addis.entities.data.Measurement buildCategoricalMeasurement(
			String armName, String omName, List<CategoryMeasurement> cmList) {
		org.drugis.addis.entities.data.Measurement m = initMeasurement(armName,
				omName);
		CategoricalMeasurement cms = new CategoricalMeasurement();
		for (CategoryMeasurement cm : cmList) {
			cms.getCategory().add(cm);
		}
		m.setCategoricalMeasurement(cms);
		return m;
	}

	public org.drugis.addis.entities.data.Study buildStudy(String name)
			throws DatatypeConfigurationException, ConversionException {
		String indicationName = ExampleData.buildIndicationDepression()
				.getName();
		String[] endpointNames = new String[] {
				ExampleData.buildEndpointHamd().getName(),
				ExampleData.buildEndpointCgi().getName() };
		String[] adverseEventName = new String[] { ExampleData
				.buildAdverseEventConvulsion().getName() };
		String[] popCharNames = new String[] { ExampleData.buildAgeVariable()
				.getName() };
		String title = "WHOO";

		// Arms
		Arms arms = new Arms();
		String fluoxArmName = "fluox arm";
		String paroxArmName = "parox arm";
		arms.getArm().add(buildArmData(fluoxArmName, 100));
		arms.getArm().add(buildArmData(paroxArmName, 42));

		String randomizationEpochName = "Randomization";
		String mainEpochName = "Main phase";

		Epochs epochs = new Epochs();
		epochs.getEpoch().add(buildEpoch(randomizationEpochName, null));
		epochs.getEpoch().add(
				buildEpoch(mainEpochName, DatatypeFactory.newInstance()
						.newDuration("P2D")));

		StudyActivities sas = new StudyActivities();
		sas.getStudyActivity().add(
				buildStudyActivity("Randomization",
						PredefinedActivity.RANDOMIZATION));
		DrugTreatment fluoxActivity = new DrugTreatment(
				ExampleData.buildDrugFluoxetine(), new FixedDose(12.5,
						DoseUnit.MILLIGRAMS_A_DAY));
		DrugTreatment sertrActivity = new DrugTreatment(
				ExampleData.buildDrugSertraline(), new FixedDose(12.5,
						DoseUnit.MILLIGRAMS_A_DAY));
		List<DrugTreatment> combTreatment = Arrays.asList(fluoxActivity,
				sertrActivity);
		sas.getStudyActivity().add(
				buildStudyActivity("Fluox + Sertr fixed dose",
						new TreatmentActivity(combTreatment)));
		DrugTreatment paroxActivity = new DrugTreatment(
				ExampleData.buildDrugParoxetine(), new FixedDose(12.0,
						DoseUnit.MILLIGRAMS_A_DAY));
		sas.getStudyActivity().add(
				buildStudyActivity("Parox fixed dose", new TreatmentActivity(
						paroxActivity)));

		ActivityUsedBy aub0 = buildActivityUsedby(fluoxArmName,
				randomizationEpochName);
		ActivityUsedBy aub1 = buildActivityUsedby(paroxArmName,
				randomizationEpochName);

		ActivityUsedBy aub2 = buildActivityUsedby(fluoxArmName, mainEpochName);

		ActivityUsedBy aub3 = buildActivityUsedby(paroxArmName, mainEpochName);

		sas.getStudyActivity().get(0).getUsedBy().add(aub0);
		sas.getStudyActivity().get(0).getUsedBy().add(aub1);
		sas.getStudyActivity().get(1).getUsedBy().add(aub2);
		sas.getStudyActivity().get(2).getUsedBy().add(aub3);

		org.drugis.addis.entities.data.Study study = buildStudySkeleton(name,
				title, indicationName, endpointNames, adverseEventName,
				popCharNames, arms, epochs, sas);

		study.getCharacteristics().setCenters(JAXBConvertor.intWithNotes(3));
		study.getCharacteristics().setAllocation(
				JAXBConvertor.allocationWithNotes(Allocation.RANDOMIZED));

		List<org.drugis.addis.entities.data.Measurement> dataMeasurements = study
				.getMeasurements().getMeasurement();
		RateMeasurement rm1 = new RateMeasurement();
		rm1.setRate(10);
		rm1.setSampleSize(110);
		ContinuousMeasurement cm1 = new ContinuousMeasurement();
		cm1.setMean(0.2);
		cm1.setStdDev(0.01);
		cm1.setSampleSize(110);

		// note: order is important!
		addRateMeasurement(rm1, fluoxArmName, "endpoint-" + endpointNames[0], dataMeasurements);
		addRateMeasurement(rm1, paroxArmName, "endpoint-" + endpointNames[0], dataMeasurements);
		addContinuousMeasurement(cm1, null, "popChar-" + popCharNames[0], dataMeasurements);
		return study;
	}

	private void addRateMeasurement(RateMeasurement rm, String armName,
			String omName, List<org.drugis.addis.entities.data.Measurement> dataMeasurements) {
		org.drugis.addis.entities.data.Measurement m = buildRateMeasurement(
				armName, omName, "Main phase",
				ZERO_DAYS, RelativeTo.BEFORE_EPOCH_END,
				rm);
		dataMeasurements.add(m);
	}
	
	private void addContinuousMeasurement(ContinuousMeasurement cm, String armName,
			String omName, List<org.drugis.addis.entities.data.Measurement> dataMeasurements) {
		org.drugis.addis.entities.data.Measurement m = buildContinuousMeasurement(
				armName, omName, "Main phase",
				ZERO_DAYS, RelativeTo.BEFORE_EPOCH_END,
				cm);
		dataMeasurements.add(m);
	}

	private org.drugis.addis.entities.data.StudyActivity buildStudyActivity(
			String name, Activity activity) throws ConversionException {
		org.drugis.addis.entities.data.StudyActivity sa = new org.drugis.addis.entities.data.StudyActivity();
		sa.setName(name);
		org.drugis.addis.entities.data.Activity a = JAXBConvertor
				.convertActivity(activity);
		sa.setActivity(a);
		sa.setNotes(new Notes());
		return sa;
	}

	private org.drugis.addis.entities.data.Epoch buildEpoch(String epochName1,
			Duration d) {
		org.drugis.addis.entities.data.Epoch e = new org.drugis.addis.entities.data.Epoch();
		e.setName(epochName1);
		e.setDuration(d);
		e.setNotes(new Notes());
		return e;
	}

	private org.drugis.addis.entities.data.Arm buildArmData(String name,
			int size) {
		org.drugis.addis.entities.data.Arm a = new org.drugis.addis.entities.data.Arm();
		a.setName(name);
		a.setSize(size);
		a.setNotes(new Notes());
		return a;
	}

	private void initializeCharacteristics(Characteristics characteristics,
			String title) {
		characteristics.setAllocation(JAXBConvertor
				.allocationWithNotes(Allocation.UNKNOWN));
		characteristics.setBlinding(JAXBConvertor
				.blindingWithNotes(Blinding.UNKNOWN));
		characteristics.setCenters(JAXBConvertor.intWithNotes(null));
		characteristics.setCreationDate(JAXBConvertor.dateWithNotes(null));
		characteristics.setExclusion(JAXBConvertor.stringWithNotes(null));
		characteristics.setInclusion(JAXBConvertor.stringWithNotes(null));
		characteristics.setObjective(JAXBConvertor.stringWithNotes(null));
		characteristics.setReferences(new References());
		characteristics.setSource(JAXBConvertor.sourceWithNotes(Source.MANUAL));
		characteristics
				.setStatus(JAXBConvertor.statusWithNotes(Status.UNKNOWN));
		characteristics.setStudyEnd(JAXBConvertor.dateWithNotes(null));
		characteristics.setStudyStart(JAXBConvertor.dateWithNotes(null));
		characteristics.setTitle(JAXBConvertor.stringWithNotes(title));
	}

	private void addContinuousMeasurements(org.drugis.addis.entities.data.Study study, String omName) {
		ContinuousMeasurement cm = new ContinuousMeasurement();
		cm.setMean(0.5);
		cm.setSampleSize(50);
		cm.setStdDev(1.0);
		for (org.drugis.addis.entities.data.Arm arm : study.getArms().getArm()) { // ARGH
			addContinuousMeasurement(cm, arm.getName(), omName, study.getMeasurements().getMeasurement());
		}
	}
	
	private org.drugis.addis.entities.data.Study buildStudySkeleton(
			String name, String title, String indicationName,
			String[] endpointName, String[] adverseEventName,
			String[] popCharName, Arms arms, Epochs epochs, StudyActivities sas) {
		org.drugis.addis.entities.data.Study study = new org.drugis.addis.entities.data.Study();
		study.setName(name);
		NameReferenceWithNotes indicationRef = JAXBConvertor
				.nameReferenceWithNotes(indicationName);
		study.setIndication(indicationRef);

		// Outcome measures (empty)
		StudyOutcomeMeasures studyOutcomeMeasures = new StudyOutcomeMeasures();
		study.setStudyOutcomeMeasures(studyOutcomeMeasures);

		// Outcome measures: Endpoints
		for (String epName : endpointName) {
			org.drugis.addis.entities.data.StudyOutcomeMeasure ep = new org.drugis.addis.entities.data.StudyOutcomeMeasure();
			ep.setNotes(new Notes());
			ep.setId("endpoint-" + epName);
			ep.setEndpoint(nameReference(epName));
			ep.setPrimary(false);
			studyOutcomeMeasures.getStudyOutcomeMeasure().add(ep);
		}

		// Outcome measures: Adverse events
		for (String aeName : adverseEventName) {
			org.drugis.addis.entities.data.StudyOutcomeMeasure ae = new org.drugis.addis.entities.data.StudyOutcomeMeasure();
			ae.setNotes(new Notes());
			ae.setId("adverseEvent-" + aeName);
			ae.setAdverseEvent(nameReference(aeName));
			ae.getWhenTaken();
			ae.setPrimary(false);
			studyOutcomeMeasures.getStudyOutcomeMeasure().add(ae);
		}

		// Outcome measures: Population chars
		for (String pcName : popCharName) {
			org.drugis.addis.entities.data.StudyOutcomeMeasure pc = new org.drugis.addis.entities.data.StudyOutcomeMeasure();
			pc.setNotes(new Notes());
			pc.setId("popChar-" + pcName);
			pc.setPopulationCharacteristic(nameReference(pcName));
			pc.setPrimary(false);
			pc.getWhenTaken();
			studyOutcomeMeasures.getStudyOutcomeMeasure().add(pc);
		}

		// Arms
		study.setArms(arms);

		// Epochs
		study.setEpochs(epochs);

		// StudyActivities
		study.setActivities(sas);

		// Study characteristics
		Characteristics chars = new Characteristics();
		study.setCharacteristics(chars);
		initializeCharacteristics(study.getCharacteristics(), title);

		// Measurements (empty)
		Measurements measurements = new Measurements();
		study.setMeasurements(measurements);

		study.setNotes(new Notes());

		return study;
	}

	@Test
	public void testConvertStudy() throws ConversionException,
			DatatypeConfigurationException {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);

		String name = "My fancy study";
		org.drugis.addis.entities.data.Study study = buildStudy(name);

		// ----------------------------------------
		Study study2 = new Study();
		study2.setName(name);
		study2.setIndication(ExampleData.buildIndicationDepression());
		Epoch epoch1 = new Epoch("Randomization", null);
		Epoch epoch2 = new Epoch("Main phase", EntityUtil.createDuration("P2D"));
		study2.getEpochs().add(epoch1);
		study2.getEpochs().add(epoch2);
		study2.getEndpoints().add(
				new org.drugis.addis.entities.StudyOutcomeMeasure<Endpoint>(
						ExampleData.buildEndpointHamd()));
		study2.getEndpoints().add(
				new org.drugis.addis.entities.StudyOutcomeMeasure<Endpoint>(
						ExampleData.buildEndpointCgi()));
		study2.getAdverseEvents()
				.add(new org.drugis.addis.entities.StudyOutcomeMeasure<AdverseEvent>(
						ExampleData.buildAdverseEventConvulsion()));
		study2.addVariable(ExampleData.buildAgeVariable());
		Arm fluoxArm = new Arm("fluox arm", 100);
		study2.getArms().add(fluoxArm);
		Arm paroxArm = new Arm("parox arm", 42);
		study2.getArms().add(paroxArm);

		StudyActivity randomizationActivity = new StudyActivity(
				"Randomization", PredefinedActivity.RANDOMIZATION);
		DrugTreatment fluoxDrugTreatment = new DrugTreatment(
				ExampleData.buildDrugFluoxetine(), new FixedDose(12.5,
						DoseUnit.MILLIGRAMS_A_DAY));
		DrugTreatment sertrDrugTreatment = new DrugTreatment(
				ExampleData.buildDrugSertraline(), new FixedDose(12.5,
						DoseUnit.MILLIGRAMS_A_DAY));
		;
		StudyActivity combTreatmentActivity = new StudyActivity(
				"Fluox + Sertr fixed dose", new TreatmentActivity(
						Arrays.asList(fluoxDrugTreatment, sertrDrugTreatment)));
		StudyActivity paroxTreatmentActivity = new StudyActivity(
				"Parox fixed dose", new TreatmentActivity(new DrugTreatment(
						ExampleData.buildDrugParoxetine(), new FixedDose(12.0,
								DoseUnit.MILLIGRAMS_A_DAY))));
		study2.getStudyActivities().add(randomizationActivity);
		study2.getStudyActivities().add(combTreatmentActivity);
		study2.getStudyActivities().add(paroxTreatmentActivity);

		study2.setCharacteristic(BasicStudyCharacteristic.TITLE, "WHOO");
		study2.setCharacteristic(BasicStudyCharacteristic.CENTERS, 3);
		study2.setCharacteristic(BasicStudyCharacteristic.ALLOCATION,
				Allocation.RANDOMIZED);
		study2.setCharacteristic(BasicStudyCharacteristic.PUBMED,
				new PubMedIdList());
		study2.setCharacteristic(BasicStudyCharacteristic.CREATION_DATE, null);
		study2.setStudyActivityAt(fluoxArm, epoch1, randomizationActivity);
		study2.setStudyActivityAt(paroxArm, epoch1, randomizationActivity);
		study2.setStudyActivityAt(fluoxArm, epoch2, combTreatmentActivity);
		study2.setStudyActivityAt(paroxArm, epoch2, paroxTreatmentActivity);
		study2.setMeasurement(ExampleData.buildEndpointHamd(), paroxArm,
				new BasicRateMeasurement(10, 110));
		study2.setMeasurement(ExampleData.buildEndpointHamd(), fluoxArm,
				new BasicRateMeasurement(10, 110));
		study2.setMeasurement(ExampleData.buildAgeVariable(),
				new BasicContinuousMeasurement(0.2, 0.01, 110));

		assertEntityEquals(study2, JAXBConvertor.convertStudy(study, domain));
		assertEquals(study, JAXBConvertor.convertStudy(study2));
	}

	@Test
	public void testConvertNote() {
		org.drugis.addis.entities.data.Note note = new org.drugis.addis.entities.data.Note();
		note.setSource(Source.CLINICALTRIALS);
		String text = "Some text here";
		note.setValue(text);

		Note expected = new Note(Source.CLINICALTRIALS, text);

		assertEquals(expected, JAXBConvertor.convertNote(note));
		assertEquals(note, JAXBConvertor.convertNote(expected));
	}

	@Test
	public void testConvertStudyWithNotes() throws ConversionException,
			DatatypeConfigurationException {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);

		String name = "My fancy study";
		org.drugis.addis.entities.data.Study studyData = buildStudy(name);
		Study studyEntity = JAXBConvertor.convertStudy(studyData, domain);

		Note armNote = new Note(Source.CLINICALTRIALS, "Some text here");
		studyData.getArms().getArm().get(0).getNotes().getNote()
				.add(JAXBConvertor.convertNote(armNote));
		studyData.getArms().getArm().get(1).getNotes().getNote()
				.add(JAXBConvertor.convertNote(armNote));
		studyEntity.getArms().get(0).getNotes().add(armNote);
		studyEntity.getArms().get(1).getNotes().add(armNote);

		Note adeNote = new Note(Source.MANUAL,
				"I would not like to suffer from this!");
		studyData.getStudyOutcomeMeasures().getStudyOutcomeMeasure().get(2)
				.getNotes().getNote().add(JAXBConvertor.convertNote(adeNote));
		studyEntity.getAdverseEvents().get(0).getNotes().add(adeNote);
		Note hamdNote = new Note(Source.MANUAL, "Mmm... HAM!");
		studyData.getStudyOutcomeMeasures().getStudyOutcomeMeasure().get(0)
				.getNotes().getNote().add(JAXBConvertor.convertNote(hamdNote));
		studyEntity.getEndpoints().get(0).getNotes().add(hamdNote);
		Note charNote = new Note(Source.CLINICALTRIALS,
				"A randomized double blind trial of something");
		studyData.getCharacteristics().getAllocation().getNotes().getNote()
				.add(JAXBConvertor.convertNote(charNote));
		studyEntity.getCharacteristics()
				.get(BasicStudyCharacteristic.ALLOCATION).getNotes()
				.add(charNote);

		Note indicationNote = new Note(Source.CLINICALTRIALS,
				"Depression! Aah!");
		studyData.getIndication().getNotes().getNote()
				.add(JAXBConvertor.convertNote(indicationNote));
		studyEntity.getIndicationWithNotes().getNotes().add(indicationNote);

		Note idNote = new Note(Source.CLINICALTRIALS, "NCT1337");
		studyData.getNotes().getNote().add(JAXBConvertor.convertNote(idNote));
		studyEntity.getNotes().add(idNote);

		assertEntityEquals(studyEntity,
				JAXBConvertor.convertStudy(studyData, domain));
		assertEquals(studyData, JAXBConvertor.convertStudy(studyEntity));
	}

	final class MetaAnalysisAlternativeComparator implements Comparator<MetaAnalysisAlternative> {
		public int compare(MetaAnalysisAlternative o1, MetaAnalysisAlternative o2) {
			SortedSet<String> drugs1 = getDrugs(o1);
			SortedSet<String> drugs2 = getDrugs(o2);
			drugs1.removeAll(drugs2);
			drugs2.removeAll(drugs1);
			if (drugs1.size() == 0 && drugs2.size() == 0) {
				return 0;
			}
			if (drugs1.size() == 0) {
				return -1;
			}
			if (drugs2.size() == 0) {
				return 1;
			}
			return drugs1.iterator().next().compareTo(drugs2.iterator().next());
		}

		private SortedSet<String> getDrugs(MetaAnalysisAlternative o1) {
			SortedSet<String> set = new TreeSet<String>();
			for (Object a : o1.getTreatmentDefinition().getRichCategoryOrTrivialCategory()) {
				set.add(getDrugFromTrivialOrRichCategory(a));
			}
			return set;
		}
	}

	public class MetaAnalysisWithStudies {
		public org.drugis.addis.entities.data.PairwiseMetaAnalysis d_pwma;
		public org.drugis.addis.entities.data.NetworkMetaAnalysis d_nwma;
		public List<org.drugis.addis.entities.data.Study> d_studies;

		public MetaAnalysisWithStudies(
				org.drugis.addis.entities.data.PairwiseMetaAnalysis ma,
				List<org.drugis.addis.entities.data.Study> s) {
			d_pwma = ma;
			d_studies = s;
		}

		public MetaAnalysisWithStudies(
				org.drugis.addis.entities.data.NetworkMetaAnalysis ma,
				List<org.drugis.addis.entities.data.Study> s) {
			d_nwma = ma;
			d_studies = s;
		}
	}

	@Test
	public void testConvertPairWiseMetaAnalysis() throws ConversionException,
			DatatypeConfigurationException {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);

		String name = "Fluox-Venla Diarrhea for PMA";
		MetaAnalysisWithStudies ma = buildPairWiseMetaAnalysis(name);

		// -----------------------------------
		Study study = JAXBConvertor.convertStudy(ma.d_studies.get(0), domain);
		List<StudyArmsEntry> armsList = new ArrayList<StudyArmsEntry>();
		Arm base = study.getArms().get(0);
		Arm subject = study.getArms().get(1);
		armsList.add(new StudyArmsEntry(study, base, subject));
		domain.getStudies().add(study);

		RandomEffectsMetaAnalysis pwma = new RandomEffectsMetaAnalysis(
				name, 
				ExampleData.buildEndpointHamd(), 
				study.getDrugs(base),
				study.getDrugs(subject),
				armsList, false);

		assertEntityEquals(pwma,
				JAXBConvertor.convertPairWiseMetaAnalysis(ma.d_pwma, domain));

		assertEquals(ma.d_pwma, JAXBConvertor.convertPairWiseMetaAnalysis(pwma));
	}

	private MetaAnalysisWithStudies buildPairWiseMetaAnalysis(String name)
			throws DatatypeConfigurationException, ConversionException {
		String study_name = "My fancy pair-wise study";
		org.drugis.addis.entities.data.Study study = buildStudy(study_name);

		org.drugis.addis.entities.data.PairwiseMetaAnalysis pwma = new org.drugis.addis.entities.data.PairwiseMetaAnalysis();
		pwma.setName(name);
		pwma.setIndication(nameReference(ExampleData
				.buildIndicationDepression().getName()));
		pwma.setEndpoint(nameReference(ExampleData.buildEndpointHamd()
				.getName()));
		// Base
		MetaAnalysisAlternative combi = new MetaAnalysisAlternative();
		if (combi.getTreatmentDefinition() == null) {
			combi.setTreatmentDefinition(createTrivialTreatmentDefinition(new String[] {ExampleData.buildDrugFluoxetine().getName(), ExampleData.buildDrugSertraline().getName() }));
		}
		AnalysisArms combiArms = new AnalysisArms();
		combiArms.getArm().add(
				JAXBConvertor.armReference(study_name, study.getArms().getArm()
						.get(0).getName()));
		combi.setArms(combiArms);
		pwma.getAlternative().add(combi);
		// Subject
		MetaAnalysisAlternative parox = new MetaAnalysisAlternative();
		if (parox.getTreatmentDefinition() == null) {
			parox.setTreatmentDefinition(createTrivialTreatmentDefinition(new String[] {ExampleData.buildDrugParoxetine().getName()} ));
		}

		AnalysisArms paroxArms = new AnalysisArms();
		paroxArms.getArm().add(
				JAXBConvertor.armReference(study_name, study.getArms().getArm()
						.get(1).getName()));
		parox.setArms(paroxArms);
		pwma.getAlternative().add(parox);

		return new MetaAnalysisWithStudies(pwma,
				Collections.singletonList(study));
	}



	public MetaAnalysisWithStudies buildNetworkMetaAnalysis(String name) throws DatatypeConfigurationException, ConversionException {
		String study1Name = "A Network Meta analysis study 1";
		String study2Name = "A Network Meta analysis study 2";
		String study3Name = "A Network Meta analysis study 3";
		
		String[] endpointNames = new String[] { ExampleData.buildEndpointHamd().getName(), ExampleData.buildEndpointCgi().getName() };
		
		Arms arms1 = new Arms();
		Epochs epochs1 = new Epochs();
		StudyActivities sas1 = new StudyActivities();
		String combiArmName = "fluox + sertr arm";
		String sertraArmName = "sertra arm";
		String paroxArmName = "parox arm";
		String mainPhaseName = "Main phase";
		String treatmentName = "Treatment";
		DrugTreatment fluox = buildFixedDoseDrugTreatment(ExampleData.buildDrugFluoxetine(), 12.5);
		DrugTreatment sertr = buildFixedDoseDrugTreatment(ExampleData.buildDrugSertraline(), 12.5);
		TreatmentActivity combiFixedDose = new TreatmentActivity(Arrays.asList(fluox, sertr));
		TreatmentActivity sertraFixedDose = buildFixedDoseTreatmentActivity(ExampleData.buildDrugSertraline(), 12.5);
		TreatmentActivity paroxFixedDose =  buildFixedDoseTreatmentActivity(ExampleData.buildDrugParoxetine(), 12.5);
		buildArmEpochTreatmentActivityCombination(arms1, epochs1, sas1, 20, combiArmName, mainPhaseName, treatmentName, combiFixedDose);
		buildArmEpochTreatmentActivityCombination(arms1, epochs1, sas1, 20, sertraArmName, mainPhaseName, treatmentName, sertraFixedDose);

		String indicationName = ExampleData.buildIndicationDepression().getName();
		String[] popCharNames = new String[] {ExampleData.buildAgeVariable().getName()};
		String[] adverseEventNames = new String[] {};
		org.drugis.addis.entities.data.Study study1 = 
				buildStudySkeleton(study1Name, study1Name, indicationName, endpointNames, adverseEventNames, popCharNames, arms1, epochs1, sas1);
		String omName = "endpoint-" + ExampleData.buildEndpointCgi().getName();
		
		addContinuousMeasurements(study1, omName);

		Arms arms2 = new Arms();
		Epochs epochs2 = new Epochs();
		StudyActivities sas2 = new StudyActivities();
		buildArmEpochTreatmentActivityCombination(arms2, epochs2, sas2, 20, paroxArmName, mainPhaseName, treatmentName, paroxFixedDose);
		buildArmEpochTreatmentActivityCombination(arms2, epochs2, sas2, 20, sertraArmName, mainPhaseName, treatmentName, sertraFixedDose);
		org.drugis.addis.entities.data.Study study2 = 
				buildStudySkeleton(study2Name, study2Name, indicationName, endpointNames, adverseEventNames, popCharNames, arms2, epochs2, sas2);		
		addContinuousMeasurements(study2, omName);

		
		Arms arms3 = new Arms();
		Epochs epochs3 = new Epochs();
		StudyActivities sas3 = new StudyActivities();
		buildArmEpochTreatmentActivityCombination(arms3, epochs3, sas3, 20, sertraArmName, mainPhaseName, treatmentName, sertraFixedDose);
		buildArmEpochTreatmentActivityCombination(arms3, epochs3, sas3, 20, paroxArmName, mainPhaseName, treatmentName, paroxFixedDose);
		buildArmEpochTreatmentActivityCombination(arms3, epochs3, sas3, 20, combiArmName, mainPhaseName, treatmentName, combiFixedDose);
		org.drugis.addis.entities.data.Study study3 = 
				buildStudySkeleton(study3Name, study3Name, indicationName, endpointNames, adverseEventNames, popCharNames, arms3, epochs3, sas3);		
		addContinuousMeasurements(study3, omName);

		org.drugis.addis.entities.data.NetworkMetaAnalysis nma = new org.drugis.addis.entities.data.NetworkMetaAnalysis();
		nma.setName(name);		
		nma.setIndication(nameReference(indicationName));
		nma.setEndpoint(nameReference(ExampleData.buildEndpointCgi().getName()));
		
		List<org.drugis.addis.entities.data.Study> studiesl = new ArrayList<org.drugis.addis.entities.data.Study>();
		studiesl.add(study1);
		studiesl.add(study2);
		studiesl.add(study3);

		
		// Fluoxetine
		MetaAnalysisAlternative combi = new MetaAnalysisAlternative();
		if (combi.getTreatmentDefinition() == null) {
			combi.setTreatmentDefinition(createTrivialTreatmentDefinition(new String[] {ExampleData.buildDrugFluoxetine().getName(), ExampleData.buildDrugSertraline().getName() }));
		}
		AnalysisArms combiArms = new AnalysisArms();
		combiArms.getArm().add(JAXBConvertor.armReference(study1Name, arms1.getArm().get(0).getName())); // study 1
		combiArms.getArm().add(JAXBConvertor.armReference(study3Name, arms3.getArm().get(2).getName())); // study 3
		combi.setArms(combiArms);
		nma.getAlternative().add(combi);
		// Paroxetine		
		MetaAnalysisAlternative parox = new MetaAnalysisAlternative();
		if (parox.getTreatmentDefinition() == null) {
			parox.setTreatmentDefinition(createTrivialTreatmentDefinition(new String[] {ExampleData.buildDrugParoxetine().getName() }));
		}
		AnalysisArms paroxArms = new AnalysisArms();
		paroxArms.getArm().add(JAXBConvertor.armReference(study2Name, arms2.getArm().get(0).getName())); // study 2
		paroxArms.getArm().add(JAXBConvertor.armReference(study3Name, arms3.getArm().get(1).getName())); // study 3
		parox.setArms(paroxArms);
		nma.getAlternative().add(parox);
		// Sertraline
		MetaAnalysisAlternative setr = new MetaAnalysisAlternative();
		if (setr.getTreatmentDefinition() == null) {
			setr.setTreatmentDefinition(createTrivialTreatmentDefinition(new String[] {ExampleData.buildDrugSertraline().getName() }));
		}
		AnalysisArms sertrArms  = new AnalysisArms();
		sertrArms.getArm().add(JAXBConvertor.armReference(study1Name, arms1.getArm().get(1).getName())); // study 1
		sertrArms.getArm().add(JAXBConvertor.armReference(study2Name, arms2.getArm().get(1).getName())); // study 2
		sertrArms.getArm().add(JAXBConvertor.armReference(study3Name, arms3.getArm().get(0).getName())); // study 3
		setr.setArms(sertrArms);
		nma.getAlternative().add(setr);
				
		return new MetaAnalysisWithStudies(nma, studiesl);
	}

	private void buildArmEpochTreatmentActivityCombination(Arms arms,
			Epochs epochs, StudyActivities sas, int armSize,
			String armName, String mainPhaseName, String treatmentName,
			TreatmentActivity treatmentActivity) throws ConversionException {
		arms.getArm().add(buildArmData(armName, armSize));
		epochs.getEpoch().add(buildEpoch(mainPhaseName, EntityUtil.createDuration("P2D")));
		org.drugis.addis.entities.data.StudyActivity studyActivity = buildStudyActivity(treatmentName, treatmentActivity);
		studyActivity.getUsedBy().add(buildActivityUsedby(armName, mainPhaseName));
		sas.getStudyActivity().add(studyActivity);
	}

	@Test
	public void testConvertMetaAnalyses() throws NullPointerException,
			ConversionException, DatatypeConfigurationException {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);

		MetaAnalyses analyses = new MetaAnalyses();
		MetaAnalysisWithStudies ma1 = buildPairWiseMetaAnalysis("XXX");
		analyses.getPairwiseMetaAnalysisOrNetworkMetaAnalysis().add(ma1.d_pwma);
		MetaAnalysisWithStudies ma2 = buildNetworkMetaAnalysis("XXX 2");
		analyses.getPairwiseMetaAnalysisOrNetworkMetaAnalysis().add(ma2.d_nwma);

		addStudies(domain, ma1);
		addStudies(domain, ma2);

		List<MetaAnalysis> expected = new ArrayList<MetaAnalysis>();
		expected.add(JAXBConvertor.convertPairWiseMetaAnalysis(ma1.d_pwma,
				domain));
		expected.add(NetworkMetaAnalysisConverter.load(ma2.d_nwma,
				domain));

		assertEntityEquals(expected,
				JAXBConvertor.convertMetaAnalyses(analyses, domain));
		assertEquals(analyses, JAXBConvertor.convertMetaAnalyses(expected));
	}

	private void addStudies(Domain domain, MetaAnalysisWithStudies ma1)
			throws ConversionException {
		for (org.drugis.addis.entities.data.Study study : ma1.d_studies) {
			domain.getStudies().add(JAXBConvertor.convertStudy(study, domain));
		}
	}

	@Test
	public void testConvertDecisionContext() throws ConversionException,
			DatatypeConfigurationException {
		final String comparator = "Comparator bla";
		final String perspective = "Perspective";
		final String therapy = "Purely psychosomatic";
		final String horizon = "New horizons";

		DecisionContext entityContext = new DecisionContext();
		entityContext.setComparator(comparator);
		entityContext.setStakeholderPerspective(perspective);
		entityContext.setTherapeuticContext(therapy);
		entityContext.setTimeHorizon(horizon);

		org.drugis.addis.entities.data.DecisionContext dataContext = new org.drugis.addis.entities.data.DecisionContext();
		dataContext.setComparator(comparator);
		dataContext.setStakeholderPerspective(perspective);
		dataContext.setTherapeuticContext(therapy);
		dataContext.setTimeHorizon(horizon);

		assertEquals(dataContext,
				JAXBConvertor.convertDecisionContext(entityContext));
		assertEntityEquals(entityContext,
				JAXBConvertor.convertDecisionContext(dataContext));
	}

	@Test
	public void testConvertStudyBenefitRiskAnalysis()
			throws ConversionException, DatatypeConfigurationException {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		domain.getAdverseEvents().add(ExampleData.buildAdverseEventDiarrhea());

		String name = "BR Analysis";
		String[] adverseEvents = {
				ExampleData.buildAdverseEventDiarrhea().getName(),
				ExampleData.buildAdverseEventConvulsion().getName() };
		String[] endpoints = { ExampleData.buildEndpointCgi().getName(),
				ExampleData.buildEndpointHamd().getName() };
		String[] whichArms = { "parox arm high", "parox arm low" };
		Arms arms = new Arms();
		Epochs epochs = new Epochs();
		StudyActivities sas = new StudyActivities();

		TreatmentActivity fluoxTA = buildFixedDoseTreatmentActivity(
				ExampleData.buildDrugFluoxetine(), 13);
		TreatmentActivity paroxTAHigh = buildFixedDoseTreatmentActivity(
				ExampleData.buildDrugParoxetine(), 45);
		TreatmentActivity paroxTALow = buildFixedDoseTreatmentActivity(
				ExampleData.buildDrugParoxetine(), 12);
		buildArmEpochTreatmentActivityCombination(arms, epochs, sas, 12,
				"fluox arm", "Main phase", "Treatment", fluoxTA);
		buildArmEpochTreatmentActivityCombination(arms, epochs, sas, 23,
				"parox arm high", "Main phase", "Treatment", paroxTAHigh);
		buildArmEpochTreatmentActivityCombination(arms, epochs, sas, 11,
				"parox arm low", "Main phase", "Treatment", paroxTALow);

		org.drugis.addis.entities.data.Study study = buildStudySkeleton(
				"Study for Benefit-Risk", "HI", ExampleData
						.buildIndicationDepression().getName(), endpoints,
				adverseEvents, new String[] {}, arms, epochs, sas);

		DecisionContext entityContext = new DecisionContext();
		org.drugis.addis.entities.data.StudyBenefitRiskAnalysis br = buildStudyBR(
				name, study, endpoints, adverseEvents, whichArms[1], whichArms,
				entityContext);

		Study convertStudy = JAXBConvertor.convertStudy(study, domain);
		domain.getStudies().add(convertStudy);
		List<org.drugis.addis.entities.OutcomeMeasure> criteria = new ArrayList<org.drugis.addis.entities.OutcomeMeasure>();
		criteria.add(ExampleData.buildEndpointCgi());
		criteria.add(ExampleData.buildAdverseEventConvulsion());

		List<Arm> alternatives = new ArrayList<Arm>();
		alternatives.add(convertStudy.getArms().get(1));
		alternatives.add(convertStudy.getArms().get(2));

		StudyBenefitRiskAnalysis expected = new StudyBenefitRiskAnalysis(name,
				ExampleData.buildIndicationDepression(), convertStudy,
				criteria, alternatives.get(1), alternatives,
				AnalysisType.LyndOBrien, entityContext);

		assertEntityEquals(expected,
				JAXBConvertor.convertStudyBenefitRiskAnalysis(br, domain));
		assertEquals(br,
				JAXBConvertor.convertStudyBenefitRiskAnalysis(expected));
	}

	private org.drugis.addis.entities.data.StudyBenefitRiskAnalysis buildStudyBR(
			String name, org.drugis.addis.entities.data.Study study,
			String[] endpoints, String[] adverseEvents, String baseline,
			String[] whichArms, DecisionContext entityContext) {
		RateMeasurement rm = new RateMeasurement();
		rm.setRate(2);
		rm.setSampleSize(50);
		ContinuousMeasurement cm = new ContinuousMeasurement();
		cm.setMean(0.5);
		cm.setSampleSize(50);
		cm.setStdDev(1.0);
		for (String armName : whichArms) {
			for (String aeName : adverseEvents) {
				// FIXME: Magic numbers in endpoint indices (too tired)
				addRateMeasurement(rm, armName, "adverseEvent-"	+ aeName, study.getMeasurements().getMeasurement());
				addRateMeasurement(rm, armName,  "endpoint-" + endpoints[1], study.getMeasurements().getMeasurement());
				addContinuousMeasurement(cm, armName,  "endpoint-" + endpoints[0], study.getMeasurements().getMeasurement());
			}
		}

		org.drugis.addis.entities.data.StudyBenefitRiskAnalysis br = new org.drugis.addis.entities.data.StudyBenefitRiskAnalysis();
		br.setName(name);
		br.setIndication(nameReference(study.getIndication().getName()));
		br.setStudy(nameReference(study.getName()));
		br.setAnalysisType(AnalysisType.LyndOBrien);
		br.setOutcomeMeasures(new OutcomeMeasuresReferences());
		br.getOutcomeMeasures().getEndpoint().add(nameReference(endpoints[0]));
		br.getOutcomeMeasures().getAdverseEvent().add(nameReference(adverseEvents[1]));
		ArmReferences armRefs = new ArmReferences();
		for (String whichArm : whichArms) {
			armRefs.getArm().add(
					JAXBConvertor.armReference(study.getName(), whichArm));
		}
		br.setArms(armRefs);
		BaselineArmReference baselineRef = new BaselineArmReference();
		baselineRef.setArm(JAXBConvertor.armReference(study.getName(), baseline));
		br.setBaseline(baselineRef);
		if (entityContext != null) {
			br.setDecisionContext(JAXBConvertor
					.convertDecisionContext(entityContext));
		}
		return br;
	}

	@Test
	public void testConvertMetaBenefitRiskAnalysis()
			throws ConversionException, NullPointerException,
			IllegalArgumentException, EntityIdExistsException,
			DatatypeConfigurationException {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);

		String name = "Meta Benefit-Risk Analysis Test";
		// create entities
		String pairWiseName = "First Pair-Wise";
		String networkMetaName = "Second Network Meta Analysis";
		MetaAnalysisWithStudies ma1 = buildPairWiseMetaAnalysis(pairWiseName);
		MetaAnalysisWithStudies ma2 = buildNetworkMetaAnalysis(networkMetaName);

		// add to the domain
		addStudies(domain, ma1);
		addStudies(domain, ma2);
		RandomEffectsMetaAnalysis ma1ent = JAXBConvertor
				.convertPairWiseMetaAnalysis(ma1.d_pwma, domain);
		domain.getMetaAnalyses().add(ma1ent);
		MetaAnalysis ma2ent = NetworkMetaAnalysisConverter.load(
				ma2.d_nwma, domain);
		domain.getMetaAnalyses().add(ma2ent);

		// create BR analysis
		String[][] drugs = { new String[] { "Fluoxetine", "Sertraline" },
				new String[] { "Paroxetine" } };
		String indication = ma1.d_pwma.getIndication().getName();
		String[] meta = { pairWiseName, networkMetaName };
		DecisionContext entityContext = new DecisionContext();
		org.drugis.addis.entities.data.MetaBenefitRiskAnalysis br = buildMetaBR(
				name, drugs, indication, meta, entityContext);

		List<MetaAnalysis> metaList = new ArrayList<MetaAnalysis>();
		metaList.add(ma1ent);
		metaList.add(ma2ent);

		List<TreatmentDefinition> drugsEnt = new ArrayList<TreatmentDefinition>(
				ma1ent.getAlternatives());
		TreatmentDefinition baseline = drugsEnt.get(0);
		drugsEnt.remove(baseline);
		MetaBenefitRiskAnalysis expected = new MetaBenefitRiskAnalysis(name,
				ma1ent.getIndication(), metaList, baseline, drugsEnt,
				AnalysisType.SMAA, entityContext);
		assertEntityEquals(expected,
				JAXBConvertor.convertMetaBenefitRiskAnalysis(br, domain));
		assertEquals(br, JAXBConvertor.convertMetaBenefitRiskAnalysis(expected));
	}

	private org.drugis.addis.entities.data.MetaBenefitRiskAnalysis buildMetaBR(
			String name, String[][] drugs, String indication, String[] meta,
			DecisionContext entityContext) {
		org.drugis.addis.entities.data.MetaBenefitRiskAnalysis br = new org.drugis.addis.entities.data.MetaBenefitRiskAnalysis();
		br.setName(name);
		br.setAnalysisType(AnalysisType.SMAA);
		br.setIndication(nameReference(indication));
		Baseline baseline = new org.drugis.addis.entities.data.MetaBenefitRiskAnalysis.Baseline();
		org.drugis.addis.entities.data.TreatmentDefinition tc = createTrivialTreatmentDefinition(drugs[0]);
		baseline.setTreatmentDefinition(tc);
		br.setBaseline(baseline);
		br.setAlternatives(createAnalysisAlternative(drugs[0], drugs[1]));
		MetaAnalysisReferences mRefs = new MetaAnalysisReferences();
		for (String mName : meta) {
			mRefs.getMetaAnalysis().add(nameReference(mName));
		}
		br.setMetaAnalyses(mRefs);
		br.setDecisionContext(JAXBConvertor
				.convertDecisionContext(entityContext));
		return br;
	}

	private org.drugis.addis.entities.data.TreatmentDefinition createTrivialTreatmentDefinition(String[] drugs) {
		org.drugis.addis.entities.data.TreatmentDefinition tc = new org.drugis.addis.entities.data.TreatmentDefinition();
		for(String drug : drugs) { 
			TrivialCategory trivialCategory = new TrivialCategory();
			trivialCategory.setDrug(drug);
			tc.getRichCategoryOrTrivialCategory().add(trivialCategory);
		}
		return tc;
	}

	private org.drugis.addis.entities.data.MetaBenefitRiskAnalysis.Alternatives createAnalysisAlternative(String[] ... drugs) {
		Alternatives alternatives = new org.drugis.addis.entities.data.MetaBenefitRiskAnalysis.Alternatives();
		for(String[] drugPair : drugs) {
			org.drugis.addis.entities.data.TreatmentDefinition tc = new org.drugis.addis.entities.data.TreatmentDefinition();
			for(String drug : drugPair) {
				TrivialCategory trivial = new TrivialCategory();
				trivial.setDrug(drug);
				tc.getRichCategoryOrTrivialCategory().add(trivial);
			}
			alternatives.getTreatmentDefinition().add(tc);
		}
		return alternatives;
	}
	

	@Test
	public void testConvertBenefitRiskAnalyses() throws ConversionException,
			EntityIdExistsException, DatatypeConfigurationException {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		domain.getAdverseEvents().add(ExampleData.buildAdverseEventDiarrhea());

		String name = "BR Analysis";
		String[] adverseEvents = {
				ExampleData.buildAdverseEventDiarrhea().getName(),
				ExampleData.buildAdverseEventConvulsion().getName() };
		String[] endpoints = { ExampleData.buildEndpointCgi().getName(),
				ExampleData.buildEndpointHamd().getName() };
		Arms arms = new Arms();
		Epochs epochs = new Epochs();
		StudyActivities sas = new StudyActivities();

		TreatmentActivity fluoxTA = buildFixedDoseTreatmentActivity(
				ExampleData.buildDrugFluoxetine(), 13);
		TreatmentActivity paroxTAHigh = buildFixedDoseTreatmentActivity(
				ExampleData.buildDrugParoxetine(), 45);
		TreatmentActivity paroxTALow = buildFixedDoseTreatmentActivity(
				ExampleData.buildDrugParoxetine(), 12);
		buildArmEpochTreatmentActivityCombination(arms, epochs, sas, 12,
				"fluox arm", "Main phase", "Treatment", fluoxTA);
		buildArmEpochTreatmentActivityCombination(arms, epochs, sas, 23,
				"parox arm high", "Main phase", "Treatment", paroxTAHigh);
		buildArmEpochTreatmentActivityCombination(arms, epochs, sas, 11,
				"parox arm low", "Main phase", "Treatment", paroxTALow);

		org.drugis.addis.entities.data.Study study = buildStudySkeleton(
				"Study for Benefit-Risk", "HI", ExampleData
						.buildIndicationDepression().getName(), endpoints,
				adverseEvents, new String[] {}, arms, epochs, sas);

		String[] whichArms = { "parox arm high", "parox arm low" };
		org.drugis.addis.entities.data.StudyBenefitRiskAnalysis studyBR = buildStudyBR(
				name, study, endpoints, adverseEvents, whichArms[0], whichArms,
				null);

		Study convertStudy = JAXBConvertor.convertStudy(study, domain);
		domain.getStudies().add(convertStudy);

		String pwName = "pairwise MA";
		String nwName = "network MA";
		MetaAnalysisWithStudies pairWiseMetaAnalysis = buildPairWiseMetaAnalysis(pwName);
		MetaAnalysisWithStudies networkMetaAnalysis = buildNetworkMetaAnalysis(nwName);
		addStudies(domain, pairWiseMetaAnalysis);
		domain.getMetaAnalyses().add(
				JAXBConvertor.convertPairWiseMetaAnalysis(
						pairWiseMetaAnalysis.d_pwma, domain));
		addStudies(domain, networkMetaAnalysis);
		domain.getMetaAnalyses().add(
				NetworkMetaAnalysisConverter.load(
						networkMetaAnalysis.d_nwma, domain));

		org.drugis.addis.entities.data.MetaBenefitRiskAnalysis metaBR = buildMetaBR(
				"Meta BR", new String[][] {
						new String[] {
								ExampleData.buildDrugFluoxetine().getName(),
								ExampleData.buildDrugSertraline().getName() },
						new String[] { ExampleData.buildDrugParoxetine()
								.getName() } }, ExampleData
						.buildIndicationDepression().getName(), new String[] {
						nwName, pwName }, null);

		BenefitRiskAnalyses analyses = new BenefitRiskAnalyses();
		analyses.getStudyBenefitRiskAnalysisOrMetaBenefitRiskAnalysis().add(
				metaBR);
		analyses.getStudyBenefitRiskAnalysisOrMetaBenefitRiskAnalysis().add(
				studyBR);

		List<BenefitRiskAnalysis<?>> expected = new ArrayList<BenefitRiskAnalysis<?>>();
		expected.add(JAXBConvertor.convertMetaBenefitRiskAnalysis(metaBR,
				domain));
		expected.add(JAXBConvertor.convertStudyBenefitRiskAnalysis(studyBR,
				domain));

		assertEntityEquals(expected,
				JAXBConvertor.convertBenefitRiskAnalyses(analyses, domain));
		assertEquals(analyses,
				JAXBConvertor.convertBenefitRiskAnalyses(expected));
	}

	@Test
	public void testDefaultDataRoundTripConversion() throws Exception {
		doRoundTripTest(getTransformedDefaultData());
	}

	@Test
	public void testSmallerDataRoundTripConversion() throws Exception {
		// PubMedDataBankRetriever.copyStream(JAXBConvertor.transformToVersion(JAXBConvertorTest.class.getResourceAsStream(TEST_DATA_A_0),
		// 0, 2), System.out);
		doRoundTripTest(getTransformedTestData());
	}

	@Test
	@Ignore
	public void testMysteriousDataRoundTripConversion() throws Exception {
		doRoundTripTest(getTestData(TEST_DATA_A_1));
	}

	@Test
	public void testCombinationTreatmentRoundTripConversion() throws Exception {
		doRoundTripTest(getTestData(TEST_DATA_3));
	}

	public void doRoundTripTest(InputStream transformedXmlStream)
			throws JAXBException, ConversionException, SAXException, IOException {
		System.clearProperty("javax.xml.transform.TransformerFactory");
		AddisData data = (AddisData) d_unmarshaller
				.unmarshal(transformedXmlStream);
		sortMeasurements(data);
		sortAnalysisArms(data);
		sortBenefitRiskOutcomes(data);
		sortCategoricalMeasurementCategories(data);
		sortMetaAnalysisAlternatives(data);
		Domain domainData = JAXBConvertor.convertAddisDataToDomain(data);
		sortUsedBys(data);
		AddisData roundTrip = JAXBConvertor.convertDomainToAddisData(domainData);
		assertXMLSimilar(data, roundTrip);
	}

	private void sortMetaAnalysisAlternatives(AddisData data) {
		for (org.drugis.addis.entities.data.MetaAnalysis ma : data.getMetaAnalyses().getPairwiseMetaAnalysisOrNetworkMetaAnalysis()) {
			List<MetaAnalysisAlternative> alternatives;
			if (ma instanceof NetworkMetaAnalysis) {
				NetworkMetaAnalysis nma = (NetworkMetaAnalysis) ma;
				alternatives = nma.getAlternative();
			} else {
				PairwiseMetaAnalysis nma = (PairwiseMetaAnalysis) ma;
				alternatives = nma.getAlternative();
			}
			Collections.sort(alternatives, new MetaAnalysisAlternativeComparator());
		}
	}

	private static void assertXMLSimilar(AddisData expected, AddisData actual)
			throws JAXBException, SAXException, IOException {
		ByteArrayOutputStream os1 = new ByteArrayOutputStream();
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();

		d_marshaller.marshal(actual, os1);
		d_marshaller.marshal(expected, os2);
		Diff myDiff = new Diff(os1.toString(), os2.toString());
	    assertTrue("XML similar " + myDiff.toString(), myDiff.similar());
	}
	

	private void sortUsedBys(AddisData data) {
		for (org.drugis.addis.entities.data.Study s : data.getStudies()
				.getStudy()) {
			for (org.drugis.addis.entities.data.StudyActivity a : s
					.getActivities().getStudyActivity()) {
				Collections.sort(a.getUsedBy(), new UsedByComparator());
			}
		}
	}

	private class UsedByComparator implements
			Comparator<org.drugis.addis.entities.data.ActivityUsedBy> {
		public int compare(ActivityUsedBy o1, ActivityUsedBy o2) {
			if (o1.getArm().compareTo(o2.getArm()) != 0) {
				return o1.getArm().compareTo(o2.getArm());
			}
			return o1.getEpoch().compareTo(o2.getEpoch());
		}
	}

	private static void sortCategoricalMeasurementCategories(AddisData data) {
		for (org.drugis.addis.entities.data.Study s : data.getStudies()
				.getStudy()) {
			for (org.drugis.addis.entities.data.Measurement m : s
					.getMeasurements().getMeasurement()) {
				if (m.getCategoricalMeasurement() != null) {
					CategoricalVariable var = findVariable(data, s, m
							.getStudyOutcomeMeasure().getId());
					Collections.sort(m.getCategoricalMeasurement()
							.getCategory(), new CategoryMeasurementComparator(
							var));
				}
			}
		}
	}

	private static CategoricalVariable findVariable(AddisData data,
			org.drugis.addis.entities.data.Study s, String id) {
		String varName = null;
		for (org.drugis.addis.entities.data.StudyOutcomeMeasure som : s
				.getStudyOutcomeMeasures().getStudyOutcomeMeasure()) {
			if (som.getId().equals(id)) {
				varName = som.getPopulationCharacteristic().getName();
			}
		}
		for (OutcomeMeasure om : data.getPopulationCharacteristics()
				.getPopulationCharacteristic()) {
			if (om.getName().equals(varName)) {
				return om.getCategorical();
			}
		}
		return null;
	}

	private static void sortBenefitRiskOutcomes(AddisData data) {
		for (Object obj : data.getBenefitRiskAnalyses()
				.getStudyBenefitRiskAnalysisOrMetaBenefitRiskAnalysis()) {
			if (obj instanceof org.drugis.addis.entities.data.StudyBenefitRiskAnalysis) {
				org.drugis.addis.entities.data.StudyBenefitRiskAnalysis sbr = (org.drugis.addis.entities.data.StudyBenefitRiskAnalysis) obj;
				Collections.sort(sbr.getOutcomeMeasures().getAdverseEvent(),
						new NameReferenceComparator());
				Collections.sort(sbr.getOutcomeMeasures().getEndpoint(),
						new NameReferenceComparator());
			}
		}
	}

	private static void sortAnalysisArms(AddisData data) {
		for (org.drugis.addis.entities.data.MetaAnalysis ma : data
				.getMetaAnalyses()
				.getPairwiseMetaAnalysisOrNetworkMetaAnalysis()) {
			List<MetaAnalysisAlternative> alternatives = null;
			if (ma instanceof org.drugis.addis.entities.data.PairwiseMetaAnalysis) {
				org.drugis.addis.entities.data.PairwiseMetaAnalysis pwma = (org.drugis.addis.entities.data.PairwiseMetaAnalysis) ma;
				alternatives = pwma.getAlternative();
			}
			if (ma instanceof org.drugis.addis.entities.data.NetworkMetaAnalysis) {
				org.drugis.addis.entities.data.NetworkMetaAnalysis nwma = (org.drugis.addis.entities.data.NetworkMetaAnalysis) ma;
				alternatives = nwma.getAlternative();
			}
			for (MetaAnalysisAlternative a : alternatives) {
				Collections.sort(a.getArms().getArm(), new ArmComparator());
				Collections.sort(a.getTreatmentDefinition().getRichCategoryOrTrivialCategory(), new CategoryComparator());
			}
		}
	}
	
	private static void sortMeasurements(AddisData data) {
		for (org.drugis.addis.entities.data.Study s : data.getStudies()
				.getStudy()) {
			Collections.sort(s.getMeasurements().getMeasurement(),
					new MeasurementComparator(s));
		}
	}

	public static class CategoryMeasurementComparator implements
			Comparator<CategoryMeasurement> {
		private final CategoricalVariable d_var;

		public CategoryMeasurementComparator(CategoricalVariable var) {
			d_var = var;
		}

		public int compare(CategoryMeasurement o1, CategoryMeasurement o2) {
			return d_var.getCategory().indexOf(o1.getName())
					- d_var.getCategory().indexOf(o2.getName());
		}
	}

	public static class NameReferenceComparator implements
			Comparator<org.drugis.addis.entities.data.NameReference> {
		public int compare(org.drugis.addis.entities.data.NameReference o1,
				org.drugis.addis.entities.data.NameReference o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}
	
	public static class ArmComparator implements
			Comparator<org.drugis.addis.entities.data.ArmReference> {
		public int compare(ArmReference o1, ArmReference o2) {
			return o1.getStudy().compareTo(o2.getStudy());
		}
	}
	
	public static class CategoryComparator implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			return getDrugFromTrivialOrRichCategory(o1).compareTo(getDrugFromTrivialOrRichCategory(o2));
		}
	}
	
	public static String getDrugFromTrivialOrRichCategory(Object o1) {
		if (o1 instanceof TrivialCategory) {
			return ((TrivialCategory) o1).getDrug();
		} else {
			return ((TreatmentCategoryRef)o1).getDrug();
		}
	}
	
	public static class MeasurementComparator implements
			Comparator<org.drugis.addis.entities.data.Measurement> {
		private org.drugis.addis.entities.data.Study d_study;

		public MeasurementComparator(org.drugis.addis.entities.data.Study s) {
			d_study = s;
		}

		public int compare(org.drugis.addis.entities.data.Measurement o1,
				org.drugis.addis.entities.data.Measurement o2) {
			org.drugis.addis.entities.data.StudyOutcomeMeasure om1 = findOutcomeMeasure(o1
					.getStudyOutcomeMeasure().getId());
			org.drugis.addis.entities.data.StudyOutcomeMeasure om2 = findOutcomeMeasure(o2
					.getStudyOutcomeMeasure().getId());
			if (!om1.equals(om2)) {
				return compareOutcomeMeasure(om1, om2);
			}
			if (o1.getArm() == null) {
				return o2.getArm() == null ? 0 : 1;
			}
			if (o2.getArm() == null) {
				return -1;
			}
			return o1.getArm().getName().compareTo(o2.getArm().getName());
		}

		private static int compareOutcomeMeasure(
				org.drugis.addis.entities.data.StudyOutcomeMeasure om1,
				org.drugis.addis.entities.data.StudyOutcomeMeasure om2) {
			if (om1.getEndpoint() != null) {
				return (om2.getEndpoint() == null ? -1 : compareOutcomeId(om1,
						om2));
			} else if (om1.getAdverseEvent() != null) {
				if (om2.getEndpoint() != null) {
					return 1;
				}
				return (om2.getAdverseEvent() == null ? -1 : compareOutcomeId(
						om1, om2));
			} else {
				return om2.getPopulationCharacteristic() == null ? 1
						: compareOutcomeId(om1, om2);
			}
		}

		private static int compareOutcomeId(
				org.drugis.addis.entities.data.StudyOutcomeMeasure om1,
				org.drugis.addis.entities.data.StudyOutcomeMeasure om2) {
			return om1.getId().compareTo(om2.getId());
		}

		private org.drugis.addis.entities.data.StudyOutcomeMeasure findOutcomeMeasure(
				String id) {
			List<org.drugis.addis.entities.data.StudyOutcomeMeasure> oms = d_study
					.getStudyOutcomeMeasures().getStudyOutcomeMeasure();
			for (org.drugis.addis.entities.data.StudyOutcomeMeasure om : oms) {
				if (om.getId().equals(id)) {
					return om;
				}
			}
			return null;
		}
	}

	@Test
	public void testDateWithNotes() {
		String date = "2010-11-12";
		Date oldXmlDate = new GregorianCalendar(2010, 11 - 1, 12).getTime();
		DateWithNotes dwn = JAXBConvertor.dateWithNotes(oldXmlDate);

		XMLGregorianCalendar cal = XMLGregorianCalendarImpl.parse(date);
		DateWithNotes dwn2 = new DateWithNotes();
		dwn2.setNotes(new Notes());
		dwn2.setValue(cal);

		assertEquals(dwn, dwn2);
	}

	@Test
	public void writeTransformedXML() throws TransformerException, IOException {
		InputStream transformedXmlStream = getTransformedDefaultData();
		FileOutputStream output = new FileOutputStream(
				"transformedDefaultData.xml");
		PubMedDataBankRetriever.copyStream(transformedXmlStream, output);
		output.close();
	}

	public static InputStream getTestData(String fileName)
			throws TransformerException, IOException {
		InputStream is = JAXBConvertorTest.class.getResourceAsStream(fileName);
		XmlFormatType xmlType = JAXBHandler.determineXmlType(is);
		int version = 0;
		if (xmlType.isFuture()) {
			throw new RuntimeException("XML Version from the future");
		}
		if (xmlType.isLegacy()) {
			is = JAXBConvertor.transformLegacyXML(is);
			version = 1;
		} else {
			version = xmlType.getVersion();
		}
		return JAXBConvertor.transformToLatest(is, version);
	}

	private static InputStream getTransformedDefaultData()
			throws TransformerException, IOException {
		return getTestData(TEST_DATA_PATH +  "defaultData.addis");
	}

	private static InputStream getTransformedTestData()
			throws TransformerException, IOException {
		return getTestData(TEST_DATA_A_0);
	}
}
