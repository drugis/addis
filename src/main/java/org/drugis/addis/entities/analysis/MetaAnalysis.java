/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;

public interface MetaAnalysis extends Entity, Comparable<MetaAnalysis> {
	public static final String PROPERTY_NAME = "name";	
	public String getName();
	public void setName(String name);
	
	public static final String PROPERTY_TYPE = "type";
	public String getType();
	
	public static final String PROPERTY_INDICATION = "indication";
	public Indication getIndication();

	public static final String PROPERTY_OUTCOME_MEASURE = "outcomeMeasure";
	public OutcomeMeasure getOutcomeMeasure();
	
	public static final String PROPERTY_SAMPLE_SIZE = "sampleSize";
	public int getSampleSize();

	public static final String PROPERTY_INCLUDED_DRUGS = "includedDrugs";
	public List<Drug> getIncludedDrugs();
	
	public static final String PROPERTY_INCLUDED_STUDIES = "includedStudies";
	public List<Study> getIncludedStudies();
	public RelativeEffect<? extends Measurement> getRelativeEffect(
			Drug d1, Drug d2, Class<? extends RelativeEffect<?>> type);
}
