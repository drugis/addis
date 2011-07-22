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

package org.drugis.addis.presentation;

import java.util.List;

import org.drugis.addis.entities.CategoricalVariableType;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.VariableType;
import org.drugis.addis.gui.CategoryKnowledgeFactory;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class VariablePresentation extends PresentationModel<Variable> implements StudyListPresentation, LabeledPresentation {
	private ListHolder<Study> d_studies;
	private CharacteristicVisibleMap d_characteristicVisibleMap = new CharacteristicVisibleMap();
	private ContinuousVariableType d_continuousVariableType = new ContinuousVariableType();
	private RateVariableType d_rateVariableType = new RateVariableType();
	private CategoricalVariableType d_categoricalVariableType = new CategoricalVariableType();

	public VariablePresentation(Variable bean, ListHolder<Study> studies, PresentationModelFactory pmf) {
		super(bean);
		d_studies = studies;
	}
	
	public ListHolder<Study> getIncludedStudies() {
		return d_studies;
	}
	
	public AbstractValueModel getCharacteristicVisibleModel(Characteristic c) {
		return d_characteristicVisibleMap.get(c);
	}

	public AbstractValueModel getLabelModel() {
		return new DefaultLabelModel(getBean());
	}
	
	public static String getEntityName(Variable om) throws IllegalArgumentException{
		return CategoryKnowledgeFactory.getCategoryKnowledge(om.getClass()).getSingularCapitalized();
	}
	
	public String getCategoryName() throws IllegalArgumentException{
		return getEntityName(getBean());
	}
	
	public ValueModel getTypeModel() {
		return new PropertyAdapter<Variable>(getBean(), Variable.PROPERTY_VARIABLE_TYPE);
	}
	
	public ObservableList<String> getCategoriesListModel() {
		assertCategorical();
		
		return ((CategoricalVariableType)getBean().getVariableType()).getCategories();
	}

	private void assertCategorical() {
		if (!(getBean().getVariableType() instanceof CategoricalVariableType))
			throw new IllegalStateException(getBean() + " is not categorical");
	}
	
	public void addNewCategory (String category) {
		assertCategorical();
		
		List<String> catsList = d_categoricalVariableType.getCategories();
		if (!catsList.contains(category))
			catsList.add(category);
	}

	public VariableType[] getVariableTypes() {
		if (getBean() instanceof PopulationCharacteristic) {
			return new VariableType[] { d_continuousVariableType, d_rateVariableType, d_categoricalVariableType };
		} else {
			return new VariableType[] { d_continuousVariableType, d_rateVariableType };
		}
	}
	
	public PresentationModel<ContinuousVariableType> getContinuousModel() {
		return new PresentationModel<ContinuousVariableType>(d_continuousVariableType);
	}
	
	public PresentationModel<CategoricalVariableType> getCategoricalModel() {
		return new PresentationModel<CategoricalVariableType>(d_categoricalVariableType);
	}
	
	public PresentationModel<RateVariableType> getRateModel() {
		return new PresentationModel<RateVariableType>(d_rateVariableType);
	}
}
