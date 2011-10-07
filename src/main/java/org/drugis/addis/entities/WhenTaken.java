/**
 * 
 */
package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

import javax.xml.datatype.Duration;

import org.drugis.addis.presentation.DurationPresentation;
import org.drugis.common.EqualsUtil;

public class WhenTaken extends AbstractEntity implements Entity, Comparable<WhenTaken>, TypeWithDuration {

	public enum RelativeTo {
		BEFORE_EPOCH_END("Before end of"),
		FROM_EPOCH_START("From start of");

		String d_string;
		
		RelativeTo(String s) {
			d_string = s;
		}
		
		@Override
		public String toString() {
			return d_string;
		}
	}
	
	
	public static final String PROPERTY_EPOCH = "epoch";
	public static final String PROPERTY_RELATIVE_TO = "relativeTo";
	public static final String PROPERTY_OFFSET = "offset";
	private Duration d_offset;
	private RelativeTo d_relativeTo;
	private Epoch d_epoch;

	public WhenTaken(Duration offset, RelativeTo relativeTo, Epoch epoch) {
		d_offset = offset;
		d_epoch = epoch;
		d_relativeTo = relativeTo;
	}

	public Duration getOffset() {
		return d_offset;
	}

	public void setOffset(Duration duration) {
		Duration oldValue = d_offset;
		d_offset = duration;
		firePropertyChange(PROPERTY_OFFSET, oldValue, d_offset);
	}

	public RelativeTo getRelativeTo() {
		return d_relativeTo;
	}

	public void setRelativeTo(RelativeTo relativeTo) {
		RelativeTo oldValue = d_relativeTo;
		d_relativeTo = relativeTo;
		firePropertyChange(PROPERTY_RELATIVE_TO, oldValue, d_relativeTo);
	}
	
	public Epoch getEpoch() {
		return d_epoch;
	}
	
	public void setEpoch(Epoch epoch) {
		Epoch oldValue = d_epoch;
		d_epoch = epoch;
		firePropertyChange(PROPERTY_EPOCH, oldValue, d_epoch);
	}

	/**
	 * @see org.drugis.addis.entities.TypeWithDuration
	 */
	public Duration getDuration() {
		return getOffset();
	}
	
	/**
	 * @see org.drugis.addis.entities.TypeWithDuration
	 */	
	public void setDuration(Duration duration) {
		setOffset(duration);
	}

	
	public Set<? extends Entity> getDependencies() {
		return Collections.singleton(d_epoch);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof WhenTaken)) {
			return false;
		}
		WhenTaken other = (WhenTaken) obj;
		return EqualsUtil.equal(getOffset(), other.getOffset())
				&& EqualsUtil.equal(d_relativeTo, other.d_relativeTo)
				&& EqualsUtil.equal(d_epoch, other.d_epoch);
	}

	@Override
	public String toString() {
		String epochName = d_epoch == null ? "UNKNOWN" : d_epoch.getName();
		return DurationPresentation.parseDuration(getOffset(), null) + " " + formatRelativeTo(d_relativeTo) + epochName;
	}

	private String formatRelativeTo(RelativeTo relativeTo) {
		return relativeTo == RelativeTo.BEFORE_EPOCH_END ? "before end of " : "from start of ";
	}

	@Override
	public int compareTo(WhenTaken o) {
		if (d_relativeTo == o.d_relativeTo) {
			return getOffset().compare(o.getOffset());
		}
		return d_relativeTo == RelativeTo.FROM_EPOCH_START ? -1 : 1;
	}
	
	@Override
	public int hashCode() {
		int code = 1;
		code = code * 31 + d_epoch.hashCode();
		code = code * 31 + getOffset().hashCode();
		code = code * 31 + d_relativeTo.hashCode();
		return code;
	}

}