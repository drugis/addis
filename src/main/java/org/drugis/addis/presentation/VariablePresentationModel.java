package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.CategoricalPopulationCharacteristic;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.ContinuousPopulationCharacteristic;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.Variable.Type;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class VariablePresentationModel extends PresentationModel<Variable> implements StudyListPresentationModel, LabeledPresentationModel {

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
			
			else if (!newValue.equals(Type.CATEGORICAL) && (bean instanceof PopulationCharacteristic)) {
				ContinuousPopulationCharacteristic newBean = new ContinuousPopulationCharacteristic();
				newBean.setType((Type) newValue);
				setBean(newBean);
			}
			super.setValue(newValue);
			
			if ((bean instanceof OutcomeMeasure)) {
				((OutcomeMeasure) bean).setType(getValue());
			}
		}
	}
	
	public VariablePresentationModel(Variable bean, ListHolder<Study> studies, PresentationModelFactory pmf) {
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
		return new ValueHolder(getBean().getName());
	}
	
	public static String getEntityName(Variable om) throws IllegalArgumentException{
		if(om instanceof Endpoint)
			return "Endpoint";
		if(om instanceof AdverseEvent)
			return "Adverse drug event";
		if(om instanceof Variable)
			return "Population characteristic";
		else
			throw new IllegalArgumentException("Category not recognized");
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
