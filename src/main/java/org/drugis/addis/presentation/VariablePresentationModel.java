package org.drugis.addis.presentation;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.CategoricalPopulationCharacteristic;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.ContinuousPopulationCharacteristic;
import org.drugis.addis.entities.Endpoint;
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
		@Override
		public void setValue(Object newValue) {
			if (newValue.equals(Type.CATEGORICAL) && (getBean() instanceof PopulationCharacteristic))
				setBean(new CategoricalPopulationCharacteristic());
			else if (!newValue.equals(Type.CATEGORICAL) && (getBean() instanceof PopulationCharacteristic)) 
				setBean(new ContinuousPopulationCharacteristic());
			super.setValue(newValue);
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
		return new TypeValueHolder();
	}
	
	public SelectionInList<String> getCategoriesListModel() {
		
		if (!(getBean() instanceof CategoricalPopulationCharacteristic ))
			throw new IllegalStateException(getBean() + " is not a categoricalPopulationCharacteristic");
		
		ValueModel stringListModel =  d_pmf.getModel((CategoricalPopulationCharacteristic) getBean())
		.getModel(CategoricalPopulationCharacteristic.PROPERTY_CATEGORIESASLIST);
		return new SelectionInList<String>(stringListModel);
		//return stringListModel;
	}
}
