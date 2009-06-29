/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package nl.rug.escher.addis.analyses;

import nl.rug.escher.addis.entities.ContinuousMeasurement;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.RateMeasurement;
import nl.rug.escher.addis.entities.RiskRatio;
import nl.rug.escher.addis.entities.Study;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.CardinalCriterion;
import fi.smaa.jsmaa.model.CardinalMeasurement;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.LogNormalMeasurement;
import fi.smaa.jsmaa.model.NoSuchValueException;
import fi.smaa.jsmaa.model.SMAAModel;

public class SMAAAdapter {
	
	public static SMAAModel getModel(Study study) {
		SMAAModel model = new SMAAModel(study.getId());
		addAlternativesToModel(study, model);
		
		try {
			addCriteriaToModel(study, model);
		} catch (NoSuchValueException e) {
			throw new IllegalStateException("illegal state in model");
		}
		return model;
	}

	private static void addCriteriaToModel(Study study, SMAAModel model) throws NoSuchValueException {
		for (Endpoint e : study.getEndpoints()) {
			buildCriterion(model, e, study);	
		}
	}

	private static void buildCriterion(SMAAModel model, Endpoint e,
			Study study) throws NoSuchValueException {
		CardinalCriterion crit = new CardinalCriterion(e.getName());
		model.addCriterion(crit);
		
		if (e.getType().equals(Endpoint.Type.RATE)) {
			RateMeasurement first = null;
			for (int i=0;i<study.getPatientGroups().size();i++) {
				CardinalMeasurement meas = null;
				PatientGroup g = study.getPatientGroups().get(i);				
				if (i == 0) {
					first = (RateMeasurement)study.getMeasurement(e, study.getPatientGroups().get(0));
					meas = new LogNormalMeasurement(1.0, 0.0);
				} else {
					RateMeasurement other = (RateMeasurement)study.getMeasurement(e, g);
					RiskRatio od = new RiskRatio(first, other);
					meas = new LogNormalMeasurement(
							od.getMean(), od.getStdDev());							
				}

				Alternative alt = findAlternative(g, model);
				model.getImpactMatrix().setMeasurement(crit, alt, meas);				
			}
		} else if (e.getType().equals(Endpoint.Type.CONTINUOUS)) {
			for (PatientGroup g : study.getPatientGroups()) {
				Alternative alt = findAlternative(g, model);				
				ContinuousMeasurement cm = (ContinuousMeasurement) study.getMeasurement(e, g);
				GaussianMeasurement meas = new GaussianMeasurement(cm.getMean(), cm.getStdDev());
				model.getImpactMatrix().setMeasurement(crit, alt, meas);
			}			
		} else {
			throw new RuntimeException("Unknown endpoint type");
		}
	}
	
	private static void addAlternativesToModel(Study study, SMAAModel model) {
		for (PatientGroup d : study.getPatientGroups()) {
			model.addAlternative(new Alternative(d.getLabel()));
		}
	}

	public static Alternative findAlternative(PatientGroup g, SMAAModel model) {
		for (Alternative a : model.getAlternatives()) {
			if (a.getName().equals(g.getLabel())) {
				return a;
			}
		}
		throw new IllegalStateException("Alternative not found");
	}

}
