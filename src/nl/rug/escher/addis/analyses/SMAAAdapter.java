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

import java.util.HashSet;
import java.util.Set;

import nl.rug.escher.addis.entities.CombinedStudy;
import nl.rug.escher.addis.entities.ContinuousMeasurement;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.LogRiskRatio;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.RateMeasurement;
import nl.rug.escher.addis.entities.RiskRatio;
import nl.rug.escher.addis.entities.Study;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.CardinalCriterion;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.LogNormalMeasurement;
import fi.smaa.jsmaa.model.NoSuchValueException;
import fi.smaa.jsmaa.model.SMAAModel;

public class SMAAAdapter {
	
	public static SMAAModel getModel(Study study) throws UnableToBuildModelException {
		SMAAModel model = new SMAAModel(study.getId());
		addAlternativesToModel(study, model);
		
		try {
			addCriteriaToModel(study, model);
		} catch (NoSuchValueException e) {
			throw new IllegalStateException("illegal state in model");
		}
		return model;
	}

	private static void addCriteriaToModel(Study study, SMAAModel model) throws NoSuchValueException, UnableToBuildModelException {
		for (Endpoint e : study.getEndpoints()) {
			buildCriterion(model, e, study);	
		}
	}
	
	private static void buildCriterion(SMAAModel model, Endpoint e,
			Study study) throws NoSuchValueException, UnableToBuildModelException {
		CardinalCriterion crit = new CardinalCriterion(e.getName());
		model.addCriterion(crit);

		if (e.getType().equals(Endpoint.Type.RATE)) {
			buildRateMeasurement(model, crit, e, study);
		} else if (e.getType().equals(Endpoint.Type.CONTINUOUS)) {
			for (Drug d : study.getDrugs()) {
				Alternative alt = findAlternative(d, model);
				PatientGroup g = findPatientGroupForDrug(study, d);
				ContinuousMeasurement cm = (ContinuousMeasurement) study.getMeasurement(e, g);
				GaussianMeasurement meas = new GaussianMeasurement(cm.getMean(), cm.getStdDev());
				model.getImpactMatrix().setMeasurement(crit, alt, meas);
			}			
		} else {
			throw new RuntimeException("Unknown endpoint type");
		}
	}
	
	private static void buildRateMeasurement(SMAAModel model,
			CardinalCriterion crit, Endpoint e, Study study) throws UnableToBuildModelException, NoSuchValueException {
		if (study instanceof CombinedStudy) {
			CombinedStudy cs = (CombinedStudy) study;
			Set<Drug> drugs = cs.getCommonDrugs();
			if (drugs.size() != 1) {
				throw new UnableToBuildModelException("Not exactly 1 common drug in studies");
			}
			Drug commonDrug = drugs.iterator().next();
			// measurement for the common drug
			model.getImpactMatrix().setMeasurement(crit, findAlternative(commonDrug, model), 
					new LogNormalMeasurement(0.0, 0.0));
			// rest of the measurement
			// one measurement per study
			for (Study s : cs.getStudies()) {
				//find measurement for the common drug (to compare against it)
				RateMeasurement first = (RateMeasurement) s.getMeasurement(e,
						findPatientGroupForDrug(s, commonDrug)
				);

				Set<Drug> subStudyDrugs = new HashSet<Drug>(s.getDrugs());
				subStudyDrugs.remove(commonDrug);
				if (subStudyDrugs.size() > 1) {
					throw new UnableToBuildModelException("More than 2 drugs in a rate measurement study");
				}
				Drug subStudyDrug = subStudyDrugs.iterator().next();
				Alternative alt = findAlternative(subStudyDrug, model);

				RateMeasurement other = (RateMeasurement)s.getMeasurement(e,
						findPatientGroupForDrug(s, subStudyDrug));
				RiskRatio od = new LogRiskRatio(first, other);
				model.getImpactMatrix().setMeasurement(crit, alt, 
						new LogNormalMeasurement(od.getMean(), od.getStdDev()));				
			}
		} else {
			throw new UnableToBuildModelException("Not supported for other studies than combines ones.");
		}
	}

	private static PatientGroup findPatientGroupForDrug(Study study, Drug d) throws UnableToBuildModelException{
		PatientGroup pg = null;
		for (PatientGroup g : study.getPatientGroups()) {
			if (g.getDrug().equals(d)) {
				if (pg != null) {
					throw new UnableToBuildModelException("Study with more than 1 patient group for the same drug not supported.");				
				}
				pg = g;
			}
		}
		return pg;
	}

	private static void addAlternativesToModel(Study study, SMAAModel model) {
		for (Drug d : study.getDrugs()) {
			model.addAlternative(new Alternative(d.getName()));
		}
	}

	public static Alternative findAlternative(Drug g, SMAAModel model) {
		for (Alternative a : model.getAlternatives()) {
			if (a.getName().equals(g.getName())) {
				return a;
			}
		}
		throw new IllegalStateException("Alternative not found");
	}

}
