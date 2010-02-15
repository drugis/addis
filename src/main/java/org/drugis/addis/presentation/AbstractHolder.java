package org.drugis.addis.presentation;

import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public abstract class AbstractHolder<T> extends AbstractValueModel implements ValueHolder<T> {
	protected abstract void checkArgument(Object newValue);
	
	private T d_content = null;

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
		checkArgument(newValue);
		T oldValue = d_content;
		d_content = (T) newValue;
		fireValueChange(oldValue, d_content);
		conditionalCascade(newValue, oldValue);
	}

	private void conditionalCascade(Object newValue, T oldValue) {
		if (!EqualsUtil.equal(oldValue, newValue)) {
			cascade();
		}
	}
	
	public void unSet() {
		T oldValue = d_content;
		d_content = null;
		fireValueChange(oldValue, d_content);
		conditionalCascade(null, oldValue);
	}

	protected abstract void cascade();
}