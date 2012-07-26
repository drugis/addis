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

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseQuantityChoiceNode;
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.knowledge.DosedDrugTreatmentKnowledge.CategorySpecifiers;
import org.drugis.common.beans.ContentAwareListModel;
import org.drugis.common.beans.FilteredObservableList;
import org.drugis.common.beans.ValueEqualsModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class DosedDrugTreatmentPresentation extends PresentationModel<DosedDrugTreatment> {
	private final Domain d_domain;
	private final ObservableList<Category> d_categories;
	private final ValueModel d_knownDoseChoice = new ModifiableHolder<DecisionTreeNode>();
	private final ValueModel d_considerDoseType;
	private final ValueModel d_ignoreDoseType;
	private final ValueModel d_unknownDoseChoice;
	private final ValueModel d_fixedDoseChoice;
	private final ValueModel d_flexibleDoseChoice;

	private final DoseQuantityChoiceNode d_fixedRangeNode;
	private final DoseQuantityChoiceNode d_flexibleLowerNode;
	private final DoseQuantityChoiceNode d_flexibleUpperNode;

	private final ValueModel d_considerFixed;
	private final ValueModel d_considerFlexibleLower;
	private final ValueModel d_considerFlexibleUpper;
	
	private Map<Category, StudyListPresentation> d_studyListPresentations = new HashMap<Category, StudyListPresentation>();

	private class StudyCategoryFilter implements FilteredObservableList.Filter<Study> {
		private Category d_category;

		public StudyCategoryFilter(Category category) {
			d_category = category;
		}

		public boolean accept(Study s) {
			for(Arm arm : s.getArms()) {
				TreatmentActivity treatment = s.getTreatment(arm);
				for(DrugTreatment drugTreatment : treatment.getTreatments()) {
					String category = getCategory(drugTreatment.getDose());
					if(drugTreatment.getDrug().equals(getDrug()) && category.equals(d_category.getName())) {
						return true;
					}
				}
			}
			return false;
		}
	};


	private class CategorizedStudyListPresentation implements StudyListPresentation {
		private FilteredObservableList<Study> d_studies;
		private CharacteristicVisibleMap d_characteristicVisibleMap;

		public CategorizedStudyListPresentation(final Category category) {
			final StudyCategoryFilter filter = new StudyCategoryFilter(category);
			d_studies = new FilteredObservableList<Study>(d_domain.getStudies(((Drug)getDrug().getValue())), filter);
			d_characteristicVisibleMap = new CharacteristicVisibleMap();
		}

		public ObservableList<Study> getIncludedStudies() {
			return d_studies;
		}

		public AbstractValueModel getCharacteristicVisibleModel(Characteristic c) {
			return d_characteristicVisibleMap.get(c);
		}
	}
	

	public DosedDrugTreatmentPresentation(final DosedDrugTreatment bean) {
		this(bean, Main.getMainWindow().getDomain());
	}

	public DosedDrugTreatmentPresentation(final DosedDrugTreatment bean, final Domain domain) {
		super(bean);
		d_domain = domain;
		d_categories = new ContentAwareListModel<Category>(bean.getCategories());
		d_considerDoseType = new ValueEqualsModel(d_knownDoseChoice, CategorySpecifiers.CONSIDER);
		d_ignoreDoseType = new ValueEqualsModel(d_knownDoseChoice, CategorySpecifiers.DO_NOT_CONSIDER);

		d_unknownDoseChoice = getModelForType(UnknownDose.class);
		d_fixedDoseChoice = getModelForType(FixedDose.class);
		d_flexibleDoseChoice = getModelForType(FlexibleDose.class);

		d_fixedRangeNode = new DoseQuantityChoiceNode(FixedDose.class, FixedDose.PROPERTY_QUANTITY, getBean().getDoseUnit());
		d_flexibleLowerNode = new DoseQuantityChoiceNode(FlexibleDose.class, FlexibleDose.PROPERTY_MIN_DOSE, getBean().getDoseUnit());
		d_flexibleUpperNode = new DoseQuantityChoiceNode(FlexibleDose.class, FlexibleDose.PROPERTY_MAX_DOSE, getBean().getDoseUnit());

		d_considerFixed = new ValueEqualsModel(d_fixedDoseChoice, d_fixedRangeNode);
		d_considerFlexibleLower = new ValueEqualsModel(d_flexibleDoseChoice, d_flexibleLowerNode);
		d_considerFlexibleUpper = new ValueEqualsModel(d_flexibleDoseChoice, d_flexibleUpperNode);
		
		for(Category category : getCategories()) {
			d_studyListPresentations.put(category, new CategorizedStudyListPresentation(category));
		}
	}

	public ValueModel getDrug() {
		return getModel(DosedDrugTreatment.PROPERTY_DRUG);
	}

	public ValueModel getName() {
		return getModel(DosedDrugTreatment.PROPERTY_NAME);
	}

	public ObservableList<Category> getCategories() {
		return d_categories;
	}

	public DoseUnit getDoseUnit() {
		return getBean().getDoseUnit();
	}

	public void setDoseUnit(final DoseUnit unit) {
		getBean().setDoseUnit(unit);
	}

	public DoseUnitPresentation getDoseUnitPresentation() {
		return new DoseUnitPresentation(getDoseUnit());
	}

	public void resetTree() {
		getBean().resetTree();
	}

	/**
	 * Add the DosedDrugTreatment to the domain. Throws an exception if the treatment is already in the domain.
	 * Note that domain can be null in which case a null-pointer exception will occur.
	 * @return The DosedDrugTreatment that was added.
	 */
	public DosedDrugTreatment commit() {
		if (d_domain.getTreatments().contains(getBean())) {
			throw new IllegalStateException("Treatment already exists in domain");
		}

		d_domain.getTreatments().add(getBean());
		return getBean();
	}

	public ObservableList<DecisionTreeEdge> getOutEdges(final DecisionTreeNode node) {
		return new DecisionTreeOutEdgesModel(getBean().getDecisionTree(), node);
	}

//	private DoseRangeNode inheritPrototype(final RangeNode protoRange, final Class<? extends AbstractDose> beanClass, final String property) {
//		 return new DoseRangeNode(
//				beanClass,
//				property,
//				protoRange.getRangeLowerBound(),
//				protoRange.isRangeLowerBoundOpen(),
//				protoRange.getRangeUpperBound(),
//				protoRange.isRangeUpperBoundOpen(),
//				getDoseUnit());
//		}

	public String getCategory(final AbstractDose dose) {
		return getBean().getCategory(dose).toString();
	}

	/**
	 * ValueModel that holds the decision (DecisionTreeNode) for the given dose type.
	 */
	private ValueModel getModelForType(final Class<?> type) {
		final DecisionTree tree = getBean().getDecisionTree();
		final DecisionTreeChildModel model = new DecisionTreeChildModel(tree, tree.findMatchingEdge(tree.getRoot(), type));
		return model;
	}

	/**
	 * Selection holder for action on unknown doses.
	 */
	public ValueModel getModelForUnknownDose() {
		return d_unknownDoseChoice;
	}

	/**
	 * Selection holder for action on "known" doses (fixed or flexible).
	 */
	public ValueModel getModelForKnownDose() {
		return d_knownDoseChoice;
	}

	/**
	 * Selection holder for action on fixed doses.
	 */
	public ValueModel getModelForFixedDose() {
		return d_fixedDoseChoice;
	}

	/**
	 * Selection holder for action on flexible doses.
	 */
	public ValueModel getModelForFlexibleDose() {
		return d_flexibleDoseChoice;
	}

	/**
	 * ValueModel (Boolean) that indicates whether fixed and flexible doses should be treated separately.
	 */
	public ValueModel getConsiderDoseType() {
		return d_considerDoseType;
	}

	/**
	 * ValueModel (Boolean) that indicates whether fixed and flexible doses should be treated identically.
	 */
	public ValueModel getIgnoreDoseType() {
		return d_ignoreDoseType;
	}

	/**
	 * ValueModel (Boolean) that indicates whether quantity should be considered for fixed doses.
	 */
	public ValueModel getConsiderFixed() {
		return d_considerFixed;
	}

	/**
	 * ValueModel (Boolean) that indicates whether the MIN_DOSE should be considered first for flexible doses.
	 */
	public ValueModel getConsiderFlexibleLowerFirst() {
		return d_considerFlexibleLower;
	}

	/**
	 * ValueModel (Boolean) that indicates whether the MAX_DOSE should be considered first for flexible doses.
	 */
	public ValueModel getConsiderFlexibleUpperFirst() {
		return d_considerFlexibleUpper;
	}

	public ObservableList<DecisionTreeEdge> getFlexibleLowerRanges() {
		return new DecisionTreeOutEdgesModel(getBean().getDecisionTree(), d_flexibleLowerNode);
	}

	public ObservableList<DecisionTreeEdge> getFlexibleUpperRanges() {
		return new DecisionTreeOutEdgesModel(getBean().getDecisionTree(), d_flexibleUpperNode);
	}

	public ChoiceNode getFlexibleLowerRangeNode() {
		return d_flexibleLowerNode;
	}

	public ChoiceNode getFlexibleUpperRangeNode() {
		return d_flexibleUpperNode;
	}

	public ChoiceNode getFixedRangeNode() {
		return d_fixedRangeNode;
	}

	public StudyListPresentation getCategorizedStudyList(Category category) {
		StudyListPresentation result = d_studyListPresentations.get(category);
		if (result == null) {
			result = new CategorizedStudyListPresentation(category);
			d_studyListPresentations.put(category, result);
		}
		return result;
	}

	public DrugPresentation getDrugPresentation() {
		return new DrugPresentation(((Drug)getDrug().getValue()), d_domain);
}
}
