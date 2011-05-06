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

package org.drugis.addis.gui;

import org.drugis.addis.entities.PredefinedActivity;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.presentation.ModifiableHolder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;

public class AddStudyActivityPresentationModel extends PresentationModel<StudyActivity> {
	private static final long serialVersionUID = -2471695041884415688L;

	private class StudyActivityValueHolder extends ModifiableHolder<StudyActivity> {
		private static final long serialVersionUID = -1601203195395456237L;

		public StudyActivityValueHolder(StudyActivity t) {
			super(t);
		}
		
		@Override
		public void setValue(Object newValue) {
			StudyActivity bean = getBean();
			if (bean.getActivity() instanceof TreatmentActivity) {
				// do one thing
			} else if (bean.getActivity() instanceof PredefinedActivity) {
				// do another
			}
		}
	}
	
	public AddStudyActivityPresentationModel(StudyActivity bean) {
		super(bean);
	}
	
	ValueModel getActivityTypeModel() {
		return new StudyActivityValueHolder(getBean());
	}
}
