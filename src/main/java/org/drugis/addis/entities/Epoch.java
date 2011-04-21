package org.drugis.addis.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.Duration;

import org.drugis.addis.util.EntityUtil;
import org.drugis.common.EqualsUtil;

public class Epoch extends AbstractEntity implements TypeWithNotes {
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_DURATION = "duration";

	private String d_name;
	private Duration d_duration;
	private List<Note> d_notes = new ArrayList<Note>();
	
	public Epoch(String name, Duration duration) {
		d_name = name;
		d_duration = duration;
	}

	public List<Note> getNotes() {
		return d_notes;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}

	public void setName(String name) {
		String oldValue = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldValue, d_name);
	}

	public String getName() {
		return d_name;
	}

	public void setDuration(Duration duration) {
		Duration oldValue = d_duration;
		d_duration = duration;
		firePropertyChange(PROPERTY_DURATION, oldValue, d_duration);
	}

	public Duration getDuration() {
		return d_duration;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Epoch) {
			Epoch other = (Epoch) obj;
			return EqualsUtil.equal(other.getName(), getName());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (d_name == null ? 0 : d_name.hashCode()); 
	}
	
	public boolean deepEquals(Entity obj) {
		if(!equals(obj)) return false;
		Epoch other = (Epoch) obj;
		return EqualsUtil.equal(other.getDuration(), getDuration()) && EntityUtil.deepEqual(other.getNotes(), getNotes());
	}
	
	@Override
	protected Epoch clone() {
		return new Epoch(d_name, d_duration);
	}
}
