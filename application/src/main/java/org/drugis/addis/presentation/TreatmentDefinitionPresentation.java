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

package org.drugis.addis.presentation;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.beans.FilteredObservableList;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class TreatmentDefinitionPresentation extends PresentationModel<TreatmentDefinition> implements LabeledPresentation {
	
	public class LabelModel extends DefaultLabelModel {
		
		public LabelModel() {
			super(getBean());
		}

		@Override
		public Object getValue() {			
			return getBean().getLabel();
		}
	}

	private StudyListPresentation d_studyListPresentation;
		
	public TreatmentDefinitionPresentation(final TreatmentDefinition drugs, Domain domain) {
		super(drugs);
		ObservableList<Study> studies = new FilteredObservableList<Study>(domain.getStudies(), new FilteredObservableList.Filter<Study>() {
			@Override
			public boolean accept(Study s) {
				return EntityUtil.flatten(s.getTreatmentDefinitions()).equals(drugs);
			}
		});		
		d_studyListPresentation = new StudyListPresentation(studies);
	}

	public StudyListPresentation getStudyListPresentation() {
		return d_studyListPresentation;
	}

	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}
	
	@Override
	public String toString() {
		return (String) getLabelModel().getValue();
	}
}