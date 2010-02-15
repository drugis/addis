/**
 * 
 */
package org.drugis.addis.presentation;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class UnmodifiableHolder<T> extends AbstractValueModel implements ValueHolder<T> {
	private T d_obj;
	
	public UnmodifiableHolder(T obj) {
		d_obj = obj;
	}

	public T getValue() {
		return d_obj;
	}

	public void setValue(Object newValue) {
		throw new RuntimeException("Unexpected modification");
	}
}