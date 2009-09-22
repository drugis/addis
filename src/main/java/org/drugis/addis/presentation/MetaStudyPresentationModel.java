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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;

@SuppressWarnings("serial")
public class MetaStudyPresentationModel extends PresentationModel<MetaStudy> {
	private Map<StudyCharacteristic, AbstractValueModel> d_characteristicVisibleMap;
	
	public MetaStudyPresentationModel(MetaStudy study) {
		super(study);
		d_characteristicVisibleMap = new HashMap<StudyCharacteristic, AbstractValueModel>();
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			d_characteristicVisibleMap.put(c, new ValueHolder(true));
		}
	}
	
	public List<Study> getIncludedStudies() {
		return getBean().getAnalysis().getStudies();
	}
	
	public AbstractValueModel getCharacteristicVisibleModel(StudyCharacteristic c) {
		return d_characteristicVisibleMap.get(c);
	}
}
