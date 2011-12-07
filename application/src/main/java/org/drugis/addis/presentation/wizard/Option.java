/**
 * 
 */
package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.common.EqualsUtil;
import org.drugis.common.beans.AbstractObservable;

public class Option<E extends Comparable<? super E>> extends AbstractObservable implements Comparable<Option<E>> {
	public static final String PROPERTY_TOGGLED = "toggled";
	public final E item;
	public final ModifiableHolder<Boolean> toggle;
	
	public Option(E it, boolean value) {
		item = it;
		toggle = new ModifiableHolder<Boolean>(value);
		toggle.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				firePropertyChange(PROPERTY_TOGGLED, evt.getOldValue(), evt.getNewValue());
			}
		});
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Option<?>) {
			Option<?> other = (Option<?>) obj;
			return EqualsUtil.equal(item, other.item);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return item.hashCode();
	}

	@Override
	public int compareTo(Option<E> o) {
		return item.compareTo(o.item);
	}
}