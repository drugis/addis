package org.drugis.addis.presentation;

import java.util.List;

import com.jgoodies.binding.value.ValueModel;

public interface ListHolder<E> extends ValueModel {

	public List<E> getValue();

	public void setValue(Object newValue);

}