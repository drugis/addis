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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.SortedSet;

import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class IndicationPresentation extends LabeledPresentationModel<Indication> {
	public static class LabelModel extends AbstractLabelModel<Indication> {
		protected LabelModel(Indication bean) {
			super(bean);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(Indication.PROPERTY_CODE)) {
				firePropertyChange(evt.getOldValue() + " " + getBean().getName(), getValue());
			} else if (evt.getPropertyName().equals(Indication.PROPERTY_NAME)) {
				firePropertyChange(getBean().getCode() + " " + evt.getOldValue(), getValue());
			}
		}
	}

	private StudyListPresentationModelImpl d_studyListModel;

	public IndicationPresentation(Indication bean, SortedSet<Study> studies) {
		super(bean);
		d_studyListModel = new StudyListPresentationModelImpl(new ArrayList<Study>(studies));
	}

	public StudyListPresentationModel getStudyListModel() {
		return d_studyListModel;
	}

	@Override
	public AbstractValueModel getLabelModel() {
		return new LabelModel(getBean());
	}

}
