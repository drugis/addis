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

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.CategoricalPopulationCharacteristic;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.ContinuousPopulationCharacteristic;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.RatePopulationCharacteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.Variable.Type;
import org.drugis.addis.gui.CategoryKnowledgeFactory;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class VariablePresentation extends PresentationModel<Variable> implements StudyListPresentation, LabeledPresentation {

	private ListHolder<Study> d_studies;
	private CharacteristicVisibleMap d_characteristicVisibleMap = new CharacteristicVisibleMap();
	private PresentationModelFactory d_pmf;
	
	
	private class TypeValueHolder extends ModifiableHolder<Type> {
			
		public TypeValueHolder(Type t) {
			super(t);
		}
		
		@Override
		public void setValue(Object newValue) {
			Variable bean = getBean();
			if (newValue.equals(Type.CATEGORICAL) && (bean instanceof PopulationCharacteristic))
				setBean(new CategoricalPopulationCharacteristic());
			
			else if (newValue.equals(Type.CONTINUOUS) && (bean instanceof PopulationCharacteristic)) {
				ContinuousPopulationCharacteristic newBean = new ContinuousPopulationCharacteristic();
				newBean.setType((Type) newValue);
				setBean(newBean);
			} else if (newValue.equals(Type.RATE) && (bean instanceof PopulationCharacteristic)) {
				RatePopulationCharacteristic newBean = new RatePopulationCharacteristic();
				newBean.setType((Type) newValue);
				setBean(newBean);
			}
			super.setValue(newValue);
			
			if ((bean instanceof OutcomeMeasure)) {
				((OutcomeMeasure) bean).setType(getValue());
			}
		}
	}
	
	public VariablePresentation(Variable bean, ListHolder<Study> studies, PresentationModelFactory pmf) {
		super(bean);
		d_studies = studies;
		d_pmf = pmf;
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
		return new TypeValueHolder(getBean().getType());
	}
	
	public SelectionInList<String> getCategoriesListModel() {
		
		if (!(getBean() instanceof CategoricalPopulationCharacteristic ))
			throw new IllegalStateException(getBean() + " is not a categoricalPopulationCharacteristic");
		
		ValueModel stringListModel =  d_pmf.getModel((CategoricalPopulationCharacteristic) getBean())
										            .getModel(CategoricalPopulationCharacteristic.PROPERTY_CATEGORIESASLIST);
		
		return new SelectionInList<String>(stringListModel);
	}
	
	public void addNewCategory (String category) {
		if (!(getBean() instanceof CategoricalPopulationCharacteristic ))
			throw new IllegalStateException(getBean() + " is not a categoricalPopulationCharacteristic");
		
		CategoricalPopulationCharacteristic catVar = (CategoricalPopulationCharacteristic) getBean();
		List<String> catsList = new ArrayList<String>(catVar.getCategoriesAsList());
		if (!catsList.contains(category))
			catsList.add(category);
		catVar.setCategoriesAsList(catsList);	
	}
}
