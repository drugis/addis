package org.drugis.addis.entities;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Set;

import org.drugis.common.EqualsUtil;

public class OtherActivity implements Activity {

	private final String d_description;

	public OtherActivity(String description) {
		d_description = description;
	}
	
	public boolean deepEquals(Entity other) {
		return equals(other);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof OtherActivity) {
			return EqualsUtil.equal(toString(), other.toString());
		}
		return false;
	}

	public String getLabel() {
		return d_description;
	}
	
	public String getDescription() {
		return d_description;
	}
	
	@Override
	public String toString() {
		return getLabel();
	}

	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {}
	public void removePropertyChangeListener(PropertyChangeListener listener) {}
	
	@Override
	protected OtherActivity clone() {
		return new OtherActivity(d_description);
	}
}
