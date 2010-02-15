package org.drugis.addis.presentation;

import com.jgoodies.binding.value.ValueModel;

public interface ValueHolder<T> extends ValueModel {

	public T getValue();

	public void setValue(Object newValue);

}