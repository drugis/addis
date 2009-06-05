package nl.rug.escher.addis.analyses;

import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.Study;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.AlternativeExistsException;
import fi.smaa.jsmaa.model.SMAAModel;

public class SMAAAdapter {
	
	public static SMAAModel getModel(Study study) {
		SMAAModel model = new SMAAModel(study.getId());
		addAlternativesToModel(study, model);
		addCriteriaToModel(study, model);
		return model;
	}

	private static void addCriteriaToModel(Study study, SMAAModel model) {
		for (Endpoint e : study.getEndpoints()) {
			model.addCriterion(SMAACriterionAdapter.buildCriterion(e, study, model.getAlternatives()));
		}
	}

	private static void addAlternativesToModel(Study study, SMAAModel model) {
		for (PatientGroup d : study.getPatientGroups()) {
			try {
				model.addAlternative(new Alternative(d.getLabel()));
			} catch (AlternativeExistsException e) {
				throw new IllegalStateException(e);
			}
		}
	}

}
