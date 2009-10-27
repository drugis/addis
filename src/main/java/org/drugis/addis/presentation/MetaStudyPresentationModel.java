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

package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class MetaStudyPresentationModel extends PresentationModel<MetaStudy> 
										implements StudyListPresentationModel {
	private CharacteristicVisibleMap d_characteristicVisibleMap = new CharacteristicVisibleMap();
	
	public MetaStudyPresentationModel(MetaStudy study) {
		super(study);
	}
	
	public ListHolder<Study> getIncludedStudies() {
		return new StudyListHolder();
	}
	
	public AbstractValueModel getCharacteristicVisibleModel(StudyCharacteristic c) {
		return d_characteristicVisibleMap.get(c);
	}
	
	class StudyListHolder extends AbstractListHolder<Study> {
		@Override
		public List<Study> getValue() {
			return new ArrayList<Study>(getBean().getAnalysis().getStudies());
		}
	}
}

