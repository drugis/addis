package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

import org.drugis.common.EqualsUtil;

public class OtherActivity extends AbstractEntity implements Activity {

	public static final String PROPERTY_DESCRIPTION = "description";
	private String d_description;

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
	
	public void setDescription(String d) {
		String oldValue = d_description;
		d_description = d;
		firePropertyChange(PROPERTY_DESCRIPTION, oldValue, d_description);
		System.out.println("FIRED ");
	}
	
	@Override
	public String toString() {
		return getLabel();
	}

	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}

	@Override
	protected OtherActivity clone() {
		return new OtherActivity(d_description);
	}
}
