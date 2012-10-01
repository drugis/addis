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

import java.util.Arrays;
import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.relativeeffect.BasicRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffectFactory;

public class StudyForestPlotPresentation extends AbstractForestPlotPresentation {
	private Arm d_baseline;
	private Arm d_subject;

	public StudyForestPlotPresentation(OutcomeMeasure om, StudyArmsEntry studyArmsEntry, 
			Class<? extends RelativeEffect<?>> type) {
		super(om, Arrays.asList(studyArmsEntry.getStudy()),
				createRelativeEffect(om, studyArmsEntry, type), null);
		d_baseline = studyArmsEntry.getBase();
		d_subject = studyArmsEntry.getSubject();
	}

	private static List<BasicRelativeEffect<?>> createRelativeEffect(
			OutcomeMeasure om, StudyArmsEntry studyArmsEntry,
			Class<? extends RelativeEffect<?>> type) {
		final BasicRelativeEffect<?> re = (BasicRelativeEffect<?>)RelativeEffectFactory.buildRelativeEffect(studyArmsEntry, om, type, false);
		return Arrays.<BasicRelativeEffect<?>>asList(re);
	}

	protected String getBaselineLabel() {
		return d_baseline.getLabel();
	}

	protected String getSubjectLabel() {
		return d_subject.getLabel();
	}
}
