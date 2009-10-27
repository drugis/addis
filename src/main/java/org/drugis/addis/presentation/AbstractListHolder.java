/**
 * 
 */
package org.drugis.addis.presentation;

import java.util.List;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public abstract class AbstractListHolder<E> extends AbstractValueModel implements ListHolder<E> {
	/**
	 * @see org.drugis.addis.presentation.ListHolder#getValue()
	 */
	public abstract List<E> getValue();

	/**
	 * @see org.drugis.addis.presentation.ListHolder#setValue(java.lang.Object)
	 */
	public void setValue(Object newValue) {
		throw new UnsupportedOperationException("AbstractListModel is read-only");
	}
}