package org.drugis.addis.util;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.CategoricalPopulationCharacteristic;
import org.drugis.addis.entities.CharacteristicsMap;
import org.drugis.addis.entities.ContinuousPopulationCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.entities.data.Characteristics;
import org.drugis.addis.entities.data.ContinuousVariable;
import org.drugis.addis.entities.data.Direction;
import org.drugis.addis.entities.data.OutcomeMeasure;
import org.drugis.addis.entities.data.RateVariable;
import org.drugis.addis.entities.data.Unit;
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

	static Study convertStudy(org.drugis.addis.entities.data.Study s,
			Domain newDomain) {
		Study st = new Study();
		st.setStudyId(s.getName());
		Indication findIndication = findIndication(newDomain, s.getIndication().getName());
		st.setIndication(findIndication);
		// of course other fields should also be copied; apparently assertentityequals does not check deeply in order
		// WHAT?!
		return st;
	}

	static AdverseEvent convertAdverseEvent(org.drugis.addis.entities.data.OutcomeMeasure ae) throws ConversionException {
		AdverseEvent a = new AdverseEvent();
		convertOutcomeMeasure(ae, a);
		return a;
	}

	static Indication convertIndication(
			org.drugis.addis.entities.data.Indication i) {
		return new Indication(i.getCode().longValue(), i.getName());
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
			Arm newArm = new Arm(d, flexDose, arm.getSize().intValue());
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

	public static CharacteristicsMap convertStudyCharacteristics(Characteristics chars1) {
		CharacteristicsMap map = new CharacteristicsMap();
		map.put(BasicStudyCharacteristic.ALLOCATION, chars1.getAllocation().getValue());
		map.put(BasicStudyCharacteristic.TITLE, chars1.getTitle().getValue());
		return map;
	}

}
