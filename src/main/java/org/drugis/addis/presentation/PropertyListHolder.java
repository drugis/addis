package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.drugis.addis.util.comparator.AlphabeticalComparator;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

/**
 * Wraps a beans-property that returns a list in a ListHolder
 */
@SuppressWarnings("serial")
public class PropertyListHolder<E> extends AbstractListHolder<E> implements PropertyChangeListener {
	private final AbstractValueModel d_vm;

	@SuppressWarnings("unchecked")
	public PropertyListHolder(Object bean, String propertyName, Class<E> objType) {
		PresentationModel pm = new PresentationModel(bean);
		d_vm = pm.getModel(propertyName);
		d_vm.addValueChangeListener(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<E> getValue() {
		if (d_vm.getValue() instanceof Set) {
			List<E> lst = new ArrayList((Set<E>)d_vm.getValue());
			Collections.sort(lst, new AlphabeticalComparator());
			return lst;
		}
		return Collections.unmodifiableList((List<E>)d_vm.getValue());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object list) {
		setValue((List<E>)list);
	}
	
	public void setValue(List<E> list) {
		// FIXME: NI
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		fireValueChange(null, getValue());
	}
}
