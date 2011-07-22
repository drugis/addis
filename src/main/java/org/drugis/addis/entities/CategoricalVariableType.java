package org.drugis.addis.entities;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class CategoricalVariableType extends AbstractEntity implements VariableType {
	public static final String PROPERTY_CATEGORIES = "categories";
	private ObservableList<String> d_cats = new ArrayListModel<String>();
	
	public CategoricalVariableType() {
	}
	
	public CategoricalVariableType(List<String> cats) {
		d_cats.addAll(cats);
	}

	public BasicMeasurement buildMeasurement() {
		return new FrequencyMeasurement(d_cats.toArray(new String[]{}), new HashMap<String, Integer>());
	}

	public BasicMeasurement buildMeasurement(int size) {
		return new FrequencyMeasurement(d_cats.toArray(new String[]{}), new HashMap<String, Integer>());
	}

	public String getType() {
		return "Categorical";
	}

	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	public ObservableList<String> getCategories() {
		return d_cats;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CategoricalVariableType) {
			CategoricalVariableType other = (CategoricalVariableType) obj;
			return EqualsUtil.equal(d_cats, other.d_cats);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return d_cats == null ? 0 : d_cats.hashCode();
	}
	
	@Override
	public String toString() {
		return getType();
	}
}
