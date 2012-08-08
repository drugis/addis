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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.AbstractNamedEntity;
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
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.MeasurementKey;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.ObjectWithNotes;
import org.drugis.addis.entities.OtherActivity;
import org.drugis.addis.entities.OutcomeMeasure;
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
import org.drugis.addis.entities.Unit;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.WhenTaken;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.DecisionContext;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.data.ActivityUsedBy;
import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.entities.data.AdverseEvents;
import org.drugis.addis.entities.data.AnalysisArms;
import org.drugis.addis.entities.data.ArmReference;
import org.drugis.addis.entities.data.ArmReferences;
import org.drugis.addis.entities.data.BaselineArmReference;
import org.drugis.addis.entities.data.BenefitRiskAnalyses;
import org.drugis.addis.entities.data.CategoricalMeasurement;
import org.drugis.addis.entities.data.CategoricalVariable;
import org.drugis.addis.entities.data.CategoryMeasurement;
import org.drugis.addis.entities.data.Characteristics;
import org.drugis.addis.entities.data.ContinuousMeasurement;
import org.drugis.addis.entities.data.ContinuousVariable;
import org.drugis.addis.entities.data.DateWithNotes;
import org.drugis.addis.entities.data.Drugs;
import org.drugis.addis.entities.data.Endpoints;
import org.drugis.addis.entities.data.IdReference;
import org.drugis.addis.entities.data.Indications;
import org.drugis.addis.entities.data.IntegerWithNotes;
import org.drugis.addis.entities.data.Measurements;
import org.drugis.addis.entities.data.MetaAnalyses;
import org.drugis.addis.entities.data.MetaAnalysisAlternative;
import org.drugis.addis.entities.data.MetaAnalysisReferences;
import org.drugis.addis.entities.data.MetaBenefitRiskAnalysis.Baseline;
import org.drugis.addis.entities.data.NameReference;
import org.drugis.addis.entities.data.NameReferenceWithNotes;
import org.drugis.addis.entities.data.Notes;
import org.drugis.addis.entities.data.OutcomeMeasuresReferences;
import org.drugis.addis.entities.data.PairwiseMetaAnalysis;
import org.drugis.addis.entities.data.PopulationCharacteristics;
import org.drugis.addis.entities.data.RateMeasurement;
import org.drugis.addis.entities.data.RateVariable;
import org.drugis.addis.entities.data.References;
import org.drugis.addis.entities.data.RelativeTime;
import org.drugis.addis.entities.data.StringIdReference;
import org.drugis.addis.entities.data.StringWithNotes;
import org.drugis.addis.entities.data.Studies;
import org.drugis.addis.entities.data.StudyActivities;
import org.drugis.addis.entities.data.StudyOutcomeMeasures;
import org.drugis.addis.entities.data.TreatmentCategorizations;
import org.drugis.addis.entities.data.Units;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.util.jaxb.JAXBHandler.XmlFormatType;
import org.drugis.common.Interval;
import org.drugis.common.beans.SortedSetModel;

public class JAXBConvertor {
	@SuppressWarnings("serial")
	public static class ConversionException extends Exception {
		public ConversionException(String msg) {
			super(msg);
		}
		
		public ConversionException(String msg, Throwable cause) {
			super(msg, cause);
		}
	}
	
	private JAXBConvertor() {}
	
	public static Domain convertAddisDataToDomain(AddisData addisData) throws ConversionException {
		Domain newDomain = new org.drugis.addis.entities.DomainImpl();
		for (org.drugis.addis.entities.data.Unit u : addisData.getUnits().getUnit()) {
			Unit unit = new Unit(u.getName(), u.getSymbol());
			if (!newDomain.getUnits().contains(unit)) {
				newDomain.getUnits().add(unit);
			}
		}
		for (org.drugis.addis.entities.data.Indication i : addisData.getIndications().getIndication()) {
			newDomain.getIndications().add(convertIndication(i));
		}
		for (org.drugis.addis.entities.data.Drug d : addisData.getDrugs().getDrug()) {
			newDomain.getDrugs().add(convertDrug(d));
		}
		for (org.drugis.addis.entities.data.TreatmentCategorization t : addisData.getTreatmentCategorizations().getTreatmentCategorization()) {
			newDomain.getTreatmentCategorizations().add(TreatmentCategorizationsConverter.load(t, newDomain));
		}
		for (org.drugis.addis.entities.data.OutcomeMeasure om : addisData.getEndpoints().getEndpoint()) {
			newDomain.getEndpoints().add(convertEndpoint(om));
		}
		for(org.drugis.addis.entities.data.OutcomeMeasure ae : addisData.getAdverseEvents().getAdverseEvent()) {
			newDomain.getAdverseEvents().add(convertAdverseEvent(ae));
		}
		for(org.drugis.addis.entities.data.OutcomeMeasure ae : addisData.getPopulationCharacteristics().getPopulationCharacteristic()) {
			newDomain.getPopulationCharacteristics().add(((PopulationCharacteristic) convertPopulationCharacteristic(ae)));
		}
		for(org.drugis.addis.entities.data.Study s : addisData.getStudies().getStudy()) {
			newDomain.getStudies().add(convertStudy(s, newDomain));
		}
		// Meta-analyses
		for(MetaAnalysis ma : convertMetaAnalyses(addisData.getMetaAnalyses(), newDomain)) {
			newDomain.getMetaAnalyses().add(ma);
		}
		// Benefit-risk analyses
		for(BenefitRiskAnalysis<?> br : convertBenefitRiskAnalyses(addisData.getBenefitRiskAnalyses(), newDomain)) {
			newDomain.getBenefitRiskAnalyses().add(br);
		}
		return newDomain;	
	}

	public static AddisData convertDomainToAddisData(Domain domain) throws ConversionException {
		AddisData addisData = new AddisData();
		addisData.setUnits(new Units());
		for (Unit u : domain.getUnits()) {
			addisData.getUnits().getUnit().add(convertUnit(u));
		}
		addisData.setIndications(new Indications());
		for (Indication i : domain.getIndications()) {
			addisData.getIndications().getIndication().add(convertIndication(i));
		}
		addisData.setDrugs(new Drugs());
		for (Drug d : domain.getDrugs()) {
			addisData.getDrugs().getDrug().add(convertDrug(d));
		}
		addisData.setTreatmentCategorizations(new TreatmentCategorizations());
		for(TreatmentCategorization t : domain.getTreatmentCategorizations()) { 
			addisData.getTreatmentCategorizations().getTreatmentCategorization().add(TreatmentCategorizationsConverter.save(t));
		}
		addisData.setEndpoints(new Endpoints());
		for (Endpoint e : domain.getEndpoints()) {
			addisData.getEndpoints().getEndpoint().add(convertEndpoint(e));
		}
		addisData.setAdverseEvents(new AdverseEvents());
		for (OutcomeMeasure e : domain.getAdverseEvents()) {
			addisData.getAdverseEvents().getAdverseEvent().add(convertAdverseEvent(e));
		}
		addisData.setPopulationCharacteristics(new PopulationCharacteristics());
		for (PopulationCharacteristic e : domain.getPopulationCharacteristics()) {
			addisData.getPopulationCharacteristics().getPopulationCharacteristic().add(convertPopulationCharacteristic(e));
		}
		addisData.setStudies(new Studies());
		for (Study s : domain.getStudies()) {
			addisData.getStudies().getStudy().add(convertStudy(s));
		}
		List<MetaAnalysis> metaAnalyses = new ArrayList<MetaAnalysis>(domain.getMetaAnalyses());
		addisData.setMetaAnalyses(convertMetaAnalyses(metaAnalyses));
		ArrayList<BenefitRiskAnalysis<?>> brAnalyses = new ArrayList<BenefitRiskAnalysis<?>>(domain.getBenefitRiskAnalyses());
		addisData.setBenefitRiskAnalyses(convertBenefitRiskAnalyses(brAnalyses));
		
		return addisData;
	}
	
	private static org.drugis.addis.entities.data.Unit convertUnit(Unit u) {
		org.drugis.addis.entities.data.Unit newUnit = new org.drugis.addis.entities.data.Unit();
		newUnit.setName(u.getName());
		newUnit.setSymbol(u.getSymbol());
		return newUnit;
	}

	static AdverseEvent convertAdverseEvent(org.drugis.addis.entities.data.OutcomeMeasure ae) throws ConversionException {
		AdverseEvent a = new AdverseEvent();
		convertOutcomeMeasure(ae, a);
		return a;
	}

	static org.drugis.addis.entities.data.OutcomeMeasure convertAdverseEvent(OutcomeMeasure a) throws ConversionException {
		return convertOutcomeMeasure(a);
	}

