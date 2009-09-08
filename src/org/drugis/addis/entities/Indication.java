package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

import com.jgoodies.binding.beans.Model;

public class Indication extends Model implements Comparable<Indication>, Entity {
	private static final long serialVersionUID = -4383475531365696177L;
	
	private String d_name;
	/**
	 * SNOMED CT code is defined as a 64-bit int.
	 */
	private Long d_code;
	
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_CODE = "code";
	
	public Indication(Long code, String name) {
		d_code = code;
		d_name = name;
	}

	public int compareTo(Indication other) {
		if (other == null) {
			return 1;
		}
		return d_code.compareTo(other.d_code);
	}

	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}

	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}

	public String getName() {
		return d_name;
	}

	public void setCode(Long code) {
		Long oldVal = d_code;
		d_code = code;
		firePropertyChange(PROPERTY_CODE, oldVal, d_code);
	}

	public Long getCode() {
		return d_code;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Indication) {
			Indication other = (Indication)o;
			return other.d_code.equals(d_code);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return d_code.hashCode();
	}
	
	@Override
	public String toString() {
		return d_code.toString() + " " + d_name;
	}
}