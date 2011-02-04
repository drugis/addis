package org.drugis.addis.util;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.entities.data.ContinuousVariable;
import org.drugis.addis.entities.data.Direction;
import org.drugis.addis.entities.data.RateVariable;

public class JAXBConvertor {
	
	@SuppressWarnings("serial")
	public static class ConversionException extends Exception {
		public ConversionException(String s) {
			super(s);
		}
	}
	
	public static Domain addisDataToDomain(AddisData addisData) throws ConversionException { // TODO : Ask Gert about static/throws
		Domain newDomain = new DomainImpl();
		for (org.drugis.addis.entities.data.OutcomeMeasure om : addisData.getEndpoints().getEndpoint()) {
			Endpoint e = new Endpoint();
			e.setName(om.getName());
			if (om.getCategorical() != null) {
				throw(new ConversionException("Endpoints should not be categorical (yet)"));
			} else if (om.getContinuous() != null) {
				e.setType(Type.CONTINUOUS);
				e.setUnitOfMeasurement(om.getContinuous().getUnitOfMeasurement());
				e.setDirection(dirToDir(om.getContinuous()));
			} else if (om.getRate() != null) {
				e.setType(Type.RATE);
				e.setDirection(dirToDir(om.getRate()));
			}
			newDomain.addEndpoint(e);
		}
		for (org.drugis.addis.entities.data.Drug d : addisData.getDrugs().getDrug()) {
			newDomain.addDrug(new Drug(d.getName(), d.getAtcCode()));
		}
		for(org.drugis.addis.entities.data.Indication i : addisData.getIndications().getIndication()) {
			newDomain.addIndication(new Indication(i.getCode().longValue(), i.getName()));
		}
		for(org.drugis.addis.entities.data.OutcomeMeasure ae : addisData.getAdverseEvents().getAdverseEvent()) {
			AdverseEvent a = new AdverseEvent();
			a.setName(ae.getName());
			newDomain.addAdverseEvent(a);
		}
		for(org.drugis.addis.entities.data.Study s : addisData.getStudies().getStudy()) {
			Study st = new Study();
			st.setStudyId(s.getName());
			Indication findIndication = findIndication(newDomain, s.getIndication().getName());
			st.setIndication(findIndication);
			// of course other fields should also be copied; apparently assertentityequals does not check deeply in order
			newDomain.addStudy(st);
		}
		return newDomain;	
	}

	private static Indication findIndication(Domain domain, String name) {
		for (Indication i : domain.getIndications()) {
			if (i.getName().equals(name)) {
				return i;
			}
		}
		return null;
	}

	private static org.drugis.addis.entities.OutcomeMeasure.Direction dirToDir(RateVariable rate) {
		return rate.getDirection() == Direction.HIGHER_IS_BETTER ? 
				org.drugis.addis.entities.OutcomeMeasure.Direction.HIGHER_IS_BETTER :
				org.drugis.addis.entities.OutcomeMeasure.Direction.LOWER_IS_BETTER;
	}

	private static org.drugis.addis.entities.OutcomeMeasure.Direction dirToDir(ContinuousVariable continuousVariable) {
		return continuousVariable.getDirection() == Direction.HIGHER_IS_BETTER ? 
				org.drugis.addis.entities.OutcomeMeasure.Direction.HIGHER_IS_BETTER :
				org.drugis.addis.entities.OutcomeMeasure.Direction.LOWER_IS_BETTER;
	}

	public static AddisData domainToAddisData(Domain domain) {
		return null;
	}

}