	static Indication convertIndication(
			org.drugis.addis.entities.data.Indication i) {
		return new Indication(i.getCode(), i.getName());
	}
	
	static org.drugis.addis.entities.data.Indication convertIndication(Indication i) {
		org.drugis.addis.entities.data.Indication ind = new org.drugis.addis.entities.data.Indication();
		ind.setCode(i.getCode());
		ind.setName(i.getName());
		return ind;
	}

	static Drug convertDrug(org.drugis.addis.entities.data.Drug d) {
		return new Drug(d.getName(), d.getAtcCode());
	}
	
	static org.drugis.addis.entities.treatment.TreatmentCategorization convertTreatmentCategorization(
			org.drugis.addis.entities.data.TreatmentCategorization t,
			Domain domain) throws ConversionException {
		return TreatmentCategorizationsConverter.load(t, domain);	
	}
	
	static org.drugis.addis.entities.data.TreatmentCategorization convertTreatmentCategorization(org.drugis.addis.entities.treatment.TreatmentCategorization t) {
		return TreatmentCategorizationsConverter.save(t);	
	}

	public static org.drugis.addis.entities.data.Drug convertDrug(Drug d) {
		org.drugis.addis.entities.data.Drug drug = new org.drugis.addis.entities.data.Drug();
		drug.setName(d.getName());
		drug.setAtcCode(d.getAtcCode());
		return drug;
	}
	
	static Endpoint convertEndpoint(org.drugis.addis.entities.data.OutcomeMeasure om) throws ConversionException {
		Endpoint e = new Endpoint();
		convertOutcomeMeasure(om, e);
		return e;
	}

	static org.drugis.addis.entities.data.OutcomeMeasure convertEndpoint(Endpoint e) throws ConversionException {
		return convertOutcomeMeasure(e);
	}

	private static org.drugis.addis.entities.data.OutcomeMeasure convertOutcomeMeasure(Variable o) throws ConversionException {
		org.drugis.addis.entities.data.OutcomeMeasure om = new org.drugis.addis.entities.data.OutcomeMeasure();
		om.setDescription(o.getDescription());
		om.setName(o.getName());
		if(o instanceof OutcomeMeasure) {
			om.setDirection(((OutcomeMeasure) o).getDirection());
		}
		if (o.getVariableType() instanceof CategoricalVariableType) {
			CategoricalVariableType cat = (CategoricalVariableType) o.getVariableType();
			CategoricalVariable varCat = new CategoricalVariable();
			varCat.getCategory().addAll(cat.getCategories());
			om.setCategorical(varCat);
		} else if (o.getVariableType() instanceof ContinuousVariableType) {
			ContinuousVariable varC = new ContinuousVariable();
			varC.setUnitOfMeasurement(((ContinuousVariableType) o.getVariableType()).getUnitOfMeasurement());
			om.setContinuous(varC);
		} else if (o.getVariableType() instanceof RateVariableType) {
			RateVariable varR = new RateVariable();
			om.setRate(varR);
		} else {
			throw new ConversionException("Variable type " + o.getVariableType() + " unknown");
		}
		
		return om;
	}
	
	private static void convertOutcomeMeasure(org.drugis.addis.entities.data.OutcomeMeasure from, org.drugis.addis.entities.OutcomeMeasure to)
	throws ConversionException {
		to.setName(from.getName());
		to.setDescription(from.getDescription());
		if (from.getCategorical() != null) {
			throw(new ConversionException("Endpoints should not be categorical (yet)"));
		} else if (from.getContinuous() != null) {
			ContinuousVariableType type = new ContinuousVariableType(from.getContinuous().getUnitOfMeasurement());
			to.setVariableType(type);
			to.setDirection(from.getDirection());
		} else if (from.getRate() != null) {
			to.setVariableType(new RateVariableType());
			to.setDirection(from.getDirection());
		}
	}
	
	public static <T extends AbstractNamedEntity<?>> T findNamedItem(Collection<T> items, String name) {
		for (T t: items) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		return null;
	}
	
	public static void convertNotes(List<org.drugis.addis.entities.data.Note> source, List<Note> target) {
		for(org.drugis.addis.entities.data.Note note : source) {
			target.add(convertNote(note));
		}
	}
	
	public static void convertOldNotes(List<Note> source, List<org.drugis.addis.entities.data.Note> target) {
		for(Note note : source) {
			target.add(convertNote(note));
		}
	}
	

	public static StudyActivity convertStudyActivity(org.drugis.addis.entities.data.StudyActivity saData, Study s, Domain domain) throws ConversionException {
		StudyActivity newStudyActivity = new StudyActivity(saData.getName(), convertActivity(saData.getActivity(), domain));
		
		List<ActivityUsedBy> usedByData = saData.getUsedBy();
		Set<UsedBy> usedBy = new HashSet<UsedBy>(newStudyActivity.getUsedBy());
		for(ActivityUsedBy aub: usedByData) {
			usedBy.add(convertUsedBy(aub, s));
		}
		newStudyActivity.setUsedBy(usedBy);
		return newStudyActivity;
	}
	
	private static UsedBy convertUsedBy(ActivityUsedBy aub, Study s) throws ConversionException {
		try {
			Arm a = findArm(aub.getArm(), s);
			Epoch e = findEpoch(aub.getEpoch(), s);
			return new UsedBy(a, e);
		} catch (ConversionException e) {
			throw new ConversionException("Could not parse activities in study \"" + s + "\"", e);
		}
	}

	static Activity convertActivity(org.drugis.addis.entities.data.Activity activity, Domain domain) throws ConversionException {
		if (activity.getPredefined() != null) {
			return activity.getPredefined();
		} else if (activity.getTreatment() != null) {
			return convertTreatmentActivity(activity.getTreatment(), domain);
		} else if (activity.getOther() != null) {
			return new OtherActivity(activity.getOther());
		} else {
			throw new ConversionException("Unknown Activity type " + activity);
		}
	}


	public static org.drugis.addis.entities.data.Activity convertActivity(Activity activity) throws ConversionException {
		org.drugis.addis.entities.data.Activity converted = new org.drugis.addis.entities.data.Activity();
		if (activity instanceof PredefinedActivity) {
			converted.setPredefined((PredefinedActivity) activity);
		} else if (activity instanceof TreatmentActivity){
			converted.setTreatment(convertCombinationTreatment((TreatmentActivity) activity));
		} else if (activity instanceof OtherActivity) {
			converted.setOther(((OtherActivity) activity).getDescription());
		} else {
			throw new ConversionException("Unknown Activity type " + activity);
		}
		return converted;
	}

	public static org.drugis.addis.entities.data.StudyActivity convertStudyActivity(StudyActivity sa) throws ConversionException {
		org.drugis.addis.entities.data.StudyActivity newActivity = new org.drugis.addis.entities.data.StudyActivity();
		newActivity.setName(sa.getName());
		newActivity.setActivity(convertActivity(sa.getActivity()));
		for(UsedBy ub : new TreeSet<UsedBy>(sa.getUsedBy())) {
			newActivity.getUsedBy().add(convertUsedBy(ub));
		}
		newActivity.setNotes(new Notes());
		return newActivity;
	}
	
	private static ActivityUsedBy convertUsedBy(UsedBy ub) {
		ActivityUsedBy aub = new ActivityUsedBy();
		aub.setArm(ub.getArm().getName());
		aub.setEpoch(ub.getEpoch().getName());
		return aub;
	}

	static DrugTreatment convertDrugTreatment(org.drugis.addis.entities.data.DrugTreatment t, Domain domain) throws ConversionException {
		Drug drug = findNamedItem(domain.getDrugs(), t.getDrug().getName());
		AbstractDose dose;
		if (t.getFixedDose() != null) {
			dose = new FixedDose(t.getFixedDose().getQuantity(), convertDoseUnit(t.getFixedDose().getDoseUnit(), domain));
		} else if (t.getFlexibleDose() != null) {
			dose = new FlexibleDose(new Interval<Double>(t.getFlexibleDose().getMinDose(), t.getFlexibleDose().getMaxDose()), 
					convertDoseUnit(t.getFlexibleDose().getDoseUnit(), domain));
		} else {
			throw new ConversionException("Unknown dose type " + t );
		}
		DrugTreatment newT = new DrugTreatment(drug, dose);
		return newT;
	}
	
	
	public static DoseUnit convertDoseUnit(org.drugis.addis.entities.data.DoseUnit doseUnit, Domain domain) {
		Unit findNamedItem = findNamedItem(domain.getUnits(), doseUnit.getUnit().getName());
		return new DoseUnit(findNamedItem, doseUnit.getScaleModifier(), doseUnit.getPerTime());
	}

