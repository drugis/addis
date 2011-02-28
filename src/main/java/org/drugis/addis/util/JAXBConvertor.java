package org.drugis.addis.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.CategoricalPopulationCharacteristic;
import org.drugis.addis.entities.Characteristic;
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
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;
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
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.entities.data.ArmReference;
import org.drugis.addis.entities.data.Arms;
import org.drugis.addis.entities.data.BenefitRiskAnalyses;
import org.drugis.addis.entities.data.CategoricalMeasurement;
import org.drugis.addis.entities.data.CategoricalVariable;
import org.drugis.addis.entities.data.Category;
import org.drugis.addis.entities.data.Characteristics;
import org.drugis.addis.entities.data.ContinuousMeasurement;
import org.drugis.addis.entities.data.ContinuousVariable;
import org.drugis.addis.entities.data.DateWithNotes;
import org.drugis.addis.entities.data.IdReference;
import org.drugis.addis.entities.data.Measurements;
import org.drugis.addis.entities.data.MetaAnalyses;
import org.drugis.addis.entities.data.NameReference;
import org.drugis.addis.entities.data.NetworkMetaAnalysis;
import org.drugis.addis.entities.data.Notes;
import org.drugis.addis.entities.data.PairwiseMetaAnalysis;
import org.drugis.addis.entities.data.RateMeasurement;
import org.drugis.addis.entities.data.RateVariable;
import org.drugis.addis.entities.data.References;
import org.drugis.addis.entities.data.StringIdReference;
import org.drugis.addis.entities.data.StringWithNotes;
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
	
	public static Domain addisDataToDomain(AddisData addisData) throws ConversionException {
		Domain newDomain = new org.drugis.addis.entities.DomainImpl();
		for (org.drugis.addis.entities.data.OutcomeMeasure om : addisData.getEndpoints().getEndpoint()) {
			newDomain.addEndpoint(convertEndpoint(om));
		}
		for (org.drugis.addis.entities.data.Drug d : addisData.getDrugs().getDrug()) {
			newDomain.addDrug(convertDrug(d));
		}
		for(org.drugis.addis.entities.data.Indication i : addisData.getIndications().getIndication()) {
			newDomain.addIndication(convertIndication(i));
		}
		for(org.drugis.addis.entities.data.OutcomeMeasure ae : addisData.getAdverseEvents().getAdverseEvent()) {
			newDomain.addAdverseEvent(convertAdverseEvent(ae));
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

	static Study convertStudy(org.drugis.addis.entities.data.Study study, Domain domain) throws ConversionException {
		Study newStudy = new Study();
		newStudy.setStudyId(study.getName());
		newStudy.setIndication(findIndication(domain, study.getIndication().getName()));
		
		LinkedHashMap<String, Variable> outcomeMeasures = convertStudyOutcomeMeasures(study.getStudyOutcomeMeasures(), domain);
		for(Entry<String, Variable> om : outcomeMeasures.entrySet()) {
			newStudy.addOutcomeMeasure(om.getValue());
		}
		
		LinkedHashMap<Integer, Arm> arms = convertStudyArms(study.getArms(), domain);
		List<Integer> ids = new ArrayList<Integer>();
		for(Entry<Integer, Arm> arm : arms.entrySet()) { 
			newStudy.addArm(arm.getValue());
			ids.add(arm.getKey());
		}
		newStudy.setArmIds(ids);
		
		CharacteristicsMap map = convertStudyCharacteristics(study.getCharacteristics());
		for(Entry<Characteristic, Object> m : map.entrySet()) {
			newStudy.setCharacteristic((BasicStudyCharacteristic) m.getKey(), m.getValue());
		}
		
		Map<MeasurementKey, Measurement> measurements = convertMeasurements(study.getMeasurements(), arms, outcomeMeasures);
		for(Entry<MeasurementKey, Measurement> m : measurements.entrySet()) {
			newStudy.setMeasurement(m.getKey(), m.getValue());
		}
		
		return newStudy;
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
	
	static Arm convertArm(org.drugis.addis.entities.data.Arm arm, Domain domain) throws ConversionException {
		Drug d = findDrug(domain, arm.getDrug().getName());
		
		if(arm.getFixedDose() != null) {
			FixedDose fixDose = new FixedDose(arm.getFixedDose().getQuantity(), arm.getFixedDose().getUnit());
			Arm newArm = new Arm(d, fixDose, arm.getSize().intValue());
			return newArm;
		}
		else if(arm.getFlexibleDose() != null) {
			FlexibleDose flexDose = new FlexibleDose(new Interval<Double> (
													(double) arm.getFlexibleDose().getMinDose(), 
													(double) arm.getFlexibleDose().getMaxDose()
												 ), arm.getFlexibleDose().getUnit());
			Arm newArm = new Arm(d, flexDose, arm.getSize());
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
		//newArm.setId(arm.get)
		newArm.setNotes(new Notes());

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

	public static AddisData domainToAddisData(Domain domain) {
		return null;
	}

	static Variable convertPopulationCharacteristic(org.drugis.addis.entities.data.OutcomeMeasure m) throws ConversionException {
		if(m.getRate() != null) {
			throw new ConversionException("Population Characteristics should not be rate");
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
		/*org.drugis.addis.entities.data.OutcomeMeasure om = new org.drugis.addis.entities.data.OutcomeMeasure();
		om.setName(pc.getName());
		om.setDescription(pc.getDescription());
		om.setContinuous()*/
		
		return convertOutcomeMeasure(pc);
	}
	
	private static PubMedIdList getPubMedIds(References refs) {
		PubMedIdList pubMedList = new PubMedIdList();
		for(Integer ref : refs.getPubMedId()) {
			pubMedList.add(new PubMedId(ref.toString()));
		}
		return pubMedList;
	}

	public static CharacteristicsMap convertStudyCharacteristics(Characteristics chars1) {
		CharacteristicsMap map = new CharacteristicsMap();
		if (chars1.getAllocation() != null) {
			map.put(BasicStudyCharacteristic.ALLOCATION, chars1.getAllocation().getValue());
		}
		map.put(BasicStudyCharacteristic.TITLE, chars1.getTitle().getValue());
		if (chars1.getBlinding() != null) {
			map.put(BasicStudyCharacteristic.BLINDING, chars1.getBlinding().getValue());
		}
		if (chars1.getCenters() != null) {
			map.put(BasicStudyCharacteristic.CENTERS, chars1.getCenters().getValue());
		}
		if (chars1.getObjective() != null) {
			map.put(BasicStudyCharacteristic.OBJECTIVE, chars1.getObjective().getValue());
		}
		if (chars1.getStudyStart() != null) {
			map.put(BasicStudyCharacteristic.STUDY_START, chars1.getStudyStart().getValue().toGregorianCalendar().getTime());
		}
		if (chars1.getStudyEnd() != null) {
			map.put(BasicStudyCharacteristic.STUDY_END, chars1.getStudyEnd().getValue().toGregorianCalendar().getTime());
		}
		if (chars1.getInclusion() != null) {
			map.put(BasicStudyCharacteristic.INCLUSION, chars1.getInclusion().getValue());
		}
		if (chars1.getExclusion() != null) {
				map.put(BasicStudyCharacteristic.EXCLUSION, chars1.getExclusion().getValue());
		}
		if (chars1.getStatus() != null) {
			map.put(BasicStudyCharacteristic.STATUS, chars1.getStatus().getValue());
		}
		if (chars1.getSource() != null) {
			map.put(BasicStudyCharacteristic.SOURCE, chars1.getSource().getValue());
		}
		if (chars1.getCreationDate() != null) {
			map.put(BasicStudyCharacteristic.CREATION_DATE, chars1.getCreationDate().getValue().toGregorianCalendar().getTime());
		}
		map.put(BasicStudyCharacteristic.PUBMED, getPubMedIds(chars1.getReferences()));
		return map;
	}	

	public static Characteristics convertStudyCharacteristics(CharacteristicsMap characteristics) {
		Characteristics newChars = new Characteristics();
		newChars.setAllocation(allocationWithNotes((Allocation) characteristics.get(BasicStudyCharacteristic.ALLOCATION)));
		newChars.setBlinding(blindingWithNotes((Blinding) characteristics.get(BasicStudyCharacteristic.BLINDING)));
		newChars.setCenters(intWithNotes((Integer) characteristics.get(BasicStudyCharacteristic.CENTERS)));
		newChars.setCreationDate(dateWithNotes((Date) characteristics.get(BasicStudyCharacteristic.CREATION_DATE)));
		newChars.setExclusion(stringWithNotes((String) characteristics.get(BasicStudyCharacteristic.EXCLUSION)));
		newChars.setInclusion(stringWithNotes((String) characteristics.get(BasicStudyCharacteristic.INCLUSION)));
		newChars.setObjective(stringWithNotes((String) characteristics.get(BasicStudyCharacteristic.OBJECTIVE)));
		newChars.setReferences(convertReferences((PubMedIdList)characteristics.get(BasicStudyCharacteristic.PUBMED)));
		newChars.setSource(sourceWithNotes((Source) characteristics.get(BasicStudyCharacteristic.SOURCE)));
		newChars.setStatus(statusWithNotes((Status) characteristics.get(BasicStudyCharacteristic.STATUS)));
		newChars.setStudyStart(dateWithNotes((Date) characteristics.get(BasicStudyCharacteristic.STUDY_START)));
		newChars.setStudyEnd(dateWithNotes((Date) characteristics.get(BasicStudyCharacteristic.STUDY_END)));
		newChars.setTitle(stringWithNotes((String) characteristics.get(BasicStudyCharacteristic.TITLE)));
		return newChars;
	}
	
	private static References convertReferences(PubMedIdList pubMedIdList) {
		References refs = new References();
		for(PubMedId x : pubMedIdList) {
			refs.getPubMedId().add(Integer.parseInt(x.getId()));
		}
		return refs;
	}

	public static org.drugis.addis.entities.Variable convertStudyOutcomeMeasure(StudyOutcomeMeasure om, Domain domain) throws ConversionException {
		if(om.getEndpoint() != null) {
			return findEndpoint(domain, om.getEndpoint().getName());
		}
		if(om.getAdverseEvent() != null) {
			return findAdverseEvent(domain, om.getAdverseEvent().getName());
		}
		if(om.getPopulationCharacteristic() != null) {
			return findPopulationCharacteristic(domain, om.getPopulationCharacteristic().getName());
		}
		
		throw new ConversionException("StudyOutcomeMeasure type not supported: " + om.toString());
	}
	
	public static StudyOutcomeMeasure convertStudyOutcomeMeasure(Variable var) throws ConversionException {
		StudyOutcomeMeasure newOutcome = new StudyOutcomeMeasure();
		NameReference value = new NameReference();
		value.setName(var.getName());
		if(var instanceof Endpoint) {
			newOutcome.setEndpoint(value);
		} else if(var instanceof AdverseEvent){
			newOutcome.setAdverseEvent(value);
		} else if(var instanceof PopulationCharacteristic) {
			newOutcome.setPopulationCharacteristic(value);
		} else {
			throw new ConversionException("Unsupported type of StudyOutcomeMeasure: " + var);
		}
		return newOutcome;
	}

	public static LinkedHashMap<String, Variable> convertStudyOutcomeMeasures(StudyOutcomeMeasures oms, Domain domain) throws ConversionException {
		LinkedHashMap<String, Variable> map = new LinkedHashMap<String, Variable>();
		for(StudyOutcomeMeasure om : oms.getStudyOutcomeMeasure()) {
			map.put(om.getId(), convertStudyOutcomeMeasure(om, domain));
		}
		return map;
	}
	
	public static StudyOutcomeMeasures convertStudyOutcomeMeasures(LinkedHashMap<String, Variable> linkedMap) throws ConversionException {
		StudyOutcomeMeasures measures = new StudyOutcomeMeasures();
		for(Entry<String, Variable> item : linkedMap.entrySet()) {
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
			for(Category c : m.getCategoricalMeasurement().getCategory()) {
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
				Category newCat = new Category();
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
	
	public static Map<MeasurementKey, Measurement> convertMeasurements(Measurements measurements, Map<Integer, Arm> arms, Map<String, Variable> oms) 
	throws ConversionException {
		Map<MeasurementKey, Measurement> map = new HashMap<MeasurementKey, Measurement>();
		for(org.drugis.addis.entities.data.Measurement m : measurements.getMeasurement()) {
			String omId = m.getStudyOutcomeMeasure().getId();
			Arm arm = m.getArm() != null ? arms.get(m.getArm().getId()) : null;
			map.put(new MeasurementKey(oms.get(omId), arm), convertMeasurement(m));
		}
		return map;
	}
	
	public static Measurements convertMeasurements(Map<MeasurementKey, Measurement> map, Map<Integer, Arm> arms, Map<String, Variable> oms) throws ConversionException {
		Measurements measurements = new Measurements();
		for(Entry<MeasurementKey, Measurement> item : map.entrySet()) {
			org.drugis.addis.entities.data.Measurement m = convertMeasurement(item.getValue());
			Integer armId = findKey(arms, item.getKey().getArm());
			if (armId != null) {
				m.setArm(idReference(armId));
			}
			m.setStudyOutcomeMeasure(stringIdReference(findKey(oms, item.getKey().getVariable())));
			measurements.getMeasurement().add(m);
		}
		return measurements ;
	}
	
	public static <K, V> K findKey(Map<K,V> map, V value) {
		for (Entry<K, V> e : map.entrySet()) {
			if (e.getValue().equals(value)) {
				return e.getKey();
			}
		}
		return null;
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
	
	public static MetaAnalysis convertNetworkMetaAnalysis(NetworkMetaAnalysis nma, Domain domain) throws ConversionException {
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

		return new org.drugis.addis.entities.analysis.NetworkMetaAnalysis(name, indication, om, armMap);
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
			if(ma instanceof NetworkMetaAnalysis) {
				list.add(convertNetworkMetaAnalysis((NetworkMetaAnalysis)ma, domain));
			}else if(ma instanceof PairwiseMetaAnalysis) {
				list.add(convertPairWiseMetaAnalysis((PairwiseMetaAnalysis)ma, domain));
			}else {
				throw new ConversionException("Unsupported MetaAnalysis Type" + ma);
			}
		}
		return list;
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
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		return dateWithNotes(cal);
	}
	
	public static DateWithNotes dateWithNotes(GregorianCalendar cal) {
		org.drugis.addis.entities.data.DateWithNotes date = new org.drugis.addis.entities.data.DateWithNotes();
		date.setValue(new XMLGregorianCalendarImpl(cal));
		date.setNotes(new Notes());
		return date;
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
}
