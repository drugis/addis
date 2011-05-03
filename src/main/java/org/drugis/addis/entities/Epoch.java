package org.drugis.addis.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.Duration;

import org.drugis.addis.util.EntityUtil;
import org.drugis.common.EqualsUtil;

public class Epoch extends AbstractNamedEntity<Epoch> implements TypeWithNotes {
	public static final String PROPERTY_DURATION = "duration";

	private Duration d_duration;
	private List<Note> d_notes = new ArrayList<Note>();
	
	public Epoch(String name, Duration duration) {
		super(name);
		d_duration = duration;
	}

	public List<Note> getNotes() {
		return d_notes;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
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
		if (obj instanceof Epoch) {
			return super.equals(obj);
		}
		return false;
	}
	
	public boolean deepEquals(Entity obj) {
		if(!equals(obj)) return false;
		Epoch other = (Epoch) obj;
		return EqualsUtil.equal(other.getDuration(), getDuration()) && EntityUtil.deepEqual(other.getNotes(), getNotes());
	}
	
	@Override
	protected Epoch clone() {
		return new Epoch(getName(), d_duration);
	}
	
	@Override
	public String toString() {
		return getName() + " " + getDuration() + " " + getNotes();
	}
}
