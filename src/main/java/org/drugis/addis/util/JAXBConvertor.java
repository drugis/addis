package org.drugis.addis.util;

import java.util.ArrayList;
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
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.PubMedId;
import org.drugis.addis.entities.PubMedIdList;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.Study.MeasurementKey;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.entities.data.Arms;
import org.drugis.addis.entities.data.Category;
import org.drugis.addis.entities.data.Characteristics;
import org.drugis.addis.entities.data.Measurements;
import org.drugis.addis.entities.data.OutcomeMeasure;
import org.drugis.addis.entities.data.PairwiseMetaAnalysis;
import org.drugis.addis.entities.data.References;
import org.drugis.addis.entities.data.StudyOutcomeMeasure;
import org.drugis.addis.entities.data.StudyOutcomeMeasures;
import org.drugis.common.Interval;

public class JAXBConvertor {
	
	@SuppressWarnings("serial")
	public static class ConversionException extends Exception {
		public ConversionException(String s) {
			super(s);
		}
	}
	
	private JAXBConvertor() {}
	
	public static Domain addisDataToDomain(AddisData addisData) throws ConversionException {
		Domain newDomain = new DomainImpl();
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
		// Benefit-risk analyses
		return newDomain;	
	}

	static Study convertStudy(org.drugis.addis.entities.data.Study study, Domain domain) throws ConversionException {
		Study newStudy = new Study();
		newStudy.setStudyId(study.getName());
		newStudy.setIndication(findIndication(domain, study.getIndication().getName()));
		
		// TODO: workin here !!
		LinkedHashMap<String, Variable> outcomeMeasures = convertStudyOutcomeMeasures(study.getStudyOutcomeMeasures(), domain);
		for(Entry<String, Variable> om : outcomeMeasures.entrySet()) {
			newStudy.addOutcomeMeasure(om.getValue());
		}
		
		LinkedHashMap<Integer, Arm> arms = convertStudyArms(study.getArms(), domain);
		for(Entry<Integer, Arm> arm : arms.entrySet()) { 
			newStudy.addArm(arm.getValue());
		}
		
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

	static Indication convertIndication(
			org.drugis.addis.entities.data.Indication i) {
		return new Indication(i.getCode(), i.getName());
	}

	static Drug convertDrug(org.drugis.addis.entities.data.Drug d) {
		return new Drug(d.getName(), d.getAtcCode());
	}

	static Endpoint convertEndpoint(org.drugis.addis.entities.data.OutcomeMeasure om) throws ConversionException {
		Endpoint e = new Endpoint();
		convertOutcomeMeasure(om, e);
		return e;
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

	public static AddisData domainToAddisData(Domain domain) {
		return null;
	}

	static Variable convertPopulationCharacteristic(OutcomeMeasure m) throws ConversionException {
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

	public static LinkedHashMap<String, Variable> convertStudyOutcomeMeasures(StudyOutcomeMeasures oms, Domain domain) throws ConversionException {
		LinkedHashMap<String, Variable> map = new LinkedHashMap<String, Variable>();
		for(StudyOutcomeMeasure om : oms.getStudyOutcomeMeasure()) {
			map.put(om.getId(), convertStudyOutcomeMeasure(om, domain));
		}
		return map;
	}
	
	public static LinkedHashMap<Integer, Arm> convertStudyArms(Arms arms, Domain domain) throws ConversionException {
		LinkedHashMap<Integer, Arm> map = new LinkedHashMap<Integer, Arm>();
		for(org.drugis.addis.entities.data.Arm a : arms.getArm()) {
			map.put(a.getId(), convertArm(a, domain));
		}
		return map;
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

	public static RandomEffectsMetaAnalysis convertPairWiseMetaAnalysis(PairwiseMetaAnalysis pwma, Domain domain) {
		
		List<StudyArmsEntry> studyArms;
		
		StudyArmsEntry e = new StudyArmsEntry(study, base, pwma.getEndpoint());
		studyArms.add(e);
		org.drugis.addis.entities.OutcomeMeasure om;
		
		return new RandomEffectsMetaAnalysis(pwma.getName(), om, studyArms);
	}
}