	static Activity convertTreatmentActivity(org.drugis.addis.entities.data.Treatment treatment, Domain domain) throws ConversionException {
		TreatmentActivity newCombinationTreatment = new TreatmentActivity();
		for(org.drugis.addis.entities.data.DrugTreatment ct : treatment.getDrugTreatment()) {
			newCombinationTreatment.getTreatments().add(convertDrugTreatment(ct, domain));
		}
		return newCombinationTreatment;
	}
	
	private static org.drugis.addis.entities.data.DrugTreatment convertDrugTreatmentActivity(DrugTreatment ta)  throws ConversionException {
		org.drugis.addis.entities.data.DrugTreatment t = new org.drugis.addis.entities.data.DrugTreatment();
		t.setDrug(nameReference(ta.getDrug().getName()));
		if (ta.getDose() instanceof FixedDose) {
			t.setFixedDose(convertFixedDose((FixedDose) ta.getDose()));
		} else if (ta.getDose() instanceof FlexibleDose) {
			t.setFlexibleDose(convertFlexibleDose((FlexibleDose) ta.getDose()));
		} else {
			throw new ConversionException("Unknown dose type " + ta.getDose());
		}
		return t;
	}
	

	private static org.drugis.addis.entities.data.Treatment convertCombinationTreatment(TreatmentActivity activity) throws ConversionException {
		org.drugis.addis.entities.data.Treatment ct = new org.drugis.addis.entities.data.Treatment();
		for(DrugTreatment ta : activity.getTreatments()) {
			ct.getDrugTreatment().add(convertDrugTreatmentActivity(ta));
		}
		return ct;
	}

	static org.drugis.addis.entities.data.Epoch convertEpoch(Epoch e) {
		org.drugis.addis.entities.data.Epoch newEpoch = new org.drugis.addis.entities.data.Epoch();
		newEpoch.setName(e.getName());
		newEpoch.setDuration(e.getDuration());
		newEpoch.setNotes(new Notes());
		convertOldNotes(e.getNotes(), newEpoch.getNotes().getNote());
		return newEpoch ;
	}

	static Epoch convertEpoch(org.drugis.addis.entities.data.Epoch e) {
		Epoch newEpoch = new Epoch(e.getName(), e.getDuration());
		convertNotes(e.getNotes().getNote(), newEpoch.getNotes());
		return newEpoch;
	}

	
	static Arm convertArm(org.drugis.addis.entities.data.Arm arm) throws ConversionException {
		Arm newArm = new Arm(arm.getName(), arm.getSize());
		convertNotes(arm.getNotes().getNote(), newArm.getNotes());
		return newArm;
	}
	
	static org.drugis.addis.entities.data.Arm convertArm(Arm arm) throws ConversionException {
		org.drugis.addis.entities.data.Arm newArm = new org.drugis.addis.entities.data.Arm();
		
		newArm.setName(arm.getName());
		newArm.setSize(arm.getSize());
		
		newArm.setNotes(new Notes());
		convertOldNotes(arm.getNotes(), newArm.getNotes().getNote());
	
		return newArm;
	}

	private static org.drugis.addis.entities.data.FlexibleDose convertFlexibleDose(FlexibleDose dose) {
		org.drugis.addis.entities.data.FlexibleDose newDose = new org.drugis.addis.entities.data.FlexibleDose();
		newDose.setDoseUnit(convertDoseUnit(dose.getDoseUnit()));
		newDose.setMinDose(dose.getMinDose());
		newDose.setMaxDose(dose.getMaxDose());
		return newDose;
	}

	public static org.drugis.addis.entities.data.DoseUnit convertDoseUnit(DoseUnit unit) {
		org.drugis.addis.entities.data.DoseUnit du = new org.drugis.addis.entities.data.DoseUnit();
		du.setUnit(nameReference(unit.getUnit().getName()));
		du.setScaleModifier(unit.getScaleModifier());
		du.setPerTime(unit.getPerTime());
		return du;
	}

	private static org.drugis.addis.entities.data.FixedDose convertFixedDose(FixedDose dose) {
		org.drugis.addis.entities.data.FixedDose newDose = new org.drugis.addis.entities.data.FixedDose();
		newDose.setQuantity(dose.getQuantity());
		newDose.setDoseUnit(convertDoseUnit(dose.getDoseUnit()));
		return newDose;
	}

	static Variable convertPopulationCharacteristic(org.drugis.addis.entities.data.OutcomeMeasure m) throws ConversionException {
		PopulationCharacteristic pc = null;
		if(m.getRate() != null) {
			pc =  new PopulationCharacteristic("", new RateVariableType());
		}
		if(m.getContinuous() != null) {
			pc = new PopulationCharacteristic("", new ContinuousVariableType());
			((ContinuousVariableType)pc.getVariableType()).setUnitOfMeasurement(m.getContinuous().getUnitOfMeasurement());
		}
		if(m.getCategorical() != null) {
			pc = new PopulationCharacteristic("", new CategoricalVariableType());
			((CategoricalVariableType)pc.getVariableType()).getCategories().addAll(m.getCategorical().getCategory());
		}
		
		if (pc == null) {
			throw new ConversionException("Unknown variable type");
		}
		
		pc.setName(m.getName());
		pc.setDescription(m.getDescription());
		return pc;
	}
	
	static org.drugis.addis.entities.data.OutcomeMeasure convertPopulationCharacteristic(PopulationCharacteristic pc) throws ConversionException {
		return convertOutcomeMeasure(pc);
	}
	
	private static PubMedIdList getPubMedIds(References refs) {
		PubMedIdList pubMedList = new PubMedIdList();
		for(BigInteger ref : refs.getPubMedId()) {
			pubMedList.add(new PubMedId(ref.toString()));
		}
		return pubMedList;
	}
	
	static ObjectWithNotes<Object> objectWithNotes(Object obj, Notes notes) {
		ObjectWithNotes<Object> objWithNotes = new ObjectWithNotes<Object>(obj);
		convertNotes(notes.getNote() , objWithNotes.getNotes());
		return objWithNotes;
	}

	public static CharacteristicsMap convertStudyCharacteristics(Characteristics chars1) {
		CharacteristicsMap map = new CharacteristicsMap();
		if (chars1.getAllocation() != null) {
			map.put(BasicStudyCharacteristic.ALLOCATION, objectWithNotes(chars1.getAllocation().getValue(), chars1.getAllocation().getNotes()));
		}
		if (chars1.getBlinding() != null) {
			map.put(BasicStudyCharacteristic.BLINDING, objectWithNotes(chars1.getBlinding().getValue(), chars1.getBlinding().getNotes()));
		}
		if (chars1.getCenters() != null) {
			map.put(BasicStudyCharacteristic.CENTERS, objectWithNotes(chars1.getCenters().getValue(), chars1.getCenters().getNotes()));
		}
		if (chars1.getObjective() != null) {
			map.put(BasicStudyCharacteristic.OBJECTIVE, objectWithNotes(chars1.getObjective().getValue(), chars1.getObjective().getNotes()));
		}
		if (chars1.getStudyStart() != null) {
			map.put(BasicStudyCharacteristic.STUDY_START, objectWithNotes(xmlToDate(chars1.getStudyStart().getValue()), chars1.getStudyStart().getNotes()));
		}
		if (chars1.getStudyEnd() != null) {
			map.put(BasicStudyCharacteristic.STUDY_END, objectWithNotes(xmlToDate(chars1.getStudyEnd().getValue()), chars1.getStudyEnd().getNotes()));
		}
		if (chars1.getInclusion() != null) {
			map.put(BasicStudyCharacteristic.INCLUSION, objectWithNotes(chars1.getInclusion().getValue(), chars1.getInclusion().getNotes()));
		}
		if (chars1.getExclusion() != null) {
				map.put(BasicStudyCharacteristic.EXCLUSION, objectWithNotes(chars1.getExclusion().getValue(), chars1.getExclusion().getNotes()));
		}
		if (chars1.getStatus() != null) {
			map.put(BasicStudyCharacteristic.STATUS, objectWithNotes(chars1.getStatus().getValue(), chars1.getStatus().getNotes()));
		}
		if (chars1.getSource() != null) {
			map.put(BasicStudyCharacteristic.SOURCE, objectWithNotes(chars1.getSource().getValue(), chars1.getSource().getNotes()));
		}
		if (chars1.getCreationDate() != null) {
			map.put(BasicStudyCharacteristic.CREATION_DATE, objectWithNotes(xmlToDate(chars1.getCreationDate().getValue()), chars1.getCreationDate().getNotes()));
		}
		map.put(BasicStudyCharacteristic.TITLE, objectWithNotes(chars1.getTitle().getValue(), chars1.getTitle().getNotes()));
		map.put(BasicStudyCharacteristic.PUBMED, new ObjectWithNotes<Object>(getPubMedIds(chars1.getReferences())));
		return map;
	}	

