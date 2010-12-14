package org.drugis.addis.gui;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainEvent;
import org.drugis.addis.entities.DomainListener;
import org.drugis.addis.presentation.ValueHolder;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class DomainChangedModel extends AbstractValueModel implements ValueHolder<Boolean> {
	private boolean d_changed;

	public DomainChangedModel(Domain domain, boolean changed) {
		d_changed = changed;
		domain.addListener(new DomainListener() {
			public void domainChanged(DomainEvent evt) {
				setValue(true);
			}
		});
	}

	public Boolean getValue() {
		return d_changed;
	}
	
	public void setValue(Object newValue) {
		boolean oldValue = d_changed;
		d_changed = ((Boolean)newValue);
		fireValueChange(oldValue, d_changed);
	}
}
