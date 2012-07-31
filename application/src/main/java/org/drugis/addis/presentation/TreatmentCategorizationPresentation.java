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
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.common.beans.FilteredObservableList;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;

public class TreatmentCategorizationPresentation extends PresentationModel<TreatmentCategorization> implements StudyListPresentation {
	private static final long serialVersionUID = 134566312654511102L;
	private CharacteristicVisibleMap d_charVisibleMap = new CharacteristicVisibleMap();
	private final Map<Category, StudyListPresentation> d_studyListPresentations = new HashMap<Category, StudyListPresentation>();
	private final Domain d_domain;

	private class StudyCategoryFilter implements FilteredObservableList.Filter<Study> {
		private final Category d_category;

		public StudyCategoryFilter(final Category category) {
			d_category = category;
		}

		@Override
		public boolean accept(final Study s) {
			for (final Arm arm : s.getArms()) {
				final TreatmentActivity treatment = s.getTreatment(arm);
				for (final DrugTreatment drugTreatment : treatment.getTreatments()) {
					final Category category = ((LeafNode)getBean().getCategory(drugTreatment.getDose())).getCategory();
					if (drugTreatment.getDrug().equals(getBean().getDrug()) && d_category.equals(category)) {
						return true;
					}
				}
			}
			return false;
		}
	};

	private class CategorizedStudyListPresentation implements StudyListPresentation {
		private final FilteredObservableList<Study> d_studies;
		private final CharacteristicVisibleMap d_characteristicVisibleMap;

		public CategorizedStudyListPresentation(final Category category) {
			final StudyCategoryFilter filter = new StudyCategoryFilter(category);
			d_studies = new FilteredObservableList<Study>(d_domain.getStudies(getBean().getDrug()), filter);
			d_characteristicVisibleMap = new CharacteristicVisibleMap();
		}

		@Override
		public ObservableList<Study> getIncludedStudies() {
			return d_studies;
		}

		@Override
		public AbstractValueModel getCharacteristicVisibleModel(final Characteristic c) {
			return d_characteristicVisibleMap.get(c);
		}
	}

	public TreatmentCategorizationPresentation(final TreatmentCategorization bean, final Domain domain) {
		super(bean);
		d_domain = domain;

		for(final Category category : getBean().getCategories()) {
			d_studyListPresentations.put(category, new CategorizedStudyListPresentation(category));
		}
	}

	public StudyListPresentation getCategorizedStudyList(final Category category) {
		StudyListPresentation result = d_studyListPresentations.get(category);
		if (result == null) {
			result = new CategorizedStudyListPresentation(category);
			d_studyListPresentations.put(category, result);
		}
		return result;
	}

	public DrugPresentation getDrugPresentation() {
		return new DrugPresentation(getBean().getDrug(), d_domain);
	}

	@Override
	public ObservableList<Study> getIncludedStudies() {
		ObservableList<Study> result = new ArrayListModel<Study>();
		for (StudyListPresentation slp : d_studyListPresentations.values()) {
			for (Study study : slp.getIncludedStudies()) {
				if (!result.contains(study)) {
					result.add(study);
				}
			}
		}
		return result;
	}

	@Override
	public AbstractValueModel getCharacteristicVisibleModel(Characteristic c) {
		return d_charVisibleMap.get(c);
	}}