	private static Date xmlToDate(XMLGregorianCalendar value) {
		return value == null ? null : value.toGregorianCalendar().getTime();
	}

	public static XMLGregorianCalendar dateToXml(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = dateFormat.format(date);
		try {
			XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(strDate);
			return xmlDate;
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static Characteristics convertStudyCharacteristics(CharacteristicsMap characteristics) {
		Characteristics newChars = new Characteristics();
		if (inMap(characteristics, BasicStudyCharacteristic.ALLOCATION)) {
			org.drugis.addis.entities.data.Allocation allocationWithNotes = allocationWithNotes((Allocation) characteristics.get(BasicStudyCharacteristic.ALLOCATION).getValue());
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.ALLOCATION).getNotes() , allocationWithNotes.getNotes().getNote());
			newChars.setAllocation(allocationWithNotes);
		}
		if (inMap(characteristics, BasicStudyCharacteristic.BLINDING)) {
			org.drugis.addis.entities.data.Blinding blindingWithNotes = blindingWithNotes((Blinding) characteristics.get(BasicStudyCharacteristic.BLINDING).getValue());
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.BLINDING).getNotes(), blindingWithNotes.getNotes().getNote());
			newChars.setBlinding(blindingWithNotes);
		}
		if (inMap(characteristics, BasicStudyCharacteristic.CENTERS)) {
			IntegerWithNotes intWithNotes = intWithNotes((Integer) characteristics.get(BasicStudyCharacteristic.CENTERS).getValue());
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.CENTERS).getNotes(), intWithNotes.getNotes().getNote());
			newChars.setCenters(intWithNotes);
		}
		if (inMap(characteristics, BasicStudyCharacteristic.CREATION_DATE)) {
			DateWithNotes dateWithNotes = dateWithNotes((Date) characteristics.get(BasicStudyCharacteristic.CREATION_DATE).getValue());
			newChars.setCreationDate(dateWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.CREATION_DATE).getNotes(), dateWithNotes.getNotes().getNote());
		}
		if (inMap(characteristics, BasicStudyCharacteristic.EXCLUSION)) {
			StringWithNotes stringWithNotes = stringWithNotes((String) characteristics.get(BasicStudyCharacteristic.EXCLUSION).getValue());
			newChars.setExclusion(stringWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.EXCLUSION).getNotes(), stringWithNotes.getNotes().getNote());
		}
		if (inMap(characteristics, BasicStudyCharacteristic.INCLUSION)) {
			StringWithNotes stringWithNotes = stringWithNotes((String) characteristics.get(BasicStudyCharacteristic.INCLUSION).getValue());
			newChars.setInclusion(stringWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.INCLUSION).getNotes(), stringWithNotes.getNotes().getNote());
		}
		if (inMap(characteristics, BasicStudyCharacteristic.OBJECTIVE)) {
			StringWithNotes stringWithNotes = stringWithNotes((String) characteristics.get(BasicStudyCharacteristic.OBJECTIVE).getValue());
			newChars.setObjective(stringWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.OBJECTIVE).getNotes(), stringWithNotes.getNotes().getNote());
		}
		if (inMap(characteristics, BasicStudyCharacteristic.STATUS)) {
			org.drugis.addis.entities.data.Status statusWithNotes = statusWithNotes((Status) characteristics.get(BasicStudyCharacteristic.STATUS).getValue());
			newChars.setStatus(statusWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.STATUS).getNotes(), statusWithNotes.getNotes().getNote());
		}
		if (inMap(characteristics, BasicStudyCharacteristic.SOURCE)) {
			org.drugis.addis.entities.data.Source sourceWithNotes = sourceWithNotes((Source) characteristics.get(BasicStudyCharacteristic.SOURCE).getValue());
			newChars.setSource(sourceWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.SOURCE).getNotes(), sourceWithNotes.getNotes().getNote());
		}
		if (inMap(characteristics, BasicStudyCharacteristic.STUDY_START)) {
			DateWithNotes dateWithNotes = dateWithNotes((Date) characteristics.get(BasicStudyCharacteristic.STUDY_START).getValue());
			newChars.setStudyStart(dateWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.STUDY_START).getNotes(), dateWithNotes.getNotes().getNote());
		}
		if (inMap(characteristics, BasicStudyCharacteristic.STUDY_END)) {
			DateWithNotes dateWithNotes = dateWithNotes((Date) characteristics.get(BasicStudyCharacteristic.STUDY_END).getValue());
			newChars.setStudyEnd(dateWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.STUDY_END).getNotes(), dateWithNotes.getNotes().getNote());
		}
		StringWithNotes stringWithNotes = stringWithNotes((String) characteristics.get(BasicStudyCharacteristic.TITLE).getValue());
		newChars.setTitle(stringWithNotes);
		convertOldNotes(characteristics.get(BasicStudyCharacteristic.TITLE).getNotes(), stringWithNotes.getNotes().getNote());
		newChars.setReferences(convertReferences((PubMedIdList)characteristics.get(BasicStudyCharacteristic.PUBMED).getValue()));
		return newChars;
	}

	private static boolean inMap(CharacteristicsMap characteristics, BasicStudyCharacteristic c) {
		return characteristics.containsKey(c) && characteristics.get(c) != null;
	}
	
	private static References convertReferences(PubMedIdList pubMedIdList) {
		References refs = new References();
		for(PubMedId x : pubMedIdList) {
			refs.getPubMedId().add(new BigInteger(x.getId()));
		}
		return refs;
	}

	public static StudyOutcomeMeasure<?> convertStudyOutcomeMeasure(org.drugis.addis.entities.data.StudyOutcomeMeasure om, List<Epoch> epochs, Domain domain) throws ConversionException {
		Variable var = null;
		if(om.getEndpoint() != null) {
			var = findNamedItem(domain.getEndpoints(), om.getEndpoint().getName());
		} else if(om.getAdverseEvent() != null) {
			var = findNamedItem(domain.getAdverseEvents(), om.getAdverseEvent().getName());
		} else if(om.getPopulationCharacteristic() != null) {
			var = findNamedItem(domain.getPopulationCharacteristics(), om.getPopulationCharacteristic().getName());
		} else {
			throw new ConversionException("StudyOutcomeMeasure type not supported: " + om.toString());
		}
		StudyOutcomeMeasure<Variable> studyOutcomeMeasure = new StudyOutcomeMeasure<Variable>(var);
		boolean isPrimaryNull = om.isPrimary() == null ? true : om.isPrimary();
		studyOutcomeMeasure.setIsPrimary(om.getEndpoint() != null ? isPrimaryNull: false);
		
		for (RelativeTime wt : om.getWhenTaken()) {
			studyOutcomeMeasure.getWhenTaken().add(convertWhenTaken(wt, epochs));
		}
		
		List<org.drugis.addis.entities.data.Note> notes = om.getNotes() == null ? Collections.<org.drugis.addis.entities.data.Note>emptyList() : om.getNotes().getNote();
		
		convertNotes(notes, studyOutcomeMeasure.getNotes());
		return studyOutcomeMeasure;
	}
	
	public static org.drugis.addis.entities.data.StudyOutcomeMeasure convertStudyOutcomeMeasure(StudyOutcomeMeasure<?> studyOutcomeMeasure) throws ConversionException {
		org.drugis.addis.entities.data.StudyOutcomeMeasure newOutcome = new org.drugis.addis.entities.data.StudyOutcomeMeasure();
		newOutcome.setNotes(new Notes());
		NameReference nameRef = nameReference(studyOutcomeMeasure.getValue().getName());
		newOutcome.setPrimary(true);
		if(studyOutcomeMeasure.getValue() instanceof Endpoint) {
			newOutcome.setEndpoint(nameRef);
			newOutcome.setPrimary(studyOutcomeMeasure.getIsPrimary());
		} else if(studyOutcomeMeasure.getValue() instanceof AdverseEvent){
			newOutcome.setAdverseEvent(nameRef);
			newOutcome.setPrimary(false);
		} else if(studyOutcomeMeasure.getValue() instanceof PopulationCharacteristic) {
			newOutcome.setPopulationCharacteristic(nameRef);
			newOutcome.setPrimary(false);
		} else {
			throw new ConversionException("Unsupported type of StudyOutcomeMeasure: " + studyOutcomeMeasure);
		}
		newOutcome.setNotes(new Notes());
		convertOldNotes(studyOutcomeMeasure.getNotes(), newOutcome.getNotes().getNote());
		for (WhenTaken wt : studyOutcomeMeasure.getWhenTaken()) {
			newOutcome.getWhenTaken().add(convertWhenTaken(wt));
		}
		return newOutcome;
	}

	public static LinkedHashMap<String, org.drugis.addis.entities.StudyOutcomeMeasure<?>> convertStudyOutcomeMeasures(StudyOutcomeMeasures oms, List<Epoch> epochs, Domain domain) throws ConversionException {
		LinkedHashMap<String, StudyOutcomeMeasure<?>> map = new LinkedHashMap<String, StudyOutcomeMeasure<?>>();
		for(org.drugis.addis.entities.data.StudyOutcomeMeasure om : oms.getStudyOutcomeMeasure()) {
			org.drugis.addis.entities.StudyOutcomeMeasure<?> convOm = convertStudyOutcomeMeasure(om, epochs, domain);
			map.put(om.getId(), convOm);
		}
		return map;
	}
	
	public static StudyOutcomeMeasures convertStudyOutcomeMeasures(LinkedHashMap<String, StudyOutcomeMeasure<?>> linkedMap) throws ConversionException {
		StudyOutcomeMeasures measures = new StudyOutcomeMeasures();
		for(Entry<String, StudyOutcomeMeasure<?>> item : linkedMap.entrySet()) {
			org.drugis.addis.entities.data.StudyOutcomeMeasure om = new org.drugis.addis.entities.data.StudyOutcomeMeasure();
			om = convertStudyOutcomeMeasure(item.getValue());
			om.setId(item.getKey());
			measures.getStudyOutcomeMeasure().add(om);
		}
		return measures;
	}

	
	public static BasicMeasurement convertMeasurement(org.drugis.addis.entities.data.Measurement m) throws ConversionException {
		if(m.getRateMeasurement() != null) {
			return new BasicRateMeasurement(m.getRateMeasurement().getRate(), m.getRateMeasurement().getSampleSize());
		}
		if(m.getContinuousMeasurement() != null) {			
			return new BasicContinuousMeasurement(
					m.getContinuousMeasurement().getMean(), 
					m.getContinuousMeasurement().getStdDev(),
					m.getContinuousMeasurement().getSampleSize()
					);
		}
		if(m.getCategoricalMeasurement() != null) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			List<String> names = new ArrayList<String>();
			for(CategoryMeasurement c : m.getCategoricalMeasurement().getCategory()) {
				map.put(c.getName(), c.getRate());
				names.add(c.getName());
			}
			return new FrequencyMeasurement(names, map);
		}
		
		throw new ConversionException("Measurement type not supported: " + m.toString());
	}
	
	public static org.drugis.addis.entities.data.Measurement convertMeasurement(MeasurementKey mk, Measurement m, String omId) throws ConversionException {
		org.drugis.addis.entities.data.Measurement dMeas = convertMeasurement(m);
		dMeas.setWhenTaken(convertWhenTaken(mk.getWhenTaken()));
		dMeas.setStudyOutcomeMeasure(stringIdReference(omId));
		if (mk.getArm() != null) {
			dMeas.setArm(nameReference(mk.getArm().getName()));
		}
		return dMeas;
	}
	
	public static org.drugis.addis.entities.data.Measurement convertMeasurement(Measurement m) throws ConversionException {
		org.drugis.addis.entities.data.Measurement measurement = new org.drugis.addis.entities.data.Measurement();
		Integer sampleSize = m.getSampleSize();
		if(m instanceof BasicRateMeasurement) {
			RateMeasurement value = new RateMeasurement();
			value.setSampleSize(sampleSize);
			value.setRate(((BasicRateMeasurement) m).getRate());
			measurement.setRateMeasurement(value);
		} else if(m instanceof BasicContinuousMeasurement) {
			ContinuousMeasurement value = new ContinuousMeasurement();
			value.setSampleSize(sampleSize);
			value.setMean(((BasicContinuousMeasurement) m).getMean());
			value.setStdDev(((BasicContinuousMeasurement) m).getStdDev());
			measurement.setContinuousMeasurement(value);
		} else if(m instanceof FrequencyMeasurement) {
			CategoricalMeasurement value = new CategoricalMeasurement();
			for(String cat : ((FrequencyMeasurement) m).getCategories()) {
				CategoryMeasurement newCat = new CategoryMeasurement();
				newCat.setName(cat);
				newCat.setRate(((FrequencyMeasurement) m).getFrequency(cat));
				value.getCategory().add(newCat);
			}
			measurement.setCategoricalMeasurement(value);
		} else {
			throw new ConversionException("Measurement type not supported: " + m.toString());
		}
		return measurement;
	}
	
	public static Map<MeasurementKey, BasicMeasurement> convertMeasurements(Measurements measurements, List<Arm> arms, 
			List<Epoch> epochs, Map<String, org.drugis.addis.entities.StudyOutcomeMeasure<?>> outcomeMeasures) 
	throws ConversionException {
		Map<MeasurementKey, BasicMeasurement> map = new TreeMap<MeasurementKey, BasicMeasurement>();
		for(org.drugis.addis.entities.data.Measurement m : measurements.getMeasurement()) {
			String omId = m.getStudyOutcomeMeasure().getId();
			Arm arm = m.getArm() != null ? findArm(m.getArm().getName(), arms) : null;
			map.put(new MeasurementKey(outcomeMeasures.get(omId).getValue(), arm, convertWhenTaken(m.getWhenTaken(), epochs)), convertMeasurement(m));
		}
		return map;
	}
	
	private static WhenTaken convertWhenTaken(RelativeTime rt, List<Epoch> epochs) {
		WhenTaken whenTaken = new WhenTaken(rt.getHowLong(), rt.getRelativeTo(), findNamedItem(epochs, rt.getEpoch().getName()));
		whenTaken.commit();
		return whenTaken;
	}

	private static RelativeTime convertWhenTaken(WhenTaken whenTaken) {
		RelativeTime rt = new RelativeTime();
		rt.setHowLong(whenTaken.getDuration());
		rt.setRelativeTo(whenTaken.getRelativeTo());
		rt.setEpoch(nameReference(whenTaken.getEpoch().getName()));
		return rt;
	}

	public static Measurements convertMeasurements(Map<MeasurementKey, BasicMeasurement> measurements, Map<String, StudyOutcomeMeasure<?>> oms) throws ConversionException {
		Measurements ms = new Measurements();
		for (MeasurementKey key : measurements.keySet()) {
			String omId = findKey(oms, new StudyOutcomeMeasure<Variable>(key.getVariable(), key.getWhenTaken()));
			ms.getMeasurement().add(convertMeasurement(key, measurements.get(key), omId));
		}
		return ms;
	}

	public static <K, V> K findKey(Map<K,V> map, V value) {
		for (Entry<K, V> e : map.entrySet()) {
			if (e.getValue().equals(value)) {
				return e.getKey();
			}
		}
		return null;
	}
	
	public static Study convertStudy(org.drugis.addis.entities.data.Study study, Domain domain) throws ConversionException {
		Study newStudy = new Study();
		newStudy.setName(study.getName());
		newStudy.setIndication(findNamedItem(domain.getIndications(), study.getIndication().getName()));
		convertNotes(study.getIndication().getNotes().getNote(), newStudy.getIndicationWithNotes().getNotes());

		List<Arm> arms = convertStudyArms(study.getArms());
		newStudy.getArms().addAll(arms);

		newStudy.getEpochs().addAll(convertEpochs(study.getEpochs()));

		newStudy.getStudyActivities().addAll(convertStudyActivities(study.getActivities(), newStudy, domain));
		
		CharacteristicsMap map = convertStudyCharacteristics(study.getCharacteristics());
		newStudy.setCharacteristics(map);
		
		
		LinkedHashMap<String, StudyOutcomeMeasure<?>> outcomeMeasures = convertStudyOutcomeMeasures(study.getStudyOutcomeMeasures(), newStudy.getEpochs(), domain);
		for(Entry<String, StudyOutcomeMeasure<?>> om : outcomeMeasures.entrySet()) {
			newStudy.addStudyOutcomeMeasure(om.getValue());
		}
		
		Map<MeasurementKey, BasicMeasurement> measurements = convertMeasurements(study.getMeasurements(), arms, newStudy.getEpochs(), outcomeMeasures);
		for(Entry<MeasurementKey, BasicMeasurement> m : measurements.entrySet()) {
			newStudy.setMeasurement(m.getKey(), m.getValue());
		}
		
		convertNotes(study.getNotes().getNote(), newStudy.getNotes());
		
		return newStudy;
	}
	
	public static Collection<? extends StudyActivity> convertStudyActivities(org.drugis.addis.entities.data.StudyActivities activities, Study s, Domain domain) throws ConversionException {
		List<StudyActivity> l = new ArrayList<StudyActivity>();
		for(org.drugis.addis.entities.data.StudyActivity sa: activities.getStudyActivity()) {
			l.add(convertStudyActivity(sa, s, domain));
		}
		return l;	
	}

	private static List<Epoch> convertEpochs(org.drugis.addis.entities.data.Epochs epochs) {
		List<Epoch> l = new ArrayList<Epoch>();
		for(org.drugis.addis.entities.data.Epoch e: epochs.getEpoch()) {
			Epoch newEpoch = new Epoch(e.getName(), e.getDuration());
			convertNotes(e.getNotes().getNote(), newEpoch.getNotes());
			l.add(newEpoch);
		}
		return l;
	}

	private static List<Arm> convertStudyArms(org.drugis.addis.entities.data.Arms arms) {
		List<Arm> l = new ArrayList<Arm>();
		for (org.drugis.addis.entities.data.Arm a: arms.getArm()) {
			Arm newA = new Arm(a.getName(), a.getSize());
			convertNotes(a.getNotes().getNote(), newA.getNotes());
			l.add(newA);
		}
		return l;
	}
	
	private static org.drugis.addis.entities.data.Arms convertStudyArms(List<Arm> arms) throws ConversionException {
		org.drugis.addis.entities.data.Arms newArms = new org.drugis.addis.entities.data.Arms();
		for (Arm a: arms) {
			newArms.getArm().add(convertArm(a));
		}
		return newArms; 
	}

	public static org.drugis.addis.entities.data.Study convertStudy(Study study) throws ConversionException {
		org.drugis.addis.entities.data.Study newStudy = new org.drugis.addis.entities.data.Study();
		newStudy.setName(study.getName());
		NameReferenceWithNotes indication = nameReferenceWithNotes(study.getIndication().getName());
		convertOldNotes(study.getIndicationWithNotes().getNotes(), indication.getNotes().getNote());
		newStudy.setIndication(indication);
		
		// convert arms
		newStudy.setArms(convertStudyArms(study.getArms()));
		
		newStudy.setEpochs(convertEpochs(study.getEpochs()));
		
		newStudy.setActivities(convertStudyActivities(study.getStudyActivities()));
		// convert outcome measures
		LinkedHashMap<String,org.drugis.addis.entities.StudyOutcomeMeasure<?>> omMap = new LinkedHashMap<String, StudyOutcomeMeasure<?>>();
		for (StudyOutcomeMeasure<Endpoint> e : study.getEndpoints()) {
			omMap.put("endpoint-" + e.getValue().getName(), e);
		}
		for (org.drugis.addis.entities.StudyOutcomeMeasure<AdverseEvent> e : study.getAdverseEvents()) {
			omMap.put("adverseEvent-" + e.getValue().getName(), e);
		}
		for (org.drugis.addis.entities.StudyOutcomeMeasure<PopulationCharacteristic> e : study.getPopulationChars()) {
			omMap.put("popChar-" + e.getValue().getName(), e);
		}
		newStudy.setStudyOutcomeMeasures(convertStudyOutcomeMeasures(omMap));
		
		// convert measurements
		newStudy.setMeasurements(convertMeasurements(study.getMeasurements(), omMap));
		
		// convert characteristics
		newStudy.setCharacteristics(convertStudyCharacteristics(study.getCharacteristics()));
		
		Notes notes = new Notes();
		convertOldNotes(study.getNotes(), notes.getNote());
		newStudy.setNotes(notes);
		
		return newStudy ;
	}

	public static org.drugis.addis.entities.data.StudyActivities convertStudyActivities(List<StudyActivity> studyActivities) throws ConversionException {
		StudyActivities newActivities = new StudyActivities();
		for (StudyActivity sa : studyActivities) {
			newActivities.getStudyActivity().add(convertStudyActivity(sa));
		}
		return newActivities;
	}

	private static org.drugis.addis.entities.data.Epochs convertEpochs(List<Epoch> epochs) {
		org.drugis.addis.entities.data.Epochs newEpochs = new org.drugis.addis.entities.data.Epochs();
		for (Epoch e: epochs) {
			newEpochs.getEpoch().add(convertEpoch(e));
		}
		return newEpochs;
	}

	public static RandomEffectsMetaAnalysis convertPairWiseMetaAnalysis(PairwiseMetaAnalysis pwma, Domain domain)
	throws ConversionException {
		List<StudyArmsEntry> studyArms = new ArrayList<StudyArmsEntry>();
		
		org.drugis.addis.entities.OutcomeMeasure om = findOutcomeMeasure(domain, pwma);
		if (pwma.getAlternative().size() != 2) {
			throw new ConversionException("PairWiseMetaAnalysis must have exactly 2 alternatives. Offending MA: " + pwma);
		}
		List<ArmReference> baseArms = pwma.getAlternative().get(0).getArms().getArm();
		List<ArmReference> subjArms = pwma.getAlternative().get(1).getArms().getArm();
		if (baseArms.size() != subjArms.size()) {
			throw new ConversionException("Alternative lists must have equal length. Offending MA: " + pwma);
		}
		TreatmentDefinition baseCat = null;
		TreatmentDefinition subjCat = null;
		for (int i = 0; i < baseArms.size(); ++i) {
			if (!baseArms.get(i).getStudy().equals(subjArms.get(i).getStudy())) {
				throw new ConversionException("Matching arms must be from the same study. Offending arms: " + 
						baseArms.get(i) + " -- " + subjArms.get(i) + " -- from " + pwma.getName());
			}
			Study study = findNamedItem(domain.getStudies(), baseArms.get(i).getStudy());
			Arm base = findArm(baseArms.get(i).getName(), study.getArms());
			Arm subj = findArm(subjArms.get(i).getName(), study.getArms());
			studyArms.add(new StudyArmsEntry(study, base, subj));
			if (i == 0) {
				baseCat = study.getDrugs(base);
				subjCat = study.getDrugs(subj);
			}
		}
		
		Collections.sort(studyArms);
		
		return new RandomEffectsMetaAnalysis(pwma.getName(), om, baseCat, subjCat, studyArms, false);
	}
	
	public static PairwiseMetaAnalysis convertPairWiseMetaAnalysis(RandomEffectsMetaAnalysis reMa) throws ConversionException {
		PairwiseMetaAnalysis pwma = new PairwiseMetaAnalysis();
		pwma.setName(reMa.getName());
		pwma.setIndication(nameReference(reMa.getIndication().getName()));
		if(reMa.getOutcomeMeasure() instanceof Endpoint) {
			pwma.setEndpoint(nameReference(reMa.getOutcomeMeasure().getName()));
		} else if(reMa.getOutcomeMeasure() instanceof AdverseEvent) {
			pwma.setAdverseEvent(nameReference(reMa.getOutcomeMeasure().getName()));
		} else {
			throw new ConversionException("Outcome Measure type not supported: " + reMa.getOutcomeMeasure());
		}
		for(TreatmentDefinition t : reMa.getAlternatives()) {
			MetaAnalysisAlternative alt = new MetaAnalysisAlternative();
			alt.setTreatmentDefinition(TreatmentDefinitionConverter.save(t));
			AnalysisArms arms = new AnalysisArms();
			for(StudyArmsEntry item : reMa.getStudyArms()) {
				Arm arm = null;
				if (reMa.getFirstAlternative().equals(t)) {
					arm = item.getBase();
				} else {
					arm = item.getSubject();
				}
				arms.getArm().add(armReference(item.getStudy().getName(), arm.getName()));
			}
			alt.setArms(arms);
			pwma.getAlternative().add(alt);
		}
		return pwma;
	}

	static Epoch findEpoch(String name, Study study) throws ConversionException {
		for (Epoch epoch : study.getEpochs()) {
			if (epoch.getName().equals(name)) {
				return epoch;
			}
		}
		throw new ConversionException("Undefined epoch name \"" + name + "\"");
	}
	
	static Arm findArm(String name, Study study) throws ConversionException {
		return findArm(name, study.getArms());
	}

	public static Arm findArm(String name, List<Arm> arms) throws ConversionException {
		for (Arm arm : arms) {
			if (arm.getName().equals(name)) {
				return arm;
			}
		}
		throw new ConversionException("Undefined arm name \"" + name + "\"");
	}

	public static org.drugis.addis.entities.OutcomeMeasure findOutcomeMeasure(Domain domain, 
			org.drugis.addis.entities.data.MetaAnalysis ma)
	throws ConversionException {
		org.drugis.addis.entities.OutcomeMeasure om = null;
		if (ma.getEndpoint() != null) {
			om = findNamedItem(domain.getEndpoints(), ma.getEndpoint().getName());
		} else if (ma.getAdverseEvent() != null) {
			om = findNamedItem(domain.getAdverseEvents(), ma.getAdverseEvent().getName());
		} else {
			throw new ConversionException("MetaAnalysis has unsupported OutcomeMeasure: " + ma);
		}
		return om;
	}

	public static List<MetaAnalysis> convertMetaAnalyses(MetaAnalyses analyses, Domain domain) throws ConversionException {
		List<MetaAnalysis> list = new ArrayList<MetaAnalysis>();
		
		for(org.drugis.addis.entities.data.MetaAnalysis ma : analyses.getPairwiseMetaAnalysisOrNetworkMetaAnalysis()) {
			if(ma instanceof org.drugis.addis.entities.data.NetworkMetaAnalysis) {
				list.add(NetworkMetaAnalysisConverter.load((org.drugis.addis.entities.data.NetworkMetaAnalysis)ma, domain));
			} else if(ma instanceof PairwiseMetaAnalysis) {
				list.add(convertPairWiseMetaAnalysis((PairwiseMetaAnalysis)ma, domain));
			} else {
				throw new ConversionException("Unsupported MetaAnalysis Type" + ma);
			}
		}
		return list;
	}
	
	public static MetaAnalyses convertMetaAnalyses(List<MetaAnalysis> list) throws ConversionException {
		MetaAnalyses analyses = new MetaAnalyses();
		for(MetaAnalysis ma : list) {
			if(ma instanceof NetworkMetaAnalysis) {
				analyses.getPairwiseMetaAnalysisOrNetworkMetaAnalysis().add(NetworkMetaAnalysisConverter.save((NetworkMetaAnalysis) ma));
			} else if(ma instanceof RandomEffectsMetaAnalysis) {
				analyses.getPairwiseMetaAnalysisOrNetworkMetaAnalysis().add(convertPairWiseMetaAnalysis((RandomEffectsMetaAnalysis) ma));
			} else {
				throw new ConversionException("Unsupported MetaAnalysis Type" + ma);
			}
		}
		return analyses;
	}

	public static StudyBenefitRiskAnalysis convertStudyBenefitRiskAnalysis(
			org.drugis.addis.entities.data.StudyBenefitRiskAnalysis br, Domain domain) throws ConversionException {
		
		Indication indication = findNamedItem(domain.getIndications(), br.getIndication().getName());
		Study study = findNamedItem(domain.getStudies(), br.getStudy().getName());
		List<org.drugis.addis.entities.OutcomeMeasure> criteria = new ArrayList<org.drugis.addis.entities.OutcomeMeasure>();
		for (NameReference ref : br.getOutcomeMeasures().getEndpoint()) {
			criteria.add(findNamedItem(domain.getEndpoints(), ref.getName()));
		}
		for (NameReference ref : br.getOutcomeMeasures().getAdverseEvent()) {
			criteria.add(findNamedItem(domain.getAdverseEvents(), ref.getName()));
		}
		if (!br.getOutcomeMeasures().getPopulationCharacteristic().isEmpty()) {
			throw new ConversionException("PopulationCharacteristics not supported as criteria. " + br);
		}
		List<Arm> alternatives = new ArrayList<Arm>();
		for (ArmReference ref : br.getArms().getArm()) {
			alternatives.add(findArm(ref.getName(), study.getArms()));
		}
		
		Arm baseline = findArm(br.getBaseline().getArm().getName(), study.getArms());
		
		return new StudyBenefitRiskAnalysis(br.getName(), indication, study, criteria, baseline, alternatives, br.getAnalysisType(), convertDecisionContext(br.getDecisionContext()));
	}
	
	public static org.drugis.addis.entities.data.StudyBenefitRiskAnalysis convertStudyBenefitRiskAnalysis(StudyBenefitRiskAnalysis br) throws ConversionException {
		org.drugis.addis.entities.data.StudyBenefitRiskAnalysis newBr = new org.drugis.addis.entities.data.StudyBenefitRiskAnalysis();
		
		newBr.setName(br.getName());
		newBr.setAnalysisType(br.getAnalysisType());
		newBr.setIndication(nameReference(br.getIndication().getName()));
		newBr.setStudy(nameReference(br.getStudy().getName()));
		
		OutcomeMeasuresReferences oms = new OutcomeMeasuresReferences();
		for (OutcomeMeasure om : br.getCriteria()) {
			if (om instanceof Endpoint) {
				oms.getEndpoint().add(nameReference(om.getName()));
			} else if (om instanceof AdverseEvent) {
				oms.getAdverseEvent().add(nameReference(om.getName()));
			} else {
				throw new ConversionException("Unsupported OutcomeMeasure type " + om);
			}
		}
		newBr.setOutcomeMeasures(oms);
		
		ArmReferences arms = new ArmReferences();
		for (Arm arm : br.getAlternatives()) {
			arms.getArm().add(armReference(br.getStudy().getName(), arm.getName()));
		}
		newBr.setArms(arms);
		
		BaselineArmReference baseline = new BaselineArmReference();
		baseline.setArm(armReference(br.getStudy().getName(), br.getBaseline().getName()));
		newBr.setBaseline(baseline);
		
		newBr.setDecisionContext(convertDecisionContext(br.getDecisionContext()));

		return newBr;
	}

	public static MetaBenefitRiskAnalysis convertMetaBenefitRiskAnalysis(org.drugis.addis.entities.data.MetaBenefitRiskAnalysis br, Domain domain) {
		Indication indication = findNamedItem(domain.getIndications(), br.getIndication().getName());
		TreatmentDefinition baseline = TreatmentDefinitionConverter.load(br.getBaseline().getTreatmentDefinition(), domain);
		List<TreatmentDefinition> treatmentAlternative = new SortedSetModel<TreatmentDefinition>();
		for (org.drugis.addis.entities.data.TreatmentDefinition set : br.getAlternatives().getTreatmentDefinition()) {
			treatmentAlternative.add(TreatmentDefinitionConverter.load(set, domain));
		}
		List<MetaAnalysis> metaAnalysis = new ArrayList<MetaAnalysis>();
		for (NameReference ref : br.getMetaAnalyses().getMetaAnalysis()) {
			metaAnalysis.add(findMetaAnalysis(domain, ref.getName()));
		}
		treatmentAlternative.remove(baseline);
		return new MetaBenefitRiskAnalysis(br.getName(), indication, metaAnalysis, baseline, treatmentAlternative, br.getAnalysisType(), convertDecisionContext(br.getDecisionContext()));
	}
	

	public static org.drugis.addis.entities.data.MetaBenefitRiskAnalysis convertMetaBenefitRiskAnalysis(MetaBenefitRiskAnalysis br) {
		org.drugis.addis.entities.data.MetaBenefitRiskAnalysis newBr = new org.drugis.addis.entities.data.MetaBenefitRiskAnalysis();
		newBr.setName(br.getName());
		newBr.setAnalysisType(br.getAnalysisType());
		Baseline baseline = new Baseline();
		baseline.setTreatmentDefinition(TreatmentDefinitionConverter.save((br.getBaseline())));
		newBr.setBaseline(baseline);
		newBr.setIndication(nameReference(br.getIndication().getName()));
		
		org.drugis.addis.entities.data.MetaBenefitRiskAnalysis.Alternatives alternatives = new org.drugis.addis.entities.data.MetaBenefitRiskAnalysis.Alternatives();
		for(TreatmentDefinition t : br.getAlternatives()) {
			alternatives.getTreatmentDefinition().add(TreatmentDefinitionConverter.save(t));
		}
		newBr.setAlternatives(alternatives);
		
		MetaAnalysisReferences maRefs = new MetaAnalysisReferences();
		for(MetaAnalysis m : br.getMetaAnalyses()) {
			maRefs.getMetaAnalysis().add(nameReference(m.getName()));
		}
		
		newBr.setMetaAnalyses(maRefs);
		newBr.setDecisionContext(convertDecisionContext(br.getDecisionContext()));
		return newBr;
	}

	public static List<BenefitRiskAnalysis<?>> convertBenefitRiskAnalyses(BenefitRiskAnalyses analyses, Domain domain) throws ConversionException {
		List<BenefitRiskAnalysis<?>> list = new ArrayList<BenefitRiskAnalysis<?>>();
		for (Object o : analyses.getStudyBenefitRiskAnalysisOrMetaBenefitRiskAnalysis()) {
			if(o instanceof org.drugis.addis.entities.data.StudyBenefitRiskAnalysis) {
				list.add(convertStudyBenefitRiskAnalysis((org.drugis.addis.entities.data.StudyBenefitRiskAnalysis) o, domain));
			}else if (o instanceof org.drugis.addis.entities.data.MetaBenefitRiskAnalysis) {
				list.add(convertMetaBenefitRiskAnalysis((org.drugis.addis.entities.data.MetaBenefitRiskAnalysis) o, domain));
			}else {
				throw new ConversionException("Unsupported Benefit-Risk Analysis Type" + o);
			}
		}
		return list;
	}
	

	public static BenefitRiskAnalyses convertBenefitRiskAnalyses(List<BenefitRiskAnalysis<?>> list) throws ConversionException {
		BenefitRiskAnalyses analyses = new BenefitRiskAnalyses();
		for (BenefitRiskAnalysis<?> br : list) {
			if (br instanceof StudyBenefitRiskAnalysis) {
				analyses.getStudyBenefitRiskAnalysisOrMetaBenefitRiskAnalysis().add(convertStudyBenefitRiskAnalysis((StudyBenefitRiskAnalysis) br));
			} else if (br instanceof MetaBenefitRiskAnalysis) {
				analyses.getStudyBenefitRiskAnalysisOrMetaBenefitRiskAnalysis().add(convertMetaBenefitRiskAnalysis((MetaBenefitRiskAnalysis) br));
			} else {
				throw new ConversionException("Unsupported Benefit-Risk Analysis Type" + br);
			}
		}
		return analyses;
	}
	
	private static MetaAnalysis findMetaAnalysis(Domain domain, String name) {
		for (MetaAnalysis ma : domain.getMetaAnalyses()) {
			if (ma.getName().equals(name)) {
				return ma;
			}
		}
		return null;
	}
	

	public static NameReference nameReference(String name) {
		NameReference ref = new NameReference();
		ref.setName(name);
		return ref;
	}
	
	public static org.drugis.addis.entities.data.Allocation allocationWithNotes(Allocation nested) {
		org.drugis.addis.entities.data.Allocation allocation = new org.drugis.addis.entities.data.Allocation();
		allocation.setValue(nested);
		allocation.setNotes(new Notes());
		return allocation;
	}

	public static StringWithNotes stringWithNotes(String string) {
		StringWithNotes strNot = new StringWithNotes();
		strNot.setValue(string);
		strNot.setNotes(new Notes());
		return strNot;
	}

	public static org.drugis.addis.entities.data.Blinding blindingWithNotes(Blinding nested) {
		org.drugis.addis.entities.data.Blinding blinding = new org.drugis.addis.entities.data.Blinding();
		blinding.setValue(nested);
		blinding.setNotes(new Notes());
		return blinding;
	}

	public static org.drugis.addis.entities.data.IntegerWithNotes intWithNotes(Integer centers) {
		org.drugis.addis.entities.data.IntegerWithNotes integer = new org.drugis.addis.entities.data.IntegerWithNotes();
		integer.setValue(centers);
		integer.setNotes(new Notes());
		return integer;
	}

	public static org.drugis.addis.entities.data.Status statusWithNotes(Status nested) {
		org.drugis.addis.entities.data.Status status = new org.drugis.addis.entities.data.Status();
		status.setValue(nested);
		status.setNotes(new Notes());
		return status;
	}

	public static org.drugis.addis.entities.data.Source sourceWithNotes(Source nested) {
		org.drugis.addis.entities.data.Source source = new org.drugis.addis.entities.data.Source();
		source.setValue(nested);
		source.setNotes(new Notes());
		return source;
	}
	
	public static DateWithNotes dateWithNotes(Date date) {
		org.drugis.addis.entities.data.DateWithNotes dateWithNotes = new org.drugis.addis.entities.data.DateWithNotes();
		if (date != null) {
			dateWithNotes.setValue(dateToXml(date));
		}
		dateWithNotes.setNotes(new Notes());
		return dateWithNotes;
	}

	public static IdReference idReference(int id) {
		IdReference ref = new IdReference();
		ref.setId(id);
		return ref;
	}

	public static StringIdReference stringIdReference(String id) {
		StringIdReference ref = new StringIdReference();
		ref.setId(id);
		return ref;
	}

	public static NameReferenceWithNotes nameReferenceWithNotes(String name) {
		NameReferenceWithNotes reference = new NameReferenceWithNotes();
		reference.setName(name);
		reference.setNotes(new Notes());

		return reference;
	}

	public static ArmReference armReference(String studyName, String armName) {
		ArmReference ref = new ArmReference();
		ref.setStudy(studyName);
		ref.setName(armName);
		return ref;
	}

	public static Note convertNote(org.drugis.addis.entities.data.Note note) {
		return new Note(note.getSource(), note.getValue());
	}

	public static org.drugis.addis.entities.data.Note convertNote(Note note) {
		org.drugis.addis.entities.data.Note converted = new org.drugis.addis.entities.data.Note();
		converted.setSource(note.getSource());
		converted.setValue(note.getText());
		return converted;
	}
	
	public static org.drugis.addis.entities.data.DecisionContext convertDecisionContext(DecisionContext entityContext) {
		if (entityContext == null) {
			return null;
		}
		org.drugis.addis.entities.data.DecisionContext context = new org.drugis.addis.entities.data.DecisionContext();
		context.setComparator(entityContext.getComparator());
		context.setStakeholderPerspective(entityContext.getStakeholderPerspective());
		context.setTherapeuticContext(entityContext.getTherapeuticContext());
		context.setTimeHorizon(entityContext.getTimeHorizon());
		return context;
	}

	public static DecisionContext convertDecisionContext(org.drugis.addis.entities.data.DecisionContext dataContext) {
		if (dataContext == null) {
			return null;
		}
		DecisionContext context = new DecisionContext();
		context.setComparator(dataContext.getComparator());
		context.setStakeholderPerspective(dataContext.getStakeholderPerspective());
		context.setTherapeuticContext(dataContext.getTherapeuticContext());
		context.setTimeHorizon(dataContext.getTimeHorizon());
		return context;
	}

	/**
	 * Convert legacy XML ("version 0") to schema v1 compliant XML.
	 * @param xml Legacy XML input stream.
	 * @return Schema v1 compliant XML.
	 */
	public static InputStream transformLegacyXML(InputStream xml)
	throws TransformerException, IOException {
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		TransformerFactory tFactory = TransformerFactory.newInstance(); 
		InputStream xsltFile = JAXBConvertor.class.getResourceAsStream("transform-0-1.xslt");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
	    javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(xml);
	    javax.xml.transform.Source xsltSource = new javax.xml.transform.stream.StreamSource(xsltFile);
	    javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(os);
	    
	    javax.xml.transform.Transformer trans = tFactory.newTransformer(xsltSource);
	    trans.transform(xmlSource, result);
	    
	    os.close();
	
	    return new ByteArrayInputStream(os.toByteArray());
	}

	/**
	 * Convert an XML stream to the latest XML version.
	 * @param xml An XML input stream
	 * @param sourceVersion The schema version of the source.
	 * @return XML compliant with most recent schema.
	 */
	public static InputStream transformToLatest(InputStream xml, int sourceVersion)
	throws TransformerException, IOException {
		return transformToVersion(xml, sourceVersion, XmlFormatType.CURRENT_VERSION);
	}
	
	/**
	 * Convert an XML stream to a specific XML version.
	 * @param xml An XML input stream
	 * @param sourceVersion The schema version of the source.
	 * @param targetVersion The schema version to convert to.
	 * @return XML compliant with the specified schema.
	 */
	public static InputStream transformToVersion(InputStream xml, int sourceVersion, int targetVersion)
	throws TransformerException, IOException {
		if (sourceVersion == targetVersion) {
			return xml;
		} else if (sourceVersion > targetVersion) {
			throw new RuntimeException("XML version from the future detected");
		}
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		
		TransformerFactory tFactory = TransformerFactory.newInstance();
		for (int v = sourceVersion; v < targetVersion; ++v) {
			InputStream xsltFile = JAXBConvertor.class.getResourceAsStream("transform-" + v + "-" + (v + 1) + ".xslt");
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(xml);
			javax.xml.transform.Source xsltSource = new javax.xml.transform.stream.StreamSource(xsltFile);
			javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(os);
			
			javax.xml.transform.Transformer trans = tFactory.newTransformer(xsltSource);
			trans.transform(xmlSource, result);

			os.close();

			xml = new ByteArrayInputStream(os.toByteArray()); // next version XML
		}
		
		return xml;
	}
}
