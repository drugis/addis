package org.drugis.addis.presentation;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class TypedHolder<T> extends AbstractValueModel implements ValueHolder<T> {
	private T d_content = null;
	
	public TypedHolder(T content) {
		d_content = content;
	}
	
	public TypedHolder() {
	}

	/* (non-Javadoc)
	 * @see org.drugis.addis.presentation.ValueHolder#getValue()
	 */
	public T getValue() {
		return d_content;
	}

	/* (non-Javadoc)
	 * @see org.drugis.addis.presentation.ValueHolder#setValue(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public void setValue(Object newValue) {
		T oldValue = d_content;
		d_content = (T) newValue;
		fireValueChange(oldValue, d_content);
	}
}