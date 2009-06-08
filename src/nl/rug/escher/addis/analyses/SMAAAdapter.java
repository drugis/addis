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
