package nl.rug.escher.addis.analyses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.rug.escher.addis.entities.ContinuousMeasurement;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.OddsRatio;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.RateContinuousAdapter;
import nl.rug.escher.addis.entities.RateMeasurement;
import nl.rug.escher.addis.entities.Study;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.GaussianCriterion;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.LogNormalCriterion;

public class SMAACriterionAdapter {
	
	public static Criterion buildCriterion(
			Endpoint e, Study s, List<Alternative> alts) {
		
		if (e.getType().equals(Endpoint.Type.CONTINUOUS)) {
			return buildGaussianCriterion(e, s, alts);
		} else if (e.getType().equals(Endpoint.Type.RATE)) {
			return buildLogNormalCriterion(e, s, alts);
		}
		
		return null;
	}
	
	private static Criterion buildLogNormalCriterion(
			Endpoint e, Study s, List<Alternative> alts) {
		
		LogNormalCriterion crit = new LogNormalCriterion(e.getName());
		crit.setAlternatives(alts);
		
		if (alts.size() < 1) {
			return crit;
		}
		
		Map<Alternative, GaussianMeasurement> meas = new HashMap<Alternative, GaussianMeasurement>();
		
		ContinuousMeasurement first = getAdaptedRate(s.getPatientGroups().get(0), e);
		for (PatientGroup g : s.getPatientGroups()) {
			OddsRatio od = new OddsRatio(getAdaptedRate(g, e), first);
			meas.put(getAlternative(alts, g), new GaussianMeasurement(
					Math.log(od.getMean()), Math.log(od.getStdDev())));
		}
		crit.setMeasurements(meas);
		
		return crit;
	}
	
	private static ContinuousMeasurement getAdaptedRate(PatientGroup g, Endpoint e) {
		return new RateContinuousAdapter((RateMeasurement)g.getMeasurement(e));
	}

	private static Alternative getAlternative(List<Alternative> list, PatientGroup g) {
		return list.get(list.indexOf(new Alternative(g.getLabel())));
	}

	private static Criterion buildGaussianCriterion(
			Endpoint e, Study s, List<Alternative> alts) {

		GaussianCriterion crit = new GaussianCriterion(e.getName());
		crit.setAlternatives(alts);
		
		Map<Alternative, GaussianMeasurement> meas = new HashMap<Alternative, GaussianMeasurement>();
		
		for (PatientGroup g : s.getPatientGroups()) {
			ContinuousMeasurement cm = (ContinuousMeasurement) g.getMeasurement(e);
			meas.put(getAlternative(alts, g), new GaussianMeasurement(cm.getMean(), cm.getStdDev()));
		}
		crit.setMeasurements(meas);
		
		return crit;
	}

}
