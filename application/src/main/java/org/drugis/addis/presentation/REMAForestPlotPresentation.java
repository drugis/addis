/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.relativeeffect.BasicRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffectFactory;
import org.drugis.addis.entities.treatment.TreatmentDefinition;


public class REMAForestPlotPresentation extends AbstractForestPlotPresentation {
	private RandomEffectsMetaAnalysis d_analysis;
	
	public REMAForestPlotPresentation(RandomEffectsMetaAnalysis ma, Class<? extends RelativeEffect<?>> type) {
		super(ma.getOutcomeMeasure(), ma.getIncludedStudies(), createRelativeEffects(ma, type), ma.getRelativeEffect(type));
		d_analysis = ma;
	}

	private static List<BasicRelativeEffect<?>> createRelativeEffects(RandomEffectsMetaAnalysis ma,
			Class<? extends RelativeEffect<?>> type) {
		List<BasicRelativeEffect<?>> list = new ArrayList<BasicRelativeEffect<?>>();
		for (StudyArmsEntry entry : ma.getStudyArms()) {
			BasicRelativeEffect<?> re = (BasicRelativeEffect<?>) 
					RelativeEffectFactory.buildRelativeEffect(entry, ma.getOutcomeMeasure(), type, ma.getIsCorrected());
			list.add(re);
		}
		return list;
	}
	
	@Override
	protected String getBaselineLabel() {
		return getBaseline().getLabel();
	}
	
	@Override
	protected String getSubjectLabel() {
		return getSubject().getLabel();
	}

	private TreatmentDefinition getBaseline() {
		return d_analysis.getFirstAlternative();
	}

	private TreatmentDefinition getSubject() {
		return d_analysis.getSecondAlternative();
	}
}
