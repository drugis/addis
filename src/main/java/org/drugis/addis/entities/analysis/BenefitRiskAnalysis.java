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

package org.drugis.addis.entities.analysis;

import java.util.List;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.relativeeffect.Distribution;

public interface BenefitRiskAnalysis<Alternative extends Entity> extends Comparable<BenefitRiskAnalysis<?>>, Entity {
	public static String PROPERTY_NAME = "name";
	public static String PROPERTY_INDICATION = "indication";
	public static String PROPERTY_CRITERIA = "criteria";
	public static String PROPERTY_ALTERNATIVES = "alternatives";
	public static String PROPERTY_ANALYSIS_TYPE = "analysisType";

	public enum AnalysisType { LyndOBrien, SMAA };
	
	public abstract AnalysisType getAnalysisType();
	
	public abstract Indication getIndication();

	public abstract List<OutcomeMeasure> getCriteria();

	public abstract List<Alternative> getAlternatives();

	public abstract String getName();
	
	public Distribution getMeasurement(Alternative a, OutcomeMeasure criterion);
}