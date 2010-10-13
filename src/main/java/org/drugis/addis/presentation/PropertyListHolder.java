package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
			System.out.println("instanceof Set");
			return lst;
		}
		System.out.println("NOT instanceof Set");
		return Collections.unmodifiableList((List<E>) d_vm.getValue());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object list) {
		System.out.println(list);
		if (d_vm.getValue() instanceof List) {
			d_vm.setValue((List<E>)list); 
		}
		else if (d_vm.getValue() instanceof Set){
			HashSet<E> hashSet = new HashSet<E>();
			for(int i=0; i < ((List<E>) list).size(); i++) {
				hashSet.add(((List<E>) list).get(i));
			}
			d_vm.setValue(hashSet);
		}
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		fireValueChange(null, getValue());
	}
}
