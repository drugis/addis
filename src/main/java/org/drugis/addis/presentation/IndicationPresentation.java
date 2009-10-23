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
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.SortedSet;

import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.PropertyNotFoundException;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class IndicationPresentation extends PresentationModel<Indication> implements LabeledPresentationModel {
	private StudyListPresentationModelImpl d_studyListModel;
	protected PresentationModelManager d_pmm;

	public IndicationPresentation(Indication bean, SortedSet<Study> studies) {
		super(bean);
		getBean().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(Indication.PROPERTY_CODE)) {
					firePropertyChange(PROPERTY_LABEL, evt.getOldValue() + " " + getBean().getName(), getLabel());
				} else if (evt.getPropertyName().equals(Indication.PROPERTY_NAME)) {
					firePropertyChange(PROPERTY_LABEL, getBean().getCode() + " " + evt.getOldValue(), getLabel());
				}
			}
		});
		d_studyListModel = new StudyListPresentationModelImpl(new ArrayList<Study>(studies));
	}

	public StudyListPresentationModel getStudyListModel() {
		return d_studyListModel;
	}

	public String getLabel() {
		return getBean().toString();
	}
	
	public AbstractValueModel getModel(String propertyName) {
		try {
			PresentationModel<IndicationPresentation> pm = new PresentationModel<IndicationPresentation>(this);
			return (pm.getModel(propertyName));
		} catch (PropertyNotFoundException e) {
			return super.getModel(propertyName);
		}
	}

	public AbstractValueModel getLabelModel() {
		return getModel(PROPERTY_LABEL);
	}
}
