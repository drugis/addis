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

package org.drugis.addis.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.CategoricalPopulationCharacteristic;
import org.drugis.addis.entities.CharacteristicsMap;
import org.drugis.addis.entities.ContinuousPopulationCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.ObjectWithNotes;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.addis.entities.RatePopulationCharacteristic;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.BasicStudyCharacteristic.Allocation;
import org.drugis.addis.entities.BasicStudyCharacteristic.Blinding;
import org.drugis.addis.entities.BasicStudyCharacteristic.Status;
import org.drugis.addis.entities.Study.MeasurementKey;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.entities.data.AdverseEvents;
import org.drugis.addis.entities.data.Alternative;
import org.drugis.addis.entities.data.AnalysisArms;
import org.drugis.addis.entities.data.ArmReference;
import org.drugis.addis.entities.data.ArmReferences;
import org.drugis.addis.entities.data.Arms;
import org.drugis.addis.entities.data.BenefitRiskAnalyses;
import org.drugis.addis.entities.data.CategoricalMeasurement;
import org.drugis.addis.entities.data.CategoricalVariable;
import org.drugis.addis.entities.data.CategoryMeasurement;
import org.drugis.addis.entities.data.Characteristics;
import org.drugis.addis.entities.data.ContinuousMeasurement;
import org.drugis.addis.entities.data.ContinuousVariable;
import org.drugis.addis.entities.data.DateWithNotes;
import org.drugis.addis.entities.data.DrugReferences;
import org.drugis.addis.entities.data.Drugs;
import org.drugis.addis.entities.data.Endpoints;
import org.drugis.addis.entities.data.IdReference;
import org.drugis.addis.entities.data.Indications;
import org.drugis.addis.entities.data.IntegerWithNotes;
import org.drugis.addis.entities.data.Measurements;
import org.drugis.addis.entities.data.MetaAnalyses;
import org.drugis.addis.entities.data.MetaAnalysisReferences;
import org.drugis.addis.entities.data.NameReference;
import org.drugis.addis.entities.data.NameReferenceWithNotes;
import org.drugis.addis.entities.data.Notes;
import org.drugis.addis.entities.data.OutcomeMeasuresReferences;
import org.drugis.addis.entities.data.PairwiseMetaAnalysis;
import org.drugis.addis.entities.data.PopulationCharacteristics;
import org.drugis.addis.entities.data.RateMeasurement;
import org.drugis.addis.entities.data.RateVariable;
import org.drugis.addis.entities.data.References;
import org.drugis.addis.entities.data.StringIdReference;
import org.drugis.addis.entities.data.StringWithNotes;
import org.drugis.addis.entities.data.Studies;
import org.drugis.addis.entities.data.StudyOutcomeMeasure;
import org.drugis.addis.entities.data.StudyOutcomeMeasures;
import org.drugis.common.Interval;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class JAXBConvertor {
	
	@SuppressWarnings("serial")
	public static class ConversionException extends Exception {
		public ConversionException(String s) {
			super(s);
		}
	}
	
	private JAXBConvertor() {}
	
	public static Domain convertAddisDataToDomain(AddisData addisData) throws ConversionException {
		Domain newDomain = new org.drugis.addis.entities.DomainImpl();
		for(org.drugis.addis.entities.data.Indication i : addisData.getIndications().getIndication()) {
			newDomain.addIndication(convertIndication(i));
		}
		for (org.drugis.addis.entities.data.Drug d : addisData.getDrugs().getDrug()) {
			newDomain.addDrug(convertDrug(d));
		}
		for (org.drugis.addis.entities.data.OutcomeMeasure om : addisData.getEndpoints().getEndpoint()) {
			newDomain.addEndpoint(convertEndpoint(om));
		}
		for(org.drugis.addis.entities.data.OutcomeMeasure ae : addisData.getAdverseEvents().getAdverseEvent()) {
			newDomain.addAdverseEvent(convertAdverseEvent(ae));
		}
		for(org.drugis.addis.entities.data.OutcomeMeasure ae : addisData.getPopulationCharacteristics().getPopulationCharacteristic()) {
			newDomain.addPopulationCharacteristic((PopulationCharacteristic) convertPopulationCharacteristic(ae));
		}
		for(org.drugis.addis.entities.data.Study s : addisData.getStudies().getStudy()) {
			newDomain.addStudy(convertStudy(s, newDomain));
		}
		// Meta-analyses
		for(MetaAnalysis ma : convertMetaAnalyses(addisData.getMetaAnalyses(), newDomain)) {
			try {
				newDomain.addMetaAnalysis(ma);
			} catch (EntityIdExistsException e) {
				throw new ConversionException("Duplicate entity in XML: " + e);
			}
		}
		// Benefit-risk analyses
		for(BenefitRiskAnalysis<?> br : convertBenefitRiskAnalyses(addisData.getBenefitRiskAnalyses(), newDomain)) {
			newDomain.addBenefitRiskAnalysis(br);
		}
		return newDomain;	
	}

	public static AddisData convertDomainToAddisData(Domain domain) throws ConversionException {
		AddisData addisData = new AddisData();
		addisData.setIndications(new Indications());
		for (Indication i : domain.getIndications()) {
			addisData.getIndications().getIndication().add(convertIndication(i));
		}
		addisData.setDrugs(new Drugs());
		for (Drug d : domain.getDrugs()) {
			addisData.getDrugs().getDrug().add(convertDrug(d));
		}
		addisData.setEndpoints(new Endpoints());
		for (Endpoint e : domain.getEndpoints()) {
			addisData.getEndpoints().getEndpoint().add(convertEndpoint(e));
		}
		addisData.setAdverseEvents(new AdverseEvents());
		for (AdverseEvent e : domain.getAdverseEvents()) {
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
	
	static AdverseEvent convertAdverseEvent(org.drugis.addis.entities.data.OutcomeMeasure ae) throws ConversionException {
		AdverseEvent a = new AdverseEvent();
		convertOutcomeMeasure(ae, a);
		return a;
	}

	static org.drugis.addis.entities.data.OutcomeMeasure convertAdverseEvent(AdverseEvent a) throws ConversionException {
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

	static org.drugis.addis.entities.data.Drug convertDrug(Drug d) {
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
		switch (o.getType()) {
		case CATEGORICAL:
			if(o instanceof CategoricalPopulationCharacteristic) {
				CategoricalVariable varCat = new CategoricalVariable();
				varCat.getCategory().addAll(((CategoricalPopulationCharacteristic) o).getCategoriesAsList());
				om.setCategorical(varCat);
			} else {
				throw new ConversionException("Categorical outcomemeasures don't exist");
			}
			break;
		case CONTINUOUS:
			ContinuousVariable varC = new ContinuousVariable();
			if(o instanceof OutcomeMeasure) {
				varC.setDirection(((OutcomeMeasure) o).getDirection());
			}
			varC.setUnitOfMeasurement(o.getUnitOfMeasurement());
			om.setContinuous(varC);
			break;
		case RATE:
			RateVariable varR = new RateVariable();
			if(o instanceof OutcomeMeasure) {
				varR.setDirection(((OutcomeMeasure) o).getDirection());
			}
			om.setRate(varR);
			break;
		}
		return om;
	}
	
	private static void convertOutcomeMeasure(
			org.drugis.addis.entities.data.OutcomeMeasure from,
			org.drugis.addis.entities.OutcomeMeasure to)
	throws ConversionException {
		to.setName(from.getName());
		if (from.getCategorical() != null) {
			throw(new ConversionException("Endpoints should not be categorical (yet)"));
		} else if (from.getContinuous() != null) {
			to.setType(Type.CONTINUOUS);
			to.setUnitOfMeasurement(from.getContinuous().getUnitOfMeasurement());
			to.setDirection(from.getContinuous().getDirection());
		} else if (from.getRate() != null) {
			to.setType(Type.RATE);
			to.setDirection(from.getRate().getDirection());
		}
	}
	
	static Indication findIndication(Domain domain, String name) {
		for (Indication i : domain.getIndications()) {
			if (i.getName().equals(name)) {
				return i;
			}
		}
		return null;
	}
	
	static Drug findDrug(Domain domain, String name) {
		for(Drug d: domain.getDrugs()) {
			if(d.getName().equals(name)) {
				return d;
			}
		}
		return null;
	}
	
	static Endpoint findEndpoint(Domain domain, String name) {
		for (Endpoint e : domain.getEndpoints()) {
			if (e.getName().equals(name)) {
				return e;
			}
		}
		return null;
	}
	
	static AdverseEvent findAdverseEvent(Domain domain, String name) {
		for (AdverseEvent ae : domain.getAdverseEvents()) {
			if (ae.getName().equals(name)) {
				return ae;
			}
		}
		return null;
	}
	
	private static PopulationCharacteristic findPopulationCharacteristic(Domain domain,	String name) {
		for (PopulationCharacteristic pc : domain.getPopulationCharacteristics()) {
			if (pc.getName().equals(name)) {
				return pc;
			}
		}
		return null;
	}
	

	private static Study findStudy(String name, Domain domain) {
		for (Study s : domain.getStudies()) {
			if (s.getStudyId().equals(name)) {
				return s;
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
	
	static Arm convertArm(org.drugis.addis.entities.data.Arm arm, Domain domain) throws ConversionException {
		Drug d = findDrug(domain, arm.getDrug().getName());
		
		if(arm.getFixedDose() != null) {
			FixedDose fixDose = new FixedDose(arm.getFixedDose().getQuantity(), arm.getFixedDose().getUnit());
			Arm newArm = new Arm(d, fixDose, arm.getSize().intValue());
			convertNotes(arm.getNotes().getNote(), newArm.getNotes());
			return newArm;
		}
		else if(arm.getFlexibleDose() != null) {
			FlexibleDose flexDose = new FlexibleDose(new Interval<Double> (
													(double) arm.getFlexibleDose().getMinDose(), 
													(double) arm.getFlexibleDose().getMaxDose()
												 ), arm.getFlexibleDose().getUnit());
			Arm newArm = new Arm(d, flexDose, arm.getSize());
			convertNotes(arm.getNotes().getNote(), newArm.getNotes());
			return newArm;
		}
		
		return null;
	}
	
	static org.drugis.addis.entities.data.Arm convertArm(Arm arm) throws ConversionException {
		org.drugis.addis.entities.data.Arm newArm = new org.drugis.addis.entities.data.Arm();
		newArm.setDrug(nameReference(arm.getDrug().getName()));
		
		if(arm.getDose() instanceof FixedDose) {
			newArm.setFixedDose(convertFixedDose((FixedDose)arm.getDose()));
		} else {
			newArm.setFlexibleDose(convertFlexibleDose((FlexibleDose)arm.getDose()));
		}
		newArm.setSize(arm.getSize());
		
		newArm.setNotes(new Notes());
		convertOldNotes(arm.getNotes(), newArm.getNotes().getNote());
	
		return newArm;
	}

	private static org.drugis.addis.entities.data.FlexibleDose convertFlexibleDose(FlexibleDose dose) {
		org.drugis.addis.entities.data.FlexibleDose newDose = new org.drugis.addis.entities.data.FlexibleDose();
		newDose.setUnit(dose.getUnit());
		newDose.setMinDose(dose.getMinDose());
		newDose.setMaxDose(dose.getMaxDose());
		return newDose;
	}

	private static org.drugis.addis.entities.data.FixedDose convertFixedDose(FixedDose dose) {
		org.drugis.addis.entities.data.FixedDose newDose = new org.drugis.addis.entities.data.FixedDose();
		newDose.setQuantity(dose.getQuantity());
		newDose.setUnit(dose.getUnit());
		return newDose;
	}

	static Variable convertPopulationCharacteristic(org.drugis.addis.entities.data.OutcomeMeasure m) throws ConversionException {
		if(m.getRate() != null) {
			RatePopulationCharacteristic ratePC =  new RatePopulationCharacteristic();
			ratePC.setName(m.getName());
			ratePC.setDescription(m.getDescription());
			ratePC.setType(Type.RATE);
			ratePC.setUnitOfMeasurement(Variable.UOM_DEFAULT_RATE);
			return ratePC;
		}
		if(m.getContinuous() != null) {
			ContinuousPopulationCharacteristic contPC = new ContinuousPopulationCharacteristic();
			contPC.setName(m.getName());
			contPC.setDescription(m.getDescription());
			contPC.setType(Type.CONTINUOUS);
			contPC.setUnitOfMeasurement(m.getContinuous().getUnitOfMeasurement());
			return contPC;
		}
		if(m.getCategorical() != null) {
			CategoricalPopulationCharacteristic catPC = new CategoricalPopulationCharacteristic();
			catPC.setName(m.getName());
			catPC.setDescription(m.getDescription());
			catPC.setCategories(m.getCategorical().getCategory().toArray(new String[]{}));
			return catPC;
		}
		throw new ConversionException("Unknown variable type");
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
			map.put(BasicStudyCharacteristic.STUDY_START, objectWithNotes(calToDate(chars1.getStudyStart().getValue()), chars1.getStudyStart().getNotes()));
		}
		if (chars1.getStudyEnd() != null) {
			map.put(BasicStudyCharacteristic.STUDY_END, objectWithNotes(calToDate(chars1.getStudyEnd().getValue()), chars1.getStudyEnd().getNotes()));
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
			map.put(BasicStudyCharacteristic.CREATION_DATE, objectWithNotes(calToDate(chars1.getCreationDate().getValue()), chars1.getCreationDate().getNotes()));
		}
		map.put(BasicStudyCharacteristic.TITLE, objectWithNotes(chars1.getTitle().getValue(), chars1.getTitle().getNotes()));
		map.put(BasicStudyCharacteristic.PUBMED, new ObjectWithNotes<Object>(getPubMedIds(chars1.getReferences())));
		return map;
	}	

	private static Object calToDate(XMLGregorianCalendar value) {
		return value == null ? null : value.toGregorianCalendar().getTime();
	}

	public static Characteristics convertStudyCharacteristics(CharacteristicsMap characteristics) {
		Characteristics newChars = new Characteristics();
		if (characteristics.containsKey(BasicStudyCharacteristic.ALLOCATION)) {
			org.drugis.addis.entities.data.Allocation allocationWithNotes = allocationWithNotes((Allocation) characteristics.get(BasicStudyCharacteristic.ALLOCATION).getValue());
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.ALLOCATION).getNotes() , allocationWithNotes.getNotes().getNote());
			newChars.setAllocation(allocationWithNotes);
		}
		if (characteristics.containsKey(BasicStudyCharacteristic.BLINDING)) {
			org.drugis.addis.entities.data.Blinding blindingWithNotes = blindingWithNotes((Blinding) characteristics.get(BasicStudyCharacteristic.BLINDING).getValue());
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.BLINDING).getNotes(), blindingWithNotes.getNotes().getNote());
			newChars.setBlinding(blindingWithNotes);
		}
		if (characteristics.containsKey(BasicStudyCharacteristic.CENTERS)) {
			IntegerWithNotes intWithNotes = intWithNotes((Integer) characteristics.get(BasicStudyCharacteristic.CENTERS).getValue());
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.CENTERS).getNotes(), intWithNotes.getNotes().getNote());
			newChars.setCenters(intWithNotes);
		}
		if (characteristics.containsKey(BasicStudyCharacteristic.CREATION_DATE)) {
			DateWithNotes dateWithNotes = dateWithNotes((Date) characteristics.get(BasicStudyCharacteristic.CREATION_DATE).getValue());
			newChars.setCreationDate(dateWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.CREATION_DATE).getNotes(), dateWithNotes.getNotes().getNote());
		}
		if (characteristics.containsKey(BasicStudyCharacteristic.EXCLUSION)) {
			StringWithNotes stringWithNotes = stringWithNotes((String) characteristics.get(BasicStudyCharacteristic.EXCLUSION).getValue());
			newChars.setExclusion(stringWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.EXCLUSION).getNotes(), stringWithNotes.getNotes().getNote());
		}
		if (characteristics.containsKey(BasicStudyCharacteristic.INCLUSION)) {
			StringWithNotes stringWithNotes = stringWithNotes((String) characteristics.get(BasicStudyCharacteristic.INCLUSION).getValue());
			newChars.setInclusion(stringWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.INCLUSION).getNotes(), stringWithNotes.getNotes().getNote());
		}
		if (characteristics.containsKey(BasicStudyCharacteristic.OBJECTIVE)) {
			StringWithNotes stringWithNotes = stringWithNotes((String) characteristics.get(BasicStudyCharacteristic.OBJECTIVE).getValue());
			newChars.setObjective(stringWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.OBJECTIVE).getNotes(), stringWithNotes.getNotes().getNote());
		}
		if (characteristics.containsKey(BasicStudyCharacteristic.STATUS)) {
			org.drugis.addis.entities.data.Status statusWithNotes = statusWithNotes((Status) characteristics.get(BasicStudyCharacteristic.STATUS).getValue());
			newChars.setStatus(statusWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.STATUS).getNotes(), statusWithNotes.getNotes().getNote());
		}
		if (characteristics.containsKey(BasicStudyCharacteristic.SOURCE)) {
			org.drugis.addis.entities.data.Source sourceWithNotes = sourceWithNotes((Source) characteristics.get(BasicStudyCharacteristic.SOURCE).getValue());
			newChars.setSource(sourceWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.SOURCE).getNotes(), sourceWithNotes.getNotes().getNote());
		}
		if (characteristics.containsKey(BasicStudyCharacteristic.STUDY_START)) {
			DateWithNotes dateWithNotes = dateWithNotes((Date) characteristics.get(BasicStudyCharacteristic.STUDY_START).getValue());
			newChars.setStudyStart(dateWithNotes);
			convertOldNotes(characteristics.get(BasicStudyCharacteristic.STUDY_START).getNotes(), dateWithNotes.getNotes().getNote());
		}
		if (characteristics.containsKey(BasicStudyCharacteristic.STUDY_END)) {
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
	
	private static References convertReferences(PubMedIdList pubMedIdList) {
		References refs = new References();
		for(PubMedId x : pubMedIdList) {
			refs.getPubMedId().add(new BigInteger(x.getId()));
		}
		return refs;
	}

	public static Study.StudyOutcomeMeasure<?> convertStudyOutcomeMeasure(StudyOutcomeMeasure om, Domain domain) throws ConversionException {
		Variable var = null;
		if(om.getEndpoint() != null) {
			var = findEndpoint(domain, om.getEndpoint().getName());
		} else if(om.getAdverseEvent() != null) {
			var = findAdverseEvent(domain, om.getAdverseEvent().getName());
		} else if(om.getPopulationCharacteristic() != null) {
			var = findPopulationCharacteristic(domain, om.getPopulationCharacteristic().getName());
		} else {
			throw new ConversionException("StudyOutcomeMeasure type not supported: " + om.toString());
		}
		Study.StudyOutcomeMeasure<Variable> studyOutcomeMeasure = new Study.StudyOutcomeMeasure<Variable>(var);
		List<org.drugis.addis.entities.data.Note> notes = om.getNotes() == null ? Collections.<org.drugis.addis.entities.data.Note>emptyList() : om.getNotes().getNote();
		convertNotes(notes, studyOutcomeMeasure.getNotes());
		return studyOutcomeMeasure;
	}
	
	public static StudyOutcomeMeasure convertStudyOutcomeMeasure(org.drugis.addis.entities.Study.StudyOutcomeMeasure<?> studyOutcomeMeasure) throws ConversionException {
		StudyOutcomeMeasure newOutcome = new StudyOutcomeMeasure();
		newOutcome.setNotes(new Notes());
		NameReference value = new NameReference();
		value.setName(studyOutcomeMeasure.getValue().getName());
		if(studyOutcomeMeasure.getValue() instanceof Endpoint) {
			newOutcome.setEndpoint(value);
		} else if(studyOutcomeMeasure.getValue() instanceof AdverseEvent){
			newOutcome.setAdverseEvent(value);
		} else if(studyOutcomeMeasure.getValue() instanceof PopulationCharacteristic) {
			newOutcome.setPopulationCharacteristic(value);
		} else {
			throw new ConversionException("Unsupported type of StudyOutcomeMeasure: " + studyOutcomeMeasure);
		}
		newOutcome.setNotes(new Notes());
		convertOldNotes(studyOutcomeMeasure.getNotes(), newOutcome.getNotes().getNote());
		return newOutcome;
	}

	public static LinkedHashMap<String, Study.StudyOutcomeMeasure<?>> convertStudyOutcomeMeasures(StudyOutcomeMeasures oms, Domain domain) throws ConversionException {
		LinkedHashMap<String, Study.StudyOutcomeMeasure<?>> map = new LinkedHashMap<String, Study.StudyOutcomeMeasure<?>>();
		for(StudyOutcomeMeasure om : oms.getStudyOutcomeMeasure()) {
			map.put(om.getId(), convertStudyOutcomeMeasure(om, domain));
		}
		return map;
	}
	
	public static StudyOutcomeMeasures convertStudyOutcomeMeasures(LinkedHashMap<String, Study.StudyOutcomeMeasure<?>> linkedMap) throws ConversionException {
		StudyOutcomeMeasures measures = new StudyOutcomeMeasures();
		for(Entry<String, Study.StudyOutcomeMeasure<?>> item : linkedMap.entrySet()) {
			StudyOutcomeMeasure om = new StudyOutcomeMeasure();
			om = convertStudyOutcomeMeasure(item.getValue());
			om.setId(item.getKey());
			measures.getStudyOutcomeMeasure().add(om);
		}
		return measures;
	}

	
	public static LinkedHashMap<Integer, Arm> convertStudyArms(Arms arms, Domain domain) throws ConversionException {
		LinkedHashMap<Integer, Arm> map = new LinkedHashMap<Integer, Arm>();
		for(org.drugis.addis.entities.data.Arm a : arms.getArm()) {
			map.put(a.getId(), convertArm(a, domain));
		}
		return map;
	}
	
	public static Arms convertStudyArms(LinkedHashMap<Integer, Arm> map) throws ConversionException {
		Arms arms = new Arms();
		for (Entry<Integer, Arm> x : map.entrySet()) {
			org.drugis.addis.entities.data.Arm convertedArm = convertArm(x.getValue());
			convertedArm.setId(x.getKey());
			arms.getArm().add(convertedArm);
		}
		return arms;
	}
	
	public static Measurement convertMeasurement(org.drugis.addis.entities.data.Measurement m) throws ConversionException {
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
			return new FrequencyMeasurement(names.toArray(new String[]{}), map);
		}
		
		throw new ConversionException("Measurement type not supported: " + m.toString());
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
	
	public static Map<MeasurementKey, Measurement> convertMeasurements(Measurements measurements, Map<Integer, Arm> arms, Map<String, org.drugis.addis.entities.Study.StudyOutcomeMeasure<?>> outcomeMeasures) 
	throws ConversionException {
		Map<MeasurementKey, Measurement> map = new HashMap<MeasurementKey, Measurement>();
		for(org.drugis.addis.entities.data.Measurement m : measurements.getMeasurement()) {
			String omId = m.getStudyOutcomeMeasure().getId();
			Arm arm = m.getArm() != null ? arms.get(m.getArm().getId()) : null;
			map.put(new MeasurementKey(outcomeMeasures.get(omId).getValue(), arm), convertMeasurement(m));
		}
		return map;
	}
	
	public static Measurements convertMeasurements(Map<MeasurementKey, Measurement> map, Map<Integer, Arm> arms, Map<String, Study.StudyOutcomeMeasure<?>> oms) throws ConversionException {
		Measurements measurements = new Measurements();
		for (Entry<String, Study.StudyOutcomeMeasure<?>> omEntry : oms.entrySet()) {
			for (Entry<Integer, Arm> armEntry : arms.entrySet()) {
				findAndAddMeasurement(map, armEntry.getKey(), armEntry.getValue(), omEntry.getKey(), omEntry.getValue().getValue(), measurements);
			}
			findAndAddMeasurement(map, null, null, omEntry.getKey(), omEntry.getValue().getValue(), measurements);
		}
		return measurements;
	}


	private static void findAndAddMeasurement(
			Map<MeasurementKey, Measurement> source, 
			Integer armId, Arm arm,	String omId, Variable om,
			Measurements target)
	throws ConversionException {
		if (om instanceof OutcomeMeasure && arm == null) {
			return;
		}
		MeasurementKey key = new MeasurementKey(om, arm);
		if (source.containsKey(key)) {
			org.drugis.addis.entities.data.Measurement m = convertMeasurement(source.get(key));
			if (armId != null) {
				m.setArm(idReference(armId));
			}
			m.setStudyOutcomeMeasure(stringIdReference(omId));
			target.getMeasurement().add(m);
		}
	}

	public static <K, V> K findKey(Map<K,V> map, V value) {
		for (Entry<K, V> e : map.entrySet()) {
			if (e.getValue().equals(value)) {
				return e.getKey();
			}
		}
		return null;
	}
	
	static Study convertStudy(org.drugis.addis.entities.data.Study study, Domain domain) throws ConversionException {
		Study newStudy = new Study();
		newStudy.setStudyId(study.getName());
		newStudy.setIndication(findIndication(domain, study.getIndication().getName()));
		convertNotes(study.getIndication().getNotes().getNote(), newStudy.getIndicationWithNotes().getNotes());
		
		LinkedHashMap<String, Study.StudyOutcomeMeasure<?>> outcomeMeasures = convertStudyOutcomeMeasures(study.getStudyOutcomeMeasures(), domain);
//		System.out.println(study.getStudyOutcomeMeasures());
		for(Entry<String, Study.StudyOutcomeMeasure<?>> om : outcomeMeasures.entrySet()) {
			newStudy.addStudyOutcomeMeasure(om.getValue());
		}
		
		LinkedHashMap<Integer, Arm> arms = convertStudyArms(study.getArms(), domain);
		List<Integer> ids = new ArrayList<Integer>();
		for(Entry<Integer, Arm> arm : arms.entrySet()) { 
			newStudy.addArm(arm.getValue());
			ids.add(arm.getKey());
		}
		newStudy.setArmIds(ids);
		
		CharacteristicsMap map = convertStudyCharacteristics(study.getCharacteristics());
		newStudy.setCharacteristics(map);
		
		Map<MeasurementKey, Measurement> measurements = convertMeasurements(study.getMeasurements(), arms, outcomeMeasures);
		for(Entry<MeasurementKey, Measurement> m : measurements.entrySet()) {
			newStudy.setMeasurement(m.getKey(), m.getValue());
		}
		
		convertNotes(study.getNotes().getNote(), newStudy.getStudyIdWithNotes().getNotes());
		
		return newStudy;
	}
	
	public static org.drugis.addis.entities.data.Study convertStudy(Study study) throws ConversionException {
		org.drugis.addis.entities.data.Study newStudy = new org.drugis.addis.entities.data.Study();
		newStudy.setName(study.getStudyId());
		NameReferenceWithNotes indication = nameReferenceWithNotes(study.getIndication().getName());
		convertOldNotes(study.getIndicationWithNotes().getNotes(), indication.getNotes().getNote());
		newStudy.setIndication(indication);
		
		// convert arms
		LinkedHashMap<Integer, Arm> armMap = new LinkedHashMap<Integer, Arm>();
		for(int i = 0; i < study.getArms().size(); ++i) {
			armMap.put(study.getArmIds().get(i), study.getArms().get(i));
		}
		newStudy.setArms(convertStudyArms(armMap));
		
		// convert outcome measures
		LinkedHashMap<String, Study.StudyOutcomeMeasure<?>> omMap = new LinkedHashMap<String, Study.StudyOutcomeMeasure<?>>();
		for (Study.StudyOutcomeMeasure<Endpoint> e : study.getStudyEndpoints()) {
			omMap.put("endpoint-" + e.getValue().getName(), e);
		}
		for (org.drugis.addis.entities.Study.StudyOutcomeMeasure<AdverseEvent> e : study.getStudyAdverseEvents()) {
			omMap.put("adverseEvent-" + e.getValue().getName(), e);
		}
		for (org.drugis.addis.entities.Study.StudyOutcomeMeasure<PopulationCharacteristic> e : study.getStudyPopulationCharacteristics()) {
			omMap.put("popChar-" + e.getValue().getName(), e);
		}
		newStudy.setStudyOutcomeMeasures(convertStudyOutcomeMeasures(omMap));
		
		// convert measurements
		newStudy.setMeasurements(convertMeasurements(study.getMeasurements(), armMap, omMap));
		
		// convert characteristics
		newStudy.setCharacteristics(convertStudyCharacteristics(study.getCharacteristics()));
		
		Notes notes = new Notes();
		convertOldNotes(study.getStudyIdWithNotes().getNotes(), notes.getNote());
		newStudy.setNotes(notes);
		
		return newStudy ;
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
		for (int i = 0; i < baseArms.size(); ++i) {
			if (!baseArms.get(i).getStudy().equals(subjArms.get(i).getStudy())) {
				throw new ConversionException("Matching arms must be from the same study. Offending arms: " + 
						baseArms.get(i) + " -- " + subjArms.get(i) + " -- from " + pwma.getName());
			}
			Study study = findStudy(baseArms.get(i).getStudy(), domain);
			Arm base = findArm(study, baseArms.get(i).getId());
			Arm subj = findArm(study, subjArms.get(i).getId());
			studyArms.add(new StudyArmsEntry(study, base, subj));
		}
		
		Collections.sort(studyArms);
		
		return new RandomEffectsMetaAnalysis(pwma.getName(), om, studyArms);
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
		for(Drug d : reMa.getIncludedDrugs()) {
			Alternative alt = new Alternative();
			alt.setDrug(nameReference(d.getName()));
			AnalysisArms arms = new AnalysisArms();
			for(StudyArmsEntry item : reMa.getStudyArms()) {
				Arm arm = null;
				if (reMa.getFirstDrug().equals(d)) {
					arm = item.getBase();
				} else {
					arm = item.getSubject();
				}
				Integer armId = findArmId(item.getStudy(), arm);
				arms.getArm().add(armReference(item.getStudy().getStudyId(), armId));
			}
			alt.setArms(arms);
			pwma.getAlternative().add(alt);
		}
		return pwma ;
	}
	
	public static NetworkMetaAnalysis convertNetworkMetaAnalysis(org.drugis.addis.entities.data.NetworkMetaAnalysis nma, Domain domain) throws ConversionException {
		String name = nma.getName();
		Indication indication = findIndication(domain, nma.getIndication().getName());
		org.drugis.addis.entities.OutcomeMeasure om = findOutcomeMeasure(domain, nma);
		List<Study> studies = new ArrayList<Study>();		
		List<Drug> drugs = new ArrayList<Drug>();
		Map<Study, Map<Drug, Arm>> armMap = new HashMap<Study, Map<Drug,Arm>>();
		for (org.drugis.addis.entities.data.Alternative a : nma.getAlternative()) {
			Drug drug = findDrug(domain, a.getDrug().getName());
			drugs.add(drug);
			for (ArmReference armRef : a.getArms().getArm()) {
				Study study = findStudy(armRef.getStudy(), domain);
				if (!studies.contains(study)) {
					studies.add(study);
					armMap.put(study, new HashMap<Drug, Arm>());
				}
				Arm arm = findArm(study, armRef.getId());
				armMap.get(study).put(drug, arm);
			}
		}

		return new NetworkMetaAnalysis(name, indication, om, armMap);
	}
	
	public static org.drugis.addis.entities.data.NetworkMetaAnalysis convertNetworkMetaAnalysis(NetworkMetaAnalysis ma) throws ConversionException {
		org.drugis.addis.entities.data.NetworkMetaAnalysis nma = new org.drugis.addis.entities.data.NetworkMetaAnalysis();
		nma.setName(ma.getName());
		nma.setIndication(nameReference(ma.getIndication().getName()));
		if(ma.getOutcomeMeasure() instanceof Endpoint) {
			nma.setEndpoint(nameReference(ma.getOutcomeMeasure().getName()));
		} else if(ma.getOutcomeMeasure() instanceof AdverseEvent) {
			nma.setAdverseEvent(nameReference(ma.getOutcomeMeasure().getName()));
		} else {
			throw new ConversionException("Outcome Measure type not supported: " + ma.getOutcomeMeasure());
		}
		for(Drug d : ma.getIncludedDrugs()) {
			Alternative alt = new Alternative();
			alt.setDrug(nameReference(d.getName()));
			AnalysisArms arms = new AnalysisArms();
			
			for(Study study : ma.getIncludedStudies()) {
				Arm arm = ma.getArm(study, d);
				Integer id = findArmId(study, arm);
				if(id != null) {
					arms.getArm().add(armReference(study.getStudyId(), id));
				}
			}
			alt.setArms(arms);
			nma.getAlternative().add(alt);
		}
		
		return nma; 
	}

	public static Integer findArmId(Study study, Arm arm) {
		int index = study.getArms().indexOf(arm);
		if (index != -1) {
			return study.getArmIds().get(index);
		}
		return null;
	}

	private static Arm findArm(Study study, Integer id) {
		for (int i = 0; i < study.getArmIds().size(); ++i) {
			if (study.getArmIds().get(i).equals(id)) {
				return study.getArms().get(i);
			}
		}
		return null;
	}

	private static org.drugis.addis.entities.OutcomeMeasure findOutcomeMeasure(Domain domain, 
			org.drugis.addis.entities.data.MetaAnalysis ma)
	throws ConversionException {
		org.drugis.addis.entities.OutcomeMeasure om = null;
		if (ma.getEndpoint() != null) {
			om = findEndpoint(domain, ma.getEndpoint().getName());
		} else if (ma.getAdverseEvent() != null) {
			om = findAdverseEvent(domain, ma.getAdverseEvent().getName());
		} else {
			throw new ConversionException("MetaAnalysis has unsupported OutcomeMeasure: " + ma);
		}
		return om;
	}

	public static List<MetaAnalysis> convertMetaAnalyses(MetaAnalyses analyses, Domain domain) throws ConversionException {
		List<MetaAnalysis> list = new ArrayList<MetaAnalysis>();
		
		for(org.drugis.addis.entities.data.MetaAnalysis ma : analyses.getPairwiseMetaAnalysisOrNetworkMetaAnalysis()) {
			if(ma instanceof org.drugis.addis.entities.data.NetworkMetaAnalysis) {
				list.add(convertNetworkMetaAnalysis((org.drugis.addis.entities.data.NetworkMetaAnalysis)ma, domain));
			}else if(ma instanceof PairwiseMetaAnalysis) {
				list.add(convertPairWiseMetaAnalysis((PairwiseMetaAnalysis)ma, domain));
			}else {
				throw new ConversionException("Unsupported MetaAnalysis Type" + ma);
			}
		}
		return list;
	}
	
	public static MetaAnalyses convertMetaAnalyses(List<MetaAnalysis> list) throws ConversionException {
		MetaAnalyses analyses = new MetaAnalyses();
		for(MetaAnalysis ma : list) {
			if(ma instanceof NetworkMetaAnalysis) {
				analyses.getPairwiseMetaAnalysisOrNetworkMetaAnalysis().add(convertNetworkMetaAnalysis((NetworkMetaAnalysis) ma));
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
		
		Indication indication = findIndication(domain, br.getIndication().getName());
		Study study = findStudy(br.getStudy().getName(), domain);
		List<org.drugis.addis.entities.OutcomeMeasure> criteria = new ArrayList<org.drugis.addis.entities.OutcomeMeasure>();
		for (NameReference ref : br.getOutcomeMeasures().getEndpoint()) {
			criteria.add(findEndpoint(domain, ref.getName()));
		}
		for (NameReference ref : br.getOutcomeMeasures().getAdverseEvent()) {
			criteria.add(findAdverseEvent(domain, ref.getName()));
		}
		if (!br.getOutcomeMeasures().getPopulationCharacteristic().isEmpty()) {
			throw new ConversionException("PopulationCharacteristics not supported as criteria. " + br);
		}
		List<Arm> alternatives = new ArrayList<Arm>();
		for (ArmReference ref : br.getArms().getArm()) {
			alternatives.add(findArm(study, ref.getId()));
		}
		
		return new StudyBenefitRiskAnalysis(br.getName(), indication, study, criteria, alternatives, br.getAnalysisType());
	}
	
	public static org.drugis.addis.entities.data.StudyBenefitRiskAnalysis convertStudyBenefitRiskAnalysis(StudyBenefitRiskAnalysis br) throws ConversionException {
		org.drugis.addis.entities.data.StudyBenefitRiskAnalysis newBr = new org.drugis.addis.entities.data.StudyBenefitRiskAnalysis();
		
		newBr.setName(br.getName());
		newBr.setAnalysisType(br.getAnalysisType());
		newBr.setIndication(nameReference(br.getIndication().getName()));
		newBr.setStudy(nameReference(br.getStudy().getStudyId()));
		
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
			arms.getArm().add(armReference(br.getStudy().getStudyId(), findArmId(br.getStudy(), arm)));
		}
		newBr.setArms(arms);
		
		return newBr;
	}

	public static MetaBenefitRiskAnalysis convertMetaBenefitRiskAnalysis(
			org.drugis.addis.entities.data.MetaBenefitRiskAnalysis br, Domain domain) {
		Indication indication = findIndication(domain, br.getIndication().getName());
		Drug baseline = findDrug(domain, br.getBaseline().getName());
		List<Drug> drugs = new ArrayList<Drug>();
		for (NameReference ref : br.getDrugs().getDrug()) {
			drugs.add(findDrug(domain, ref.getName()));
		}
		List<MetaAnalysis> metaAnalysis = new ArrayList<MetaAnalysis>();
		for (NameReference ref : br.getMetaAnalyses().getMetaAnalysis()) {
			metaAnalysis.add(findMetaAnalysis(domain, ref.getName()));
		}
		drugs.remove(baseline);
		return new MetaBenefitRiskAnalysis(br.getName(), indication, metaAnalysis, baseline, drugs, br.getAnalysisType());
	}
	

	public static org.drugis.addis.entities.data.MetaBenefitRiskAnalysis convertMetaBenefitRiskAnalysis(MetaBenefitRiskAnalysis br) {
		org.drugis.addis.entities.data.MetaBenefitRiskAnalysis newBr = new org.drugis.addis.entities.data.MetaBenefitRiskAnalysis();
		newBr.setName(br.getName());
		newBr.setAnalysisType(br.getAnalysisType());
		newBr.setBaseline(nameReference(br.getBaseline().getName()));
		newBr.setIndication(nameReference(br.getIndication().getName()));
		
		DrugReferences drugRefs = new DrugReferences();
		for(Drug d : br.getDrugs()) {
			drugRefs.getDrug().add(nameReference(d.getName()));
		}
		newBr.setDrugs(drugRefs);
		
		MetaAnalysisReferences maRefs = new MetaAnalysisReferences();
		for(MetaAnalysis m : br.getMetaAnalyses()) {
			maRefs.getMetaAnalysis().add(nameReference(m.getName()));
		}
		
		newBr.setMetaAnalyses(maRefs);
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

	public static org.drugis.addis.entities.data.IntegerWithNotes intWithNotes(int centers) {
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
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String calFormat = dateFormat.format(date);
			dateWithNotes.setValue(XMLGregorianCalendarImpl.parse(calFormat));
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

	public static ArmReference armReference(String study_name, org.drugis.addis.entities.data.Arm arm1) {
		Integer id = arm1.getId();
		return armReference(study_name, id);
	}

	private static ArmReference armReference(String study_name, Integer id) {
		ArmReference fluoxArmRef = new ArmReference();
		fluoxArmRef.setStudy(study_name);
		fluoxArmRef.setId(id);
		return fluoxArmRef;
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

	public static InputStream transformLegacyXML(InputStream xmlFile)
	throws TransformerException, IOException {
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		TransformerFactory tFactory = TransformerFactory.newInstance(); 
		InputStream xsltFile = JAXBConvertor.class.getResourceAsStream("transform-0-1.xslt");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
	    javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(xmlFile);
	    javax.xml.transform.Source xsltSource = new javax.xml.transform.stream.StreamSource(xsltFile);
	    javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(os);
	    
	    javax.xml.transform.Transformer trans = tFactory.newTransformer(xsltSource);
	    trans.transform(xmlSource, result);
	    os.close();
	
	    return new ByteArrayInputStream(os.toByteArray());
	}
}
