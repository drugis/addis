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

import java.util.HashMap;
import java.util.Map;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.common.beans.FilteredObservableList;

import com.jgoodies.binding.PresentationModel;

public class TreatmentCategorizationPresentation extends PresentationModel<TreatmentCategorization> {
	private static final long serialVersionUID = 134566312654511102L;
	private final Map<Category, StudyListPresentation> d_studyListPresentations = new HashMap<Category, StudyListPresentation>();
	private final Domain d_domain;

	private static class StudyCategoryFilter implements FilteredObservableList.Filter<Study> {
		private final Category d_category;

		public StudyCategoryFilter(final Category category) {
			d_category = category;
		}

		@Override
		public boolean accept(final Study s) {
			for (final Arm arm : s.getArms()) {
				final TreatmentActivity treatment = s.getTreatment(arm);
				for (final DrugTreatment drugTreatment : treatment.getTreatments()) {
					if (d_category.match(drugTreatment)) {
						return true;
					}
				}
			}
			return false;
		}
	};

	public TreatmentCategorizationPresentation(final TreatmentCategorization bean, final Domain domain) {
		super(bean);
		d_domain = domain;

		for(final Category category : getBean().getCategories()) {
			d_studyListPresentations.put(category, new StudyListPresentation(createCategoryStudyList(category)));
		}
	}

	private FilteredObservableList<Study> createCategoryStudyList(final Category category) {
		return new FilteredObservableList<Study>(
				d_domain.getTreatmentDefinition(getBean().getDrug()),
				new StudyCategoryFilter(category));
	}
	
	public StudyListPresentation getCategorizedStudyList(final Category category) {
		return d_studyListPresentations.get(category);
	}

	public DrugPresentation getDrugPresentation() {
		return new DrugPresentation(getBean().getDrug(), d_domain);
	}
}
