/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.util;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.DurationPresentation;

public class ArmForTemplate
{
	private Study d_study;
	
	/**
	 * Arm Class used by the template. 
	 * The getters are important, should not be renamed.
	 * $it.name$ in template corresponds to getName(), where $it is the iterator
	 */
	private Arm d_arm;

	public ArmForTemplate(Study study, Arm arm) {
		d_study = study;
		d_arm = arm;
	}

	public String getName() {
		return d_arm.getName();
	}
	public String getTreatment() {
		return d_study.getTreatment(d_arm).getLabel();
	}
	public String getDuration() {
		return getEpochDuration(d_study.findTreatmentEpoch());
	}
	public String getNrRandomized() {
		return d_arm.getSize().toString();
	}
	
	private static String getEpochDuration(Epoch epoch) {
		if (epoch != null && epoch.getDuration() != null) {
			DurationPresentation<Epoch> pm = new DurationPresentation<Epoch>(epoch);
			return pm.getLabel();
		}
		return "&lt;duration&gt;";
	}
}